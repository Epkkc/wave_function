package com.example.demo.factories;

import com.example.demo.model.filter.FilterContext;
import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.power.node.PowerNodeType;
import com.example.demo.model.power.node.ThreeWSubStation;
import com.example.demo.model.power.node.TwoWSubStation;
import com.example.demo.model.power.node.VoltageLevel;
import com.example.demo.model.status.PowerNodeStatusMeta;
import com.example.demo.services.ElementServiceImpl;
import com.example.demo.utils.RandomUtils;

import java.util.List;
import java.util.Optional;

public class SubstationFactory extends PowerNodeFactory {

    public SubstationFactory(ElementServiceImpl elementsService) {
        super(elementsService);
    }

    @Override
    public PowerNodeType getType() {
        return PowerNodeType.SUBSTATION;
    }

    @Override
    public PowerNode createNode(int x, int y, int power, VoltageLevel... voltageLevels) {
        PowerNode node = null;
        if (voltageLevels.length == 2) {
            node = new TwoWSubStation(
                elementsService.getBaseSize(),
                power,
                voltageLevels[0],
                voltageLevels[1]
            );
        } else if (voltageLevels.length == 3) {
            node = new ThreeWSubStation(
                elementsService.getBaseSize(),
                power,
                voltageLevels[0],
                voltageLevels[1],
                voltageLevels[2]
            );
        } else {
            throw new UnsupportedOperationException("Invalid voltage size = " + voltageLevels.length);
        }
        node.setX(x);
        node.setY(y);

        return node;
    }

}
