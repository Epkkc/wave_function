package com.example.demo.java.fx.model.power;

import com.example.demo.java.fx.model.grid.ConnectionPoint;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FxThreeSubStation extends FxPowerNode {

    @JsonIgnore
    private Map<VoltageLevel, Circle> circlesMap = new HashMap<>();
    private VoltageLevel level1;
    private VoltageLevel level2;
    private VoltageLevel level3;


    public FxThreeSubStation(int x, int y, int power, VoltageLevel level1, VoltageLevel level2, VoltageLevel level3, double size) {
        super(PowerNodeType.SUBSTATION, x, y, power, List.of(level1, level2, level3), size);
        this.level1 = level1;
        this.level2 = level2;
        this.level3 = level3;
        fillBasePane();
    }

    protected void fillBasePane() {

        double radius = size * 7 / 30;
        double offset = radius / 2;

        Circle circle1 = new Circle();
        circle1.setRadius(radius);
        circle1.setFill(Color.TRANSPARENT);
        circle1.setStroke(level1.getColor());
        circle1.setStrokeWidth(size * 8 / 300);
        circle1.setTranslateX(-offset);
        circle1.setTranslateY(-offset);

        Circle circle2 = new Circle();
        circle2.setRadius(radius);
        circle2.setFill(Color.TRANSPARENT);
        circle2.setStroke(level2.getColor());
        circle2.setStrokeWidth(size * 8 / 300);
        circle2.setTranslateX(offset);
        circle2.setTranslateY(-offset);

        Circle circle3 = new Circle();
        circle3.setRadius(radius);
        circle3.setFill(Color.TRANSPARENT);
        circle3.setStroke(level3.getColor());
        circle3.setStrokeWidth(size * 8 / 300);
        circle3.setTranslateY(radius-offset);

        connectionPoints.put(level1, new ConnectionPoint(-offset - radius, -offset, level1, 0, 2));
        connectionPoints.put(level2, new ConnectionPoint(offset + radius, -offset, level2, 0, 2));
        connectionPoints.put(level3, new ConnectionPoint(0, radius+offset, level3, 0, 2));

        Bounds bounds1 = circle1.localToScreen(circle1.getLayoutBounds());

        addHoverListener(circle1, level1);
        addHoverListener(circle2, level2);
        addHoverListener(circle3, level3);

        basePane.getStackPane().getChildren().add(circle1);
        basePane.getStackPane().getChildren().add(circle2);
        basePane.getStackPane().getChildren().add(circle3);

        circlesMap.put(level1, circle1);
        circlesMap.put(level2, circle2);
        circlesMap.put(level3, circle3);
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
                lowText.setText(String.join("\n", getUuid(), voltageLevel.getDescription(), Integer.toString(power)));
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
}
