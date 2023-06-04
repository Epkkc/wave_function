package com.example.demo.export.cim;

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
import com.example.demo.export.cim.model.BusBarSection;
import com.example.demo.export.cim.model.ConformLoad;
import com.example.demo.export.cim.model.ConnectivityNode;
import com.example.demo.export.cim.model.PowerTransformer;
import com.example.demo.export.cim.model.PowerTransformerEnd;
import com.example.demo.export.cim.model.Terminal;
import com.fasterxml.jackson.databind.InjectableValues;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

@RequiredArgsConstructor
public class CimExportService<PNODE extends AbstractPowerNode<? extends BaseStatus, ? extends BaseConnection>, LINE extends AbstractLine<PNODE>> {

    private final double LENGTH_KOEF = 4;

    protected final BaseConfiguration configuration;
    protected final ElementService<PNODE, LINE> elementService;
    protected final Map<String, PowerTransformer> powerTransformerMap = new HashMap<>();
    protected final List<PowerTransformer> powerTransformers = new ArrayList<>();
    protected final List<PowerTransformerEnd> powerTransformerEnds = new ArrayList<>();
    protected final List<ACLine> acLines = new ArrayList<>();
    protected final List<BusBarSection> busBarSections = new ArrayList<>();
    protected final List<Terminal> terminals = new ArrayList<>();
    protected final List<ConnectivityNode> connectivityNodes = new ArrayList<>();
    protected final List<ConformLoad> conformLoads = new ArrayList<>();
    protected final List<InjectableValues> injectableValues = new ArrayList<>();

    public String exportIntoCim() {
        List<PNODE> transformers = elementService.getNodes().stream().filter(node -> PowerNodeType.SUBSTATION.equals(node.getNodeType())).collect(Collectors.toList());

        for (PNODE transformer : transformers) {
            VoltageLevel baseLevel = transformer.getVoltageLevels().stream().max(Comparator.comparingInt(VoltageLevel::getVoltageLevel)).get();
            String transformerId = getTransformerId(transformer.getUuid());
            PowerTransformer powerTransformer = createPowerTransformer(transformerId, baseLevel);

            Map<VoltageLevel, PowerTransformerEnd> ends = new HashMap<>();
            List<? extends BaseConnection> connections = transformer.getConnections().values().stream().toList();
            for (int iter = 0; iter < connections.size(); iter++) {
                BaseConnection connection = connections.get(iter);
                String transformerEndId = getTransformerEndId(transformer.getUuid(), iter);

                ConnectivityNode connectivityNode = createConnectivityNode(transformerEndId, (double) connection.getVoltageLevel().getVoltageLevel());

                Terminal terminal = createTerminal(transformerEndId, connectivityNode);

                // todo может и не нужно пока что делать BusBar-ы
                String busBarEndId = getBusBarEndId(transformerEndId);
                Terminal busTerminal = createTerminal(busBarEndId, connectivityNode);

                BusBarSection busBarSection = createBusBarSection(busBarEndId, connection.getVoltageLevel(), busTerminal);

                PowerTransformerEnd powerTransformerEnd = createPowerTransformerEnd(transformerEndId, connection, terminal, powerTransformer, iter);
                ends.put(connection.getVoltageLevel(), powerTransformerEnd);

                connectivityNode.addTerminal(terminal);
                connectivityNode.addTerminal(busTerminal);
            }
            powerTransformer.setEnds(ends);

            // Создаём и соединяем ПС линиями
            for (BaseConnection connection : connections) {
                for (NodeLineDto dto : connection.getNodeLineDtos()) {
                    if (nodeIsTransformer(dto.getNodeUuid()) && transformerWasCreated(dto.getNodeUuid())) {
                        // Если второй трансформатор уже был создан, то их нужно соединить
                        PowerTransformerEnd powerTransformer1End = powerTransformer.getEnds().get(connection.getVoltageLevel());
                        PowerTransformerEnd powerTransformer2End = getTransformerFromMap(dto.getNodeUuid()).getEnds().get(connection.getVoltageLevel());

                        ConnectivityNode connectivityNode1 = powerTransformer1End.getTerminal().getConnectivityNode();
                        ConnectivityNode connectivityNode2 = powerTransformer2End.getTerminal().getConnectivityNode();

                        String acLineId = getACLineId(transformer, elementService.getNode(dto.getNodeUuid()));

                        Terminal terminal1 = createTerminal(acLineId, connectivityNode1);
                        Terminal terminal2 = createTerminal(acLineId, connectivityNode2);

                        ACLine acLine = createACLine(acLineId, connection.getVoltageLevel(), calculateLength(transformer, elementService.getNode(dto.getNodeUuid())),
                            List.of(terminal1, terminal2));

                        connectivityNode1.addTerminal(terminal1);
                        connectivityNode2.addTerminal(terminal2);
                    }
                }
            }
        }



        return saveAsFile();
    }


    private String saveAsFile() {


        final String PREFIX = "scheme_";
        String date = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd_MM_yyyy__hh_mm_ss"));
        String fileName = "C:\\Users\\mnikitin\\IdeaProjects\\other\\demo\\src\\main\\resources\\schemes\\" + PREFIX + date + "_cim.xml";
        File file = new File(fileName);
        System.out.println("File name: " + PREFIX + date);
        try (FileWriter writer = new FileWriter(file)) {
//            writer.write(objectMapper.writeValueAsString(dto));
        } catch (Exception e) {
            System.out.println("Exception : " + e);
        }

//        return fileName;
        return "cimFile"; // TODO раскомментить, когда будет готова логика экспорта сим файла
    }

    private boolean nodeIsTransformer(String nodeUuid) {
        return PowerNodeType.SUBSTATION.equals(elementService.getNode(nodeUuid).getNodeType());
    }

    private boolean transformerWasCreated(String nodeUuid) {
        return powerTransformerMap.containsKey(getTransformerId(getTransformerId(nodeUuid)));
    }

    private PowerTransformer getTransformerFromMap(String nodeUuid) {
        return powerTransformerMap.get(getTransformerId(nodeUuid));
    }

    private ConnectivityNode createConnectivityNode(String id, Double voltage) {
        String connectivityNodeId = getConnectivityNodeId(id);
        ConnectivityNode connectivityNode = new ConnectivityNode(connectivityNodeId, connectivityNodeId, connectivityNodeId, voltage, voltage);
        connectivityNodes.add(connectivityNode);
        return connectivityNode;
    }

    private Terminal createTerminal(String id, ConnectivityNode connectivityNode) {
        String terminalId = getTerminalId(id);
        Terminal terminal = new Terminal(terminalId, terminalId, terminalId, getRdfResource(id), connectivityNode);
        terminals.add(terminal);
        return terminal;
    }

    private PowerTransformerEnd createPowerTransformerEnd(String transformerEndId, BaseConnection connection, Terminal terminal, PowerTransformer powerTransformer, int iter) {
        PowerTransformerEnd powerTransformerEnd = new PowerTransformerEnd(
            transformerEndId,
            transformerEndId,
            transformerEndId,
            getBaseVoltageRdfResource(connection.getVoltageLevel()),
            true,
            getRdfResource(terminal.getRdfId()),
            powerTransformer.getRdfId(),
            iter,
            terminal,
            powerTransformer
        );
        powerTransformerEnds.add(powerTransformerEnd);
        return powerTransformerEnd;
    }

    private PowerTransformer createPowerTransformer(String transformerId, VoltageLevel level) {
        PowerTransformer powerTransformer = new PowerTransformer(
            transformerId,
            transformerId,
            transformerId,
            getBaseVoltageRdfResource(level),
            true);
        powerTransformerMap.put(powerTransformer.getRdfId(), powerTransformer);
        return powerTransformer;
    }

    private BusBarSection createBusBarSection(String id, VoltageLevel level, Terminal terminal) {
        BusBarSection busBarSection = new BusBarSection(id, id, id, getBaseVoltageRdfResource(level), true, terminal);
        busBarSections.add(busBarSection);
        return busBarSection;
    }

    private ACLine createACLine(String id, VoltageLevel level, double length, List<Terminal> terminals) {
        ACLine acLine = new ACLine(id, id, id, getBaseVoltageRdfResource(level), true, length, terminals);
        acLines.add(acLine);
        return acLine;
    }

    private String getTransformerId(String id) {
        return String.join("_", "TR", id);
    }

    private String getTransformerEndId(String id, int i) {
        return String.join("_", "TRE", Integer.toString(i), id);
    }

    private String getBusBarEndId(String id) {
        return String.join("_", "BS", id);
    }

    private String getConnectivityNodeId(String id) {
        return String.join("_", "CN", id);
    }

    private String getTerminalId(String id) {
        return String.join("_", "T", id);
    }

    private String getACLineId(PNODE node1, PNODE node2) {
        return String.join("_", "ACLine", Integer.toString(node1.getX()), Integer.toString(node1.getY()), Integer.toString(node2.getX()), Integer.toString(node2.getY()));
    }

    private String getBaseVoltageRdfResource(VoltageLevel baseLevel) {
        return String.join("_", "#", Integer.toString(baseLevel.getVoltageLevel()));
    }

    private String getRdfResource(String id) {
        return String.join("_", "#", id);
    }

    private double calculateLength(PNODE node1, PNODE node2) {
        double lengthInUnits = sqrt(pow(node1.getX() - node2.getX(), 2) + pow(node1.getY() - node2.getY(), 2));
        return lengthInUnits * LENGTH_KOEF;
    }

}
