package com.example.demo.base.model.power;

import com.example.demo.base.model.enums.VoltageLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
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

    protected Set<String> connectedUuids = new HashSet<>();
    protected Set<String> lineUuids = new HashSet<>();

    public BaseConnection(VoltageLevel voltageLevel, int limit, int chainLinkOrder) {
        this.voltageLevel = voltageLevel;
        this.limit = limit;
        this.chainLinkOrder = chainLinkOrder;
    }

    public int getConnections() {
        return connectedUuids.size();
    }

    public boolean addConnection(String nodeUuid, String lineUuid) {
        if (getConnections() < limit && !connectedUuids.contains(nodeUuid)) {
            connectedUuids.add(nodeUuid);
            lineUuids.add(lineUuid);
            return true;
        }
        return false;
    }

    public boolean removeConnection(String nodeUuid, String lineUuid) {
        if (connectedUuids.contains(nodeUuid)) {
            connectedUuids.remove(nodeUuid);
            lineUuids.remove(lineUuid);
            return true;
        }
        return false;
    }

}
