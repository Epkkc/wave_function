package com.example.demo.model.power.node;

public record ConnectionPoint(
    double x,
    double y,
    int exitLines,
    int exitLimit,
    int entranceLines,
    int entranceLimit
) {
}
