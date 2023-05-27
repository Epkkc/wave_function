package com.example.demo.java.fx.factories;

import com.example.demo.java.fx.model.power.FxAbstractPowerNode;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.services.FxElementService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class FxPowerNodeFactory {

    protected final FxElementService elementsService;

    public abstract PowerNodeType getType();

    public abstract FxAbstractPowerNode createNode(int x, int y, int power, VoltageLevel... voltageLevels);


}
