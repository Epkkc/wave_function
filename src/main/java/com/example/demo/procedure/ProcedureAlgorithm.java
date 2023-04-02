package com.example.demo.procedure;

import com.example.demo.model.Matrix;
import com.example.demo.model.filter.FilterContext;
import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.power.node.PowerNodeType;
import com.example.demo.model.power.node.VoltageLevelInfo;
import com.example.demo.model.status.BlockType;
import com.example.demo.model.status.PowerNodeStatusMeta;
import com.example.demo.model.status.StatusSupplier;
import com.example.demo.services.Configuration;
import com.example.demo.services.ConnectionService;
import com.example.demo.services.DecisionMaker;
import com.example.demo.services.ElementServiceImpl;
import com.example.demo.services.FilterServiceImpl;
import com.example.demo.services.StatusService;
import com.example.demo.thread.StoppableThread;
import com.example.demo.utils.RandomUtils;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@RequiredArgsConstructor
public class ProcedureAlgorithm {

    private final Matrix<PowerNode> matrix;
    private final StatusSupplier statusSupplier;
    private final FilterServiceImpl filterService;
    private final DecisionMaker decisionMaker;
    private final ElementServiceImpl elementsService;
    private final StatusService statusService;
    private final ConnectionService connectionService;
    private final Configuration configuration;
    private final List<VoltageLevelInfo> voltageLevels;
    private final AbstractNodeFabric nodeFabric;
    private final Random random = new Random();


    public void start() {

        List<PowerNode> nodes = matrix.toNodeList();

        for (int i = 0; i < voltageLevels.size(); i++) {
            VoltageLevelInfo currentVoltage = voltageLevels.get(i);

            System.out.println(currentVoltage);

            do {

                do {
                    // Задержка для удобства просмотра
                    try {
                        Thread.sleep(configuration.getDelay());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    // Обработка остановки потока
                } while (((StoppableThread) Thread.currentThread()).isStopped());

                PowerNode powerNode = RandomUtils.randomValue(nodes);

                // TODO нужно чтобы была хотя бы одна ПС с нижним классом напряжения на 1 ступень ниже
                boolean three = false;
                if (currentVoltage.getLevel().isThreeWindings()) {
                    three = random.nextInt(2) == 1;
                }
                PowerNode resultNode = null;

                three = false;

                if (three) {
                    resultNode = nodeFabric.createThreeWindingsSubstation(currentVoltage.getLevel(), voltageLevels.get(i + 1).getLevel(), voltageLevels.get(i + 2).getLevel(), powerNode);
                    fillToGrid(resultNode, currentVoltage, voltageLevels.get(i + 1), voltageLevels.get(i + 2));

                } else {
                    int gap = random.nextInt(currentVoltage.getLevel().getGap()) + 1;
                    gap = Math.min(gap, voltageLevels.size());
                    resultNode = nodeFabric.createTwoWindingsSubstation(currentVoltage.getLevel(), voltageLevels.get(i + gap).getLevel(), powerNode);
                    fillToGrid(resultNode, currentVoltage, voltageLevels.get(i + gap));
                }

                System.out.println(resultNode);


            } while (!(nodes = matrix.getAll(
                node -> node.getBasePane().getStatusPane().getStatuses().stream()
                    .anyMatch(status -> status.getType().getBlockType().equals(BlockType.SHOULD)
                        && status.getVoltageLevels().contains(currentVoltage.getLevel())
                        && status.getType().getNodeType().equals(PowerNodeType.SUBSTATION))
            )).isEmpty());

        }

    }

    private void fillToGrid(PowerNode node, VoltageLevelInfo... levels) {
        elementsService.addPowerNodeToGrid(node);
        // Заполняем area статусом, согласно только что добавленной ноде
        statusService.setStatusToAreaP(node, levels);
        // Соединяем сгенерированную ноду с соседями
        connectionService.connectNode(node);
    }


    public void startAlgo() {
        for (PowerNode node : matrix) {

            do {
                // Задержка для удобства просмотра
                try {
                    Thread.sleep(configuration.getDelay());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // Обработка остановки потока
            } while (((StoppableThread) Thread.currentThread()).isStopped());

            // Получаем все возможные варианты ноды
            Set<PowerNodeStatusMeta> allPowerNodeStatuses = statusSupplier.getAllPowerNodeStatuses();

            // Убираем из всех возможных вариантов ноды те, которые противоречат статусам текущей ноды
            FilterContext context = filterService.filter(new FilterContext(allPowerNodeStatuses, new HashSet<>(), node));
            context.clearPossibleStatuses();
            // Из оставшихся возможных статусов выбираем рандомно единственный и создаём для него PowerNode
            Optional<PowerNode> resultNode = decisionMaker.makeDecision(context);
            resultNode.ifPresent(result -> {
                elementsService.addPowerNodeToGrid(result);
                // Заполняем area статусом, согласно только что добавленной ноде
                statusService.setStatusToArea(result);
                // Соединяем сгенерированную ноду с соседями
                connectionService.connectNode(result);
            });

        }
        System.out.println("FINISH");
    }

}
