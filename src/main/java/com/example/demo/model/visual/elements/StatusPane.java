package com.example.demo.model.visual.elements;

import com.example.demo.model.Matrix;
import com.example.demo.model.status.Status;
import com.example.demo.model.status.StatusMeta;
import com.example.demo.model.status.StatusType;
import com.example.demo.model.power.node.VoltageLevel;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class StatusPane {

    private GridPane statusPane;

    private Matrix<Status> statusMatrix;

    private int limitInOneRow = 7;

    private int numberOfElements = 0;

    private final double size;

    public StatusPane(double size) {
        createGridPane();
        this.size = size * 15 / 200;
        this.statusMatrix = new Matrix<>(1, limitInOneRow);
    }

    private void createGridPane() {
        // TODO перенести в параметры конструктора и привязать к параметру size
        double value = 0.5d;
        statusPane = new GridPane();
        statusPane.setAlignment(Pos.BOTTOM_LEFT);
        statusPane.setGridLinesVisible(false);
        statusPane.setPadding(new Insets(value));
        statusPane.setVgap(value);
        statusPane.setHgap(value);

    }

    public void addStatus(StatusType type, boolean show, VoltageLevel... voltageLevel) {
        Optional<Status> status1 = statusMatrix.get(s -> type.equals(s.getType()));
        if (status1.isPresent()) {
            status1.get().addVoltageLevel(voltageLevel);
        } else {
            int x = numberOfElements / limitInOneRow;
            int y = numberOfElements - x * limitInOneRow;
            if (y == 0) statusMatrix.addRow();

            Status status = new Status(type, x, y, size, voltageLevel);
            statusMatrix.fill(status);

            if (show) {
                Platform.runLater(() -> statusPane.add(status.getShape(), status.getY(), status.getX()));
            } else {
                statusPane.add(status.getShape(), status.getY(), status.getX());
            }

            numberOfElements++;
        }
    }

    public void addStatus(StatusMeta statusMeta, boolean show) {
        addStatus(statusMeta.getType(), show, statusMeta.getVoltageLevels().stream().toArray(VoltageLevel[]::new));
    }


    public List<Status> getStatuses() {
        return statusMatrix.toNodeList();
    }
}
