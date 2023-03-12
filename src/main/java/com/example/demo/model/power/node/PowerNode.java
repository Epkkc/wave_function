package com.example.demo.model.power.node;

import com.example.demo.model.status.StatusMeta;
import com.example.demo.model.status.StatusType;
import com.example.demo.model.visual.elements.BasePane;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import lombok.Data;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// TODO Генератор, подстанцию, нагрузку можно сделать внутренними классами PowerNode, как в SecurityChecker-е
@Data
public abstract class PowerNode implements GridElement {

    protected PowerNodeType nodeType;
    protected int x;
    protected int y;
    protected double size;
    protected Map<VoltageLevel, ConnectionPoint> connectionPoints = new HashMap<>();
    protected BasePane basePane;
    protected final double hoverOpacity = 0.5d;
    protected final double defaultOpacity = 1d;

    private final String uuid = UUID.randomUUID().toString();


    public PowerNode(double size, PowerNodeType nodeType) {
        this.size = size;
        this.nodeType = nodeType;
        basePane = createBasePane();
    }

    protected BasePane createBasePane() {
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

    public void setHoverOpacity(VoltageLevel voltageLevel) {
        setOpacity(voltageLevel, hoverOpacity);
    }

    public void setDefaultOpacity(VoltageLevel voltageLevel) {
        setOpacity(voltageLevel, defaultOpacity);
    }

    protected abstract void setOpacity(VoltageLevel voltageLevel, double value);

    @Override
    public StackPane getStackPane() {
        return basePane.getStackPane();
    }

    // TODO Доработать этот
    protected void addHoverListener(Node node, VoltageLevel voltageLevel, String message, double x, double y) {
        Text text = new Text();
        text.setTextAlignment(TextAlignment.CENTER);
        text.setFont(Font.getDefault());
        text.setText(message);

        StackPane stickyNotesPane = new StackPane();
        stickyNotesPane.setPadding(new Insets(2));
        stickyNotesPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.85);");
        stickyNotesPane.getChildren().add(text);

        Popup popup = new Popup();
        popup.getContent().add(stickyNotesPane);

        node.hoverProperty().addListener((obs, oldVal, newValue) -> {
            if (newValue) {
                setHoverOpacity(voltageLevel);
                popup.show(node, x, y);
            } else {
                setDefaultOpacity(voltageLevel);
                popup.hide();
            }
        });
    }
}
