package com.example.demo.java.fx.model.power;

import com.example.demo.base.model.power.LevelChainNumberDto;
import com.example.demo.java.fx.model.grid.ConnectionPoint;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FxTwoSubStation extends FxAbstractPowerNode {

    @JsonIgnore
    private Map<VoltageLevel, Circle> circlesMap = new HashMap<>();
    private VoltageLevel level1;
    private VoltageLevel level2;

    public FxTwoSubStation(int x, int y, int power, List<LevelChainNumberDto> levelChainNumberDtos, double size) {
        super(PowerNodeType.SUBSTATION, x, y, power, levelChainNumberDtos, size);
        if (levelChainNumberDtos.size() != 2) {
            throw new UnsupportedOperationException("Creation two windings substation with LevelChainNumberDto size != 2 : " + levelChainNumberDtos);
        }
        this.level1 = levelChainNumberDtos.get(0).getVoltageLevel();
        this.level2 = levelChainNumberDtos.get(1).getVoltageLevel();
        fillBasePane(levelChainNumberDtos);
    }

    protected void fillBasePane(List<LevelChainNumberDto> levelChainNumberDtos) {
        LevelChainNumberDto dto1 = levelChainNumberDtos.stream()
            .filter(node -> node.getVoltageLevel().equals(level1))
            .findFirst()
            .orElseThrow(() -> new UnsupportedOperationException("There is no levelChainNumberDto with voltage level " + level1));
        LevelChainNumberDto dto2 = levelChainNumberDtos.stream()
            .filter(node -> node.getVoltageLevel().equals(level2))
            .findFirst()
            .orElseThrow(() -> new UnsupportedOperationException("There is no levelChainNumberDto with voltage level " + level2));

        double radius = size * 7 / 30;
        double offset = radius / 2;

        Circle circle1 = new Circle();
        circle1.setRadius(radius);
        circle1.setFill(Color.TRANSPARENT);
        circle1.setStroke(level1.getColor());
        circle1.setStrokeWidth(size * 8 / 300);
        circle1.setTranslateX(-offset);

        Circle circle2 = new Circle();
        circle2.setRadius(radius);
        circle2.setFill(Color.TRANSPARENT);
        circle2.setStroke(level2.getColor());
        circle2.setStrokeWidth(size * 8 / 300);
        circle2.setTranslateX(offset);

        connections.put(level1, new ConnectionPoint(-offset - radius, 0, level1, 100, dto1.getChainLinkNumber()));

        connections.put(level2, new ConnectionPoint(offset + radius, 0, level2, 100, dto2.getChainLinkNumber()));

        Bounds bounds1 = circle1.localToScreen(circle1.getLayoutBounds());

        addHoverListener(circle1, level1);
        addHoverListener(circle2, level2);

        basePane.getStackPane().getChildren().add(circle1);
        basePane.getStackPane().getChildren().add(circle2);

        circlesMap.put(level1, circle1);
        circlesMap.put(level2, circle2);
    }

    private void addHoverListener(Circle circle, VoltageLevel voltageLevel) {
        Text lowText = new Text();
        lowText.setTextAlignment(TextAlignment.CENTER);
        lowText.setFont(Font.getDefault());

        StackPane stickyNotesPane = new StackPane();
        stickyNotesPane.setPadding(new Insets(2));
        stickyNotesPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.85);");
        stickyNotesPane.getChildren().add(lowText);

        Popup popup = new Popup();
        popup.getContent().add(stickyNotesPane);


        circle.hoverProperty().addListener((obs, oldVal, newValue) -> {
            if (newValue) {
                Bounds bnds = circle.localToScreen(circle.getLayoutBounds());
                double x = bnds.getMinX() - (stickyNotesPane.getWidth() / 2) + (circle.getRadius());
                double y = bnds.getMinY() - stickyNotesPane.getHeight();
                setHoverOpacity(voltageLevel);
                lowText.setText(String.join("\n", getUuid(), voltageLevel.getDescription(), String.format("Мощность: %s", power), String.format("Номер звена: %s", connections.get(voltageLevel).getChainLinkOrder())));
                popup.show(circle, x, y);
            } else {
                setDefaultOpacity(voltageLevel);
                popup.hide();
            }
        });
    }

    @Override
    protected void setOpacity(VoltageLevel voltageLevel, double value) {
        circlesMap.get(voltageLevel).setOpacity(value);
    }

    @Override
    public void setStrokeColor(Color color) {
        circlesMap.values().forEach(element -> element.setStroke(color));
    }

    @Override
    public Collection<DoubleProperty> getOpacityProperty() {
        return circlesMap.values().stream().map(Node::opacityProperty).collect(Collectors.toList());
    }
}
