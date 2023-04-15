package com.example.demo.services;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Configuration {
    // TODO:SPRING При переходе сделать этот класс как @Property класс
    //  baseSize рассчитывать в elementService, если количество тайлов умноженное на preferredSize меньше, чем размер экрана

    // TODO здесь хранить всякую конфигурационную инфу, или ту, которая высчитывается в начале работы приложения,
    //  например:
    //  delay - задержка в миллисекундах
    //  baseSize - поле size - базовый размер каждого тайла, от которого рассчитываются все остальные размеры
    //  padding - padding главной gridPane
    //  vGap - отступ по вертикали между тайлами
    //  hGap - отступ по горизонтали между тайлами
    //  может быть ещё:
    //  rowsNumber - количество рядов тайлов
    //  columnsNumber - количество колонок тайлов

    private double padding = 2d;
    private double vGap = 4d;
    private double hGap = 4d;
    private int rows = 10;
    private int columns = 20;
    private double baseSize = 45;
    private int delay = 20000;

    public Configuration(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
    }

}
