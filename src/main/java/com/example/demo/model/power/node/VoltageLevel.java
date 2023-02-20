package com.example.demo.model.power.node;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public enum VoltageLevel {

    LEVEL_35(1, 35, "35 кВ", Paint.valueOf("#E67E22"), 2), // Color.BROWN
    LEVEL_110(2, 110, "110 кВ", Paint.valueOf("#27AE60"), 3), // Color.SEAGREEN
    LEVEL_220(3, 220, "220 кВ", Paint.valueOf("#3498DB"), 4), // Color.DEEPSKYBLUE
    LEVEL_500(4, 500, "500 кВ", Paint.valueOf("#E74C3C"), 5); // Color.RED

    private final int order;
    private final int voltageLevel;
    private final String description;
    private final Paint color;
    private final int boundingArea;

}
