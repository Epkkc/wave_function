package com.example.demo.base.model.power;

import com.example.demo.base.model.grid.Coordinates;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.status.BaseStatus;
import com.example.demo.base.model.status.BlockType;
import com.example.demo.base.model.status.StatusType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Data
@NoArgsConstructor
public class BasePowerNode implements Coordinates, PowerNode {

    protected PowerNodeType nodeType;
    protected int x;
    protected int y;
    protected int power;
    protected String uuid = UUID.randomUUID().toString();
    protected List<VoltageLevel> voltageLevels;
    protected List<BaseStatus> statuses;
    protected Map<VoltageLevel, Connection> connections;

    public BasePowerNode(PowerNodeType nodeType, int x, int y, int power, Collection<VoltageLevel> voltageLevels) {
        this.nodeType = nodeType;
        this.x = x;
        this.y = y;
        this.power = power;
        this.voltageLevels = voltageLevels.stream().toList();
        this.statuses = new ArrayList<>();
        this.connections = new HashMap<>();
        voltageLevels.forEach(level -> connections.put(level, new BaseConnection(level, 0, 2)));
    }

    public void addStatus(StatusType statusType, VoltageLevel... voltageLevels) {
        Collection<VoltageLevel> levels = List.of(voltageLevels);
        Optional<BaseStatus> existed = statuses.stream().filter(status -> status.getType().equals(statusType)).findFirst();
        Optional<BaseStatus> opposite = statuses.stream()
            .filter(status -> status.getType().getNodeType().equals(statusType.getNodeType()) && !status.getType().getBlockType().equals(statusType.getBlockType()))
            .findFirst();

        if (opposite.isPresent()) {
            if (BlockType.BLOCK.equals(statusType.getBlockType())) {
                Collection<VoltageLevel> finalLevels = levels;
                opposite.ifPresent(opp -> opp.removeVoltageLevel(finalLevels.stream().toList()));
            } else {
                // SHOULD Оставляю только те уровни напряжения, которых нет в блокирующем статусе
                Collection<VoltageLevel> finalLevels1 = levels;
                levels = opposite.map(opp -> CollectionUtils.subtract(finalLevels1.stream().toList(), opp.getVoltageLevels())).orElse(List.of());
            }
        }

        if (levels.isEmpty()) return;

        Collection<VoltageLevel> finalLevels2 = levels;
        existed.ifPresentOrElse(
            ex -> ex.addVoltageLevel(finalLevels2.stream().toList()),
            () ->statuses.add(new BaseStatus(statusType, voltageLevels))
        );

        // Удаляем статусы, в которых нет ни одного voltageLevel-а
        statuses.removeIf(status -> status.getVoltageLevels().isEmpty());
    }
}
