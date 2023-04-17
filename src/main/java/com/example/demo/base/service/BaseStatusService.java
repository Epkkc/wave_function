package com.example.demo.base.service;

import com.example.demo.base.model.configuration.LoadConfiguration;
import com.example.demo.base.model.configuration.VoltageLevelInfo;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.BasePowerNode;
import com.example.demo.java.fx.model.power.FxPowerNode;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

@RequiredArgsConstructor
public class BaseStatusService implements StatusService{

    private final Matrix<BasePowerNode> matrix;
    private final boolean roundedArea;

    public void setTransformerStatusToArea(BasePowerNode powerNode, VoltageLevelInfo... levels) {

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
                    node.addStatus(powerNode.getNodeType().getBlockingStatus(), level.getLevel());
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
                    node.addStatus(powerNode.getNodeType().getShouldStatus(), level.getLevel());
                });


            // TODO удалить
            List<BasePowerNode> powerNodes = matrix.toNodeList()
                .stream()
                .filter(node -> node.getStatuses().stream().anyMatch(status -> status.getVoltageLevels().isEmpty()))
                .toList();
            System.out.println(powerNodes);
            counter1.set(0);
            counter2.set(0);
        }

        // Добавляем запрет на расстановку рядом любых объектов
        matrix.getArea(powerNode.getX(), powerNode.getY()).forEach(node -> PowerNodeType.getValidValues().forEach(
            pnt -> node.addStatus(pnt.getBlockingStatus(), VoltageLevel.values()))
        );

        System.out.println();
    }


    public void setLoadStatusToArea(BasePowerNode powerNode, LoadConfiguration loadCfg) {

        // Установка blocking статусов
        matrix.getArea(powerNode.getX(), powerNode.getY(), loadCfg.getBoundingArea()).stream()
            .filter(node -> node.getNodeType().equals(PowerNodeType.EMPTY))
            .forEach(node -> {
                if (roundedArea) {
                    // Отбрасываем все ноды, которые выходят за зону
                    if (sqrt(pow(node.getX() - powerNode.getX(), 2) + pow(node.getY() - powerNode.getY(), 2)) > (loadCfg.getBoundingArea())) {
                        return;
                    }
                }
                node.addStatus(powerNode.getNodeType().getBlockingStatus(), loadCfg.getLevel());
            });

        System.out.println("Set load powerNode = " + powerNode);
        List<BasePowerNode> powerNodes = matrix.toNodeList()
            .stream()
            .filter(node -> node.getStatuses().stream().anyMatch(status -> status.getVoltageLevels().isEmpty()))
            .toList();
        System.out.println(powerNodes);

        // Добавляем запрет на расстановку рядом любых объектов
        matrix.getArea(powerNode.getX(), powerNode.getY()).forEach(node -> PowerNodeType.getValidValues().forEach(
            pnt -> node.addStatus(pnt.getBlockingStatus(), VoltageLevel.values()))
        );
    }

}