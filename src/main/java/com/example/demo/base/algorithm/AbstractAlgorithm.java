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
import com.example.demo.base.model.power.LevelChainNumberDto;
import com.example.demo.base.model.power.NodeLineDto;
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

        // Расстановка нагрузок и генераторов
        fillLoadsAndGenerators();

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
        List<String> errorMessage = validateScheme();
        return new GeneralResult(elementService.getTotalNumberOfNodes(), elementService.getTotalNumberOfEdges(), generatedFileName, nodeTypeResults, errorMessage);
    }

    private void printSchemeMetaInformation(String title) {
        System.out.println(title);
        System.out.println("Number of nodes = " + matrix.toNodeList().stream().filter(node -> !node.getNodeType().equals(PowerNodeType.EMPTY)).count());
        System.out.println("Number of lines = " + elementService.getLines().size());
        System.out.println("Total load = " + elementService.getSumLoad());
        System.out.println("Total generation = " + elementService.getSumPower());
    }

    private List<String> validateScheme() {

        List<String> errorMessage = new ArrayList<>();

        List<PNODE> unconnectedNodes = getUnconnectedNodes();
        System.out.printf("Ни с чем не соединённые ноды %s шт. : %s%n", unconnectedNodes.size(), unconnectedNodes);
        if (!unconnectedNodes.isEmpty()) {
            errorMessage.add("Есть ни с чем не связанные ноды: " + unconnectedNodes.stream().map(PNODE::getUuid).toList());
        }


        List<PNODE> emptyStatusNodes = matrix.toNodeList()
            .stream()
            .filter(node -> node.getStatuses().stream().anyMatch(status -> status.getVoltageLevels().isEmpty()))
            .toList();
        System.out.println("Ноды с статусами, которые не имеют ни одного уровня напряжения: " + emptyStatusNodes);
        if (!emptyStatusNodes.isEmpty()) {
            errorMessage.add("Есть статусы без уровней напряжения");
        }

        boolean nodeRequirement = elementService.getTotalNumberOfNodes() == configurationService.getRequiredNumberOfNodes();
        if (!nodeRequirement) {
            errorMessage.add(String.format("Не выполнено условие по количеству нод: требуемое = %s , фактическое = %s", configurationService.getRequiredNumberOfNodes(),
                elementService.getTotalNumberOfNodes()));
        }

        boolean edgeRequirement = elementService.getTotalNumberOfEdges() == configurationService.getRequiredNumberOfEdges();
        if (!edgeRequirement) {
            errorMessage.add(String.format("Не выполнено условие по количеству нод: требуемое = %s , фактическое = %s", configurationService.getRequiredNumberOfEdges(),
                elementService.getTotalNumberOfEdges()));
        }

        // todo раскомментировать проверку
//        boolean loadLessThanPower = elementService.getSumLoad() <= elementService.getSumPower();
//        if (!loadLessThanPower) {
//            errorMessage.add(String.format("Суммарная нагрузка = %d больше, чем суммарная генерация = %d", elementService.getSumLoad(), elementService.getSumPower()));
//        }

        return errorMessage;
    }

    private void finalizeScheme() {
        System.out.println("Finalizing scheme");

        deleteExcessLoads();

        finalizeUnconnectedNodes();

        finalizeExcessLines();

        // Повтор требуется для того, чтобы покрыть сценарий, когда обмотка трансформатора соединена с другими нодами
        // исключительно через breaker-ы. Тогда в негативном сценарии все линии с breaker-ами будут удалены методом выше
        // и обмотка останется ни с чем не соединённой.
        finalizeUnconnectedNodes();

        finalizeExcessLines();

        printSchemeMetaInformation("After finalizing");
    }

    private void deleteExcessLoads() {
        int required = configurationService.getRequiredNumberOfNodes();
        int total = elementService.getTotalNumberOfNodes();
        if (total > required)
        for (int i = 0; i < total - required; i++) {
            Optional<PNODE> excessLoadOptional = getExcessLoad();
            if (excessLoadOptional.isPresent()) {
                PNODE excessLoad = excessLoadOptional.get();
                elementService.removeNode(excessLoad, getBaseNode(excessLoad.getX(), excessLoad.getY())); // Удаление excessLoad ноды и линий с ней связанных
                statusService.removeStatusesByNodeUuid(excessLoad.getUuid()); // Удаление статусов, порождённых excessLoad
                // todo может быть в теории проблема со статусами, но пока замечено не было
            } else {
                System.out.println("Unable to remove excess node");
            }
        }
    }

    private void finalizeExcessLines() {
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
    }


    protected void finalizeUnconnectedNodes() {
        // Соединяем несоединённые обмотки трансформаторов с новыми нагрузками
        List<PNODE> unconnectedNodes = getUnconnectedNodes();

        OUTER:
        for (PNODE unconnectedNode : unconnectedNodes) {
            System.out.println("Unconnected node : " + unconnectedNode);
            for (BaseConnection connection : unconnectedNode.getConnections().values()) {
                if (connection.getConnectedNodes() > 0) continue;
                System.out.println("Unconnected connection : " + connection);

                Optional<PNODE> excessLoadO = getExcessLoad();
                if (excessLoadO.isPresent()) {
                    PNODE excessLoad = excessLoadO.get();

                    System.out.println("Removing excess load : " + excessLoad);
                    elementService.removeNode(excessLoad, getBaseNode(excessLoad.getX(), excessLoad.getY())); // Удаление excessLoad ноды и линий с ней связанных
                    statusService.removeStatusesByNodeUuid(excessLoad.getUuid()); // Удаление статусов, порождённых excessLoad

                    // Обновляем статусы вокруг unconnectedNode
                    statusService.setTransformerStatusToArea(unconnectedNode,
                        transformerConfigurations.stream().filter(cfg -> unconnectedNode.getVoltageLevels().contains(cfg.getLevel())).collect(
                            Collectors.toList()));

                    Optional<PNODE> resultNode = matrix.toNodeList().stream()
                        .filter(node -> hasShouldStatus(node, connection.getVoltageLevel(), PowerNodeType.LOAD, unconnectedNode.getUuid()))
                        .findFirst();

                    resultNode.ifPresent(node -> {
                        LoadConfiguration loadConfiguration = loadConfigurations.stream()
                            .filter(cfg -> cfg.getLevel().equals(connection.getVoltageLevel()))
                            .findFirst()
                            .orElseThrow(() -> new UnsupportedOperationException("Unable to find load configuration with voltage level = " + connection.getVoltageLevel()));

                        StatusMetaDto shouldStatus = getShouldStatus(node, connection.getVoltageLevel(), PowerNodeType.LOAD);

                        PNODE load = nodeFactory.createNode(
                            PowerNodeType.LOAD, node.getX(), node.getY(),
                            getLoadPower(loadConfiguration, shouldStatus),
                            List.of(new LevelChainNumberDto(connection.getVoltageLevel(), 1)));

                        fillLoadToGrid(load, loadConfiguration, shouldStatus);
                    });
                } else {
                    // На схеме нет лишних нод, это означает, что мы не можем сделать схему валидной
                    System.out.println("There is no excess loads");
                    break OUTER;
                }

            }
        }
    }


    protected abstract PNODE getBaseNode(int x, int y);

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
        return getChainLinkOrder(line.getPoint1(), line.getVoltageLevel()) == getChainLinkOrder(line.getPoint2(), line.getVoltageLevel());
    }

    protected boolean pointConnectedWithAnotherSubstationWithLessChainLinkOrder(PNODE point, LINE line) {
        boolean connectedWithAnotherSubstation = false;

        PNODE connectedPoint = getSecondPoint(line, point);

        BaseConnection connection = point.getConnections().get(line.getVoltageLevel());

        Set<String> extraNodeUuids = connection.getNodeLineDtos().stream()
            .map(NodeLineDto::getNodeUuid)
            .filter(uuid -> !uuid.equals(connectedPoint.getUuid()))
            .collect(Collectors.toSet());

        for (String connectedNodeUuid : extraNodeUuids) {
            PNODE connectedNode = elementService.getNodeByUuid(connectedNodeUuid);
            if (PowerNodeType.SUBSTATION.equals(connectedNode.getNodeType()) &&
                getChainLinkOrder(connectedNode, line.getVoltageLevel()) < getChainLinkOrder(point, line.getVoltageLevel())
            ) {
                connectedWithAnotherSubstation = true;
                break;
            }
        }
        return connectedWithAnotherSubstation;
    }


    private int getChainLinkOrder(PNODE node, VoltageLevel voltageLevel) {
        return node.getConnections().get(voltageLevel).getChainLinkOrder();
    }

    protected PNODE getSecondPoint(LINE line, PNODE firstPoint) {
        if (line.getPoint1().getUuid().equals(firstPoint.getUuid())) {
            return line.getPoint2();
        } else {
            return line.getPoint1();
        }
    }

    protected boolean connectionPointHasMoreThanOneConnection(PNODE node, VoltageLevel voltageLevel) {
        return node.getConnections().get(voltageLevel).getConnectedNodes() > 1;
    }

    protected Optional<PNODE> getExcessLoad() {
        // Сортируем по chainLinkOrder DESC и по общему количеству присоединений ASC
        List<PNODE> loads = matrix.toNodeList().stream()
            .filter(node -> PowerNodeType.LOAD.equals(node.getNodeType()))
            .sorted(Comparator.<PNODE, Integer>comparing(node -> getChainLinkOrder(node, node.getVoltageLevels().get(0)), Comparator.reverseOrder())
                .thenComparingInt(node -> node.getConnections().values().size()))
            .toList();

        for (PNODE node : loads) {
            if (getChainLinkOrder(node, node.getVoltageLevels().get(0)) > 1) {
                return Optional.of(node);
            } else {
                // Необходимо убедиться, что выбранная нода является не единственным присоединением трансформатора
                // (среди нагрузок и генераторов)
                // (присоединения с breaker=true не учитываются)
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
            for (NodeLineDto dto : value.getNodeLineDtos()) {
                PNODE node = elementService.getNodeByUuid(dto.getNodeUuid());
                if (node != null && PowerNodeType.SUBSTATION.equals(node.getNodeType())) {
                    return Optional.of(node);
                }
            }
        }
        return Optional.empty();
    }

    // Возвращает количество присоединённых к обмотке (voltageLevel) трансформатора нод, являющихся генераторами и нагрузками
    // Ноды, соединённые через breaker=true не учитываются, т.к. в негативном сценарии соединяющие линии будут удалены.
    protected int getUnsubstationConnectionsCount(PNODE substation, VoltageLevel voltageLevel) {
        boolean b = substation.getConnections()
            .get(voltageLevel)
            .getNodeLineDtos()
            .stream()
            .anyMatch(dto -> elementService.getLine(dto.getLineUuid()).map(line -> line.isBreaker()).orElse(false)); // todo for delete

        int a =  (int) substation.getConnections().values().stream()
            .filter(connection -> connection.getVoltageLevel().equals(voltageLevel))
            .map(BaseConnection::getNodeLineDtos)
            .flatMap(List::stream)
            .filter(
                dto -> !elementService.getLine(dto.getLineUuid()).orElseThrow(() -> new UnsupportedOperationException("Unable to find line with uuid : " + dto.getLineUuid())).isBreaker())
            .map(NodeLineDto::getNodeUuid)
            .distinct()
            .map(elementService::getNodeByUuid)
            .filter(node -> PowerNodeType.LOAD.equals(node.getNodeType()) || PowerNodeType.GENERATOR.equals(node.getNodeType()))
            .count();
        return a;
    }

    protected List<PNODE> getUnconnectedNodes() {
        return matrix.toNodeList().stream().filter(
            node -> node.getConnections().values().stream().anyMatch(
                meta -> meta.getConnectedNodes() == 0)
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
                for (NodeLineDto dto : baseConnection.getNodeLineDtos()) {
                    if (!processedUuids.contains(dto.getNodeUuid())) {
                        ignoreConnectedSubstation(elementService.getNodeByUuid(dto.getNodeUuid()), voltageLevel, ignoreUuids, processedUuids);
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
                if (first && !randomFirst) {
                    int x = configurationService.getRows() / 2;
                    int y = configurationService.getColumns() / 2;
                    powerNode = matrix.getNode(x, y).orElseThrow(() -> new UnsupportedOperationException(String.format("There is no node in matrix with x=%s, y=%s", x, y)));
                } else {
                    powerNode = RandomUtils.randomValue(nodes);
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
                    getLevelChainNumberDto(powerNode, currentConfiguration, voltageLevels, first));
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

    private List<LevelChainNumberDto> getLevelChainNumberDto(PNODE node, TransformerConfiguration configuration, List<VoltageLevel> voltageLevels, boolean first) {
        List<LevelChainNumberDto> result = new ArrayList<>();
        for (VoltageLevel voltageLevel : voltageLevels) {
            int chainLinkOrder;
            if (configuration.getLevel().equals(voltageLevel)) {
                chainLinkOrder = first ? 1 : getShouldStatus(node, voltageLevel, PowerNodeType.SUBSTATION).getChainLinkOrder();
            } else {
                chainLinkOrder = 1;
            }
            result.add(new LevelChainNumberDto(voltageLevel, chainLinkOrder));
        }

        return result;
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

    protected void fillLoadsAndGenerators() {
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

                int rate = loadConfiguration.getGenerationRate() - random.nextInt(100);
                if (rate < 0) {
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
                    resPower,
                    List.of(new LevelChainNumberDto(loadConfiguration.getLevel(), shouldStatus.getChainLinkOrder()))
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
                    .sorted(Comparator.comparingInt(node -> node.getConnections().get(generatorConfiguration.getLevel()).getConnectedNodes())).toList();

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

        resultNode = nodeFactory.createNode(PowerNodeType.GENERATOR, resultNode.getX(), resultNode.getY(), generatorPower, List.of(new LevelChainNumberDto(configuration.getLevel(), 1)));
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

    protected StatusMetaDto getShouldStatus(PNODE powerNode, VoltageLevel voltageLevel, PowerNodeType powerNodeType, String uuid) {
        return powerNode.getStatuses()
            .stream()
            .filter(status ->
                status.getType().getBlockType().equals(BlockType.SHOULD)
                    && status.getType().getNodeType().equals(powerNodeType)
                    && status.getVoltageLevels().contains(voltageLevel))
            .filter(status -> {
                if (uuid != null && uuid.length() > 0) {
                    return status.getMeta(voltageLevel).getNodeUuid().equals(uuid);
                } else {
                    return true;
                }
            })
            .findFirst()
            .map(baseStatus -> baseStatus.getMeta(voltageLevel))
            .orElseThrow(() -> new UnsupportedOperationException(
                String.format("Chosen power node doesn`t contain status SHOULD_%s with voltageLevel=%s", powerNodeType, voltageLevel)));
    }

    protected boolean hasShouldStatus(PNODE powerNode, VoltageLevel voltageLevel, PowerNodeType powerNodeType, String uuid) {
        return powerNode.getStatuses()
            .stream()
            .filter(status ->
                status.getType().getBlockType().equals(BlockType.SHOULD)
                    && status.getType().getNodeType().equals(powerNodeType)
                    && status.getVoltageLevels().contains(voltageLevel))
            .anyMatch(status -> {
                if (uuid != null && uuid.length() > 0) {
                    return status.getMeta(voltageLevel).getNodeUuid().equals(uuid);
                } else {
                    return true;
                }
            });
    }

    protected StatusMetaDto getShouldStatus(PNODE powerNode, VoltageLevel voltageLevel, PowerNodeType powerNodeType) {
        return getShouldStatus(powerNode, voltageLevel, powerNodeType, null);
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
