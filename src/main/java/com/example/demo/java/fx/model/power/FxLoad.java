package com.example.demo.java.fx.model.power;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.power.LevelChainNumberDto;
import com.example.demo.java.fx.model.grid.ConnectionPoint;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FxLoad extends FxAbstractPowerNode {

    private VoltageLevel voltageLevel;

    @JsonIgnore
    private List<Shape> shapes = new ArrayList<>();

    public FxLoad(int x, int y, int power, List<LevelChainNumberDto> levelChainNumberDtos, double size) {
        super(PowerNodeType.LOAD, x, y, power, levelChainNumberDtos, size);
        if (levelChainNumberDtos.size() != 1) {
            throw new UnsupportedOperationException("Creation load with LevelChainNumberDto size != 1 : " + levelChainNumberDtos);
        }
        this.voltageLevel = levelChainNumberDtos.get(0).getVoltageLevel();
        fillBasePane(levelChainNumberDtos.get(0));
    }

    protected void fillBasePane(LevelChainNumberDto dto) {
        double length = size / 5;
        double width = size / 50;

        Line topLine = new Line(-length, 0, length, 0);
        topLine.setTranslateX(0);
        topLine.setTranslateY(-length);
        topLine.setStrokeWidth(width);
        topLine.setStroke(voltageLevel.getColor());

        Line centerLine = new Line(0, 0, 0, -length * 2);
        centerLine.setStrokeWidth(width);
        centerLine.setStroke(Color.BLUE);
        centerLine.setStroke(voltageLevel.getColor());

        double v = -length * 2 + length * Math.sin(Math.PI / 3);
        double h = length * Math.cos(Math.PI / 3);

        Line rightLine = new Line(0, -length * 2, -h, v);
        rightLine.setTranslateX(h / 2);
        rightLine.setTranslateY(length + v / 3);
        rightLine.setStrokeWidth(width);
        rightLine.setStroke(voltageLevel.getColor());

        Line leftLine = new Line(0, -length * 2, h, v);
        leftLine.setTranslateX(-h / 2);
        leftLine.setTranslateY(length + v / 3);
        leftLine.setStrokeWidth(width);
        leftLine.setStroke(voltageLevel.getColor());

        basePane.getStackPane().getChildren().add(topLine);
        basePane.getStackPane().getChildren().add(centerLine);
        basePane.getStackPane().getChildren().add(rightLine);
        basePane.getStackPane().getChildren().add(leftLine);

        shapes.add(topLine);
        shapes.add(centerLine);
        shapes.add(rightLine);
        shapes.add(leftLine);

        connections.put(dto.getVoltageLevel(), new ConnectionPoint(0, -length, dto.getVoltageLevel(), 100, dto.getChainLinkNumber()));

        addHoverListener();
    }

    @Override
    protected void setOpacity(VoltageLevel voltageLevel, double value) {
        shapes.forEach(shape -> shape.setOpacity(value));
    }

    private void addHoverListener() {
        Text text = new Text();
        text.setTextAlignment(TextAlignment.CENTER);
        text.setFont(Font.getDefault());
        text.setText(String.join("\n", getUuid(), voltageLevel.getDescription(), String.format("Мощность: %s", power),
            String.format("Номер звена: %s", connections.get(voltageLevel).getChainLinkOrder())));

        StackPane stickyNotesPane = new StackPane();
        stickyNotesPane.setPadding(new Insets(2));
        stickyNotesPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.85);");
        stickyNotesPane.getChildren().add(text);

        Popup popup = new Popup();
        popup.getContent().add(stickyNotesPane);

        shapes.forEach(shape -> shape.hoverProperty().addListener((obs, oldVal, newValue) -> {
            if (newValue) {
                Bounds bounds = shapes.get(0).localToScreen(shapes.get(0).getLayoutBounds());
                double x = bounds.getCenterX() - (stickyNotesPane.getWidth() / 2);
                double y = bounds.getMinY() - stickyNotesPane.getHeight();
                setHoverOpacity(voltageLevel); // Меняем прозрачноть(цвет) элемента, на который навели мышью
                popup.show(shapes.get(0), x, y);
            } else {
                setDefaultOpacity(voltageLevel); // Возвращаем дефолтную прозрачность 1
                popup.hide();
            }
        }));
    }

    @Override
    public void setStrokeColor(Color color) {
        shapes.forEach(element -> element.setStroke(color));
    }

    @Override
    public Collection<DoubleProperty> getOpacityProperty() {
        return shapes.stream().map(Node::opacityProperty).collect(Collectors.toList());
    }
}
