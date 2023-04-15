package com.example.demo.model;

import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.power.node.VoltageLevel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.shape.Line;

public record PowerLine(
    PowerNode point1,
    PowerNode point2,
    VoltageLevel voltageLevel,
    @JsonIgnore Line line,
    String uuid
) {
}
