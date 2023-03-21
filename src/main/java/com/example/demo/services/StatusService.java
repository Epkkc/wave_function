package com.example.demo.services;

import com.example.demo.model.Matrix;
import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.power.node.PowerNodeType;
import com.example.demo.model.power.node.VoltageLevel;
import com.example.demo.model.status.StatusMeta;
import com.example.demo.model.status.StatusSupplier;
import com.example.demo.model.status.StatusType;
import lombok.RequiredArgsConstructor;

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

}