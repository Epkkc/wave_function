package com.example.demo.java.fx.v2.model;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.power.BasePowerNode;
import com.example.demo.java.fx.model.visual.BasePane;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;

import java.util.Collection;

public abstract class AbstractPowerNodeFxProxy {

    protected final String uuid;
    protected BasePane basePane;
    protected double size;
    protected final double hoverOpacity = 0.5d;
    protected final double defaultOpacity = 1d;

    public AbstractPowerNodeFxProxy(String uuid, double size) {
        this.uuid = uuid;
        this.size = size;
        this.basePane = createBasePane();
    }

    protected BasePane createBasePane() {
        BasePane basePane1 = new BasePane(size);

        double infoSize = size / 14;

        Rectangle info = new Rectangle();
        info.setWidth(infoSize);
        info.setHeight(infoSize);
        info.setFill(Color.DARKGRAY); // F6FAFB // F5F9FA // F4F8F9 // #363636 // F0FFFF // e7e7e7 // FFFFF0 // #F5F5F5 - ЭТО ЦВЕТ БЭКГРАУНДА ОКНА ВИНДОУС // #F8F8F8
        info.setStroke(Color.TRANSPARENT);
        info.setStrokeWidth(0);
        info.setOpacity(0.2);

        addHoverListener(info, String.join("\n", nodeType.name(), Integer.toString(x), Integer.toString(y)));

        GridPane infoPane = new GridPane(); // При добавлении ещё одной gridPane на stackPane ломаются hoverListener-ы у статусов
        infoPane.setAlignment(Pos.TOP_RIGHT);
        infoPane.add(info, 0, 0);

        basePane1.getStackPane().getChildren().addAll(info);

        return basePane1;
    }

    public void setHoverOpacity(VoltageLevel voltageLevel) {
        setOpacity(voltageLevel, hoverOpacity);
    }

    public void setDefaultOpacity(VoltageLevel voltageLevel) {
        setOpacity(voltageLevel, defaultOpacity);
    }

    protected abstract void setOpacity(VoltageLevel voltageLevel, double value);

    protected void addHoverListener(Node node, String message) {
        Text text = new Text();
        text.setTextAlignment(TextAlignment.CENTER);
        text.setFont(Font.getDefault());

        StackPane stickyNotesPane = new StackPane();
        stickyNotesPane.setPadding(new Insets(2));
        stickyNotesPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.85);");
        stickyNotesPane.getChildren().add(text);

        Popup popup = new Popup();
        popup.getContent().add(stickyNotesPane);

        node.hoverProperty().addListener((obs, oldVal, newValue) -> {
            if (newValue) {
                Bounds bnds = node.localToScreen(node.getLayoutBounds());
                double x = bnds.getMinX() - (stickyNotesPane.getWidth() / 2);
                double y = bnds.getMinY() - stickyNotesPane.getHeight();
                text.setText(String.join("\n", nodeType.name(), Integer.toString(this.x), Integer.toString(this.y)));
                popup.show(node, x, y);
            } else {
                popup.hide();
            }
        });
    }
}
