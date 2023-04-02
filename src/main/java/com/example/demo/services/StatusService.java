package com.example.demo.services;

import com.example.demo.model.Matrix;
import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.power.node.PowerNodeType;
import com.example.demo.model.power.node.VoltageLevel;
import com.example.demo.model.power.node.VoltageLevelInfo;
import com.example.demo.model.status.StatusMeta;
import com.example.demo.model.status.StatusSupplier;
import com.example.demo.model.status.StatusType;
import javafx.application.Platform;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.*;

@RequiredArgsConstructor
public class StatusService {

    private final Matrix<PowerNode> matrix;
    private final StatusSupplier statusSupplier;
    private final boolean roundedArea;

    public void setStatusToArea(PowerNode powerNode) {
        List<StatusMeta> metas = statusSupplier.getStatusByNode(powerNode);

        metas.forEach(meta ->
            meta.getVoltageLevels().forEach(voltageLevel ->
                matrix.getArea(powerNode.getX(), powerNode.getY(), voltageLevel.getBoundingArea()).forEach(node -> {
                    if (roundedArea) {
                        // Отбрасываем все ноды, которые выходят за зону
                        if (sqrt(pow(node.getX() - powerNode.getX(), 2) + pow(node.getY() - powerNode.getY(), 2)) > (voltageLevel.getBoundingArea() * powerNode.getNodeType().getBoundingAreaKoef())) {
                            return;
                        }
                    }
                    node.addStatus(meta.getType(), true, voltageLevel);
                })));

        // Добавляем запрет на расстановку рядом любых объектов
        matrix.getArea(powerNode.getX(), powerNode.getY()).forEach(node -> PowerNodeType.getValidValues().forEach(
            pnt -> node.addStatus(pnt.getBlockingStatus(), true, VoltageLevel.values()))
        );

    }

    public void setStatusToAreaP(PowerNode powerNode, VoltageLevelInfo... levels) {
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
                runnables.addAll(node.addStatusP(powerNode.getNodeType().getBlockingStatus(), true, level.getLevel()));
            });

            // Установка should статусов
            matrix.getArea(powerNode.getX(), powerNode.getY(), level.getBoundingAreaTo()).stream()
                .filter(node -> node.getNodeType().equals(PowerNodeType.EMPTY))
                .forEach(node -> {
                if (roundedArea) {
                    // Отбрасываем все ноды, которые выходят за зону
                    if (sqrt(pow(node.getX() - powerNode.getX(), 2) + pow(node.getY() - powerNode.getY(), 2)) <= (level.getBoundingAreaFrom()) ||
                        sqrt(pow(node.getX() - powerNode.getX(), 2) + pow(node.getY() - powerNode.getY(), 2)) > (level.getBoundingAreaTo())
                    ) {
                        return;
                    }
                }
                runnables.addAll(node.addStatusP(powerNode.getNodeType().getShouldStatus(), true, level.getLevel()));
            });
        }

        // Добавляем запрет на расстановку рядом любых объектов
        matrix.getArea(powerNode.getX(), powerNode.getY()).forEach(node -> PowerNodeType.getValidValues().forEach(
            pnt -> runnables.addAll(node.addStatusP(pnt.getBlockingStatus(), true, VoltageLevel.values())))
        );

        Platform.runLater(() -> runnables.forEach(Runnable::run));

    }


}