package com.example.demo.model.status;

import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.power.node.PowerNodeType;

import java.util.List;

// TODO вынести supplier-ы в один абстрактный класс, внутри которого будут статические классы на каждый из
public class CommonStatusProvider implements StatusProvider {

    @Override
    public List<StatusMeta> provideStatuses(PowerNode node) {
        return List.of(new StatusMeta(node.getNodeType().getBlockingStatus(), node.getConnectionPoints().keySet()));
    }

    @Override
    public PowerNodeType getType() {
        return PowerNodeType.SUBSTATION;
    }
}
