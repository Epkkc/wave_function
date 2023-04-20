package com.example.demo.services;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.java.fx.model.power.FxPowerLine;
import com.example.demo.java.fx.model.grid.ConnectionPoint;
import com.example.demo.java.fx.model.power.FxPowerNode;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.java.fx.service.FxConfiguration;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import javafx.stage.Stage;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Data
@RequiredArgsConstructor
public class FxElementService {
    // TODO Попытаться разобраться как перенести линии на задний план, а ноды на передний

    private final FxConfiguration configuration;
    private final Stage stage;
    private final Scene scene;
    private final ScrollPane scrollPane;
    private final Group root;
    private final GridPane gridPane;
    private final Matrix<FxPowerNode> matrix;
    private List<FxPowerLine> lines = new ArrayList<>();
    private int sumLoad;
    private int sumPower;


    public double getBaseSize() {
        return configuration.getBaseSize();
    }

    public void addPowerNodeToGrid(FxPowerNode node) {
        matrix.add(node);
        if (PowerNodeType.LOAD.equals(node.getNodeType())) {
            sumLoad+=node.getPower();
        }
        if (PowerNodeType.GENERATOR.equals(node.getNodeType())) {
            sumPower+=node.getPower();
        }
        Platform.runLater(() -> {
            gridPane.add(node.getStackPane(), node.getY(), node.getX());
        });
    }

    public void connectTwoNodes(FxPowerNode node1, ConnectionPoint point1, FxPowerNode node2, ConnectionPoint point2, VoltageLevel voltageLevel) {
        if (point1 != null && point2 != null) {
            Bounds boundsP1 = node1.getStackPane().getBoundsInParent();
            Bounds boundsP2 = node2.getStackPane().getBoundsInParent();

            Line line = new Line();
            line.setStartX(boundsP1.getCenterX() + point1.getX());
            line.setStartY(boundsP1.getCenterY() + point1.getY());
            line.setEndX(boundsP2.getCenterX() + point2.getX());
            line.setEndY(boundsP2.getCenterY() + point2.getY());
            line.setStroke(voltageLevel.getColor());
            line.setStrokeWidth(configuration.getBaseSize() * 8 / 300);
            line.setOpacity(0.5d);

            FxPowerLine powerLine = new FxPowerLine(node1, node2, voltageLevel, line);
            lines.add(powerLine);
            point1.addConnection();
            point2.addConnection();

            addHoverListener(powerLine);

            Platform.runLater(() -> root.getChildren().add(line));
        }
    }

    private void addHoverListener(FxPowerLine powerLine) {
        Text text = new Text();
        text.setTextAlignment(TextAlignment.CENTER);
        text.setFont(Font.getDefault());

        StackPane stickyNotesPane = new StackPane();
        stickyNotesPane.setPadding(new Insets(2));
        stickyNotesPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.85);");
        stickyNotesPane.getChildren().add(text);

        Popup popup = new Popup();
        popup.getContent().add(stickyNotesPane);

        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(String.format("P1 : %s", powerLine.getPoint1().getUuid()));
        joiner.add(String.format("P2 : %s", powerLine.getPoint2().getUuid()));
        joiner.add(String.format("Line uuid : %s", powerLine.getUuid()));

        Line line = powerLine.getLine();
        line.hoverProperty().addListener((obs, oldVal, newValue) -> {
            if (newValue) {
                Bounds bnds = line.localToScreen(line.getLayoutBounds());
                double x = bnds.getCenterX();
                double y = bnds.getCenterY() + stickyNotesPane.getHeight();
                line.setOpacity(0.2); // Меняем прозрачноть(цвет) элемента, на который навели мышью
                text.setText(joiner.toString());
                popup.show(line, x, y);
                powerLine.getPoint1().setHoverOpacity(powerLine.getVoltageLevel());
                powerLine.getPoint2().setHoverOpacity(powerLine.getVoltageLevel());
            } else {
                line.setOpacity(0.5); // Возвращаем дефолтную прозрачность
                popup.hide();
                powerLine.getPoint1().setDefaultOpacity(powerLine.getVoltageLevel());
                powerLine.getPoint2().setDefaultOpacity(powerLine.getVoltageLevel());
            }
        });
    }

}