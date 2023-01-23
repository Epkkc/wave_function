package com.example.demo.model;

import javafx.scene.paint.Paint;


public enum SurfaceType {
    LAND(Paint.valueOf("#0add1c"), 1),
    SHORE(Paint.valueOf("#e8dc26"), 1.5),
    SEA(Paint.valueOf("#00daff"), 1);

    private final Paint color;
    private final double koef;

    private int currentProb;

    SurfaceType(Paint color, double koef) {
        this.color = color;
        this.koef = koef;
    }

    public Paint getColor() {
        return color;
    }

    public double getKoef() {
        return koef;
    }

    public double getCurrentProb() {
        return currentProb;
    }

    public SurfaceType setCurrentProb(int currentProb) {
        this.currentProb = currentProb;
        return this;
    }
}
