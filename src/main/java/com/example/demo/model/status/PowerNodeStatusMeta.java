package com.example.demo.model.status;

import com.example.demo.model.power.node.PowerNodeType;
import com.example.demo.model.power.node.VoltageLevel;
import lombok.Builder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

@Builder
public record PowerNodeStatusMeta(
    PowerNodeType powerNodeType,
    Set<VoltageLevel> voltageLevels
) {

    public void addVoltageLevel(VoltageLevel... voltageLevel) {
        this.voltageLevels.addAll(Arrays.asList(voltageLevel));
    }

    public void removeVoltageLevel(VoltageLevel voltageLevel) {
        this.voltageLevels.remove(voltageLevel);
    }

    public void removeVoltageLevel(Collection<VoltageLevel> voltageLevels) {
        this.voltageLevels.removeAll(voltageLevels);
    }
}
