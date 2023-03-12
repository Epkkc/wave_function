package com.example.demo.model.power.node;

public record ConnectionPoint(
    double x,
    double y,
    VoltageLevel voltageLevel,
    int exitLines,
    int exitLimit,
    int entranceLines,
    int entranceLimit
) {
}
