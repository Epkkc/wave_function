package com.example.demo.model.visual.elements;

import com.example.demo.model.power.node.ConnectionPoint;
import com.example.demo.model.power.node.PowerNodeType;
import com.example.demo.services.ElementsService;
import javafx.scene.layout.StackPane;

public class GeneratorPane implements VisualPane {

    private StackPane stackPane;

    private ConnectionPoint connectionPoint;

    public GeneratorPane(double size) {
        this.stackPane = ElementsService.createGeneratorPane(size);
        //TODO после добавления этого элемента в грид, нужно устанавливать connectionPoint
    }

    @Override
    public StackPane getStackPane() {
        return stackPane;
    }

    @Override
    public PowerNodeType getType() {
        return PowerNodeType.GENERATOR;
    }
}
