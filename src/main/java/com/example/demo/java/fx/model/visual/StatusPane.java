package com.example.demo.java.fx.model.visual;

import com.example.demo.java.fx.model.status.FxStatus;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import lombok.Data;

import java.util.List;

@Data
public class StatusPane {

    private HBox hbox;
    private final double size;

    public StatusPane(double size) {
        createHBox();
        this.size = size * 15 / 200;
    }

    private void createHBox() {
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
