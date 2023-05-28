package com.example.demo.base.service.connection;

import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.power.BaseLine;
import com.example.demo.base.model.power.BasePowerNode;
import com.example.demo.base.service.element.BaseElementService;

public class BaseConnectionService extends AbstractConnectionService<BasePowerNode, BaseLine, BaseElementService> {

    public BaseConnectionService(BaseElementService elementService) {
        super(elementService);
    }

    @Override
    protected BaseLine getLine(BasePowerNode node1, BasePowerNode node2, VoltageLevel voltageLevel, boolean breaker) {
        return new BaseLine(node1, node2, voltageLevel, breaker);
    }
}
