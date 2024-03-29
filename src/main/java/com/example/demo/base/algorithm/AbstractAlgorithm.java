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
import com.example.demo.base.service.ConfigurationStaticSupplier;
import com.example.demo.base.service.TopologyService;
import com.example.demo.base.service.connection.ConnectionService;
import com.example.demo.base.service.element.ElementService;
import com.example.demo.base.service.status.StatusService;
import com.example.demo.export.cim.CimExportService;
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
    protected final TopologyService<PNODE, LINE> topologyService;
    protected final CFG configurationService;
    protected final PowerNodeFactory<PNODE> nodeFactory;
    protected final ExportService<PNODE, LINE> exportService;
    protected final CimExportService<PNODE, LINE> cimExportService;
    protected final Random random = new Random();
    protected final boolean randomFirst;
    private static final int NEGATIVE_LOAD_POWER = -1;


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

        printSchemeMetaInformation("Before validation and optimizing");

        validateScheme();

        finalizeScheme();

        validateScheme();

        String generatedFileName = null;
        if (ConfigurationStaticSupplier.baseExport) {
            System.out.println("Export json");
            generatedFileName = exportService.saveAsFile();
        }

        String cimFileName = null;
        if (ConfigurationStaticSupplier.cimExport) {
            System.out.println("Export cim");
            cimFileName = cimExportService.exportIntoCim();
        }
        System.out.println("Finish");

        // todo вынести в отдельный метод для формирования результата
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
        return new GeneralResult(elementService.getTotalNumberOfNodes(), elementService.getTotalNumberOfEdges(), generatedFileName, cimFileName, nodeTypeResults, errorMessage);
    }

    private void printSchemeMetaInformation(String title) {
        System.out.println(title);
        System.out.println("Number of nodes = " + matrix.toNodeList().stream().filter(node -> !node.getNodeType().equals(PowerNodeType.EMPTY)).count());
        System.out.println("Number of lines = " + elementService.getLines().size());
        System.out.println("Total load = " + elementService.getSumLoad());
        System.out.println("Total generation = " + elementService.getSumPower());
    }

    // todo вынести в отдельный класс Validator
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
            errorMessage.add(String.format("Не выполнено условие по количеству линий: требуемое = %s , фактическое = %s", configurationService.getRequiredNumberOfEdges(),
                elementService.getTotalNumberOfEdges()));
        }

        boolean loadLessThanPower = elementService.getSumLoad() <= elementService.getSumPower();
        if (!loadLessThanPower) {
            errorMessage.add(String.format("Суммарная нагрузка = %d больше, чем суммарная генерация = %d", elementService.getSumLoad(), elementService.getSumPower()));
        }

        // todo добавить проверку на то, что у всех нод неотрицательный power

        return errorMessage;
    }

    private void finalizeScheme() {
        System.out.println("Finalizing scheme");
        System.out.println("deleteExcessLoads");
        deleteExcessLoads();
        System.out.println("finalizeUnconnectedNodes");
        finalizeUnconnectedNodes();
        System.out.println("finalizeExcessLines");
        finalizeExcessLines();
        System.out.println("finalizeGenerators");
        finalizeGenerators();

        // Повтор требуется для того, чтобы покрыть сценарий, когда обмотка трансформатора соединена с другими нодами
        // исключительно через breaker-ы. Тогда в негативном сценарии все линии с breaker-ами будут удалены методом выше
        // и обмотка останется ни с чем не соединённой.
        System.out.println("finalizeUnconnectedNodes");
        finalizeUnconnectedNodes();
        System.out.println("finalizeExcessLines");
        finalizeExcessLines();
        System.out.println("finalizeGenerators");
        finalizeGenerators();
        System.out.println("deleteExcessLoads");
        deleteExcessLoads();

        printSchemeMetaInformation("After finalizing");
    }

    private void finalizeGenerators() {
        List<PNODE> generators = elementService.getAllGenerators();
        while (elementService.getSumPower() < elementService.getSumLoad() && !generators.isEmpty()) {
            PNODE generator = generators.get(generators.size() - 1);
            GeneratorConfiguration configuration = configurationService.getGeneratorConfiguration(generator.getVoltageLevels().get(0));
            int maxGeneratorPower = configuration.getMaxNumberOfBlocks() * configuration.getBlockPower();
            if (generator.getPower() < maxGeneratorPower) {
                generator.setPower(maxGeneratorPower);
            }
            generators.remove(generators.size() - 1);
        }

        if (elementService.getSumPower() < elementService.getSumLoad()) {
            while (elementService.getSumPower() < elementService.getSumLoad()) {
                deleteExcessLoad();
                checkGeneratorNeed();
            }
        }
    }

    private void deleteExcessLoads() {
        int required = configurationService.getRequiredNumberOfNodes();
        int total = elementService.getTotalNumberOfNodes();
        if (total > required)
            for (int i = 0; i < total - required; i++) {
                boolean deletionResult = deleteExcessLoad();
                if (!deletionResult) {
                    System.out.println("Unable to remove excess node");
                    break;
                }
            }
    }

    private boolean deleteExcessLoad() {
        Optional<PNODE> excessLoadOptional = getExcessLoad();
        if (excessLoadOptional.isPresent()) {
            PNODE excessLoad = excessLoadOptional.get();
            elementService.removeNode(excessLoad, getBaseNode(excessLoad.getX(), excessLoad.getY())); // Удаление excessLoad ноды и линий с ней связанных
            statusService.removeStatusesByNodeUuid(excessLoad.getUuid()); // Удаление статусов, порождённых excessLoad
            // todo вернуть присоединённой подстанции load.power в availablePower
            return true;
        } else {
            return false;
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
                        configurationService.getTransformerConfigurationList().stream()
                            .filter(cfg -> unconnectedNode.getVoltageLevels().contains(cfg.getLevel()))
                            .collect(Collectors.toList()));

                    Optional<PNODE> resultNode = matrix.toNodeList().stream()
                        .filter(node -> hasShouldStatus(node, connection.getVoltageLevel(), PowerNodeType.LOAD, unconnectedNode.getUuid()))
                        .findFirst();
                    System.out.println("ResultNode : " + resultNode);

                    resultNode.ifPresent(node -> {
                        LoadConfiguration loadConfiguration = configurationService.getLoadConfiguration(connection.getVoltageLevel());

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

    // todo вынести в TopologyService
    protected Optional<LINE> getExcessLine() {
        // Линия является лишней, если она соединяет две ноды, которые имеют минимум два соединения в этом connection-е
        return elementService.getLines().stream()
            .filter(this::excessLineCondition)
            .findFirst();
    }

    // todo вынести в TopologyService
    protected boolean excessLineCondition(LINE line) {
        return line.isBreaker() || pointsHaveExtraConnections(line) && substationsCheck(line);
    }

    // todo вынести в TopologyService
    protected boolean pointsHaveExtraConnections(LINE line) {
        return connectionPointHasMoreThanOneConnection(line.getPoint1(), line.getVoltageLevel())
            && connectionPointHasMoreThanOneConnection(line.getPoint2(), line.getVoltageLevel());
    }

    // todo вынести в TopologyService
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

    // todo вынести в TopologyService
    private boolean substationsHaveEqualChainLinkOrder(LINE line) {
        return getChainLinkOrder(line.getPoint1(), line.getVoltageLevel()) == getChainLinkOrder(line.getPoint2(), line.getVoltageLevel());
    }

    // todo вынести в TopologyService
    protected boolean pointConnectedWithAnotherSubstationWithLessChainLinkOrder(PNODE point, LINE line) {
        boolean connectedWithAnotherSubstation = false;

        PNODE connectedPoint = getSecondPoint(line, point);

        BaseConnection connection = point.getConnections().get(line.getVoltageLevel());

        Set<String> extraNodeUuids = connection.getNodeLineDtos().stream()
            .map(NodeLineDto::getNodeUuid)
            .filter(uuid -> !uuid.equals(connectedPoint.getUuid()))
            .collect(Collectors.toSet());

        for (String connectedNodeUuid : extraNodeUuids) {
            PNODE connectedNode = elementService.getNode(connectedNodeUuid);
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

    // todo вынести в TopologyService
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
                PNODE substation = topologyService.getConnectedSubstation(node).orElseThrow(() -> new UnsupportedOperationException("Unable to find connected substation to load " + node));
                int connectionsCount = getUnsubstationConnectionsCount(substation, node.getVoltageLevels().get(0));
                if (connectionsCount > 1) {
                    // Нода является не единственным присоединением обмотки трансформатора
                    return Optional.of(node);
                }
            }
        }

        return Optional.empty();
    }


    // Возвращает количество присоединённых к обмотке (voltageLevel) трансформатора нод, являющихся генераторами и нагрузками
    // Ноды, соединённые через breaker=true не учитываются, т.к. в негативном сценарии соединяющие линии будут удалены.
    protected int getUnsubstationConnectionsCount(PNODE substation, VoltageLevel voltageLevel) {
        return (int) substation.getConnections().values().stream()
            .filter(connection -> connection.getVoltageLevel().equals(voltageLevel))
            .map(BaseConnection::getNodeLineDtos)
            .flatMap(List::stream)
            .filter(dto -> !elementService.getLine(dto.getLineUuid()).isBreaker())
            .map(NodeLineDto::getNodeUuid)
            .distinct()
            .map(elementService::getNode)
            .filter(node -> PowerNodeType.LOAD.equals(node.getNodeType()) || PowerNodeType.GENERATOR.equals(node.getNodeType()))
            .count();
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

        System.out.println(node);
    }


    protected void fillLoadToGrid(PNODE load, LoadConfiguration loadCfg, StatusMetaDto shouldStatus) {
        elementService.addPowerNodeToGrid(load);
        topologyService.getSourceSubstation(shouldStatus.getNodeUuid()).decreaseAvailablePower(load.getPower());
        PNODE parentNode = elementService.getNode(shouldStatus.getNodeUuid());
        // Соединяем новую ноду с нодой, установившей SHOULD статус.
        connectionService.connectNodes(load, parentNode, loadCfg.getLevel(), false);

        // Здесь все ноды фидера, включая подстанции с которыми уже есть связь
        Set<String> ignoreUuids = new HashSet<>();
        ignoreConnectedSubstationAndLoadsOfSameFeeder(parentNode, loadCfg.getLevel(), ignoreUuids);

        // Соединяем сгенерированную ноду с соседями
        connectionService.connectNode(load, ignoreUuids);
        // Заполняем area статусом, согласно только что добавленной ноде
        statusService.setLoadStatusToArea(load, loadCfg);

        System.out.println(load);
    }

    protected void ignoreConnectedSubstationAndLoadsOfSameFeeder(PNODE node, VoltageLevel voltageLevel, Set<String> ignoreUuids) {
        ignoreUuids.add(node.getUuid());
        if (node.getNodeType().equals(PowerNodeType.SUBSTATION)) {
            ignoreUuids.add(node.getUuid());
        } else {
            BaseConnection baseConnection = node.getConnections().get(voltageLevel);
            if (baseConnection != null) {
                for (NodeLineDto dto : baseConnection.getNodeLineDtos()) {
                    if (!ignoreUuids.contains(dto.getNodeUuid())) {
                        ignoreConnectedSubstationAndLoadsOfSameFeeder(elementService.getNode(dto.getNodeUuid()), voltageLevel, ignoreUuids);
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

        System.out.println(generator);
    }

    protected void fillTransformers() {
        List<PNODE> nodes = matrix.toNodeList();
        boolean first = true;
        List<TransformerConfiguration> transformerConfigurations = configurationService.getTransformerConfigurationList();
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

                List<TransformerConfiguration> nodeTransformerConfigurations = voltageLevels.stream()
                    .map(voltageLevel -> configurationService.getTransformerConfigurationList().stream()
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
        boolean three = configuration.isThreeWindingEnabled() && random.nextBoolean();
        List<TransformerConfiguration> transformerConfigurations = configurationService.getTransformerConfigurationList();
        if (three) {
            return List.of(configuration.getLevel(), transformerConfigurations.get(iter + 1).getLevel(), transformerConfigurations.get(iter + 2).getLevel());
        } else {
            int gap = firstInCfg ? 1 : random.nextInt(configuration.getGap()) + 1;
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
        boolean thereIsShouldNodes = true;
        while (!nodes.isEmpty() && isThereLimitOfNodes() && thereIsShouldNodes) {
            thereIsShouldNodes = false;

            ArrayList<LoadConfiguration> shuffledConfigurations = new ArrayList<>(configurationService.getLoadConfigurationList());
            Collections.shuffle(shuffledConfigurations);

            for (LoadConfiguration loadConfiguration : shuffledConfigurations) {
                if (!loadConfiguration.isEnabled()) {
                    continue;
                }

                List<PNODE> shouldNodes = nodes.stream().filter(node -> node.getStatuses().stream()
                        .anyMatch(status ->
                            status.getType().getBlockType().equals(BlockType.SHOULD)
                                && status.getVoltageLevels().contains(loadConfiguration.getLevel())
                                && status.getType().getNodeType().equals(PowerNodeType.LOAD))
                    )
                    .toList();

                if (shouldNodes.isEmpty()) {
                    continue;
                }

                thereIsShouldNodes = true;

                int rate = loadConfiguration.getGenerationRate() - random.nextInt(99);
                if (rate < 0) {
                    continue;
                }

                PNODE resultNode = RandomUtils.randomValue(shouldNodes);

                if (resultNode == null) {
                    throw new UnsupportedOperationException("PowerNode is null");
                }

                StatusMetaDto shouldStatus = getShouldStatus(resultNode, loadConfiguration.getLevel(), PowerNodeType.LOAD);

                int resPower = getLoadPower(loadConfiguration, shouldStatus);

                if (resPower == NEGATIVE_LOAD_POWER) {
                    continue;
                }

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

        // TODO При расстановке нагрузки уменьшать availablePower трансформатора
        PNODE parentNode = elementService.getNode(shouldStatus.getNodeUuid());
        PNODE sourceSubstation;
        if (PowerNodeType.SUBSTATION.equals(parentNode.getNodeType())) {
            sourceSubstation = parentNode;
        } else {
            sourceSubstation = topologyService.getSourceConnectedSubstation(parentNode)
                .orElseThrow(() -> new UnsupportedOperationException("Unable to find source substation of load :" + parentNode));
        }

        int availablePower = sourceSubstation.getAvailablePower();

        if (availablePower < loadConfiguration.getMinLoad()) {
            return NEGATIVE_LOAD_POWER;
        } else {
            int randomPower = random.nextInt(loadConfiguration.getMaxLoad() - loadConfiguration.getMinLoad()) + loadConfiguration.getMinLoad();
            return Math.min(randomPower, availablePower);
        }
    }

    protected void checkGeneratorNeed() {
        // Если суммарная нагрузка больше или равна суммарной генерации, то необходимо поставить генератор
        boolean thereIsShouldNodes = true;
        while (elementService.getSumPower() <= elementService.getSumLoad() && thereIsShouldNodes) {
            thereIsShouldNodes = false;

            ArrayList<GeneratorConfiguration> shuffledConfigurations = new ArrayList<>(configurationService.getGeneratorConfigurationList());
            Collections.shuffle(shuffledConfigurations);

            for (GeneratorConfiguration generatorConfiguration : shuffledConfigurations) {
                if (!generatorConfiguration.isEnabled()) {
                    continue;
                }

                List<PNODE> shouldNodes = matrix.toNodeList().stream().filter(node -> node.getStatuses().stream()
                        .anyMatch(status ->
                            status.getType().getBlockType().equals(BlockType.SHOULD)
                                && status.getVoltageLevels().contains(generatorConfiguration.getLevel())
                                && status.getType().getNodeType().equals(PowerNodeType.GENERATOR))
                    )
                    .toList();

                if (shouldNodes.isEmpty()) {
                    continue;
                }

                thereIsShouldNodes = true;

                int rate = generatorConfiguration.getGenerationRate() - random.nextInt(99); //todo добавить поле generationRate
                if (rate < 0) {
                    continue;
                }

                PNODE resultNode = RandomUtils.randomValue(shouldNodes);

                if (resultNode == null) {
                    throw new UnsupportedOperationException("PowerNode is null");
                }

                StatusMetaDto shouldStatus = getShouldStatus(resultNode, generatorConfiguration.getLevel(), PowerNodeType.GENERATOR);
                PNODE parentSubstation = elementService.getNode(shouldStatus.getNodeUuid());

                createAndFillGenerator(generatorConfiguration, parentSubstation, resultNode);
                break;
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

    // todo вынести в новый PowerCalculationService
    protected int getGeneratorPower(GeneratorConfiguration configuration) {

        int numberOfBlocks = random.nextInt(configuration.getMaxNumberOfBlocks() + 1 - configuration.getMinNumberOfBlocks()) + configuration.getMinNumberOfBlocks();

        int luck = elementService.getSumLoad() - elementService.getSumPower();

        int resPower;
        if (luck < numberOfBlocks * configuration.getBlockPower()) {
            resPower = numberOfBlocks * configuration.getBlockPower();
        } else {
            resPower = configuration.getMaxNumberOfBlocks() * configuration.getBlockPower();
        }

        return resPower;
    }

    // todo вынести в StatusService
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
