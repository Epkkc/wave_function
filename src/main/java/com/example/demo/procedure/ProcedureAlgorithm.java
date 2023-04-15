package com.example.demo.procedure;

import com.example.demo.dto.PowerLineDto;
import com.example.demo.dto.PowerNodeDto;
import com.example.demo.dto.SaveDto;
import com.example.demo.model.Matrix;
import com.example.demo.model.PowerLine;
import com.example.demo.model.power.node.LoadConfiguration;
import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.power.node.PowerNodeType;
import com.example.demo.model.power.node.VoltageLevel;
import com.example.demo.model.power.node.VoltageLevelInfo;
import com.example.demo.model.status.BlockType;
import com.example.demo.model.status.StatusType;
import com.example.demo.services.Configuration;
import com.example.demo.services.ConnectionService;
import com.example.demo.services.ElementServiceImpl;
import com.example.demo.services.StatusService;
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
public class ProcedureAlgorithm {

    private final Matrix<PowerNode> matrix;
    private final ElementServiceImpl elementsService;
    private final StatusService statusService;
    private final ConnectionService connectionService;
    private final Configuration configuration;
    private final List<VoltageLevelInfo> voltageLevels;
    private final List<LoadConfiguration> loadConfigurations;
    private final AbstractNodeFabric nodeFabric;
    private final Random random = new Random();
    private final ObjectMapper objectMapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);


    public void start() {

        List<PowerNode> nodes = matrix.toNodeList();

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

                PowerNode powerNode = RandomUtils.randomValue(nodes);

                // TODO нужно чтобы была хотя бы одна ПС с нижним классом напряжения на 1 ступень ниже
                boolean three = false;
                if (currentVoltage.getLevel().isThreeWindings()) {
                    three = random.nextInt(2) == 0;
                }
                PowerNode resultNode = null;

//                three = false;

                // TODO настроить рандомный выбор мощности для трансформатора
                if (three) {
                    resultNode = nodeFabric.createNode(PowerNodeType.SUBSTATION, powerNode.getX(), powerNode.getY(), currentVoltage.getTransformerPowerSet().get(0), currentVoltage.getLevel(), voltageLevels.get(i + 1).getLevel(), voltageLevels.get(i + 2).getLevel());
//                    resultNode = nodeFabric.createThreeWindingsSubstation(currentVoltage, voltageLevels.get(i + 1), voltageLevels.get(i + 2), powerNode);
                    fillTransformerToGrid(resultNode, currentVoltage, voltageLevels.get(i + 1), voltageLevels.get(i + 2));

                } else {
                    int gap = random.nextInt(currentVoltage.getLevel().getGap()) + 1;
                    gap = Math.min(gap, voltageLevels.size());

//                    resultNode = nodeFabric.createTwoWindingsSubstation(currentVoltage, voltageLevels.get(i + gap), powerNode);
                    resultNode = nodeFabric.createNode(PowerNodeType.SUBSTATION, powerNode.getX(), powerNode.getY(), currentVoltage.getTransformerPowerSet().get(0), currentVoltage.getLevel(), voltageLevels.get(i + gap).getLevel());
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
            List<PowerNode> transformers = matrix.getAll(
                    node -> PowerNodeType.SUBSTATION.equals(node.getNodeType())
                        && node.getConnectionPoints().containsKey(currentLevel)
                ).stream()
                .sorted(Comparator.comparingInt(node -> node.getConnectionPoints().get(currentLevel).getConnections()))
                .collect(Collectors.toList());

            for (PowerNode transformer : transformers) {
                // Здесь area квадратная !!! Потому что мы делаем не через SHOULD статусы
                List<PowerNode> area = matrix.getArea(transformer.getX(), transformer.getY(), loadCfg.getBoundingAreaFrom()).stream()
                    .filter(node -> PowerNodeType.EMPTY.equals(node.getNodeType()))
                    .filter(node -> node.getStatuses().stream()
                        .noneMatch(status -> StatusType.BLOCK_LOAD.equals(status.getType())
                            && status.getVoltageLevels().contains(loadCfg.getLevel())))
                    .collect(Collectors.toList());

                if (area.isEmpty()) break;

                int filledPower = 0;
                do {
                    // Нода для размещения нагрузки
                    PowerNode resultNode = RandomUtils.randomValue(area);

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

                    PowerNode finalResultNode = resultNode;
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

        System.out.println("Finish");
    }

    private PowerLineDto mapLineToDto(PowerLine line) {
        return PowerLineDto.builder()
            .point1(mapNodeToDto(line.point1()))
            .point2(mapNodeToDto(line.point2()))
            .uuid(line.uuid())
            .voltageLevel(line.voltageLevel())
            .build();
    }

    private PowerNodeDto mapNodeToDto(PowerNode node) {
        return PowerNodeDto.builder()
            .nodeType(node.getNodeType())
            .x(node.getX())
            .y(node.getY())
            .power(node.getX())
            .uuid(node.getUuid())
            .voltageLevels(node.getVoltageLevels())
            .build();
    }

    private void fillTransformerToGrid(PowerNode node, VoltageLevelInfo... levels) {
        elementsService.addPowerNodeToGrid(node);
        // Заполняем area статусом, согласно только что добавленной ноде
        statusService.setTransformetStatusToArea(node, levels);
        // Соединяем сгенерированную ноду с соседями
        connectionService.connectNode(node);
    }

    private void fillLoadToGrid(PowerNode load, PowerNode transformer, LoadConfiguration loadCfg) {
        elementsService.addPowerNodeToGrid(load);
        // Заполняем area статусом, согласно только что добавленной ноде
        statusService.setLoadStatusToArea(load, loadCfg);
        // Соединяем сгенерированную ноду с соседями
        connectionService.connectNodes(load, transformer, loadCfg.getLevel());
    }

}
