package com.example.demo.java.fx.algorithm;

import com.example.demo.base.algorithm.Algorithm;
import com.example.demo.base.model.configuration.GenerationConfiguration;
import com.example.demo.base.model.configuration.LoadConfiguration;
import com.example.demo.base.model.configuration.VoltageLevelInfo;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.status.BlockType;
import com.example.demo.base.model.status.StatusType;
import com.example.demo.export.dto.PowerLineDto;
import com.example.demo.export.dto.PowerNodeDto;
import com.example.demo.export.dto.SaveDto;
import com.example.demo.java.fx.factories.FxAbstractPowerNodeFactory;
import com.example.demo.java.fx.model.power.FxPowerLine;
import com.example.demo.java.fx.model.power.FxPowerNode;
import com.example.demo.java.fx.service.FxConfiguration;
import com.example.demo.services.FxConnectionService;
import com.example.demo.services.FxElementService;
import com.example.demo.services.FxStatusService;
import com.example.demo.thread.StoppableThread;
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
public class FxAlgorithm implements Algorithm {

    private final Matrix<FxPowerNode> matrix;
    private final FxElementService elementsService;
    private final FxStatusService statusService;
    private final FxConnectionService connectionService;
    private final FxConfiguration configuration;
    private final List<VoltageLevelInfo> voltageLevels;
    private final List<LoadConfiguration> loadConfigurations;
    private final List<GenerationConfiguration> generationConfigurations;
    private final FxAbstractPowerNodeFactory nodeFabric;
    private final Random random = new Random();
    private final ObjectMapper objectMapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    @Override
    public void start() {

        List<FxPowerNode> nodes = matrix.toNodeList();

        for (int i = 0; i < voltageLevels.size() - 1; i++) {
            VoltageLevelInfo currentVoltage = voltageLevels.get(i);

            System.out.println(currentVoltage);

            if (i != 0) {
                nodes = matrix.getAll(
                    node -> node.getBasePane().getStatusPane().getStatuses().stream()
                        .anyMatch(status -> status.getType().getBlockType().equals(BlockType.SHOULD)
                            && status.getVoltageLevels().contains(currentVoltage.getLevel())
                            && status.getType().getNodeType().equals(PowerNodeType.SUBSTATION))
                );
            }

            if (nodes.isEmpty()) break;

            do {

                FxPowerNode powerNode = RandomUtils.randomValue(nodes);

                // TODO нужно чтобы была хотя бы одна ПС с нижним классом напряжения на 1 ступень ниже
                boolean three = false;
                if (currentVoltage.getLevel().isThreeWindings()) {
                    three = random.nextInt(2) == 0;
                }
                FxPowerNode resultNode = null;

//                three = false;

                // TODO настроить рандомный выбор мощности для трансформатора
                if (three) {
                    resultNode = nodeFabric.createNode(PowerNodeType.SUBSTATION, powerNode.getX(), powerNode.getY(), currentVoltage.getTransformerPowerSet().get(0),
                        currentVoltage.getLevel(), voltageLevels.get(i + 1).getLevel(), voltageLevels.get(i + 2).getLevel());
//                    resultNode = nodeFabric.createThreeWindingsSubstation(currentVoltage, voltageLevels.get(i + 1), voltageLevels.get(i + 2), powerNode);
                    fillTransformerToGrid(resultNode, currentVoltage, voltageLevels.get(i + 1), voltageLevels.get(i + 2));

                } else {
                    int gap = random.nextInt(currentVoltage.getLevel().getGap()) + 1;
                    gap = Math.min(gap, voltageLevels.size());

//                    resultNode = nodeFabric.createTwoWindingsSubstation(currentVoltage, voltageLevels.get(i + gap), powerNode);
                    resultNode = nodeFabric.createNode(PowerNodeType.SUBSTATION, powerNode.getX(), powerNode.getY(), currentVoltage.getTransformerPowerSet().get(0),
                        currentVoltage.getLevel(), voltageLevels.get(i + gap).getLevel());
//                    fillToGrid(resultNode, currentVoltage, voltageLevels.get(i + 2));
                    fillTransformerToGrid(resultNode, currentVoltage, voltageLevels.get(i + gap));
                }

                System.out.println(resultNode);


                do {
                    // Задержка для удобства просмотра
                    try {
//                        Thread.sleep(configuration.getDelay());
                        Thread.sleep(currentVoltage.getLevel().getTimeout());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    // Обработка остановки потока
                } while (((StoppableThread) Thread.currentThread()).isStopped());

            } while (!(nodes = matrix.getAll(
                node -> node.getBasePane().getStatusPane().getStatuses().stream()
                    .anyMatch(status -> status.getType().getBlockType().equals(BlockType.SHOULD)
                        && status.getVoltageLevels().contains(currentVoltage.getLevel())
                        && status.getType().getNodeType().equals(PowerNodeType.SUBSTATION))
            )).isEmpty());

        }

        System.out.println("After transformers");
        System.out.println("Number of nodes = " + matrix.toNodeList().stream().filter(node -> !node.getNodeType().equals(PowerNodeType.EMPTY)).count());
        System.out.println("Number of lines = " + elementsService.getLines().size());

        try {
            // Thread.sleep(configuration.getDelay());
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        do {
            // Задержка для удобства просмотра
            try {
//                        Thread.sleep(configuration.getDelay());
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // Обработка остановки потока
        } while (((StoppableThread) Thread.currentThread()).isStopped());

        // todo могут оставаться ни с чем не соединённые обмотки ТР-ов 35кВ, можно к ним прикреплять какую-нибудь нагрузку, типа предприятия

        for (int i = 0; i < loadConfigurations.size(); i++) {
            LoadConfiguration loadCfg = loadConfigurations.get(i);

            VoltageLevel currentLevel = loadCfg.getLevel();

            //TODO  Нужно получить все трансформаторы, имеющие обмотки с currentLevel и отсортировать их по
            // количеству присоединений в возрастающем порядке
            List<FxPowerNode> transformers = matrix.getAll(
                    node -> PowerNodeType.SUBSTATION.equals(node.getNodeType())
                        && node.getConnectionPoints().containsKey(currentLevel)
                ).stream()
                .sorted(Comparator.comparingInt(node -> node.getConnectionPoints().get(currentLevel).getConnections()))
                .collect(Collectors.toList());

            for (FxPowerNode transformer : transformers) {
                // Здесь area квадратная !!! Потому что мы делаем не через SHOULD статусы
                List<FxPowerNode> area = matrix.getArea(transformer.getX(), transformer.getY(), loadCfg.getTransformerArea()).stream()
                    .filter(node -> PowerNodeType.EMPTY.equals(node.getNodeType()))
                    .filter(node -> node.getStatuses().stream()
                        .noneMatch(status -> StatusType.BLOCK_LOAD.equals(status.getType())
                            && status.getVoltageLevels().contains(loadCfg.getLevel())))
                    .collect(Collectors.toList());

                if (area.isEmpty()) break;

                int filledPower = 0;
                do {
                    // Нода для размещения нагрузки
                    FxPowerNode resultNode = RandomUtils.randomValue(area);

                    // Расчёт мощности нагрузки
                    int randomPower = random.nextInt(loadCfg.getMaxLoad() - loadCfg.getMinLoad()) + loadCfg.getMinLoad();
                    int resPower;
                    int diff = transformer.getPower() - filledPower;

                    if (diff > randomPower) {
                        resPower = randomPower;
                    } else if (diff > loadCfg.getMinLoad()) {
                        resPower = random.nextInt(transformer.getPower() - filledPower - loadCfg.getMinLoad()) + loadCfg.getMinLoad();
                    } else {
                        break;
                    }

                    resultNode = nodeFabric.createNode(PowerNodeType.LOAD, resultNode.getX(), resultNode.getY(), resPower, currentLevel);
                    fillLoadToGrid(resultNode, transformer, loadCfg);

                    do {
                        // Задержка для удобства просмотра
                        try {
//                        Thread.sleep(configuration.getDelay());
                            Thread.sleep(currentLevel.getTimeout());
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        // Обработка остановки потока
                    } while (((StoppableThread) Thread.currentThread()).isStopped());

                    FxPowerNode finalResultNode = resultNode;
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

        // Расстановка генераторов

        int totalLoad = elementsService.getSumLoad();
        int totalGeneration = 0;

        for (GenerationConfiguration generationConfiguration : generationConfigurations) {

            VoltageLevel currentLevel = generationConfiguration.getLevel();

            //TODO  Нужно получить все трансформаторы, имеющие обмотки с currentLevel и отсортировать их по
            // количеству присоединений в возрастающем порядке
            List<FxPowerNode> transformers = matrix.getAll(
                    node -> PowerNodeType.SUBSTATION.equals(node.getNodeType())
                        && node.getConnectionPoints().containsKey(currentLevel)
                ).stream()
                .sorted(Comparator.comparingInt(node -> node.getConnectionPoints().get(currentLevel).getConnections()))
                .collect(Collectors.toList());

            for (FxPowerNode transformer : transformers) {
                // Здесь area квадратная !!! Потому что мы делаем не через SHOULD статусы
                List<FxPowerNode> area = matrix.getArea(transformer.getX(), transformer.getY(), generationConfiguration.getTransformerArea()).stream()
                    .filter(node -> PowerNodeType.EMPTY.equals(node.getNodeType()))
                    .filter(node -> node.getStatuses().stream()
                        .noneMatch(status -> StatusType.BLOCK_GENERATOR.equals(status.getType())
                            && status.getVoltageLevels().contains(generationConfiguration.getLevel())))
                    .collect(Collectors.toList());

                if (area.isEmpty()) break;

//                do {
                // Нода для размещения нагрузки
                FxPowerNode resultNode = RandomUtils.randomValue(area);

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

                resultNode = nodeFabric.createNode(PowerNodeType.GENERATOR, resultNode.getX(), resultNode.getY(), resPower, currentLevel);
                fillGeneratorToGrid(resultNode, transformer, generationConfiguration);
                totalGeneration += resPower;

                do {
                    // Задержка для удобства просмотра
                    try {
//                        Thread.sleep(configuration.getDelay());
                        Thread.sleep(currentLevel.getTimeout());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    // Обработка остановки потока
                } while (((StoppableThread) Thread.currentThread()).isStopped());

//                FxPowerNode finalResultNode = resultNode;
//                area.removeIf(node -> node.getX() == finalResultNode.getX() && node.getY() == finalResultNode.getY());

//                    area = area.stream().filter(node -> node.getStatuses().stream()
//                            .noneMatch(status -> StatusType.BLOCK_LOAD.equals(status.getType())
//                                && status.getVoltageLevels().contains(generationConfiguration.getLevel())))
//                        .collect(Collectors.toList());
//                } while (!area.isEmpty());
            }
        }


        SaveDto dto = SaveDto.builder()
            .rows(configuration.getRows())
            .columns(configuration.getColumns())
            .matrix(matrix.toNodeList().stream().map(this::mapNodeToDto).collect(Collectors.toList()))
            .lines(elementsService.getLines().stream().map(this::mapLineToDto).collect(Collectors.toList()))
            .build();

        final String PREFIX = "scheme_";
        String date = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd_MM_yyyy__hh_mm_ss"));

        File file = new File("C:\\Users\\mnikitin\\IdeaProjects\\other\\demo\\src\\main\\resources\\schemes\\" + PREFIX + date);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(objectMapper.writeValueAsString(dto));
        } catch (Exception e) {
            System.out.println("Exception : " + e);
        }
        System.out.println("Number of nodes = " + matrix.toNodeList().stream().filter(node -> !node.getNodeType().equals(PowerNodeType.EMPTY)).count());
        System.out.println("Number of lines = " + elementsService.getLines().size());
        System.out.println("Total load = " + elementsService.getSumLoad());
        System.out.println("Total generation = " + elementsService.getSumPower());
        System.out.println("Finish");
    }

    private PowerLineDto mapLineToDto(FxPowerLine line) {
        return PowerLineDto.builder()
            .point1(mapNodeToDto(line.getPoint1()))
            .point2(mapNodeToDto(line.getPoint2()))
            .uuid(line.getUuid())
            .voltageLevel(line.getVoltageLevel())
            .build();
    }

    private PowerNodeDto mapNodeToDto(FxPowerNode node) {
        return PowerNodeDto.builder()
            .nodeType(node.getNodeType())
            .x(node.getX())
            .y(node.getY())
            .power(node.getX())
            .uuid(node.getUuid())
            .voltageLevels(node.getVoltageLevels())
            .build();
    }

    private void fillTransformerToGrid(FxPowerNode node, VoltageLevelInfo... levels) {
        elementsService.addPowerNodeToGrid(node);
        // Заполняем area статусом, согласно только что добавленной ноде
        statusService.setTransformerStatusToArea(node, levels);
        // Соединяем сгенерированную ноду с соседями
        connectionService.connectNode(node);
    }

    private void fillLoadToGrid(FxPowerNode load, FxPowerNode transformer, LoadConfiguration loadCfg) {
        elementsService.addPowerNodeToGrid(load);
        // Заполняем area статусом, согласно только что добавленной ноде
        statusService.setLoadStatusToArea(load, loadCfg);
        // Соединяем сгенерированную ноду с соседями
        connectionService.connectNodes(load, transformer, loadCfg.getLevel());
    }

    private void fillGeneratorToGrid(FxPowerNode generator, FxPowerNode transformer, GenerationConfiguration generationConfiguration) {
        elementsService.addPowerNodeToGrid(generator);
        // Заполняем area статусом, согласно только что добавленной ноде
        statusService.setGeneratorStatusToArea(generator, generationConfiguration);
        // Соединяем сгенерированную ноду с соседями
        connectionService.connectNodes(generator, transformer, generationConfiguration.getLevel());
    }

}
