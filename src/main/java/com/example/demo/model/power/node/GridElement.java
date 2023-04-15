package com.example.demo.model.power.node;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.layout.StackPane;

public interface GridElement extends Coordinates {

    @JsonIgnore
    StackPane getStackPane();
}
