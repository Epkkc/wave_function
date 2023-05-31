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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class AbstractAlgorithm<PNODE extends AbstractPowerNode<? extends BaseStatus, ? extends BaseConnection>, LINE extends AbstractLine<PNODE>, CFG extends BaseConfiguration> implements Algorithm {

    protected final Matrix<PNODE> matrix;
    protected final ElementService<PNODE, LINE> elementService;
    protected final StatusService<PNODE> statusService;
    protected final ConnectionService<PNODE> connectionService;
    protected final CFG configurationService;
    protected final List<TransformerConfiguration> transformerConfigurations;
    protected final List<LoadConfiguration> loadConfigurations;
    protected final List<GeneratorConfiguration> generatorConfigurations;
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

        // Расстановка нагрузок и генераторов
        fillLoadsRefactoring();

        String generatedFileName = exportService.saveAsFile();

        printSchemeMetaInformation("Before validation and optimizing");

        System.out.println("Finish");

        validateScheme();

        finalizeScheme();

        validateScheme();

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

    private void printSchemeMetaInformation(String title) {
        System.out.println(title);
        System.out.println("Number of nodes = " + matrix.toNodeList().stream().filter(node -> !node.getNodeType().equals(PowerNodeType.EMPTY)).count());
        System.out.println("Number of lines = " + elementService.getLines().size());
        System.out.println("Total load = " + elementService.getSumLoad());
        System.out.println("Total generation = " + elementService.getSumPower());
    }

    private void validateScheme() {

        List<PNODE> unconnectedNodes = getUnconnectedNodes();
        System.out.println(String.format("Ни с чем не соединённые ноды %s шт. : %s%n", unconnectedNodes.size(), unconnectedNodes));

        List<PNODE> emtyStatusNodes = matrix.toNodeList()
            .stream()
            .filter(node -> node.getStatuses().stream().anyMatch(status -> status.getVoltageLevels().isEmpty()))
            .toList();
        System.out.println("Ноды с статусами, которые не имеют ни одного уровня напряжения: " + emtyStatusNodes);
    }

    private void finalizeScheme() {
        System.out.println("Finalizing scheme");
        // Соединяем несоединённые обмотки трансформаторов с новыми нагрузками
        List<PNODE> unconnectedNodes = getUnconnectedNodes();
        for (PNODE unconnectedNode : unconnectedNodes) {
            Optional<PNODE> excessLoad = getExcessLoad();
            if (excessLoad.isPresent()) {
                // todo удалить lessLoad вместе с линиями, которые к ней присоединены, также удалить статусы, которые она породила
                // Создать новую нагрузку и соединить её с unconnected трансформатором
            } else {
                // На схеме нет лишних нод, это означает, что мы не можем сделать схему валидной
                break;
            }
        }

        // Удаление лишних линий
        // Предполагается, что в ходе синтеза мы сгенерировали лишние линии, поскольку при синтезе мы создаём все возможные связи
        if (configurationService.getRequiredNumberOfEdges() < elementService.getTotalNumberOfEdges()) {
            int count = elementService.getTotalNumberOfEdges() - configurationService.getRequiredNumberOfEdges();
            for (int i = 0; i < count; i++) {
                Optional<LINE> excessLine = getExcessLine();
                if (excessLine.isPresent()) {
                    System.out.println("Removing line: " + excessLine);
                    elementService.removeLine(excessLine.get());
                } else {
                    System.out.println("There is no exceed lines");
                    // На схеме нет лишних линий, это означает, что мы не можем сделать схему валидной
                    break;
                }
            }
        }

        printSchemeMetaInformation("After finalizing");
    }

    protected Optional<LINE> getExcessLine() {
        // Линия является лишней, если она соединяет две ноды, которые имеют минимум два соединения в этом connection-е
        return elementService.getLines().stream()
            .filter(this::excessLineCondition)
            .findFirst();
    }

    protected boolean excessLineCondition(LINE line) {
        return line.isBreaker() || pointsHaveExtraConnections(line) && substationsCheck(line);
    }

    protected boolean pointsHaveExtraConnections(LINE line) {
        return connectionPointHasMoreThanOneConnection(line.getPoint1(), line.getVoltageLevel())
            && connectionPointHasMoreThanOneConnection(line.getPoint2(), line.getVoltageLevel());
    }

    protected boolean substationsCheck(LINE line) {
        PNODE point1 = line.getPoint1();
        PNODE point2 = line.getPoint2();
        // Линия соединяет две подстанции
        if (PowerNodeType.SUBSTATION.equals(point1.getNodeType()) && PowerNodeType.SUBSTATION.equals(point2.getNodeType())) {
            return substationsHaveEqualChainLinkOrder(line) || (
                    pointConnectedWithAnotherSubstationWithLessChainLinkOrder(point1, line) &&
                    pointConnectedWithAnotherSubstationWithLessChainLinkOrder(point2, line)
            );
        }
        return false;
    }

    private boolean substationsHaveEqualChainLinkOrder(LINE line) {
        return line.getPoint1().getChainLinkOrder() == line.getPoint2().getChainLinkOrder();
    }

    protected boolean pointConnectedWithAnotherSubstationWithLessChainLinkOrder(PNODE point, LINE line) {
        boolean connectedWithAnotherSubstation = false;

        PNODE connectedPoint = getSecondPoint(line, point);

        BaseConnection connection = point.getConnections().get(line.getVoltageLevel());

        Set<String> extraNodeUuids = connection.getConnectedUuids().stream()
            .filter(uuid -> !uuid.equals(connectedPoint.getUuid()))
            .collect(Collectors.toSet());

        for (String connectedNodeUuid : extraNodeUuids) {
            PNODE connectedNode = elementService.getNodeByUuid(connectedNodeUuid);
            if (PowerNodeType.SUBSTATION.equals(connectedNode.getNodeType()) && connectedNode.getChainLinkOrder() < point.getChainLinkOrder()) {
                connectedWithAnotherSubstation = true;
                break;
            }
        }
        return connectedWithAnotherSubstation;
    }

    protected PNODE getSecondPoint(LINE line, PNODE firstPoint) {
        if (line.getPoint1().getUuid().equals(firstPoint.getUuid())) {
            return line.getPoint2();
        } else {
            return line.getPoint1();
        }
    }

    protected boolean connectionPointHasMoreThanOneConnection(PNODE node, VoltageLevel voltageLevel) {
        return node.getConnections().get(voltageLevel).getConnections() > 1;
    }

    protected Optional<PNODE> getExcessLoad() {
        List<PNODE> nodes = matrix.toNodeList().stream()
            .filter(node -> PowerNodeType.LOAD.equals(node.getNodeType()))
            .sorted(Comparator.comparing(PNODE::getChainLinkOrder, Comparator.reverseOrder()))
            .toList();

        for (PNODE node : nodes) {
            if (node.getChainLinkOrder() > 1) {
                return Optional.of(node);
            } else {
                // Необходимо убедиться, что выбранная нода является не единственным присоединением трансформатора (среди нагрузок и генераторов)
                PNODE substation = getConnectedSubstation(node).orElseThrow(() -> new UnsupportedOperationException("Unable to find connected substation to load " + node));
                int connectionsCount = getUnsubstationConnectionsCount(substation, node.getVoltageLevels().get(0));
                if (connectionsCount > 1) {
                    // Нода является не единственным присоединением обмотки трансформатора
                    return Optional.of(node);
                }
            }
        }

        return Optional.empty();
    }

    protected Optional<PNODE> getConnectedSubstation(PNODE load) {
        for (BaseConnection value : load.getConnections().values()) {
            for (String connectedUuid : value.getConnectedUuids()) {
                PNODE node = elementService.getNodeByUuid(connectedUuid);
                if (node != null && PowerNodeType.SUBSTATION.equals(node.getNodeType())) {
                    return Optional.of(node);
                }
            }
        }
        return Optional.empty();
    }

    // Возвращает количество присоединённых к обмотке (voltageLevel) трансформатора нод, являющихся генераторами и нагрузками
    protected int getUnsubstationConnectionsCount(PNODE substation, VoltageLevel voltageLevel) {
        return (int) substation.getConnections().values().stream()
            .filter(connection -> connection.getVoltageLevel().equals(voltageLevel))
            .map(BaseConnection::getConnectedUuids)
            .flatMap(Set::stream)
            .distinct()
            .map(elementService::getNodeByUuid)
            .filter(node -> PowerNodeType.LOAD.equals(node.getNodeType()) || PowerNodeType.GENERATOR.equals(node.getNodeType()))
            .count();
    }

    protected List<PNODE> getUnconnectedNodes() {
        return matrix.toNodeList().stream().filter(
            node -> node.getConnections().values().stream().anyMatch(
                meta -> meta.getConnections() == 0)
        ).toList();
    }


    protected void fillTransformerToGrid(PNODE node, List<TransformerConfiguration> transformerConfigurations) {
        elementService.addPowerNodeToGrid(node);
        // Соединяем сгенерированную ноду с соседями
        connectionService.connectNode(node);
        // Заполняем area статусом, согласно только что добавленной ноде
        statusService.setTransformerStatusToArea(node, transformerConfigurations);
    }


    protected void fillLoadToGrid(PNODE load, LoadConfiguration loadCfg, StatusMetaDto shouldStatus) {
        elementService.addPowerNodeToGrid(load);

        PNODE parentNode = elementService.getNodeByUuid(shouldStatus.getNodeUuid());
        // Соединяем новую ноду с нодой, установившей SHOULD статус.
        connectionService.connectNodes(load, parentNode, loadCfg.getLevel(), false);

        Set<String> ignoreUuids = new HashSet<>();
        Set<String> processedUuids = new HashSet<>();
        ignoreConnectedSubstation(parentNode, loadCfg.getLevel(), ignoreUuids, processedUuids);

        // Соединяем сгенерированную ноду с соседями
        connectionService.connectNode(load, ignoreUuids);
        // Заполняем area статусом, согласно только что добавленной ноде
        statusService.setLoadStatusToArea(load, loadCfg);
    }

    protected void ignoreConnectedSubstation(PNODE node, VoltageLevel voltageLevel, Set<String> ignoreUuids, Set<String> processedUuids) {
        processedUuids.add(node.getUuid());
        if (node.getNodeType().equals(PowerNodeType.SUBSTATION)) {
            ignoreUuids.add(node.getUuid());
        } else {
            BaseConnection baseConnection = node.getConnections().get(voltageLevel);
            if (baseConnection != null) {
                for (String uuid : baseConnection.getConnectedUuids()) {
                    if (!processedUuids.contains(uuid)) {
                        ignoreConnectedSubstation(elementService.getNodeByUuid(uuid), voltageLevel, ignoreUuids, processedUuids);
                    }
                }
            }
        }
    }

    protected void fillGeneratorToGrid(PNODE generator, PNODE transformer, GeneratorConfiguration genCfg) {
        elementService.addPowerNodeToGrid(generator);
        // Соединяем сгенерированную ноду с соседями
        connectionService.connectNodes(generator, transformer, genCfg.getLevel());
        // Заполняем area статусом, согласно только что добавленной ноде
        statusService.setGeneratorStatusToArea(generator, genCfg);
    }

    protected void fillTransformers() {
        List<PNODE> nodes = matrix.toNodeList();
        boolean first = true;
        for (int i = 0; i < transformerConfigurations.size() - 1; i++) {
            TransformerConfiguration currentConfiguration = transformerConfigurations.get(i);
            boolean firstInCfg = true;
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
                    int x = configurationService.getRows() / 2;
                    int y = configurationService.getColumns() / 2;
                    powerNode = matrix.getNode(x, y).orElseThrow(() -> new UnsupportedOperationException(String.format("There is no node in matrix with x=%s, y=%s", x, y)));
                }

                if (powerNode == null) {
                    throw new UnsupportedOperationException("PowerNode is null");
                }

                List<VoltageLevel> voltageLevels = getSubstationVoltageLevels(currentConfiguration, firstInCfg, i);

                List<TransformerConfiguration> nodeTransformerConfigurations = voltageLevels.stream().map(voltageLevel ->
                    transformerConfigurations.stream()
                        .filter(cfg -> cfg.getLevel().equals(voltageLevel))
                        .findFirst()
                        .orElseThrow(() -> new UnsupportedOperationException("Unable to find transformer configuration with voltage level = " + voltageLevel))
                ).toList();

                PNODE resultNode = nodeFactory.createNode(
                    PowerNodeType.SUBSTATION,
                    powerNode.getX(),
                    powerNode.getY(),
                    currentConfiguration.getTransformerPowerSet().get(0), // todo настроить выбор мощности
                    first ? 1 : getShouldStatus(powerNode, currentConfiguration.getLevel(), PowerNodeType.SUBSTATION).getChainLinkOrder(),
                    voltageLevels);
                fillTransformerToGrid(resultNode, nodeTransformerConfigurations);

                System.out.println(resultNode);

                afterTransformerSet(currentConfiguration);

                count++;
                first = false;
                firstInCfg = false;
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

    protected List<VoltageLevel> getSubstationVoltageLevels(TransformerConfiguration configuration, boolean firstInCfg, int iter) {
        // Определяем будет ли трансформатор трёхобмоточным
        boolean three = configuration.getLevel().isThreeWindings() && random.nextBoolean();

        if (three) {
            return List.of(configuration.getLevel(), transformerConfigurations.get(iter + 1).getLevel(), transformerConfigurations.get(iter + 2).getLevel());
        } else {
            int gap = firstInCfg ? 1 : random.nextInt(configuration.getLevel().getGap()) + 1;
            gap = Math.min(gap, transformerConfigurations.size());
            return List.of(configuration.getLevel(), transformerConfigurations.get(iter + gap).getLevel());
        }
    }

    protected void fillLoadsRefactoring() {
        List<PNODE> nodes = matrix.getAll(
            node -> node.getStatuses().stream()
                .anyMatch(status -> status.getType().getBlockType().equals(BlockType.SHOULD)
                    && status.getType().getNodeType().equals(PowerNodeType.LOAD)));

        // Расставляем ноды до тех пор, пока есть SHOULD_LOAD статусы и не достигнут предел по количеству нод
        while (!nodes.isEmpty() && isThereLimitOfNodes()) {

            for (LoadConfiguration loadConfiguration : loadConfigurations) {
                // todo Будущая доработка: нужна проверка, что есть хотя бы одна enable конфигурация
                if (!loadConfiguration.isEnabled()) {
                    continue;
                }

                List<PNODE> nodesWithVoltageLevel = nodes.stream().filter(node -> node.getStatuses().stream()
                        .anyMatch(status ->
                            status.getType().getBlockType().equals(BlockType.SHOULD)
                                && status.getVoltageLevels().contains(loadConfiguration.getLevel())
                                && status.getType().getNodeType().equals(PowerNodeType.LOAD))
                    )
                    .toList();

                if (nodesWithVoltageLevel.isEmpty()) {
                    continue;
                }

                PNODE resultNode = RandomUtils.randomValue(nodesWithVoltageLevel);

                if (resultNode == null) {
                    throw new UnsupportedOperationException("PowerNode is null");
                }

                StatusMetaDto shouldStatus = getShouldStatus(resultNode, loadConfiguration.getLevel(), PowerNodeType.LOAD);

                int resPower = getLoadPower(loadConfiguration, shouldStatus);

                resultNode = nodeFactory.createNode(
                    PowerNodeType.LOAD, resultNode.getX(), resultNode.getY(),
                    resPower, shouldStatus.getChainLinkOrder(), List.of(loadConfiguration.getLevel())
                );

                checkGeneratorNeed();

                if (!isThereLimitOfNodes()) {
                    break;
                }

                fillLoadToGrid(resultNode, loadConfiguration, shouldStatus);
                afterLoadSet(loadConfiguration);
            }

            nodes = matrix.getAll(
                node -> node.getStatuses().stream()
                    .anyMatch(status -> status.getType().getBlockType().equals(BlockType.SHOULD)
                        && status.getType().getNodeType().equals(PowerNodeType.LOAD)));

        }

    }

    protected boolean isThereLimitOfNodes() {
        return elementService.getTotalNumberOfNodes() < configurationService.getRequiredNumberOfNodes();
    }

    protected int getLoadPower(LoadConfiguration loadConfiguration, StatusMetaDto shouldStatus) {
        // Расчёт мощности нагрузки
        int randomPower = random.nextInt(loadConfiguration.getMaxLoad() - loadConfiguration.getMinLoad()) + loadConfiguration.getMinLoad();
        int resPower = randomPower;

//        todo сделать определение мощности нагрузки
//        int resPower;
//        if ((transformer.getPower() - filledPower) > randomPower) {
//            resPower = randomPower;
//        } else if ((transformer.getPower() - filledPower) > loadCfg.getMinLoad()) {
//            resPower = random.nextInt(transformer.getPower() - filledPower - loadCfg.getMinLoad()) + loadCfg.getMinLoad();
//        } else {
//            break;
//        }

        return resPower;
    }

    protected void checkGeneratorNeed() {
        // Если суммарная нагрузка больше или равна суммарной генерации, то необходимо поставить генератор
        while (elementService.getSumPower() <= elementService.getSumLoad()) {

            ArrayList<GeneratorConfiguration> shuffledConfigurations = new ArrayList<>(generatorConfigurations);
            Collections.shuffle(shuffledConfigurations);

            PNODE resultNode = null;
            for (GeneratorConfiguration generatorConfiguration : shuffledConfigurations) {

                if (!generatorConfiguration.isEnabled()) {
                    continue;
                }

                List<PNODE> transformers = matrix.getAll(
                        node -> PowerNodeType.SUBSTATION.equals(node.getNodeType())
                            && node.getConnections().containsKey(generatorConfiguration.getLevel())
                    ).stream()
                    .sorted(Comparator.comparingInt(node -> node.getConnections().get(generatorConfiguration.getLevel()).getConnections())).toList();

                if (transformers.isEmpty()) {
                    continue;
                }

                for (PNODE transformer : transformers) {
                    List<PNODE> area = matrix.getArea(transformer.getX(), transformer.getY(), generatorConfiguration.getTransformerArea()).stream()
                        .filter(node -> PowerNodeType.EMPTY.equals(node.getNodeType()))
                        .filter(node -> node.getStatuses().stream()
                            .noneMatch(status -> StatusType.BLOCK_GENERATOR.equals(status.getType())
                                && status.getVoltageLevels().contains(generatorConfiguration.getLevel())))
                        .collect(Collectors.toList());

                    if (area.isEmpty()) continue;

                    // Нода для размещения нагрузки
                    resultNode = RandomUtils.randomValue(area);

                    if (resultNode == null) {
                        throw new UnsupportedOperationException("PowerNode is null");
                    }

                    createAndFillGenerator(generatorConfiguration, transformer, resultNode);
                    break;
                }

                if (resultNode != null) {
                    break;
                }
            }

            if (resultNode == null) {
                throw new UnsupportedOperationException("Unable to put another generator in grid");
            }
        }
    }

    protected void createAndFillGenerator(GeneratorConfiguration configuration, PNODE transformer, PNODE resultNode) {
        // Расчёт мощности генератора
        int generatorPower = getGeneratorPower(configuration);

        resultNode = nodeFactory.createNode(PowerNodeType.GENERATOR, resultNode.getX(), resultNode.getY(), generatorPower, 1, List.of(configuration.getLevel()));
        fillGeneratorToGrid(resultNode, transformer, configuration);
        afterGeneratorSet(configuration);
    }

    protected int getGeneratorPower(GeneratorConfiguration configuration) {
        int randomPower = random.nextInt(configuration.getMaxPower() - configuration.getMinPower()) + configuration.getMinPower();
        int resPower = randomPower;
        // todo сделать определение мощности генератора
//        if ((totalLoad - totalGeneration) > randomPower) {
//            resPower = randomPower;
//        } else if ((totalLoad - totalGeneration) > generatorConfiguration.getMinPower()) {
//            resPower = random.nextInt(totalLoad - totalGeneration - generatorConfiguration.getMinPower()) + generatorConfiguration.getMinPower();
//        } else {
//            break;
//        }
        return resPower;
    }

    protected void fillGenerators() {
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

                if (area.isEmpty()) continue;

                // Нода для размещения нагрузки
                PNODE resultNode = RandomUtils.randomValue(area);

                if (resultNode == null) {
                    throw new UnsupportedOperationException("PowerNode is null");
                }

                // Расчёт мощности генератора
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


    protected void afterGeneratorSet(GeneratorConfiguration configuration) {
    }


}
