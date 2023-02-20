package com.example.demo.services.filters;

import com.example.demo.model.filter.FilterContext;
import com.example.demo.model.power.node.PowerNode;

public class SimpleExcludeStatusFilter implements Filter {

    @Override
    public void filter(FilterContext context) {
        PowerNode node = context.node();

        node.getBasePane().getStatusPane().getStatuses().forEach(status -> {
            context.possibleStatuses().stream()
                .filter(posStatus -> posStatus.powerNodeType().equals(status.getType().getNodeType()))
                // TODO добавить логику обработки BlockType (Пока по дефолту обрабатывается как BLOCK)
                .forEach(posStatus -> posStatus.removeVoltageLevel(status.getVoltageLevels()));
        });
    }
}
