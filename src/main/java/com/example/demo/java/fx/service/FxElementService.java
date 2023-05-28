package com.example.demo.java.fx.service;

import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.service.element.AbstractElementService;
import com.example.demo.java.fx.model.power.FxAbstractPowerNode;
import com.example.demo.java.fx.model.power.FxPowerLine;
import com.example.demo.java.fx.service.FxConfiguration;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import lombok.Getter;

@Getter
public class FxElementService extends AbstractElementService<FxAbstractPowerNode, FxPowerLine> {
    // TODO Попытаться разобраться как перенести линии на задний план, а ноды на передний

    private final FxConfiguration configuration;
    private final ScrollPane scrollPane;
    private final Group root;
    private final GridPane gridPane;

    public FxElementService(Matrix<FxAbstractPowerNode> matrix, FxConfiguration configuration, ScrollPane scrollPane, Group root, GridPane gridPane) {
        super(matrix);
        this.configuration = configuration;
        this.scrollPane = scrollPane;
        this.root = root;
        this.gridPane = gridPane;
    }

    public double getBaseSize() {
        return configuration.getBaseSize();
    }

    public void addPowerNodeToGrid(FxAbstractPowerNode node) {
        super.addPowerNodeToGrid(node);
        Platform.runLater(() -> {
            gridPane.add(node.getStackPane(), node.getY(), node.getX());
        });
    }

}
