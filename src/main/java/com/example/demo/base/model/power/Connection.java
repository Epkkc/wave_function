package com.example.demo.base.model.power;

import com.example.demo.base.model.enums.VoltageLevel;

public interface Connection {

    VoltageLevel getVoltageLevel();

    int getConnections();

    int getLimit();

    boolean addConnection();

}
