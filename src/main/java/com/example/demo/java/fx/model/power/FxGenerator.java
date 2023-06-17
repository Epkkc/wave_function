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
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FxGenerator extends FxAbstractPowerNode {
    @JsonIgnore
    private List<Shape> shapes = new ArrayList<>();
    @JsonIgnore
    private Circle circle;
    private VoltageLevel voltageLevel;

    public FxGenerator(int x, int y, int power, List<LevelChainNumberDto> levelChainNumberDtos, double size) {
        super(PowerNodeType.GENERATOR, x, y, power, levelChainNumberDtos, size);
        if (levelChainNumberDtos.size() != 1) {
            throw new UnsupportedOperationException("Creation generator with LevelChainNumberDto size != 1 : " + levelChainNumberDtos);
        }
        this.voltageLevel = levelChainNumberDtos.get(0).getVoltageLevel();
        fillBasePane(levelChainNumberDtos.get(0));
    }

    public void fillBasePane(LevelChainNumberDto dto) {
        double circleRadius = size * 9 / 30;
        circle = new Circle();
        circle.setRadius(circleRadius);
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(voltageLevel.getColor());
        circle.setStrokeWidth(size * 8 / 300);

        double radius = size / 10;
        double width = size / 50;

        Path path1 = drawSemiRing(0, 0, radius, radius - width, voltageLevel.getColor(), true);
        path1.setTranslateX(-(radius - width / 2));
        path1.setTranslateY(radius / 2);

        Path path2 = drawSemiRing(0, 0, radius, radius - width, voltageLevel.getColor(), false);
        path2.setTranslateX(radius - width / 2);
        path2.setTranslateY(-(radius / 2));

        basePane.getStackPane().getChildren().add(circle);
        basePane.getStackPane().getChildren().add(path1);
        basePane.getStackPane().getChildren().add(path2);

        shapes.add(circle);
        shapes.add(path1);
        shapes.add(path2);

        connections.put(dto.getVoltageLevel(), new ConnectionPoint(0, -circleRadius, dto.getVoltageLevel(), dto.getChainLinkNumber()));

        addHoverListeners();
    }

    public Path drawSemiRing(double centerX, double centerY, double radius, double innerRadius, Paint paint, boolean sweepFlag) {
        Path path = new Path();
        path.setFill(paint);
        path.setStroke(paint);
        path.setFillRule(FillRule.EVEN_ODD);

        MoveTo moveTo = new MoveTo();
        moveTo.setX(centerX + innerRadius);
        moveTo.setY(centerY);

        ArcTo arcToInner = new ArcTo();
        arcToInner.setX(centerX - innerRadius);
        arcToInner.setY(centerY);
        arcToInner.setRadiusX(innerRadius);
        arcToInner.setRadiusY(innerRadius);
        arcToInner.setSweepFlag(sweepFlag);

        MoveTo moveTo2 = new MoveTo();
        moveTo2.setX(centerX + innerRadius);
        moveTo2.setY(centerY);

        HLineTo hLineToRightLeg = new HLineTo();
        hLineToRightLeg.setX(centerX + radius);

        ArcTo arcTo = new ArcTo();
        arcTo.setX(centerX - radius);
        arcTo.setY(centerY);
        arcTo.setRadiusX(radius);
        arcTo.setRadiusY(radius);
        arcTo.setSweepFlag(sweepFlag);

        HLineTo hLineToLeftLeg = new HLineTo();
        hLineToLeftLeg.setX(centerX - innerRadius);

        path.getElements().add(moveTo);
        path.getElements().add(arcToInner);
        path.getElements().add(moveTo2);
        path.getElements().add(hLineToRightLeg);
        path.getElements().add(arcTo);
        path.getElements().add(hLineToLeftLeg);

        return path;
    }

    private void addHoverListeners() {
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

        shapes.forEach(uiEl -> {
            uiEl.hoverProperty().addListener((obs, oldVal, newValue) -> {
                if (newValue) {
                    Bounds bounds = circle.localToScreen(circle.getLayoutBounds());
                    double x = bounds.getMinX() - (stickyNotesPane.getWidth() / 2) + (circle.getRadius());
                    double y = bounds.getMinY() - stickyNotesPane.getHeight();
                    setHoverOpacity(voltageLevel); // Меняем прозрачноть(цвет) элемента, на который навели мышью
                    popup.show(circle, x, y);
                } else {
                    setDefaultOpacity(voltageLevel); // Возвращаем дефолтную прозрачность 1
                    popup.hide();
                }
            });
        });
    }

    @Override
    public void setOpacity(VoltageLevel voltageLevel, double value) {
        shapes.forEach(el -> el.setOpacity(value));
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
