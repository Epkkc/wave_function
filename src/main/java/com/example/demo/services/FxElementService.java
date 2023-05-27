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
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
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

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

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
            Shape line = createLine(node1, point1, node2, point2, voltageLevel);

            FxPowerLine powerLine = new FxPowerLine(node1, node2, voltageLevel, line);
            lines.add(powerLine);
            point1.addConnection();
            point2.addConnection();

            addHoverListener(powerLine);

            Platform.runLater(() -> root.getChildren().add(line));
        }
    }

    public void connectTwoNodes(FxPowerNode node1, ConnectionPoint point1, FxPowerNode node2, ConnectionPoint point2, VoltageLevel voltageLevel, boolean breaker) {
        if (point1 != null && point2 != null) {
            if (!breaker) {
                connectTwoNodes(node1, point1, node2, point2, voltageLevel);
            } else {
                Shape line = createPath(node1, point1, node2, point2, voltageLevel);

                FxPowerLine powerLine = new FxPowerLine(node1, node2, voltageLevel, line);
                lines.add(powerLine);
                point1.addConnection();
                point2.addConnection();

                addHoverListener(powerLine);

                Platform.runLater(() -> root.getChildren().add(line));
            }

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

        Shape line = powerLine.getLine();
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

    private Shape createLine(FxPowerNode node1, ConnectionPoint point1, FxPowerNode node2, ConnectionPoint point2, VoltageLevel voltageLevel) {
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

        return line;
    }

    private Shape createPath(FxPowerNode node1, ConnectionPoint point1, FxPowerNode node2, ConnectionPoint point2, VoltageLevel voltageLevel) {
        Path path = new Path();
        path.setStroke(voltageLevel.getColor());
        path.setStrokeWidth(configuration.getBaseSize() * 8 / 300);
        path.setOpacity(0.5d);

        Bounds boundsP1 = node1.getStackPane().getBoundsInParent();
        Bounds boundsP2 = node2.getStackPane().getBoundsInParent();

        final double startX = boundsP1.getCenterX() + point1.getX();
        final double startY = boundsP1.getCenterY() + point1.getY();
        final double endX = boundsP2.getCenterX() + point2.getX();
        final double endY = boundsP2.getCenterY() + point2.getY();

        // y1 = kx1 + b
        // b = y1 - kx1
        // y2 = kx2 + y1 - kx1
        // y2 - y1 = k (x2 - x1)
        // k = (y2 - y1) / (x2 - x1)
//        final double k = (endY - startY) / (endX - startX);
//        final double b = startY - k* startX;

        final double length = sqrt(pow(startX - endX, 2) + pow(startY - endY, 2));

        final double cos = (endX - startX) / length;
        final double sin = sqrt(1d - cos * cos);

        final double breakerSize = configuration.getBaseSize() / 15;
        final double branchLength = (length - breakerSize * 2) / 2;


        MoveTo moveTo1 = new MoveTo(startX, startY);

        double endXLine1 = startX + branchLength * cos;
        double endYLine1 = startY + branchLength * sin;
        LineTo line1 = new LineTo(endXLine1,  endYLine1);

        double line2Cos = sin;
        double line2Sin = - cos;
        double endXLine2 = endXLine1 + breakerSize * line2Cos;
        double endYLine2 = endYLine1 + breakerSize * line2Sin;
        LineTo line2 = new LineTo(endXLine2, endYLine2);

        double endXLine3 = endXLine2 + 2 * breakerSize * cos;
        double endYLine3 = endYLine2 + 2 * breakerSize * sin;
        LineTo line3 = new LineTo(endXLine3, endYLine3);

        double line4Cos = -sin;
        double line4Sin = cos;
        double endXLine4 = endXLine3 + 2 * breakerSize * line4Cos;
        double endYLine4 = endYLine3 + 2 * breakerSize * line4Sin;
        LineTo line4 = new LineTo(endXLine4, endYLine4);


        double endXLine5 = endXLine4 - 2 * breakerSize * cos;
        double endYLine5 = endYLine4 - 2 * breakerSize * sin;
        LineTo line5 = new LineTo(endXLine5, endYLine5);

        LineTo line6 = new LineTo(endXLine1, endYLine1);

        double moveTo2X = startX + (branchLength + 2 * breakerSize) * cos;
        double moveTo2Y = startY + (branchLength + 2 * breakerSize) * sin;
        MoveTo moveTo2 = new MoveTo(moveTo2X, moveTo2Y);

        LineTo line7 = new LineTo(endX,  endY);

        path.getElements().add(moveTo1);
        path.getElements().add(line1);
        path.getElements().add(line2);
        path.getElements().add(line3);
        path.getElements().add(line4);
        path.getElements().add(line5);
        path.getElements().add(line6);
        path.getElements().add(moveTo2);
        path.getElements().add(line7);

        return path;
    }

}
