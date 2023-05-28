package com.example.demo.java.fx.service;

import com.example.demo.base.model.configuration.GenerationConfiguration;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.configuration.LoadConfiguration;
import com.example.demo.base.service.BaseConfiguration;
import com.example.demo.base.service.status.AbstractStatusService;
import com.example.demo.java.fx.model.power.FxAbstractPowerNode;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.configuration.TransformerConfiguration;
import javafx.application.Platform;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class FxStatusService extends AbstractStatusService<FxAbstractPowerNode> {


    public FxStatusService(Matrix<FxAbstractPowerNode> matrix, BaseConfiguration baseConfiguration, boolean roundedArea) {
        super(matrix, baseConfiguration, roundedArea);
    }

//    public void setTransformerStatusToArea(FxAbstractPowerNode powerNode, TransformerConfiguration... levels) {
//
//        // Установка blocking статусов
//        for (TransformerConfiguration level : levels) {
//            matrix.getArea(powerNode.getX(), powerNode.getY(), level.getBoundingAreaFrom()).stream()
//                .filter(node -> node.getNodeType().equals(PowerNodeType.EMPTY))
//                .forEach(node -> {
//                    if (roundedArea) {
//                        // Отбрасываем все ноды, которые выходят за зону
//                        if (sqrt(pow(node.getX() - powerNode.getX(), 2) + pow(node.getY() - powerNode.getY(), 2)) > (level.getBoundingAreaFrom())) {
//                            return;
//                        }
//                    }
//                    node.addStatus(powerNode.getNodeType().getBlockingStatus(), level.getLevel());
//                });
//
//            AtomicInteger counter1 = new AtomicInteger();
//            AtomicInteger counter2 = new AtomicInteger();
//            // Установка should статусов
//            matrix.getArea(powerNode.getX(), powerNode.getY(), level.getBoundingAreaTo()).stream()
//                .filter(node -> node.getNodeType().equals(PowerNodeType.EMPTY))
//                .forEach(node -> {
//                    if (roundedArea) {
//                        // Отбрасываем все ноды, которые выходят за зону
//                        if (sqrt(pow(node.getX() - powerNode.getX(), 2) + pow(node.getY() - powerNode.getY(), 2)) <= level.getBoundingAreaFrom() ||
//                            sqrt(pow(node.getX() - powerNode.getX(), 2) + pow(node.getY() - powerNode.getY(), 2)) > level.getBoundingAreaTo()
//                        ) {
//                            counter1.getAndIncrement();
//                            return;
//                        }
//                    } else {
//                        if (
//                            (Math.abs(node.getX() - powerNode.getX()) <= level.getBoundingAreaFrom() || Math.abs(node.getX() - powerNode.getX()) > level.getBoundingAreaTo()) &&
//                            (Math.abs(node.getY() - powerNode.getY()) <= level.getBoundingAreaFrom() || Math.abs(node.getY() - powerNode.getY()) > level.getBoundingAreaTo())
//                        ) {
//                            counter1.getAndIncrement();
//                            return;
//                        }
//                    }
//                    counter2.getAndIncrement();
//                    node.addStatus(powerNode.getNodeType().getShouldStatus(), level.getLevel());
//                });
//
//
//            // TODO удалить
//            List<FxAbstractPowerNode> powerNodes = matrix.toNodeList()
//                .stream()
//                .filter(node -> node.getStatuses().stream().anyMatch(node1 -> node1.getVoltageLevels().isEmpty()))
//                .toList();
//            System.out.println(powerNodes);
//            counter1.set(0);
//            counter2.set(0);
//        }
//
//        // Добавляем запрет на расстановку рядом любых объектов
//        matrix.getArea(powerNode.getX(), powerNode.getY()).forEach(node -> PowerNodeType.getValidValues().forEach(
//            pnt -> node.addStatus(pnt.getBlockingStatus(), VoltageLevel.values()))
//        );
//
//    }
//
//
//    public void setGeneratorStatusToArea(FxAbstractPowerNode powerNode, LoadConfiguration loadCfg) {
//
//        // Установка blocking статусов
//        matrix.getArea(powerNode.getX(), powerNode.getY(), loadCfg.getBoundingArea()).stream()
//            .filter(node -> node.getNodeType().equals(PowerNodeType.EMPTY))
//            .forEach(node -> {
//                if (roundedArea) {
//                    // Отбрасываем все ноды, которые выходят за зону
//                    if (sqrt(pow(node.getX() - powerNode.getX(), 2) + pow(node.getY() - powerNode.getY(), 2)) > (loadCfg.getBoundingArea())) {
//                        return;
//                    }
//                }
//                node.addStatus(powerNode.getNodeType().getBlockingStatus(), loadCfg.getLevel());
//            });
//
//        System.out.println("Set load powerNode=" + powerNode);
//        List<FxAbstractPowerNode> powerNodes = matrix.toNodeList()
//            .stream()
//            .filter(node -> node.getStatuses().stream().anyMatch(node1 -> node1.getVoltageLevels().isEmpty()))
//            .toList();
//        System.out.println(powerNodes);
//
//        // Добавляем запрет на расстановку рядом любых объектов
//        matrix.getArea(powerNode.getX(), powerNode.getY()).forEach(node -> PowerNodeType.getValidValues().forEach(
//            pnt -> node.addStatus(pnt.getBlockingStatus(), VoltageLevel.values()))
//        );
//
//    }
//
//    public void setGeneratorStatusToArea(FxAbstractPowerNode powerNode, GenerationConfiguration generationConfiguration) {
//
//        // Установка blocking статусов
//        matrix.getArea(powerNode.getX(), powerNode.getY(), generationConfiguration.getBoundingArea()).stream()
//            .filter(node -> node.getNodeType().equals(PowerNodeType.EMPTY))
//            .forEach(node -> {
//                if (roundedArea) {
//                    // Отбрасываем все ноды, которые выходят за зону
//                    if (sqrt(pow(node.getX() - powerNode.getX(), 2) + pow(node.getY() - powerNode.getY(), 2)) > (generationConfiguration.getBoundingArea())) {
//                        return;
//                    }
//                }
//                node.addStatus(powerNode.getNodeType().getBlockingStatus(), generationConfiguration.getLevel());
//            });
//
//        System.out.println("Set generator powerNode=" + powerNode);
//        List<FxAbstractPowerNode> powerNodes = matrix.toNodeList()
//            .stream()
//            .filter(node -> node.getStatuses().stream().anyMatch(node1 -> node1.getVoltageLevels().isEmpty()))
//            .toList();
//        System.out.println(powerNodes);
//
//        // Добавляем запрет на расстановку рядом любых объектов
//        matrix.getArea(powerNode.getX(), powerNode.getY()).forEach(node -> PowerNodeType.getValidValues().forEach(
//            pnt -> node.addStatus(pnt.getBlockingStatus(), VoltageLevel.values()))
//        );
//
//    }

}