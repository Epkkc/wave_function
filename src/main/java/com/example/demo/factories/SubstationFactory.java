package com.example.demo.factories;

import com.example.demo.model.filter.FilterContext;
import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.power.node.PowerNodeType;
import com.example.demo.model.power.node.SubStation;
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
        PowerNodeStatusMeta status = context.possibleStatuses()
            .stream()
            .filter(s -> getType().equals(s.powerNodeType()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException(String.format("Не найден статус с type=%s\ncontext=%s", getType(), context)));


        if (status.voltageLevels().size() >= 2) {
            PowerNode node = context.node();

            List<VoltageLevel> voltageLevels = RandomUtils.randomUniqueValues(List.copyOf(status.voltageLevels()), 2);

            SubStation subStation = new SubStation(
                elementsService.getBaseSize(),
                voltageLevels.get(0),
                voltageLevels.get(1)
            );
            subStation.setX(node.getX());
            subStation.setY(node.getY());

            return Optional.of(subStation);
        } else {
            return Optional.empty();
        }
    }
}
