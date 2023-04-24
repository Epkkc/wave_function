package com.example.demo.base.model.power;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.status.StatusType;

import java.util.List;

public interface PowerNode {

    PowerNodeType getNodeType();
    int getPower();
    String getUuid();
    List<VoltageLevel> getVoltageLevels();
    void setNodeType(PowerNodeType nodeType);
    void setPower(int power);
    void addStatus(StatusType statusType, VoltageLevel... voltageLevels);


}
