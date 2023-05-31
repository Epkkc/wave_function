package com.example.demo.java.fx.service;

import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.service.element.AbstractElementService;
import com.example.demo.java.fx.model.power.FxAbstractPowerNode;
import com.example.demo.java.fx.model.power.FxPowerLine;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
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

    @Override
    public void removeLine(FxPowerLine line) {
        super.removeLine(line);
        slowRemovingLine(line);
    }

    private void slowRemovingLine(FxPowerLine line) {
        line.getLine().setStroke(Color.BLACK);
        Timeline timeline = new Timeline();

//        Text removingText = getRemovingLabel(line);
//        Platform.runLater(() -> {
//            root.getChildren().add(removingText);
//        });

        timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO, new KeyValue(line.getLine().opacityProperty(), 1.0d)));
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(100_000), new KeyValue(line.getLine().opacityProperty(), 0d)));
        timeline.play();

        timeline.setOnFinished(actionEvent -> {
            Platform.runLater(() -> {
                root.getChildren().remove(line.getLine());
//                root.getChildren().remove(removingText);
            });
        });
    }

    private Text getRemovingLabel(FxPowerLine line) {
        Bounds layoutBounds = line.getLine().getLayoutBounds();

        double centerX = layoutBounds.getCenterX();
        double centerY = layoutBounds.getCenterY();

        double width = layoutBounds.getWidth();
        double height = layoutBounds.getHeight();
//        double angle = Math.atan(height / width);
        // instantiating the Rotate class.
        Rotate rotate = new Rotate();

        //setting properties for the rotate object.
        rotate.setAngle(30);
        rotate.setPivotX(100);
        rotate.setPivotY(300);

        Text text = new Text("removing");
//        text.setRotate();
        text.setX(centerX);
        text.setY(centerY);
        text.setFill(Color.BLACK);
        return text;
    }

}
