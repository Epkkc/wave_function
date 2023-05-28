package com.example.demo.java.fx.model.visual;

import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.status.BlockType;
import com.example.demo.java.fx.model.status.FxStatus;
import com.example.demo.base.model.status.StatusType;
import com.example.demo.base.model.enums.VoltageLevel;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Data
public class StatusPane {

    private HBox hbox;
    private final double size;

    public StatusPane(double size) {
        createHBox();
        this.size = size * 15 / 200;
    }

    private void createHBox() {
        // TODO перенести в параметры конструктора и привязать к параметру size
        double value = 0.5d;
        hbox = new HBox();
        hbox.setAlignment(Pos.BOTTOM_LEFT);
        hbox.setPadding(new Insets(value));
        hbox.setSpacing(value);

    }

    public void refreshStatuses(List<FxStatus> statuses) {
        // Очищаем потомков hbox
        Platform.runLater(() -> {
            hbox.getChildren().removeAll(hbox.getChildren());
            if (statuses != null && !statuses.isEmpty()) {
                // Заполняем новыми статусами
                hbox.getChildren().addAll(statuses.stream().map(FxStatus::getShape).toList());
            }
        });
    }

}
