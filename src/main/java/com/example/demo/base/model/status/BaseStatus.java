package com.example.demo.base.model.status;

import com.example.demo.base.model.enums.VoltageLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
public class BaseStatus {

    protected StatusType type;
    protected Map<VoltageLevel, StatusMetaDto> voltageLevelChainLinkHashMap = new HashMap<>();

    public BaseStatus(StatusType type, Collection<StatusMetaDto> statusDtos) {
        this.type = type;
        statusDtos.forEach(dto -> voltageLevelChainLinkHashMap.put(dto.getVoltageLevel(), dto));
    }

    public Set<VoltageLevel> getVoltageLevels() {
        return voltageLevelChainLinkHashMap.keySet();
    }

    public void removeVoltageLevels(Collection<VoltageLevel> voltageLevels) {
        voltageLevels.forEach(level -> voltageLevelChainLinkHashMap.remove(level));
    }

}
