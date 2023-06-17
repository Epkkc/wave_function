package com.example.demo.export.cim;

import com.example.demo.base.model.configuration.TransformerConfiguration;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.power.AbstractLine;
import com.example.demo.base.model.power.AbstractPowerNode;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.power.NodeLineDto;
import com.example.demo.base.model.status.BaseStatus;
import com.example.demo.base.service.BaseConfiguration;
import com.example.demo.base.service.element.ElementService;
import com.example.demo.export.cim.model.ACLine;
import com.example.demo.export.cim.model.BaseVoltage;
import com.example.demo.export.cim.model.BusBarSection;
import com.example.demo.export.cim.model.ConformLoad;
import com.example.demo.export.cim.model.ConnectivityNode;
import com.example.demo.export.cim.model.EquivalentInjection;
import com.example.demo.export.cim.model.OuterRdfClass;
import com.example.demo.export.cim.model.PowerTransformer;
import com.example.demo.export.cim.model.PowerTransformerEnd;
import com.example.demo.export.cim.model.Terminal;
import lombok.RequiredArgsConstructor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

@RequiredArgsConstructor
public class BaseCimExportService<PNODE extends AbstractPowerNode<? extends BaseStatus, ? extends BaseConnection>, LINE extends AbstractLine<PNODE>> implements CimExportService<PNODE, LINE> {

    private final double PROPORTIONALITY_FACTOR;
    private final double INITIAL_X_OFFSET;
    private final double INITIAL_y_OFFSET;

    protected final BaseConfiguration configuration;
    protected final ElementService<PNODE, LINE> elementService;
    protected final Map<String, PowerTransformer> powerTransformers = new HashMap<>();
    protected final Map<String, PowerTransformerEnd> powerTransformerEnds = new HashMap<>();
    protected final Map<String, ConformLoad> conformLoads = new HashMap<>();
    protected final Map<String, EquivalentInjection> equivalentInjections = new HashMap<>();
    protected final Map<String, ACLine> acLines = new HashMap<>();
    protected final Map<String, BusBarSection> busBarSections = new HashMap<>();
    protected final Map<String, Terminal> terminals = new HashMap<>();
    protected final Map<String, ConnectivityNode> connectivityNodes = new HashMap<>();
    protected final Map<String, BaseVoltage> baseVoltages = new HashMap<>();
    protected final OuterRdfClass outerRdf = new OuterRdfClass();
    protected Marshaller outerRdfMar;


    {
        JAXBContext outerRdfContext = null;

        try {
            outerRdfContext = JAXBContext.newInstance(OuterRdfClass.class);
        } catch (JAXBException e) {
            System.out.printf(e.toString());
            throw new RuntimeException(e);
        }
        try {
            outerRdfMar = outerRdfContext.createMarshaller();
        } catch (JAXBException e) {
            System.out.printf(e.toString());
            throw new RuntimeException(e);
        }
    }


    public String exportIntoCim() {
        List<PNODE> transformers = getAllNodesWithType(PowerNodeType.SUBSTATION);

        // Создание элементов для трансформаторов
        for (PNODE transformer : transformers) {
            VoltageLevel baseLevel = transformer.getVoltageLevels()
                .stream()
                .max(Comparator.comparingInt(VoltageLevel::getVoltageLevel))
                .orElseThrow(() -> new UnsupportedOperationException("Unable to find max voltageLevel for transformer : " + transformer));

            // Пример: TR_5dfc614b-69de-4a47-8e28-71742363bc0a
            String transformerId = getTransformerId(transformer.getUuid());
            PowerTransformer powerTransformer = createPowerTransformer(transformerId, baseLevel);

            List<? extends BaseConnection> connections = transformer.getConnections().values().stream().toList();
            for (int iter = 0; iter < connections.size(); iter++) {
                createPowerTransformerEndCimElements(connections, iter, transformer.getUuid(), powerTransformer);
                // Создаём и соединяем ПС линиями
                createPowerTransformerACLines(connections.get(iter), powerTransformer, transformer);
            }
        }

        // Сортируем нагрузки по возрастанию chanLinkOrder, для того, чтобы сначала заполнять нагрузки с chainLinkOrder=1
        List<PNODE> loads = getAllNodesWithType(PowerNodeType.LOAD).stream()
            // У нагрузок может быть только один connection
            .sorted(Comparator.comparingInt(node -> node.getConnections().get(node.getVoltageLevels().get(0)).getChainLinkOrder()))
            .toList();

        for (PNODE load : loads) {
            ConformLoad conformLoad = createLoadCimElements(load);
            createConformLoadACLines(load, conformLoad);
        }

        List<PNODE> generators = getAllNodesWithType(PowerNodeType.GENERATOR);

        for (PNODE generator : generators) {
            EquivalentInjection equivalentInjection = createGeneratorCimElements(generator);
            createGeneratorACLines(generator, equivalentInjection);
        }

        createBaseVoltages();

        outerRdf.setPowerTransformers(powerTransformers.values().stream().toList());
        outerRdf.setPowerTransformersEnds(powerTransformerEnds.values().stream().toList());
        outerRdf.setConformLoads(conformLoads.values().stream().toList());
        outerRdf.setEquivalentInjections(equivalentInjections.values().stream().toList());
        outerRdf.setBusBarSections(busBarSections.values().stream().toList());
        outerRdf.setTerminals(terminals.values().stream().toList());
        outerRdf.setAcLines(acLines.values().stream().toList());
        outerRdf.setConnectivityNodes(connectivityNodes.values().stream().toList());
        outerRdf.setBaseVoltages(baseVoltages.values().stream().toList());

        return saveAsFile();
    }

    private void createBaseVoltages() {
        configuration.getTransformerConfigurations().values().stream()
            .filter(TransformerConfiguration::isEnabled)
            .forEach(this::createBaseVoltage);
    }

    private void createGeneratorACLines(PNODE generator, EquivalentInjection equivalentInjection) {
        BaseConnection connection = generator.getConnections().get(generator.getVoltageLevels().get(0));
        for (NodeLineDto dto : connection.getNodeLineDtos()) {
            PNODE connectedNode = elementService.getNode(dto.getNodeUuid());
            LINE line = elementService.getLine(dto.getLineUuid());

            if (PowerNodeType.SUBSTATION.equals(connectedNode.getNodeType())) {
                connectNodeAndSubstation(equivalentInjection.getTerminal(), generator, connectedNode, line);
            }
        }
    }


    private EquivalentInjection createGeneratorCimElements(PNODE generator) {
        // У генераторов может быть только один connection
        // Пример: EI_2164868e-56e0-4a09-89b1-f40ac92f46bb
        String injectableValueId = getGeneratorId(generator.getUuid());

        VoltageLevel voltageLevel = generator.getVoltageLevels().get(0);
        // connectivityNode.id пример: CNode_EI_2164868e-56e0-4a09-89b1-f40ac92f46bb
        ConnectivityNode connectivityNode = createConnectivityNode(injectableValueId, (double) voltageLevel.getVoltageLevel());

        // terminal.id пример: T_EI_2164868e-56e0-4a09-89b1-f40ac92f46bb
        Terminal terminal = createTerminal(injectableValueId, connectivityNode);

        EquivalentInjection equivalentInjection = createEquivalentInjection(injectableValueId, voltageLevel, generator.getPower(), generator.getPower(), terminal);

        return equivalentInjection;
    }

    private EquivalentInjection createEquivalentInjection(String injectableValueId, VoltageLevel voltageLevel, double activePower, double reactivePower, Terminal terminal) {
        EquivalentInjection equivalentInjection = new EquivalentInjection(injectableValueId, injectableValueId, injectableValueId, Double.toString(voltageLevel.getVoltageLevel()), true, activePower,
            reactivePower, terminal);
        equivalentInjections.put(injectableValueId, equivalentInjection);
        return equivalentInjection;
    }

    private void createConformLoadACLines(PNODE load, ConformLoad conformLoad) {
        BaseConnection connection = load.getConnections().get(load.getVoltageLevels().get(0));
        for (NodeLineDto dto : connection.getNodeLineDtos()) {
            PNODE connectedNode = elementService.getNode(dto.getNodeUuid());
            LINE line = elementService.getLine(dto.getLineUuid());

            if (PowerNodeType.SUBSTATION.equals(connectedNode.getNodeType())) {
                connectNodeAndSubstation(conformLoad.getTerminal(), load, connectedNode, line);
            } else if (PowerNodeType.LOAD.equals(connectedNode.getNodeType())) {
                connectLoads(conformLoad, load, connectedNode, line);
            }

        }
    }

    private void connectLoads(ConformLoad conformLoad1, PNODE load1, PNODE load2, LINE line) {
        VoltageLevel voltageLevel = load1.getVoltageLevels().get(0);

        ConformLoad conformLoad2 = conformLoads.get(getConformLoadId(load2.getUuid()));
        // Может быть сценарий, когда две нагрузки с одинаковым chainLinkOrder соединены, в таком случае одна из них на этом этапе может быть null
        if (conformLoad2 == null) return;

        ConnectivityNode load1ConnectivityNode = conformLoad1.getTerminal().getConnectivityNode();
        ConnectivityNode load2ConnectivityNode = conformLoad2.getTerminal().getConnectivityNode();

        String acLineId = getACLineId(line.getUuid());

        Terminal load1Terminal = createTerminal(String.join("_", acLineId, load1ConnectivityNode.getRdfId()), load1ConnectivityNode);
        Terminal load2Terminal = createTerminal(String.join("_", acLineId, load2ConnectivityNode.getRdfId()), load2ConnectivityNode);

        ACLine acLine = createACLine(acLineId, voltageLevel, line.isBreaker(), calculateLength(load1, load2), List.of(load1Terminal, load2Terminal));
    }

    private void connectNodeAndSubstation(Terminal terminal, PNODE node, PNODE substation, LINE line) {
        // todo похоже, что в этом методе создаётся только один терминал, а не два
        //  не создаётся терминал для ПС, Не создаётся потому что они кладутся в мапу с одинаковым id
        VoltageLevel voltageLevel = node.getVoltageLevels().get(0);

        BaseConnection substationConnection = substation.getConnections().get(voltageLevel);

        PowerTransformer powerTransformer = powerTransformers.get(getTransformerId(substation.getUuid()));
        PowerTransformerEnd powerTransformerEnd = powerTransformer.getEnds().get(voltageLevel);

        ConnectivityNode powerTransformerConnectivityNode = powerTransformerEnd.getTerminal().getConnectivityNode();
        ConnectivityNode loadConnectivityNode = terminal.getConnectivityNode();

        String acLineId = getACLineId(line.getUuid());

        Terminal powerTransformerTerminal = createTerminal(String.join("_", acLineId, powerTransformerConnectivityNode.getRdfId()), powerTransformerConnectivityNode);
        Terminal loadTerminal = createTerminal(String.join("_", acLineId, loadConnectivityNode.getRdfId()), loadConnectivityNode);

        ACLine acLine = createACLine(acLineId, voltageLevel, line.isBreaker(), calculateLength(node, substation), List.of(powerTransformerTerminal, loadTerminal));
    }

    private ConformLoad createLoadCimElements(PNODE load) {
        // У нагрузок может быть только один connection
        // Пример: CLoad_c156e3e7-e30a-4e32-b022-cb23169a377e
        String conformLoadId = getConformLoadId(load.getUuid());
        VoltageLevel voltageLevel = load.getVoltageLevels().get(0);
        // connectivityNode.id пример: CNode_CLoad_c156e3e7-e30a-4e32-b022-cb23169a377e
        ConnectivityNode connectivityNode = createConnectivityNode(conformLoadId, (double) voltageLevel.getVoltageLevel());

        // terminal.id пример: T_CLoad_c156e3e7-e30a-4e32-b022-cb23169a377e
        Terminal terminal = createTerminal(conformLoadId, connectivityNode);

        ConformLoad conformLoad = createConformLoad(conformLoadId, voltageLevel, load.getPower(), load.getPower(), terminal);
        conformLoads.put(conformLoadId, conformLoad);
        return conformLoad;
    }


    private void createPowerTransformerEndCimElements(List<? extends BaseConnection> connections, int iter, String transformerUuid, PowerTransformer powerTransformer) {
        BaseConnection connection = connections.get(iter);

        // Пример: TRE_1_5dfc614b-69de-4a47-8e28-71742363bc0a
        String transformerEndId = getTransformerEndId(transformerUuid, iter);

        // connectivityNode.id пример: CNode_TRE_1_5dfc614b-69de-4a47-8e28-71742363bc0a
        ConnectivityNode connectivityNode = createConnectivityNode(transformerEndId, (double) connection.getVoltageLevel().getVoltageLevel());

        // terminal.id пример: T_TRE_1_5dfc614b-69de-4a47-8e28-71742363bc0a
        Terminal terminal = createTerminal(transformerEndId, connectivityNode);

        // Сущность BusBarSection не выступает в роли узла, к которому присоединяются другие терминалы.
        // Роль общего узла выполняет один ConnectivityNode, к которому присоединяются Terminal-ы других элементов.
        // BusBarSection подключается к этому же ConnectivityNode-у и выполняет роль хранилища информации о самой шине, например: производитель, марка и т.д.

        // busBarEndId.id пример: BS_TRE_1_5dfc614b-69de-4a47-8e28-71742363bc0a
        String busBarEndId = getBusBarEndId(transformerEndId);
        // terminal.id пример: T_BS_TRE_1_5dfc614b-69de-4a47-8e28-71742363bc0a
        Terminal busTerminal = createTerminal(busBarEndId, connectivityNode);

        BusBarSection busBarSection = createBusBarSection(busBarEndId, connection.getVoltageLevel(), busTerminal);

        PowerTransformerEnd powerTransformerEnd = createPowerTransformerEnd(transformerEndId, connection, terminal, powerTransformer, iter);
        powerTransformer.addEnd(connection.getVoltageLevel(), powerTransformerEnd);
    }

    private void createPowerTransformerACLines(BaseConnection connection, PowerTransformer powerTransformer, PNODE transformer) {
        for (NodeLineDto dto : connection.getNodeLineDtos()) {
            // Проверяем, что соединяемая нода является ПС и уже была создана в рамках cim экспорта.
            if (nodeHasType(dto.getNodeUuid(), PowerNodeType.SUBSTATION) && transformerWasCreated(dto.getNodeUuid())) {
                // Если второй трансформатор уже был создан, то их нужно соединить
                PowerTransformerEnd powerTransformer1End = powerTransformer.getEnds().get(connection.getVoltageLevel());
                PowerTransformerEnd powerTransformer2End = getTransformerFromMap(dto.getNodeUuid()).getEnds().get(connection.getVoltageLevel());

                ConnectivityNode connectivityNode1 = powerTransformer1End.getTerminal().getConnectivityNode();
                ConnectivityNode connectivityNode2 = powerTransformer2End.getTerminal().getConnectivityNode();

                LINE line = elementService.getLine(dto.getLineUuid());
                // Пример: ACLine_39703b1f-7323-4260-870f-57d68103269c
                String acLineId = getACLineId(line.getUuid());

                Terminal terminal1 = createTerminal(String.join("_", acLineId, connectivityNode1.getRdfId()), connectivityNode1);//todo терминалы имеют одинаковое название
                Terminal terminal2 = createTerminal(String.join("_", acLineId, connectivityNode2.getRdfId()), connectivityNode2);

                ACLine acLine = createACLine(acLineId, connection.getVoltageLevel(), line.isBreaker(), calculateLength(transformer, elementService.getNode(dto.getNodeUuid())),
                    List.of(terminal1, terminal2));

            }
        }
    }

    private String saveAsFile() {
        final String PREFIX = "scheme_";
        String date = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd_MM_yyyy'T'hh_mm_ss_SSS"));
        String fileName = "C:\\Users\\mnikitin\\IdeaProjects\\other\\demo\\src\\main\\resources\\schemes\\" + PREFIX + date + "_cim.xml";
        File file = new File(fileName);
        System.out.println("File name: " + PREFIX + date + "_cim.xml");
        try (FileWriter writer = new FileWriter(file)) {
            outerRdfMar.marshal(outerRdf, writer);
        } catch (Exception e) {
            System.out.println("Exception : " + e);
        }

        return fileName;
//        return "cimFile";
    }

    private boolean nodeHasType(String nodeUuid, PowerNodeType powerNodeType) {
        return powerNodeType.equals(elementService.getNode(nodeUuid).getNodeType());
    }

    private List<PNODE> getAllNodesWithType(PowerNodeType powerNodeType) {
        return elementService.getNodes().stream()
            .filter(node -> powerNodeType.equals(node.getNodeType()))
            .toList();
    }

    private boolean transformerWasCreated(String nodeUuid) {
        return powerTransformers.containsKey(getTransformerId(nodeUuid));
    }

    private PowerTransformer getTransformerFromMap(String nodeUuid) {
        return powerTransformers.get(getTransformerId(nodeUuid));
    }

    private ConnectivityNode createConnectivityNode(String id, Double voltage) {
        // Пример: CNode_TRE_1_5dfc614b-69de-4a47-8e28-71742363bc0a
        String connectivityNodeId = getConnectivityNodeId(id);
        ConnectivityNode connectivityNode = new ConnectivityNode(connectivityNodeId, connectivityNodeId, connectivityNodeId, voltage, voltage);
        connectivityNodes.put(connectivityNodeId, connectivityNode);
        return connectivityNode;
    }

    private Terminal createTerminal(String id, ConnectivityNode connectivityNode) {
        // Пример: T_TRE_1_5dfc614b-69de-4a47-8e28-71742363bc0a
        String terminalId = getTerminalId(id);
        Terminal terminal = new Terminal(terminalId, terminalId, terminalId, id, connectivityNode);
        terminals.put(terminalId, terminal);
        connectivityNode.addTerminal(terminal);
        return terminal;
    }

    private PowerTransformerEnd createPowerTransformerEnd(String transformerEndId, BaseConnection connection, Terminal terminal, PowerTransformer powerTransformer, int iter) {
        PowerTransformerEnd powerTransformerEnd = new PowerTransformerEnd(
            transformerEndId,
            transformerEndId,
            transformerEndId,
            Double.toString(connection.getVoltageLevel().getVoltageLevel()),
            true,
            terminal.getRdfId(),
            powerTransformer.getRdfId(),
            iter,
            terminal,
            powerTransformer
        );
        powerTransformerEnds.put(transformerEndId, powerTransformerEnd);
        return powerTransformerEnd;
    }

    private PowerTransformer createPowerTransformer(String transformerId, VoltageLevel level) {
        PowerTransformer powerTransformer = new PowerTransformer(
            transformerId,
            transformerId,
            transformerId,
            Double.toString(level.getVoltageLevel()),
            true);
        powerTransformers.put(powerTransformer.getRdfId(), powerTransformer);
        return powerTransformer;
    }

    private BusBarSection createBusBarSection(String id, VoltageLevel level, Terminal terminal) {
        BusBarSection busBarSection = new BusBarSection(id, id, id, Double.toString(level.getVoltageLevel()), true, terminal);
        busBarSections.put(id, busBarSection);
        return busBarSection;
    }

    private ACLine createACLine(String id, VoltageLevel level, boolean breaker, double length, List<Terminal> terminals) {
        ACLine acLine = new ACLine(id, id, id, Double.toString(level.getVoltageLevel()), !breaker, length, terminals);
        acLines.put(id, acLine);
        return acLine;
    }

    private ConformLoad createConformLoad(String loadId, VoltageLevel level, double activePower, double reactivePower, Terminal terminal) {
        ConformLoad load = new ConformLoad(loadId, loadId, loadId, Double.toString(level.getVoltageLevel()), true, activePower, reactivePower, activePower, activePower, reactivePower, reactivePower,
            terminal);
        conformLoads.put(loadId, load);
        return load;
    }

    private BaseVoltage createBaseVoltage(TransformerConfiguration transformerConfiguration) {
        double nominalVoltage = transformerConfiguration.getLevel().getVoltageLevel();
        String baseVoltageId = getBaseVoltageId(nominalVoltage);
        BaseVoltage baseVoltage = new BaseVoltage(baseVoltageId, baseVoltageId, baseVoltageId, nominalVoltage);
        baseVoltages.put(baseVoltageId, baseVoltage);
        return baseVoltage;
    }

    private String getTransformerId(String id) {
        // Пример: TR_5dfc614b-69de-4a47-8e28-71742363bc0a
        return String.join("_", "TR", id);
    }

    private String getTransformerEndId(String id, int i) {
        //Пример: TRE_1_5dfc614b-69de-4a47-8e28-71742363bc0a
        return String.join("_", "TRE", Integer.toString(i), id);
    }

    private String getBusBarEndId(String id) {
        // Пример: BS_TRE_1_5dfc614b-69de-4a47-8e28-71742363bc0a
        return String.join("_", "BS", id);
    }

    private String getConnectivityNodeId(String id) {
        // Пример: CNode_TRE_1_5dfc614b-69de-4a47-8e28-71742363bc0a
        return String.join("_", "CNode", id);
    }

    private String getTerminalId(String id) {
        // Пример: T_TRE_1_5dfc614b-69de-4a47-8e28-71742363bc0a
        return String.join("_", "T", id);
    }

    private String getACLineId(String lineUuid) {
        // Пример: ACLine_39703b1f-7323-4260-870f-57d68103269c
        return String.join("_", "ACLine", lineUuid);
    }

    private String getConformLoadId(String loadUuid) {
        // Пример: CLoad_c156e3e7-e30a-4e32-b022-cb23169a377e
        return String.join("_", "CLoad", loadUuid);
    }

    private String getGeneratorId(String generatorUuid) {
        // Пример: EI_2164868e-56e0-4a09-89b1-f40ac92f46bb
        return String.join("_", "EI", generatorUuid);
    }

    private String getBaseVoltageId(double voltageLevel) {
        // Пример: _110
        return "_" + voltageLevel;
    }

    private double calculateLength(PNODE node1, PNODE node2) {
        double lengthInUnits = sqrt(pow(node1.getX() - node2.getX(), 2) + pow(node1.getY() - node2.getY(), 2));
        return lengthInUnits * PROPORTIONALITY_FACTOR;
    }

}