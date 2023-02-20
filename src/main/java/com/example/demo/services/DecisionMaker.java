package com.example.demo.services;

import com.example.demo.factories.PowerNodeFactory;
import com.example.demo.factories.SubstationFactory;
import com.example.demo.model.filter.FilterContext;
import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.power.node.PowerNodeType;
import com.example.demo.model.status.PowerNodeStatusMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class DecisionMaker {

    // TODO:SPRING При переходе на spring, необходимо будет все factory классы сделать сервисами, и заполнять эту мапу автоматически
    private final HashMap<PowerNodeType, PowerNodeFactory> factoriesMap = new HashMap<>();
    private final Random random = new Random();


    public DecisionMaker(ElementServiceImpl elementsService) {
        // TODO:SPRING От этого конструктора можно будет отказаться, потому что elementService будет автоваириться в SubstationFactory
        //  также, поскольку HashMap будет заполняться автоматически
        factoriesMap.put(PowerNodeType.SUBSTATION, new SubstationFactory(elementsService));
    }

    public Optional<PowerNode> makeDecision(FilterContext context) {
        Set<PowerNodeStatusMeta> possibleStatuses = context.possibleStatuses();
        if (possibleStatuses.isEmpty()) return Optional.empty();


        List<PowerNode> possibleNodes = new ArrayList<>();
        for (PowerNodeStatusMeta status : possibleStatuses) {
            factoriesMap.get(status.powerNodeType()).createNode(context).ifPresent(possibleNodes::add);
        }
        if (possibleNodes.size() == 0) {
            return Optional.empty();
        }

        int i = random.nextInt(possibleNodes.size());

        possibleNodes.get(i).addStatuses(context.node().getStatusMetas(), false);

        return Optional.of(possibleNodes.get(i));
    }

}
