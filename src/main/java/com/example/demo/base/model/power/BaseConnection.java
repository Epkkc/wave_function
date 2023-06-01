package com.example.demo.base.model.power;

import com.example.demo.base.model.enums.VoltageLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class BaseConnection {

    /**
     * Уровень напряжения точки присоединения
     */
    protected VoltageLevel voltageLevel;

    /**
     * Порядковый номер в цепочке соединённых нод
     */
    protected int chainLinkOrder;

    /**
     * Ограничение на максимальное количество присоединений
     */
    protected int limit;
    protected List<NodeLineDto> nodeLineDtos = new ArrayList<>();

    public BaseConnection(VoltageLevel voltageLevel, int limit, int chainLinkOrder) {
        this.voltageLevel = voltageLevel;
        this.limit = limit;
        this.chainLinkOrder = chainLinkOrder;
    }

    public int getConnectedNodes() {
        return nodeLineDtos.size();
    }

    public boolean addConnection(String nodeUuid, String lineUuid) {
        if (getConnectedNodes() < limit && nodeLineDtos.stream().noneMatch(dto->dto.getNodeUuid().equals(nodeUuid))) {
//            connectedUuids.add(nodeUuid);
//            lineUuids.add(lineUuid);
            nodeLineDtos.add(new NodeLineDto(nodeUuid, lineUuid));
            return true;
        }
        return false;
    }

    public boolean removeConnection(String nodeUuid, String lineUuid) {
        if (nodeLineDtos.stream().anyMatch(dto->dto.getNodeUuid().equals(nodeUuid))) {
//            connectedNodeUuids.remove(nodeUuid);
//            lineUuids.remove(lineUuid);
            nodeLineDtos.removeIf(dto -> dto.getNodeUuid().equals(nodeUuid) && dto.getLineUuid().equals(lineUuid));
            return true;
        }
        return false;
    }

}
