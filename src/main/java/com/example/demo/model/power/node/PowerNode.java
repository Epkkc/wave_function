package com.example.demo.model.power.node;

import com.example.demo.model.status.StatusMeta;
import com.example.demo.model.status.StatusType;
import com.example.demo.model.visual.elements.BasePane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import lombok.Data;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// TODO Генератор, подстанцию, нагрузку можно сделать внутренними классами PowerNode, как в SecurityChecker-е
@Data
public class PowerNode implements GridElement {

    protected PowerNodeType nodeType;
    protected int x;
    protected int y;
    protected StackPane stackPane;
    protected Map<VoltageLevel, ConnectionPoint> connectionPoints = new HashMap<>();
    protected BasePane basePane;

    private final String uuid = UUID.randomUUID().toString();


    public PowerNode(double size) {
        basePane = createBasePane(size);
        stackPane = basePane.getStackPane();
    }

    public PowerNode(double size, BasePane basePane) {
        this.basePane = basePane;
        stackPane = basePane.getStackPane();
    }

    protected void createAndFillStackPane(double size) {
        stackPane = new StackPane();

        Rectangle recrangle = new Rectangle();
        recrangle.setWidth(size);
        recrangle.setHeight(size);
        recrangle.setFill(Paint.valueOf("#e7e7e7")); // #363636
        recrangle.setStroke(Color.TRANSPARENT);
        recrangle.setStrokeWidth(0);


        stackPane.getChildren().add(recrangle);
    }

    protected BasePane createBasePane(double size) {
        return new BasePane(size);
    }

    public void addStatus(StatusType type, boolean show, VoltageLevel... voltageLevel) {
        basePane.getStatusPane().addStatus(type, show, voltageLevel);
    }

    public void addStatus(StatusMeta statusMeta, boolean show) {
        basePane.getStatusPane().addStatus(statusMeta, show);
    }

    public void addStatuses(Collection<StatusMeta> statusMeta, boolean show) {
        statusMeta.forEach(status -> basePane.getStatusPane().addStatus(status, show));
    }

    public List<StatusMeta> getStatusMetas() {
        return basePane.getStatusPane().getStatuses().stream().map(StatusMeta.class::cast).toList();
    }

    public void setOpacity(double value, VoltageLevel voltageLevel){}

}
