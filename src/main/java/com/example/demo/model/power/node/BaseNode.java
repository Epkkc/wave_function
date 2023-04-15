package com.example.demo.model.power.node;

public class BaseNode extends PowerNode {

    public BaseNode(double size) {
        super(size, PowerNodeType.EMPTY, 0);
    }

    @Override
    protected void setOpacity(VoltageLevel voltageLevel, double value) {}
}
