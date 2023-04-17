package com.example.demo.base.algorithm;

import com.example.demo.base.factories.PowerNodeFactory;
import com.example.demo.base.model.configuration.LoadConfiguration;
import com.example.demo.base.model.configuration.VoltageLevelInfo;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.AbstractLine;
import com.example.demo.base.model.power.BasePowerNode;
import com.example.demo.base.model.status.BlockType;
import com.example.demo.base.model.status.StatusType;
import com.example.demo.base.service.BaseConfiguration;
import com.example.demo.base.service.BaseElementService;
import com.example.demo.base.service.StatusService;
import com.example.demo.export.dto.PowerLineDto;
import com.example.demo.export.dto.PowerNodeDto;
import com.example.demo.export.dto.SaveDto;
import com.example.demo.base.service.ConnectionService;
import com.example.demo.export.service.ExportService;
import com.example.demo.java.fx.model.power.FxPowerNode;
import com.example.demo.services.FxElementService;
import com.example.demo.utils.RandomUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BaseAlgorithm<T extends BasePowerNode> implements Algorithm {

    protected final Matrix<T> matrix;
    protected final BaseElementService elementsService;
    protected final StatusService statusService;
    protected final ConnectionService connectionService;
    protected final BaseConfiguration configuration;
    protected final List<VoltageLevelInfo> voltageLevels;
    protected final List<LoadConfiguration> loadConfigurations;
    protected final PowerNodeFactory<T> nodeFactory;
    protected final ExportService exportService;
    protected final Random random = new Random();
    protected final ObjectMapper objectMapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);


    @Override
    public void start() {

        List<T> nodes = matrix.toNodeList();

        for (int i = 0; i < voltageLevels.size() - 1; i++) {
            VoltageLevelInfo currentVoltage = voltageLevels.get(i);

            System.out.println(currentVoltage);

            if (i != 0) {
                nodes = matrix.getAll(
                    node -> node.getStatuses().stream()
                        .anyMatch(status -> status.getType().getBlockType().equals(BlockType.SHOULD)
                            && status.getVoltageLevels().contains(currentVoltage.getLevel())
                            && status.getType().getNodeType().equals(PowerNodeType.SUBSTATION))
                );
            }

            if (nodes.isEmpty()) break;

            do {

                T powerNode = RandomUtils.randomValue(nodes);

                boolean three = false;

                if (currentVoltage.getLevel().isThreeWindings()) {
                    three = random.nextInt(2) == 0;
                }

                T resultNode;

                if (three) {
                    resultNode = nodeFactory.createNode(PowerNodeType.SUBSTATION, powerNode.getX(), powerNode.getY(),
                        currentVoltage.getTransformerPowerSet().get(0), // TODO НАСТРОИТЬ ВЫБОР МОЩНОСТИ
                        List.of(currentVoltage.getLevel(), voltageLevels.get(i + 1).getLevel(), voltageLevels.get(i + 2).getLevel()));
                    fillTransformerToGrid(resultNode, currentVoltage, voltageLevels.get(i + 1), voltageLevels.get(i + 2));

                } else {
                    int gap = random.nextInt(currentVoltage.getLevel().getGap()) + 1;
                    gap = Math.min(gap, voltageLevels.size());

                    resultNode = nodeFactory.createNode(PowerNodeType.SUBSTATION, powerNode.getX(), powerNode.getY(),
                        currentVoltage.getTransformerPowerSet().get(0), // TODO НАСТРОИТЬ ВЫБОР МОЩНОСТИ
                        List.of(currentVoltage.getLevel(), voltageLevels.get(i + gap).getLevel()));
                    fillTransformerToGrid(resultNode, currentVoltage, voltageLevels.get(i + gap));
                }

                System.out.println(resultNode);


            } while (!(nodes = matrix.getAll(
                node -> node.getStatuses().stream()
                    .anyMatch(status -> status.getType().getBlockType().equals(BlockType.SHOULD)
                        && status.getVoltageLevels().contains(currentVoltage.getLevel())
                        && status.getType().getNodeType().equals(PowerNodeType.SUBSTATION))
            )).isEmpty());

        }

        System.out.println("After transformers");
        System.out.println("Number of nodes = " + matrix.toNodeList().stream().filter(node -> !node.getNodeType().equals(PowerNodeType.EMPTY)).count());
        System.out.println("Number of lines = " + elementsService.getLines().size());





        // todo могут оставаться ни с чем не соединённые обмотки ТР-ов 35кВ, можно к ним прикреплять какую-нибудь нагрузку, типа предприятия

        for (LoadConfiguration loadCfg : loadConfigurations) {
            VoltageLevel currentLevel = loadCfg.getLevel();

            //TODO  Нужно получить все трансформаторы, имеющие обмотки с currentLevel и отсортировать их по
            // количеству присоединений в возрастающем порядке
            List<BasePowerNode> transformers = matrix.getAll(
                    node -> PowerNodeType.SUBSTATION.equals(node.getNodeType())
                        && node.getConnections().containsKey(currentLevel)
                ).stream()
                .sorted(Comparator.comparingInt(node -> node.getConnections().get(currentLevel).getConnections()))
                .collect(Collectors.toList());

            for (BasePowerNode transformer : transformers) {
                // Здесь area квадратная !!! Потому что мы делаем не через SHOULD статусы
                List<BasePowerNode> area = matrix.getArea(transformer.getX(), transformer.getY(), loadCfg.getBoundingArea()).stream()
                    .filter(node -> PowerNodeType.EMPTY.equals(node.getNodeType()))
                    .filter(node -> node.getStatuses().stream()
                        .noneMatch(status -> StatusType.BLOCK_LOAD.equals(status.getType())
                            && status.getVoltageLevels().contains(loadCfg.getLevel())))
                    .collect(Collectors.toList());

                if (area.isEmpty()) break;

                int filledPower = 0;
                do {
                    // Нода для размещения нагрузки
                    BasePowerNode resultNode = RandomUtils.randomValue(area);

                    // Расчёт мощности нагрузки
                    int randomPower = random.nextInt(loadCfg.getMaxLoad() - loadCfg.getMinLoad()) + loadCfg.getMinLoad();
                    int resPower;
                    if ((transformer.getPower() - filledPower) > randomPower) {
                        resPower = randomPower;
                    } else if ((transformer.getPower() - filledPower) > loadCfg.getMinLoad()) {
                        resPower = random.nextInt(transformer.getPower() - filledPower - loadCfg.getMinLoad()) + loadCfg.getMinLoad();
                    } else {
                        break;
                    }

                    resultNode = nodeFactory.createNode(PowerNodeType.LOAD, resultNode.getX(), resultNode.getY(), resPower, List.of(currentLevel));
                    fillLoadToGrid(resultNode, transformer, loadCfg);


                    BasePowerNode finalResultNode = resultNode;
                    area.removeIf(node -> node.getX() == finalResultNode.getX() && node.getY() == finalResultNode.getY());

                    area = area.stream().filter(node -> node.getStatuses().stream()
                            .noneMatch(status -> StatusType.BLOCK_LOAD.equals(status.getType())
                                && status.getVoltageLevels().contains(loadCfg.getLevel())))
                        .collect(Collectors.toList());
                } while (!area.isEmpty());
            }
        }

        System.out.println("After loads");
        System.out.println("Number of nodes = " + matrix.toNodeList().stream().filter(node -> !node.getNodeType().equals(PowerNodeType.EMPTY)).count());
        System.out.println("Number of lines = " + elementsService.getLines().size());

        exportService.saveAsFile();

        System.out.println("Finish");
    }


    private void fillTransformerToGrid(T node, VoltageLevelInfo... levels) {
        elementsService.addPowerNodeToGrid(node);
        // Заполняем area статусом, согласно только что добавленной ноде
        statusService.setTransformerStatusToArea(node, levels);
        // Соединяем сгенерированную ноду с соседями
        connectionService.connectNode(node, matrix);
    }

    private void fillLoadToGrid(BasePowerNode load, BasePowerNode transformer, LoadConfiguration loadCfg) {
        elementsService.addPowerNodeToGrid(load);
        // Заполняем area статусом, согласно только что добавленной ноде
        statusService.setLoadStatusToArea(load, loadCfg);
        // Соединяем сгенерированную ноду с соседями
        connectionService.connectNodes(load, transformer, loadCfg.getLevel());
    }

}
