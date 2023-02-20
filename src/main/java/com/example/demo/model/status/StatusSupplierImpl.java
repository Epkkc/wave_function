package com.example.demo.model.status;

import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.power.node.PowerNodeType;
import com.example.demo.model.power.node.VoltageLevel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class StatusSupplierImpl implements StatusSupplier {

    private Map<PowerNodeType, StatusProvider> statusProviderMap;

    {
        statusProviderMap = new HashMap<>();
        statusProviderMap.put(PowerNodeType.SUBSTATION, new SubstationStatusProvider());
    }

    @Override
    public Set<PowerNodeStatusMeta> getAllPowerNodeStatuses() {
        Set<PowerNodeStatusMeta> result = new HashSet<>();
        Set<VoltageLevel> allPossibleVoltages = Arrays.stream(VoltageLevel.values()).collect(Collectors.toSet());

        // TODO раскоментить, когда будут готовы все PowerNodeType-ы
//        for (PowerNodeType type : PowerNodeType.values()) {
//            result.add(new PowerNodeStatusMeta(type, allPossibleVoltages));
//        }

        result.add(new PowerNodeStatusMeta(PowerNodeType.SUBSTATION, allPossibleVoltages));

        return result;
    }

    public List<StatusMeta> getStatusByNode(PowerNode node) {
        return statusProviderMap.get(node.getNodeType()).provideStatuses(node);
    }

}
