package com.example.demo.base.algorithm;

import com.example.demo.base.factories.PowerNodeFactory;
import com.example.demo.base.model.configuration.GeneralResult;
import com.example.demo.base.model.configuration.GeneratorConfiguration;
import com.example.demo.base.model.configuration.LoadConfiguration;
import com.example.demo.base.model.configuration.NodeTypeResult;
import com.example.demo.base.model.configuration.TransformerConfiguration;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.AbstractLine;
import com.example.demo.base.model.power.AbstractPowerNode;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.status.BaseStatus;
import com.example.demo.base.model.status.BlockType;
import com.example.demo.base.model.status.StatusMetaDto;
import com.example.demo.base.model.status.StatusType;
import com.example.demo.base.service.BaseConfiguration;
import com.example.demo.base.service.connection.ConnectionService;
import com.example.demo.base.service.element.ElementService;
import com.example.demo.base.service.status.StatusService;
import com.example.demo.export.service.ExportService;
import com.example.demo.utils.RandomUtils;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class AbstractAlgorithm<PNODE extends AbstractPowerNode<? extends BaseStatus, ? extends BaseConnection>, LINE extends AbstractLine<PNODE>, CFG extends BaseConfiguration> implements Algorithm {

    protected final Matrix<PNODE> matrix;
    protected final ElementService<PNODE, LINE> elementService;
    protected final StatusService<PNODE> statusService;
    protected final ConnectionService<PNODE> connectionService;
    protected final CFG configuration;
    protected final List<TransformerConfiguration> transformerConfigurations;
    protected final List<LoadConfiguration> loadConfigurations;
    private final List<GeneratorConfiguration> generatorConfigurations;
    protected final PowerNodeFactory<PNODE> nodeFactory;
    protected final ExportService<PNODE, LINE> exportService;
    protected final Random random = new Random();
    protected final boolean randomFirst;


    @Override
    public GeneralResult start() {

        // Расстановка ПС
        fillTransformers();

        System.out.println("After transformers");
        System.out.println("Number of nodes = " + matrix.toNodeList().stream().filter(node -> !node.getNodeType().equals(PowerNodeType.EMPTY)).count());
        System.out.println("Number of lines = " + elementService.getLines().size());

        afterAllTransformersSet();

        // todo могут оставаться ни с чем не соединённые обмотки ТР-ов 35кВ, можно к ним прикреплять какую-нибудь нагрузку, типа предприятия

        // Расстановка нагрузок
        fillLoadsRefactoring();

        System.out.println("After loads");
        System.out.println("Number of nodes = " + matrix.toNodeList().stream().filter(node -> !node.getNodeType().equals(PowerNodeType.EMPTY)).count());
        System.out.println("Number of lines = " + elementService.getLines().size());

        afterAllLoadSet();

        // Расстановка генераторов
        fillGenerators();

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
            List<PNODE> collect = matrix.toNodeList()
                .stream()
                .filter(node -> type.equals(node.getNodeType()))
                .toList(); // переделать на мапу Map<VoltageLevel,Long>, где long - это количество элементов в данной группе
            for (VoltageLevel voltageLevel : VoltageLevel.values()) {
                long count = collect.stream()
                    .filter(node -> node.getVoltageLevels().stream().map(VoltageLevel::getVoltageLevel).reduce(Math::max).map(value -> voltageLevel.getVoltageLevel() == value).orElse(false))
                    .count();
                nodeTypeResults.add(new NodeTypeResult(type, voltageLevel, (int) count));
            }
        }

        return new GeneralResult(elementService.getTotalNumberOfNodes(), elementService.getTotalNumberOfEdges(), generatedFileName, nodeTypeResults);
    }

    private void fillTransformerToGrid(PNODE node, TransformerConfiguration... levels) {
        elementService.addPowerNodeToGrid(node);
        // Соединяем сгенерированную ноду с соседями
        connectionService.connectNode(node);
        // Заполняем area статусом, согласно только что добавленной ноде
        statusService.setTransformerStatusToArea(node, levels);
    }

    private void fillLoadToGrid(PNODE load, PNODE transformer, LoadConfiguration loadCfg) {
        elementService.addPowerNodeToGrid(load);
        // Соединяем сгенерированную ноду с соседями
        connectionService.connectNodes(load, transformer, loadCfg.getLevel(), false);
        // Заполняем area статусом, согласно только что добавленной ноде
        statusService.setLoadStatusToArea(load, loadCfg);
    }

    private void fillLoadToGrid(PNODE load, LoadConfiguration loadCfg) {
        elementService.addPowerNodeToGrid(load);
        // Соединяем сгенерированную ноду с соседями
        connectionService.connectNode(load);
        // Заполняем area статусом, согласно только что добавленной ноде
        statusService.setLoadStatusToArea(load, loadCfg);
    }

    private void fillGeneratorToGrid(PNODE generator, PNODE transformer, GeneratorConfiguration genCfg) {
        elementService.addPowerNodeToGrid(generator);
        // Соединяем сгенерированную ноду с соседями
        connectionService.connectNodes(generator, transformer, genCfg.getLevel(), false);
        // Заполняем area статусом, согласно только что добавленной ноде
        statusService.setGeneratorStatusToArea(generator, genCfg);
    }

    private void fillTransformers() {
        List<PNODE> nodes = matrix.toNodeList();
        boolean first = true;
        for (int i = 0; i < transformerConfigurations.size() - 1; i++) {
            TransformerConfiguration currentConfiguration = transformerConfigurations.get(i);

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
                PNODE powerNode;
                if (randomFirst) {
                    powerNode = RandomUtils.randomValue(nodes);
                } else {
                    int x = configuration.getRows() / 2;
                    int y = configuration.getColumns() / 2;
                    powerNode = matrix.getNode(x, y).orElseThrow(() -> new UnsupportedOperationException(String.format("There is no node in matrix with x=%s, y=%s", x, y)));
                }

//                BaseStatus shouldStatus = powerNode.getStatuses()
//                    .stream()
//                    .filter(status ->
//                        status.getType().getBlockType().equals(BlockType.SHOULD)
//                            && status.getVoltageLevels().contains(currentConfiguration.getLevel())
//                            && status.getType().getNodeType().equals(PowerNodeType.SUBSTATION))
//                    .findFirst()
//                    .orElseThrow(() -> new UnsupportedOperationException(
//                        String.format("Chosen power node doesn`t contain status SHOULD_SUBSTATION with voltageLevel=%s", currentConfiguration.getLevel())));

                boolean three = false;

                if (currentConfiguration.getLevel().isThreeWindings()) {
                    three = random.nextInt(2) == 0;
                }

                List<VoltageLevel> voltageLevels;
                TransformerConfiguration[] transformerConfigurationArray;

                // todo если это первая ПС в этой конфигурации, то в ней обязательно должен быть уровень напряжения на 1 ниже
                if (three) {
                    voltageLevels = List.of(currentConfiguration.getLevel(), transformerConfigurations.get(i + 1).getLevel(), transformerConfigurations.get(i + 2).getLevel());
                    transformerConfigurationArray = new TransformerConfiguration[]{currentConfiguration, transformerConfigurations.get(i + 1), transformerConfigurations.get(i + 2)};
                } else {
                    int gap = random.nextInt(currentConfiguration.getLevel().getGap()) + 1;
                    gap = Math.min(gap, transformerConfigurations.size());

                    voltageLevels = List.of(currentConfiguration.getLevel(), transformerConfigurations.get(i + gap).getLevel());
                    transformerConfigurationArray = new TransformerConfiguration[]{currentConfiguration, transformerConfigurations.get(i + gap)};
                }

                PNODE resultNode = nodeFactory.createNode(
                    PowerNodeType.SUBSTATION,
                    powerNode.getX(),
                    powerNode.getY(),
                    currentConfiguration.getTransformerPowerSet().get(0), // todo настроить выбор мощности
                    first ? 1 : getShouldStatus(powerNode, currentConfiguration.getLevel(), PowerNodeType.SUBSTATION).getChainLinkOrder(),
                    voltageLevels);
                fillTransformerToGrid(resultNode, transformerConfigurationArray);

                System.out.println(resultNode);

                afterTransformerSet(currentConfiguration);

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
    }

//    private void fillLoads() {
//        for (LoadConfiguration loadCfg : loadConfigurations) {
//            VoltageLevel currentLevel = loadCfg.getLevel();
//
//            //TODO  Нужно получить все трансформаторы, имеющие обмотки с currentLevel и отсортировать их по
//            // количеству присоединений в возрастающем порядке
//            List<PNODE> transformers = matrix.getAll(
//                    node -> PowerNodeType.SUBSTATION.equals(node.getNodeType())
//                        && node.getConnections().containsKey(currentLevel)
//                ).stream()
//                .sorted(Comparator.comparingInt(node -> node.getConnections().get(currentLevel).getConnections()))
//                .toList();
//
//            for (PNODE transformer : transformers) {
//                // Здесь area квадратная !!! Потому что мы делаем не через SHOULD статусы
//                List<PNODE> area = matrix.getArea(transformer.getX(), transformer.getY(), loadCfg.getBoundingAreaTo()).stream()
//                    .filter(node -> PowerNodeType.EMPTY.equals(node.getNodeType()))
//                    .filter(node -> node.getStatuses().stream()
//                        .noneMatch(status -> StatusType.BLOCK_LOAD.equals(status.getType())
//                            && status.getVoltageLevels().contains(loadCfg.getLevel())))
//                    .collect(Collectors.toList());
//
//                if (area.isEmpty()) break;
//
//                int filledPower = 0;
//                do {
//                    // Нода для размещения нагрузки
//                    PNODE resultNode = RandomUtils.randomValue(area);
//
//                    // Расчёт мощности нагрузки
//                    int randomPower = random.nextInt(loadCfg.getMaxLoad() - loadCfg.getMinLoad()) + loadCfg.getMinLoad();
//                    int resPower;
//                    if ((transformer.getPower() - filledPower) > randomPower) {
//                        resPower = randomPower;
//                    } else if ((transformer.getPower() - filledPower) > loadCfg.getMinLoad()) {
//                        resPower = random.nextInt(transformer.getPower() - filledPower - loadCfg.getMinLoad()) + loadCfg.getMinLoad();
//                    } else {
//                        break;
//                    }
//
//                    resultNode = nodeFactory.createNode(PowerNodeType.LOAD, resultNode.getX(), resultNode.getY(), resPower, List.of(currentLevel));
//                    fillLoadToGrid(resultNode, transformer, loadCfg);
//
//
//                    PNODE finalResultNode = resultNode;
//                    area.removeIf(node -> node.getX() == finalResultNode.getX() && node.getY() == finalResultNode.getY());
//
//                    area = area.stream().filter(node -> node.getStatuses().stream()
//                            .noneMatch(status -> StatusType.BLOCK_LOAD.equals(status.getType())
//                                && status.getVoltageLevels().contains(loadCfg.getLevel())))
//                        .collect(Collectors.toList());
//
//
//                    afterLoadSet(loadCfg);
//                } while (!area.isEmpty());
//            }
//        }
//    }

    private void fillLoadsRefactoring() {
        for (LoadConfiguration loadCfg : loadConfigurations) {
            VoltageLevel currentLevel = loadCfg.getLevel();

            List<PNODE> nodes = matrix.getAll(
                node -> node.getStatuses().stream()
                    .anyMatch(status -> status.getType().getBlockType().equals(BlockType.SHOULD)
                        && status.getVoltageLevels().contains(currentLevel)
                        && status.getType().getNodeType().equals(PowerNodeType.LOAD))
            );

            if (nodes.isEmpty()) break;

            int filledPower = 0;
            do {
                // Нода для размещения нагрузки
                PNODE resultNode = RandomUtils.randomValue(nodes);
                StatusMetaDto shouldStatus = getShouldStatus(resultNode, currentLevel, PowerNodeType.LOAD);
                // Расчёт мощности нагрузки
                int randomPower = random.nextInt(loadCfg.getMaxLoad() - loadCfg.getMinLoad()) + loadCfg.getMinLoad();
                int resPower = randomPower;

                // todo сделать определение мощности нагрузки
//                int resPower;
//                if ((transformer.getPower() - filledPower) > randomPower) {
//                    resPower = randomPower;
//                } else if ((transformer.getPower() - filledPower) > loadCfg.getMinLoad()) {
//                    resPower = random.nextInt(transformer.getPower() - filledPower - loadCfg.getMinLoad()) + loadCfg.getMinLoad();
//                } else {
//                    break;
//                }

                resultNode = nodeFactory.createNode(
                    PowerNodeType.LOAD, resultNode.getX(), resultNode.getY(),
                    resPower, shouldStatus.getChainLinkOrder(), List.of(currentLevel)
                );

                fillLoadToGrid(resultNode, loadCfg);

                nodes = matrix.getAll(
                    node -> node.getStatuses().stream()
                        .anyMatch(status -> status.getType().getBlockType().equals(BlockType.SHOULD)
                            && status.getVoltageLevels().contains(currentLevel)
                            && status.getType().getNodeType().equals(PowerNodeType.LOAD))
                );


                afterLoadSet(loadCfg);
            } while (!nodes.isEmpty());
        }
    }

    private void fillGenerators() {
        int totalLoad = (int) (elementService.getSumLoad() * 1.1);
        int totalGeneration = 0;

        for (GeneratorConfiguration generatorConfiguration : generatorConfigurations) {

            VoltageLevel currentLevel = generatorConfiguration.getLevel();

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
                List<PNODE> area = matrix.getArea(transformer.getX(), transformer.getY(), generatorConfiguration.getTransformerArea()).stream()
                    .filter(node -> PowerNodeType.EMPTY.equals(node.getNodeType()))
                    .filter(node -> node.getStatuses().stream()
                        .noneMatch(status -> StatusType.BLOCK_GENERATOR.equals(status.getType())
                            && status.getVoltageLevels().contains(generatorConfiguration.getLevel())))
                    .collect(Collectors.toList());

                if (area.isEmpty()) break;

                // Нода для размещения нагрузки
                PNODE resultNode = RandomUtils.randomValue(area);

                // Расчёт мощности нагрузки
                // TODO можно сделать как случайный выбор из набора мощностей
                int randomPower = random.nextInt(generatorConfiguration.getMaxPower() - generatorConfiguration.getMinPower()) + generatorConfiguration.getMinPower();
                int resPower;
                if ((totalLoad - totalGeneration) > randomPower) {
                    resPower = randomPower;
                } else if ((totalLoad - totalGeneration) > generatorConfiguration.getMinPower()) {
                    resPower = random.nextInt(totalLoad - totalGeneration - generatorConfiguration.getMinPower()) + generatorConfiguration.getMinPower();
                } else {
                    break;
                }

                resultNode = nodeFactory.createNode(PowerNodeType.GENERATOR, resultNode.getX(), resultNode.getY(), resPower, 1, List.of(currentLevel));
                fillGeneratorToGrid(resultNode, transformer, generatorConfiguration);
                totalGeneration += resPower;

                afterGeneratorSet(generatorConfiguration);
            }
        }
    }

    protected StatusMetaDto getShouldStatus(PNODE powerNode, VoltageLevel voltageLevel, PowerNodeType powerNodeType) {
        return powerNode.getStatuses()
            .stream()
            .filter(status ->
                status.getType().getBlockType().equals(BlockType.SHOULD)
                    && status.getType().getNodeType().equals(powerNodeType)
                    && status.getVoltageLevels().contains(voltageLevel))
            .findFirst()
            .map(baseStatus -> baseStatus.getVoltageLevelChainLinkHashMap().get(voltageLevel))
            .orElseThrow(() -> new UnsupportedOperationException(
            String.format("Chosen power node doesn`t contain status SHOULD_%s with voltageLevel=%s", powerNodeType, voltageLevel)));
    }

    protected void afterTransformerSet(TransformerConfiguration configuration) {
    }

    protected void afterAllTransformersSet() {
    }

    protected void afterLoadSet(LoadConfiguration configuration) {
    }

    protected void afterAllLoadSet() {
    }

    protected void afterGeneratorSet(GeneratorConfiguration configuration) {
    }

    protected void afterAllGeneratorSet() {
    }

}
