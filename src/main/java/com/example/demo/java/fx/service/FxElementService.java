package com.example.demo.java.fx.service;

import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.service.ConfigurationStaticSupplier;
import com.example.demo.base.service.element.AbstractElementService;
import com.example.demo.java.fx.model.power.FxAbstractPowerNode;
import com.example.demo.java.fx.model.power.FxPowerLine;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import lombok.Getter;

import java.util.List;

@Getter
public class FxElementService extends AbstractElementService<FxAbstractPowerNode, FxPowerLine> {

    private final FxConfiguration configuration;
    private final ScrollPane scrollPane;
    private final Group root;
    private final GridPane gridPane;
    private final int timeLineLength = ConfigurationStaticSupplier.fxLineDisappearanceDuration;

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
        Platform.runLater(() -> gridPane.add(node.getStackPane(), node.getY(), node.getX()));
    }

    @Override
    public void removeLine(FxPowerLine line, boolean fromRemoveNodeMethod) {
        super.removeLine(line, fromRemoveNodeMethod);
        slowRemovingLine(line);
        if (!fromRemoveNodeMethod) {
            timeLineDelay();
        }
    }

    @Override
    public void removeNode(FxAbstractPowerNode node, FxAbstractPowerNode replaceNode) {
        super.removeNode(node, replaceNode);
        slowRemovingNode(node, replaceNode);
        timeLineDelay();
    }

    private void timeLineDelay() {
        try {
            Thread.sleep(timeLineLength);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void beforeRemovingLines(List<FxPowerLine> linesForRemove) {
        linesForRemove.forEach(this::slowRemovingLine);
    }

    private void slowRemovingLine(FxPowerLine line) {
        line.getLine().setStroke(Color.BLACK);
        Timeline timeline = new Timeline();

        timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO, new KeyValue(line.getLine().opacityProperty(), 1.0d)));
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(timeLineLength), new KeyValue(line.getLine().opacityProperty(), 0d)));
        timeline.play();

        timeline.setOnFinished(actionEvent -> Platform.runLater(() -> {
            root.getChildren().remove(line.getLine());
        }));
    }

    private void slowRemovingNode(FxAbstractPowerNode node, FxAbstractPowerNode replaceNode) {
        node.setStrokeColor(Color.BLACK);

        Timeline timeline = new Timeline();

        for (DoubleProperty opacityProperty : node.getOpacityProperty()) {
            timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO, new KeyValue(opacityProperty, 1.0d)));
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(timeLineLength), new KeyValue(opacityProperty, 0d)));
        }

        timeline.play();

        timeline.setOnFinished(actionEvent -> Platform.runLater(() -> {
            gridPane.getChildren().remove(node.getStackPane());
            gridPane.add(replaceNode.getStackPane(), replaceNode.getY(), replaceNode.getX());
        }));
    }

}
