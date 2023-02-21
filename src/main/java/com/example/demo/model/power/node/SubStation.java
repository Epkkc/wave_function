package com.example.demo.model.power.node;

import com.example.demo.model.visual.elements.BasePane;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;

import java.util.HashMap;
import java.util.Map;

public class SubStation extends PowerNode {

    private VoltageLevel highVoltage;
    private VoltageLevel lowVoltage;

    private Circle highVoltageCircle;
    private Circle lowVoltageCircle;

    private Map<VoltageLevel, Circle> circlesMap = new HashMap<>();

    private final double hoverOpacity = 0.5d;
    private final double defaultOpacity = 1d;


    public SubStation(double size, VoltageLevel highVoltage, VoltageLevel lowVoltage, BasePane basePane) {
        super(size, basePane);
        this.highVoltage = highVoltage;
        this.lowVoltage = lowVoltage;
        setNodeType(PowerNodeType.SUBSTATION);
        createAndFillStackPane(size, highVoltage, lowVoltage);
    }

    public SubStation(double size, VoltageLevel highVoltage, VoltageLevel lowVoltage) {
        super(size);
        this.highVoltage = highVoltage;
        this.lowVoltage = lowVoltage;
        setNodeType(PowerNodeType.SUBSTATION);
        createAndFillStackPane(size, highVoltage, lowVoltage);
    }

    @Override
    public StackPane getStackPane() {
        return this.stackPane;
    }

    private void createAndFillStackPane(double size, VoltageLevel highVoltageLevel, VoltageLevel lowVoltageLevel) {
        // TODO Заменить ручное создание прямоугольника на кастомный объект,
        //  который представлял бы сам прямоугольник с набором статусов внутри
        //  метод можно сделать в супер классе
        Rectangle recrangle = new Rectangle();
        recrangle.setWidth(size);
        recrangle.setHeight(size);
//        recrangle.setFill(Paint.valueOf("#363636"));
        recrangle.setFill(Paint.valueOf("#e7e7e7"));
        recrangle.setStroke(Color.TRANSPARENT);
        recrangle.setStrokeWidth(0);

        double radius = size * 7 / 30;
        double offset = radius / 2;

        highVoltageCircle = new Circle();
        highVoltageCircle.setRadius(radius);
        highVoltageCircle.setFill(Color.TRANSPARENT);
        highVoltageCircle.setStroke(highVoltageLevel.getColor());
        highVoltageCircle.setStrokeWidth(size * 8 / 300);
        highVoltageCircle.setTranslateX(-offset);

        lowVoltageCircle = new Circle();
        lowVoltageCircle.setRadius(radius);
        lowVoltageCircle.setFill(Color.TRANSPARENT);
        lowVoltageCircle.setStroke(lowVoltageLevel.getColor());
        lowVoltageCircle.setStrokeWidth(size * 8 / 300);
        lowVoltageCircle.setTranslateX(offset);

        connectionPoints.put(
            highVoltageLevel,
            new ConnectionPoint(
                -offset - radius,
                0,
//                highVoltageLevel,
                0, 2,
                0, 2
            ));

        connectionPoints.put(
            lowVoltageLevel,
            new ConnectionPoint(
                offset + radius,
                0,
//                lowVoltageLevel,
                0, 2,
                0, 2
            ));

        addHoverListener(lowVoltageCircle, lowVoltageLevel);
        addHoverListener(highVoltageCircle, highVoltageLevel);

        stackPane.getChildren().add(highVoltageCircle);
        stackPane.getChildren().add(lowVoltageCircle);

        circlesMap.put(highVoltageLevel, highVoltageCircle);
        circlesMap.put(lowVoltageLevel, lowVoltageCircle);
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
                circle.setOpacity(hoverOpacity); // Меняем прозрачноть(цвет) элемента, на который навели мышью
                lowText.setText(String.join("\n", getUuid(), voltageLevel.getDescription()));
                popup.show(circle, x, y);
            } else {
                circle.setOpacity(defaultOpacity); // Возвращаем дефолтную прозрачность 1
                popup.hide();
            }
        });
    }

    @Override
    public void setHoverOpacity(VoltageLevel voltageLevel) {
        circlesMap.get(voltageLevel).setOpacity(hoverOpacity);
    }

    @Override
    public void setDefaultOpacity(VoltageLevel voltageLevel) {
        circlesMap.get(voltageLevel).setOpacity(defaultOpacity);
    }
}
