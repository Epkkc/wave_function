package com.example.demo.base.model.power;

import com.example.demo.base.model.enums.VoltageLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class BaseConnection implements Connection {

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

    public BaseConnection(VoltageLevel voltageLevel, int limit) {
        this.voltageLevel = voltageLevel;
        this.limit = limit;
    }

    @Override
    public int getConnections() {
        return connectedUuids.size();
    }

    public boolean addConnection(String uuid) {
        if (getConnections() < limit && !connectedUuids.contains(uuid)) {
            connectedUuids.add(uuid);
            return true;
        }
        return false;
    }

    public boolean removeConnection(String uuid) {
        if (connectedUuids.contains(uuid)) {
            connectedUuids.remove(uuid);
            return true;
        }
        return false;
    }

}
