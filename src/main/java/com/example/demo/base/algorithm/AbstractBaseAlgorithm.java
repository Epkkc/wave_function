package com.example.demo.base.algorithm;

import com.example.demo.base.factories.PowerNodeFactory;
import com.example.demo.base.model.configuration.GenerationConfiguration;
import com.example.demo.base.model.configuration.GenerationResult;
import com.example.demo.base.model.configuration.LoadConfiguration;
import com.example.demo.base.model.configuration.NodeTypeResult;
import com.example.demo.base.model.configuration.TransformerConfiguration;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.AbstractPowerNode;
import com.example.demo.base.model.power.AbstractLine;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.status.BaseStatus;
import com.example.demo.base.model.status.BlockType;
import com.example.demo.base.model.status.StatusType;
import com.example.demo.base.service.BaseConfiguration;
import com.example.demo.base.service.status.StatusService;
import com.example.demo.base.service.connection.ConnectionService;
import com.example.demo.base.service.element.AbstractElementService;
import com.example.demo.export.service.ExportService;
import com.example.demo.utils.RandomUtils;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class AbstractBaseAlgorithm<PNODE extends AbstractPowerNode<? extends BaseStatus, ? extends BaseConnection>, LINE extends AbstractLine<PNODE>, CFG extends BaseConfiguration> implements Algorithm {

    protected final Matrix<PNODE> matrix;
    protected final AbstractElementService<PNODE, LINE> elementService;
    protected final StatusService<PNODE> statusService;
    protected final ConnectionService<PNODE> connectionService;
    protected final CFG configuration;
    protected final List<TransformerConfiguration> transformerConfigurations;
    protected final List<LoadConfiguration> loadConfigurations;
    private final List<GenerationConfiguration> generationConfigurations;
    protected final PowerNodeFactory<PNODE> nodeFactory;
    protected final ExportService<PNODE> exportService;
    protected final Random random = new Random();


    @Override
    public GenerationResult start() {

        List<PNODE> nodes = matrix.toNodeList();
        boolean first = true;
        for (int i = 0; i < transformerConfigurations.size() - 1; i++) {
            TransformerConfiguration currentConfiguration = transformerConfigurations.get(i);

            // todo перенести
            if (!currentConfiguration.isEnabled()) {
                continue;
            }

            System.out.println(currentConfiguration);

            if (!first) {
                nodes = matrix.getAll(
                    node -> node.getStatuses().stream()
                        .anyMatch(status -> status.getType().getBlockType().equals(BlockType.SHOULD)
                            && status.getVoltageLevels().contains(currentConfiguration.getLevel())
                            && status.getType().getNodeType().equals(PowerNodeType.SUBSTATION))
                );
            }

            if (nodes.isEmpty()) break;
            int count = 0;
            do {

                PNODE powerNode = RandomUtils.randomValue(nodes);

                boolean three = false;

                if (currentConfiguration.getLevel().isThreeWindings()) {
                    three = random.nextInt(2) == 0;
                }

                PNODE resultNode;

                if (three) {
                    resultNode = nodeFactory.createNode(PowerNodeType.SUBSTATION, powerNode.getX(), powerNode.getY(),
                        currentConfiguration.getTransformerPowerSet().get(0), // TODO НАСТРОИТЬ ВЫБОР МОЩНОСТИ
                        List.of(currentConfiguration.getLevel(), transformerConfigurations.get(i + 1).getLevel(), transformerConfigurations.get(i + 2).getLevel()));
                    fillTransformerToGrid(resultNode, currentConfiguration, transformerConfigurations.get(i + 1), transformerConfigurations.get(i + 2));

                } else {
                    int gap = random.nextInt(currentConfiguration.getLevel().getGap()) + 1;
                    gap = Math.min(gap, transformerConfigurations.size());

                    resultNode = nodeFactory.createNode(PowerNodeType.SUBSTATION, powerNode.getX(), powerNode.getY(),
                        currentConfiguration.getTransformerPowerSet().get(0), // TODO НАСТРОИТЬ ВЫБОР МОЩНОСТИ
                        List.of(currentConfiguration.getLevel(), transformerConfigurations.get(i + gap).getLevel()));
                    fillTransformerToGrid(resultNode, currentConfiguration, transformerConfigurations.get(i + gap));
                }

                System.out.println(resultNode);

                count++;
                first = false;
            } while (!(nodes = matrix.getAll(
                node -> node.getStatuses().stream()
                    .anyMatch(status -> status.getType().getBlockType().equals(BlockType.SHOULD)
                        && status.getVoltageLevels().contains(currentConfiguration.getLevel())
                        && status.getType().getNodeType().equals(PowerNodeType.SUBSTATION))
            )).isEmpty()
                && currentConfiguration.getNumberOfNodes() > count
            );

        }

        System.out.println("After transformers");
        System.out.println("Number of nodes = " + matrix.toNodeList().stream().filter(node -> !node.getNodeType().equals(PowerNodeType.EMPTY)).count());
        System.out.println("Number of lines = " + elementService.getLines().size());


        // todo могут оставаться ни с чем не соединённые обмотки ТР-ов 35кВ, можно к ним прикреплять какую-нибудь нагрузку, типа предприятия

        for (LoadConfiguration loadCfg : loadConfigurations) {
            VoltageLevel currentLevel = loadCfg.getLevel();

            //TODO  Нужно получить все трансформаторы, имеющие обмотки с currentLevel и отсортировать их по
            // количеству присоединений в возрастающем порядке
            List<PNODE> transformers = matrix.getAll(
                    node -> PowerNodeType.SUBSTATION.equals(node.getNodeType())
                        && node.getConnections().containsKey(currentLevel)
                ).stream()
                .sorted(Comparator.comparingInt(node -> node.getConnections().get(currentLevel).getConnections()))
                .toList();

            for (PNODE transformer : transformers) {
                // Здесь area квадратная !!! Потому что мы делаем не через SHOULD статусы
                List<PNODE> area = matrix.getArea(transformer.getX(), transformer.getY(), loadCfg.getTransformerArea()).stream()
                    .filter(node -> PowerNodeType.EMPTY.equals(node.getNodeType()))
                    .filter(node -> node.getStatuses().stream()
                        .noneMatch(status -> StatusType.BLOCK_LOAD.equals(status.getType())
                            && status.getVoltageLevels().contains(loadCfg.getLevel())))
                    .collect(Collectors.toList());

                if (area.isEmpty()) break;

                int filledPower = 0;
                do {
                    // Нода для размещения нагрузки
                    PNODE resultNode = RandomUtils.randomValue(area);

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


                    PNODE finalResultNode = resultNode;
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
        System.out.println("Number of lines = " + elementService.getLines().size());

        // Расстановка генераторов

        int totalLoad = (int) (elementService.getSumLoad() * 1.1);
        int totalGeneration = 0;

        for (GenerationConfiguration generationConfiguration : generationConfigurations) {

            VoltageLevel currentLevel = generationConfiguration.getLevel();

            //TODO  Нужно получить все трансформаторы, имеющие обмотки с currentLevel и отсортировать их по
            // количеству присоединений в возрастающем порядке
            List<PNODE> transformers = matrix.getAll(
                    node -> PowerNodeType.SUBSTATION.equals(node.getNodeType())
                        && node.getConnections().containsKey(currentLevel)
                ).stream()
                .sorted(Comparator.comparingInt(node -> node.getConnections().get(currentLevel).getConnections()))
                .collect(Collectors.toList());

            for (PNODE transformer : transformers) {
                // Здесь area квадратная !!! Потому что мы делаем не через SHOULD статусы
                List<PNODE> area = matrix.getArea(transformer.getX(), transformer.getY(), generationConfiguration.getTransformerArea()).stream()
                    .filter(node -> PowerNodeType.EMPTY.equals(node.getNodeType()))
                    .filter(node -> node.getStatuses().stream()
                        .noneMatch(status -> StatusType.BLOCK_GENERATOR.equals(status.getType())
                            && status.getVoltageLevels().contains(generationConfiguration.getLevel())))
                    .collect(Collectors.toList());

                if (area.isEmpty()) break;

//                do {
                // Нода для размещения нагрузки
                PNODE resultNode = RandomUtils.randomValue(area);

                // Расчёт мощности нагрузки
                // TODO можно сделать как случайный выбор из набора мощностей
                int randomPower = random.nextInt(generationConfiguration.getMaxPower() - generationConfiguration.getMinPower()) + generationConfiguration.getMinPower();
                int resPower;
                if ((totalLoad - totalGeneration) > randomPower) {
                    resPower = randomPower;
                } else if ((totalLoad - totalGeneration) > generationConfiguration.getMinPower()) {
                    resPower = random.nextInt(totalLoad - totalGeneration - generationConfiguration.getMinPower()) + generationConfiguration.getMinPower();
                } else {
                    break;
                }

                resultNode = nodeFactory.createNode(PowerNodeType.GENERATOR, resultNode.getX(), resultNode.getY(), resPower, List.of(currentLevel));
                fillGeneratorToGrid(resultNode, transformer, generationConfiguration);
                totalGeneration += resPower;

            }
        }

        String generatedFileName = exportService.saveAsFile();

        System.out.println("Number of nodes = " + matrix.toNodeList().stream().filter(node -> !node.getNodeType().equals(PowerNodeType.EMPTY)).count());
        System.out.println("Number of lines = " + elementService.getLines().size());
        System.out.println("Total load = " + elementService.getSumLoad());
        System.out.println("Total generation = " + elementService.getSumPower());
        System.out.println("Finish");


        List<NodeTypeResult> nodeTypeResults = new ArrayList<>();
        for (PowerNodeType type : PowerNodeType.values()) {
            if (PowerNodeType.EMPTY.equals(type)) {
                continue;
            }
            List<PNODE> collect = matrix.toNodeList().stream().filter(node -> type.equals(node.getNodeType())).toList(); // переделать на мапу Map<VoltageLevel,Long>, где long - это количество элементов в данной группе
            for (VoltageLevel voltageLevel : VoltageLevel.values()) {
                long count = collect.stream()
                    .filter(node -> node.getVoltageLevels().stream().map(VoltageLevel::getVoltageLevel).reduce(Math::max).map(value -> voltageLevel.getVoltageLevel() == value).orElse(false))
                    .count();
                nodeTypeResults.add(new NodeTypeResult(type, voltageLevel, (int) count));
            }
        }

        return new GenerationResult(elementService.getTotalNumberOfNodes(), elementService.getTotalNumberOfEdges(), generatedFileName, nodeTypeResults);
    }


    private void fillTransformerToGrid(PNODE node, TransformerConfiguration... levels) {
        elementService.addNode(1);
        elementService.addPowerNodeToGrid(node);
        // Заполняем area статусом, согласно только что добавленной ноде
        statusService.setTransformerStatusToArea(node, levels);
        // Соединяем сгенерированную ноду с соседями
        connectionService.connectNode(node, matrix);
    }

    private void fillLoadToGrid(PNODE load, PNODE transformer, LoadConfiguration loadCfg) {
        elementService.addNode(1);
        elementService.addPowerNodeToGrid(load);
        // Заполняем area статусом, согласно только что добавленной ноде
        statusService.setLoadStatusToArea(load, loadCfg);
        // Соединяем сгенерированную ноду с соседями
        connectionService.connectNodes(load, transformer, loadCfg.getLevel());
    }

    private void fillGeneratorToGrid(PNODE generator, PNODE transformer, GenerationConfiguration genCfg) {
        elementService.addNode(1);
        elementService.addPowerNodeToGrid(generator);
        // Заполняем area статусом, согласно только что добавленной ноде
        statusService.setLoadStatusToArea(generator, genCfg);
        // Соединяем сгенерированную ноду с соседями
        connectionService.connectNodes(generator, transformer, genCfg.getLevel());
    }

}
