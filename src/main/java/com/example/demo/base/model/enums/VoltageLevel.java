package com.example.demo.base.model.enums;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public enum VoltageLevel {

    LEVEL_10(10, "10 кВ", Paint.valueOf("#7851A9")), // Color.PURPLE
    LEVEL_35(35, "35 кВ", Paint.valueOf("#E67E22")), // Color.BROWN
    LEVEL_110(110, "110 кВ", Paint.valueOf("#27AE60")), // Color.SEAGREEN
    LEVEL_220(220, "220 кВ", Paint.valueOf("#3498DB")), // Color.DEEPSKYBLUE
    LEVEL_500(500, "500 кВ", Paint.valueOf("#E74C3C")), // Color.RED // В случае 3х трансформаторной ПС - шаг получается равным 2
    LEVEL_750(750, "750 кВ", Color.DARKBLUE); // Color.DARKBLUE

    private final int voltageLevel;
    private final String description;
    private final Paint color;

}
