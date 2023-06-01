package com.example.demo.base.model.enums;

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


    LEVEL_10(1, 10, "10 кВ", Paint.valueOf("#7851A9"), 2, 0, false, 500), // Color.ФИОЛЕТОВЫЙ
    LEVEL_35(2, 35, "35 кВ", Paint.valueOf("#E67E22"), 6, 1, false, 1000), // Color.BROWN
    LEVEL_110(3, 110, "110 кВ", Paint.valueOf("#27AE60"), 18, 2, true, 2000), // Color.SEAGREEN
    LEVEL_220(4, 220, "220 кВ", Paint.valueOf("#3498DB"), 35, 2, true, 2000), // Color.DEEPSKYBLUE
    LEVEL_500(5, 500, "500 кВ", Paint.valueOf("#E74C3C"), 60, 1, true, 2000); // Color.RED // В случае 3х трансформаторной ПС - шаг получается равным 2

    private final int order;
    private final int voltageLevel;
    private final String description;
    private final Paint color;
    private final int boundingArea;
    private final int gap;
    private final boolean threeWindings;
    private final int timeout;

}
