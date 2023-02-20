package com.example.demo.model.visual.elements;

import com.example.demo.model.power.node.PowerNodeType;
import javafx.scene.layout.StackPane;

public interface VisualPane {

    StackPane getStackPane();

    PowerNodeType getType();
}
