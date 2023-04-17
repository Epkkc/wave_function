package com.example.demo.base.model.status;

import com.example.demo.base.model.enums.VoltageLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseStatus {

    protected StatusType type;
    protected Set<VoltageLevel> voltageLevels = new HashSet<>();

    public BaseStatus(StatusType type, VoltageLevel... voltageLevels) {
        this.type = type;
        this.voltageLevels.addAll(Arrays.asList(voltageLevels));
    }

    public void addVoltageLevel(VoltageLevel... voltageLevel) {
        this.voltageLevels.addAll(Arrays.asList(voltageLevel));
    }

    public void addVoltageLevel(Collection<VoltageLevel> voltageLevel) {
        this.voltageLevels.addAll(voltageLevel);
    }

    public void removeVoltageLevel(VoltageLevel voltageLevel) {
        this.voltageLevels.remove(voltageLevel);
    }

    public void removeVoltageLevel(Collection<VoltageLevel> voltageLevels) {
        this.voltageLevels.removeAll(voltageLevels);
    }

}
