package com.example.demo.model.power.node;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public enum VoltageLevel {

    // TODO по-хорошему нужно добавить 10 кВ и трёх трансформаторные ПС, что можно было делать такие конфигурации:
    //  ПС 500/220/110
    //  ПС 500/220
    //  ПС 220/110/35
    //  ПС 220/110
    //  ПС 220/35
    //  ПС 110/35/10
    //  ПС 110/35
    //  ПС 110/10
    //  ПС 35/10
    //  Также можно рассмотреть ЭЧЭ (Тяговая подстанция железной дороги), которые имеют напряжения 110 или 220 кВ. Но рассматривать их как нагрузку или как ПС непонятно.


    LEVEL_10(1, 35, "35 кВ", Paint.valueOf("#E67E22"), 2, 0, false), // Color.BROWN
    LEVEL_35(1, 35, "35 кВ", Paint.valueOf("#E67E22"), 5, 1, false), // Color.BROWN
    LEVEL_110(2, 110, "110 кВ", Paint.valueOf("#27AE60"), 8, 2, true), // Color.SEAGREEN
    LEVEL_220(3, 220, "220 кВ", Paint.valueOf("#3498DB"), 18, 2, true), // Color.DEEPSKYBLUE
    LEVEL_500(4, 500, "500 кВ", Paint.valueOf("#E74C3C"), 35, 1, true); // Color.RED // В случае 3х трансформаторной ПС - шаг получается равным 2

    private final int order;
    private final int voltageLevel;
    private final String description;
    private final Paint color;
    private final int boundingArea;
    private final int gap;
    private final boolean threeWindings;

}
