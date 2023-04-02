package com.example.demo.factories;

import com.example.demo.model.filter.FilterContext;
import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.power.node.PowerNodeType;
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
    public Optional<PowerNode> createNode(FilterContext context) {
        PowerNodeStatusMeta status = findStatus(context);

        if (status.voltageLevels().size() >= 2) {
            PowerNode node = context.node();

            List<VoltageLevel> voltageLevels = RandomUtils.randomUniqueValues(List.copyOf(status.voltageLevels()), 2);

            TwoWSubStation twoWSubStation = new TwoWSubStation(
                elementsService.getBaseSize(),
                voltageLevels.get(0),
                voltageLevels.get(1)
            );
            twoWSubStation.setX(node.getX());
            twoWSubStation.setY(node.getY());

            return Optional.of(twoWSubStation);
        } else {
            return Optional.empty();
        }
    }

    public PowerNode createNode(PowerNode node, VoltageLevel voltageLevel1, VoltageLevel voltageLevel2) {
        TwoWSubStation twoWSubStation = new TwoWSubStation(
            elementsService.getBaseSize(),
            voltageLevel1,
            voltageLevel2
        );
        twoWSubStation.setX(node.getX());
        twoWSubStation.setY(node.getY());

        return twoWSubStation;
    }
}
