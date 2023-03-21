package com.example.demo.services;

import com.example.demo.model.Matrix;
import com.example.demo.model.filter.FilterContext;
import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.status.PowerNodeStatusMeta;
import com.example.demo.model.status.StatusSupplier;
import com.example.demo.thread.StoppableThread;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class Algorithm {

    private final Matrix<PowerNode> matrix;
    private final StatusSupplier statusSupplier;
    private final FilterServiceImpl filterService;
    private final DecisionMaker decisionMaker;
    private final ElementServiceImpl elementsService;
    private final StatusService statusService;
    private final ConnectionService connectionService;
    private final Configuration configuration;


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
