package com.example.demo.params.window;

import com.example.demo.base.model.enums.VoltageLevel;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import lombok.Data;

@Data
public class VoltageLevelInput {
    private final TitleElement title;
    private final CheckBox enabledCheckBox;
    private final DropMenuElement dropMenuElement;
    private final VoltageLevel voltageLevel;
    private final GridPane outerGridPane;
    private final int row;
    private final int column;
    private final int spreadColumns;

    private boolean isDropMenuShown;

    public VoltageLevelInput(TitleElement title, CheckBox enabledCheckBox, DropMenuElement dropMenuElement, VoltageLevel voltageLevel, GridPane outerGridPane, int row, int column, int spreadColumns, boolean isDropMenuShown) {
        this.title = title;
        this.enabledCheckBox= enabledCheckBox;
        this.dropMenuElement = dropMenuElement;
        this.voltageLevel = voltageLevel;
        this.outerGridPane = outerGridPane;
        this.row = row;
        this.column = column;
        this.spreadColumns = spreadColumns;
        this.isDropMenuShown = isDropMenuShown;
        addTitleOnMouseClickListener();
        addCheckboxListener();
    }

    public void changeDropMenuShown() {
        isDropMenuShown = !isDropMenuShown;
    }

    public boolean isEnabled() {
        return enabledCheckBox.selectedProperty().get();
    }

    public void addTitleOnMouseClickListener() {


        title.getStackPane().setOnMouseClicked(mouseEvent -> {
            if (!isDropMenuShown) {
//                outerGridPane.add(dropMenuElement.getStackPane(), 0, row + 1, totalColumns, 1);
                dropMenuElement.getGridPane().setVisible(true);
                title.expandBottomLine();
            } else {
//                outerGridPane.getChildren().remove(dropMenuElement.getStackPane());
                dropMenuElement.getGridPane().setVisible(false);
                title.collapseBottomLine();
            }
            changeDropMenuShown();
        });
    }

    public void addCheckboxListener() {
        enabledCheckBox.selectedProperty().addListener(
            (observableValue, oldValue, newValue) -> {
                if (!newValue) {
                    title.disableTitle(isDropMenuShown);
                    if (isDropMenuShown) {
//                        outerGridPane.getChildren().remove(dropMenuElement.getStackPane());
                        dropMenuElement.getGridPane().setVisible(false);
                        changeDropMenuShown();
                    }
                    title.disableTitle(isDropMenuShown);
                    enabledCheckBox.setText("disabled");
                } else {
                    title.enableTitle();
                    enabledCheckBox.setText("enabled");
                }
            }
        );
    }
}
