package com.example.demo.base.service;

import com.example.demo.base.model.configuration.GenerationConfiguration;
import com.example.demo.base.model.configuration.LoadConfiguration;
import com.example.demo.base.model.configuration.TransformerConfiguration;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.BasePowerNode;
import com.example.demo.base.model.status.StatusType;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

@RequiredArgsConstructor
public class BaseStatusService implements StatusService {

    private final Matrix<BasePowerNode> matrix;
    private final BaseConfiguration baseConfiguration;
    private final boolean roundedArea;

    public void setTransformerStatusToArea(BasePowerNode powerNode, TransformerConfiguration... levels) {

        // Установка blocking статусов
        for (TransformerConfiguration transformerConfiguration : levels) {
            matrix.getArea(powerNode.getX(), powerNode.getY(), transformerConfiguration.getBoundingAreaFrom()).stream()
                .filter(node -> node.getNodeType().equals(PowerNodeType.EMPTY))
                .forEach(node -> {
                    if (roundedArea) {
                        // Отбрасываем все ноды, которые выходят за зону
                        if (sqrt(pow(node.getX() - powerNode.getX(), 2) + pow(node.getY() - powerNode.getY(), 2)) > (transformerConfiguration.getBoundingAreaFrom())) {
                            return;
                        }
                    }
                    node.addStatus(powerNode.getNodeType().getBlockingStatus(), transformerConfiguration.getLevel());
                });

            // Установка transformer should статусов
            matrix.getArea(powerNode.getX(), powerNode.getY(), transformerConfiguration.getBoundingAreaTo()).stream()
                .filter(node -> node.getNodeType().equals(PowerNodeType.EMPTY))
                .forEach(node -> {
                    if (roundedArea) {
                        // Отбрасываем все ноды, которые выходят за зону
                        if (sqrt(pow(node.getX() - powerNode.getX(), 2) + pow(node.getY() - powerNode.getY(), 2)) <= (transformerConfiguration.getBoundingAreaFrom()) ||
                            sqrt(pow(node.getX() - powerNode.getX(), 2) + pow(node.getY() - powerNode.getY(), 2)) > (transformerConfiguration.getBoundingAreaTo())
                        ) {
                            return;
                        }
                    }
                    node.addStatus(powerNode.getNodeType().getShouldStatus(), transformerConfiguration.getLevel());
                });

            // Установка SHOUD_LOAD статусов при наличии
            baseConfiguration.getLoadConfigurations()
                .stream()
                .filter(loadConfiguration -> transformerConfiguration.getLevel().equals(loadConfiguration.getLevel()))
                .findFirst()
                .ifPresent(loadConfiguration -> {

                });

            // TODO удалить
            List<BasePowerNode> powerNodes = matrix.toNodeList()
                .stream()
                .filter(node -> node.getStatuses().stream().anyMatch(status -> status.getVoltageLevels().isEmpty()))
                .toList();
            System.out.println(powerNodes);
        }

        // Добавляем запрет на расстановку рядом любых объектов
        matrix.getArea(powerNode.getX(), powerNode.getY()).forEach(node -> PowerNodeType.getValidValues().forEach(
            pnt -> node.addStatus(pnt.getBlockingStatus(), VoltageLevel.values()))
        );

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

    @Override
    public void setLoadStatusToArea(BasePowerNode powerNode, GenerationConfiguration genCfg) {
        // Установка blocking статусов
        matrix.getArea(powerNode.getX(), powerNode.getY(), genCfg.getBoundingArea()).stream()
            .filter(node -> node.getNodeType().equals(PowerNodeType.EMPTY))
            .forEach(node -> {
                if (roundedArea) {
                    // Отбрасываем все ноды, которые выходят за зону
                    if (sqrt(pow(node.getX() - powerNode.getX(), 2) + pow(node.getY() - powerNode.getY(), 2)) > (genCfg.getBoundingArea())) {
                        return;
                    }
                }
                node.addStatus(powerNode.getNodeType().getBlockingStatus(), genCfg.getLevel());
            });

        System.out.println("Set generator powerNode = " + powerNode);
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


    // todo доработать этот метод, используя StatusDto
    private void addRingStatusArea(int x, int y, int boundingAreaFrom, int boundingAreaTo, StatusType statusType, VoltageLevel voltageLevel) {
        matrix.getArea(x, y, boundingAreaTo).stream()
            .filter(node -> node.getNodeType().equals(PowerNodeType.EMPTY))
            .forEach(node -> {
                if (roundedArea) {
                    // Отбрасываем все ноды, которые выходят за зону
                    if (sqrt(pow(node.getX() - x, 2) + pow(node.getY() - y, 2)) <= (boundingAreaFrom) ||
                        sqrt(pow(node.getX() - x, 2) + pow(node.getY() - y, 2)) > (boundingAreaTo)
                    ) {
                        return;
                    }
                } else {
                    if ((Math.abs(node.getX() - x) <= boundingAreaFrom || Math.abs(node.getX() - x) > boundingAreaTo) &&
                        (Math.abs(node.getY() - y) <= boundingAreaFrom || Math.abs(node.getY() - y) > boundingAreaTo)
                    ) {
                        return;
                    }
                }
                node.addStatus(statusType, voltageLevel);
            });
    }

    // todo доработать этот метод, используя StatusDto
    private void addStatusAreaTo(int x, int y, int boundingAreaTo, StatusType statusType, VoltageLevel voltageLevel) {
        matrix.getArea(x, y, boundingAreaTo).stream().forEach(node -> {
            node.addStatus(statusType, voltageLevel);
        });
    }


}