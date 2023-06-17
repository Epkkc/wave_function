package com.example.demo.java.fx.service;

import com.example.demo.base.service.BaseConfiguration;
import lombok.Getter;

@Getter
public class FxConfiguration extends BaseConfiguration {

    protected final double padding;
    protected final double vGap;
    protected final double hGap;
    protected final double baseSize;

    public FxConfiguration(int rows, int columns, int numberOfNodes, int numberOfEdges, double padding, double vGap, double hGap, double baseSize) {
        super(rows, columns, numberOfNodes, numberOfEdges);
        this.padding = padding;
        this.vGap = vGap;
        this.hGap = hGap;
        this.baseSize = baseSize;
    }
}
