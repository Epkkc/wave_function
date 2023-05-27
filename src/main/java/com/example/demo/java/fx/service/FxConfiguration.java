package com.example.demo.java.fx.service;

import com.example.demo.base.service.BaseConfiguration;
import lombok.Getter;

@Getter
public class FxConfiguration extends BaseConfiguration {
    // TODO:SPRING При переходе сделать этот класс как @Property класс
    //  baseSize рассчитывать в elementService, если количество тайлов умноженное на preferredSize меньше, чем размер экрана

    protected final double padding;
    protected final double vGap;
    protected final double hGap;
    protected final double baseSize;

    public FxConfiguration(int rows, int columns, int delay, int numberOfNodes, int numberOfEdges, double padding, double vGap, double hGap, double baseSize) {
        super(rows, columns, delay, numberOfNodes, numberOfEdges);
        this.padding = padding;
        this.vGap = vGap;
        this.hGap = hGap;
        this.baseSize = baseSize;
    }
}
