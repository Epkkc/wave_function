package com.example.demo.services;

import com.example.demo.model.Matrix;
import com.example.demo.model.power.node.LoadConfiguration;
import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.power.node.PowerNodeType;
import com.example.demo.model.power.node.VoltageLevel;
import com.example.demo.model.power.node.VoltageLevelInfo;
import javafx.application.Platform;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

@RequiredArgsConstructor
public class StatusService {

    private final Matrix<PowerNode> matrix;
    private final boolean roundedArea;

    public void setTransformetStatusToArea(PowerNode powerNode, VoltageLevelInfo... levels) {
        //todo Для оптимизации можно брать самую большую зону и для неё уже прогонять все уровни напряжения

        List<Runnable> runnables = new ArrayList<>();

        // Установка blocking статусов
        for (VoltageLevelInfo level : levels) {
            matrix.getArea(powerNode.getX(), powerNode.getY(), level.getBoundingAreaFrom()).stream()
                .filter(node -> node.getNodeType().equals(PowerNodeType.EMPTY))
                .forEach(node -> {
                    if (roundedArea) {
                        // Отбрасываем все ноды, которые выходят за зону
                        if (sqrt(pow(node.getX() - powerNode.getX(), 2) + pow(node.getY() - powerNode.getY(), 2)) > (level.getBoundingAreaFrom())) {
                            return;
                        }
                    }
                    runnables.addAll(node.addStatus(powerNode.getNodeType().getBlockingStatus(), true, level.getLevel()));
                });

            AtomicInteger counter1 = new AtomicInteger();
            AtomicInteger counter2 = new AtomicInteger();
            // Установка should статусов
            matrix.getArea(powerNode.getX(), powerNode.getY(), level.getBoundingAreaTo()).stream()
                .filter(node -> node.getNodeType().equals(PowerNodeType.EMPTY))
                .forEach(node -> {
                    if (roundedArea) {
                        // Отбрасываем все ноды, которые выходят за зону
                        if (sqrt(pow(node.getX() - powerNode.getX(), 2) + pow(node.getY() - powerNode.getY(), 2)) <= (level.getBoundingAreaFrom()) ||
                            sqrt(pow(node.getX() - powerNode.getX(), 2) + pow(node.getY() - powerNode.getY(), 2)) > (level.getBoundingAreaTo())
                        ) {
                            counter1.getAndIncrement();
                            return;
                        }
                    }
                    counter2.getAndIncrement();
                    runnables.addAll(node.addStatus(powerNode.getNodeType().getShouldStatus(), true, level.getLevel()));
                });


            // TODO удалить
            List<PowerNode> powerNodes = matrix.toNodeList()
                .stream()
                .filter(node -> node.getBasePane().getStatusPane().getStatusMatrix().toNodeList().stream().anyMatch(node1 -> node1.getVoltageLevels().isEmpty()))
                .toList();
            System.out.println(powerNodes);
            counter1.set(0);
            counter2.set(0);
        }

        // Добавляем запрет на расстановку рядом любых объектов
        matrix.getArea(powerNode.getX(), powerNode.getY()).forEach(node -> PowerNodeType.getValidValues().forEach(
            pnt -> runnables.addAll(node.addStatus(pnt.getBlockingStatus(), true, VoltageLevel.values())))
        );

        Platform.runLater(() -> runnables.forEach(Runnable::run));

    }


    public void setLoadStatusToArea(PowerNode powerNode, LoadConfiguration loadCfg) {
        List<Runnable> runnables = new ArrayList<>();

        // Установка blocking статусов
        matrix.getArea(powerNode.getX(), powerNode.getY(), loadCfg.getBoundingAreaFrom()).stream()
            .filter(node -> node.getNodeType().equals(PowerNodeType.EMPTY))
            .forEach(node -> {
                if (roundedArea) {
                    // Отбрасываем все ноды, которые выходят за зону
                    if (sqrt(pow(node.getX() - powerNode.getX(), 2) + pow(node.getY() - powerNode.getY(), 2)) > (loadCfg.getBoundingAreaFrom())) {
                        return;
                    }
                }
                runnables.addAll(node.addStatus(powerNode.getNodeType().getBlockingStatus(), true, loadCfg.getLevel()));
            });

        System.out.println("Set load powerNode=" + powerNode);
        List<PowerNode> powerNodes = matrix.toNodeList()
            .stream()
            .filter(node -> node.getBasePane().getStatusPane().getStatusMatrix().toNodeList().stream().anyMatch(node1 -> node1.getVoltageLevels().isEmpty()))
            .toList();
        System.out.println(powerNodes);

        // Добавляем запрет на расстановку рядом любых объектов
        matrix.getArea(powerNode.getX(), powerNode.getY()).forEach(node -> PowerNodeType.getValidValues().forEach(
            pnt -> runnables.addAll(node.addStatus(pnt.getBlockingStatus(), true, VoltageLevel.values())))
        );

        Platform.runLater(() -> runnables.forEach(Runnable::run));
    }

}