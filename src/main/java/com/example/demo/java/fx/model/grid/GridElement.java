package com.example.demo.java.fx.model.grid;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.layout.StackPane;

public interface GridElement {

    @JsonIgnore
    StackPane getStackPane();
}
