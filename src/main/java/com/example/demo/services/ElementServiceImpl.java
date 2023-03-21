package com.example.demo.services;

import com.example.demo.model.Matrix;
import com.example.demo.model.PowerLine;
import com.example.demo.model.power.node.ConnectionPoint;
import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.power.node.VoltageLevel;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
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
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Data
@RequiredArgsConstructor
public class ElementServiceImpl {

    // TODO здесь хранить и манипулировать графическим отображением
    //  Хранить например:
    //      stage
    //      scene
    //      scrollPane -
    //      root - объект класса Group, обёртка над сеткой тайлов и линиями. В целом основной компонент.
    //      gridPane - сетку с тайлами
    //  Манипулировать например:
    //      делать стартовую конфигурацию элементов
    //      добавлять powerNode-ы в gridPane
    //      добавлять линии в root
    //      добавлять колбэки на элементы
    //      и другие функции отображения и взаимодействия с графическими элементами

    private final Configuration configuration;
    private final Stage stage;
    private final Scene scene;
    private final ScrollPane scrollPane;
    private final Group root;
    private final GridPane gridPane;
    private final Matrix<PowerNode> matrix;
    private List<PowerLine> lines = new ArrayList<>();

    public double getBaseSize() {
        return configuration.getBaseSize();
    }

    public void addPowerNodeToGrid(PowerNode node) {
        Platform.runLater(() -> {
            matrix.add(node);
            gridPane.add(node.getStackPane(), node.getY(), node.getX());
        });
    }

    public void connectTwoNodes(PowerNode node1, ConnectionPoint point1, PowerNode node2, ConnectionPoint point2, VoltageLevel voltageLevel) {
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
//                line.toBack(); // TODO Разобраться, как вывести некоторые элементы на передний план (кольца трансформатора и статусы), а линию убрать на задний план

            PowerLine powerLine = new PowerLine(node1, node2, voltageLevel, line, UUID.randomUUID().toString());
            lines.add(powerLine);

            point1.addConnection();
            point2.addConnection();

            addHoverListener(powerLine);

            Platform.runLater(() -> root.getChildren().add(line));
        }
    }

    private void addHoverListener(PowerLine powerLine) {
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
        joiner.add(String.format("P1 : %s", powerLine.point1().getUuid()));
        joiner.add(String.format("P2 : %s", powerLine.point2().getUuid()));
        joiner.add(String.format("Line uuid : %s", powerLine.uuid()));

        Line line = powerLine.line();
        line.hoverProperty().addListener((obs, oldVal, newValue) -> {
            if (newValue) {
                Bounds bnds = line.localToScreen(line.getLayoutBounds());
                double x = bnds.getCenterX();
                double y = bnds.getCenterY() + stickyNotesPane.getHeight();
                line.setOpacity(0.2); // Меняем прозрачноть(цвет) элемента, на который навели мышью
                text.setText(joiner.toString());
                popup.show(line, x, y);
                powerLine.point1().setHoverOpacity(powerLine.voltageLevel());
                powerLine.point2().setHoverOpacity(powerLine.voltageLevel());
            } else {
                line.setOpacity(0.5); // Возвращаем дефолтную прозрачность
                popup.hide();
                powerLine.point1().setDefaultOpacity(powerLine.voltageLevel());
                powerLine.point2().setDefaultOpacity(powerLine.voltageLevel());
            }
        });
    }

}
