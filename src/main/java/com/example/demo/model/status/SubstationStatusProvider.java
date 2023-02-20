package com.example.demo.model.status;

import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.power.node.PowerNodeType;

import java.util.List;

public class SubstationStatusProvider implements StatusProvider {

    @Override
    public List<StatusMeta> provideStatuses(PowerNode node) {
        return List.of(new StatusMeta(StatusType.BLOCK_SUBSTATION, node.getConnectionPoints().keySet()));
    }

    @Override
    public PowerNodeType getType() {
        return PowerNodeType.SUBSTATION;
    }
}
