package com.example.demo.model.status;

import com.example.demo.model.power.node.VoltageLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@SuperBuilder
public class StatusMeta {

    protected StatusType type;
    protected Set<VoltageLevel> voltageLevels = new HashSet<>();


    public StatusMeta(StatusType type, VoltageLevel... voltageLevels) {
        this.type = type;
        this.voltageLevels.addAll(Arrays.asList(voltageLevels));
    }

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
