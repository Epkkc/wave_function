package com.example.demo.base.model.power;

import com.example.demo.base.model.grid.Coordinates;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.status.BaseStatus;
import com.example.demo.base.model.status.BlockType;
import com.example.demo.base.model.status.StatusLevelChainLinkDto;
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
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public abstract class AbstractPowerNode<STATUS extends BaseStatus, CONNECTION extends BaseConnection> implements Coordinates {

    protected PowerNodeType nodeType;
    protected int x;
    protected int y;
    protected int power;
    protected int chainLinkOrder;
    protected String uuid = UUID.randomUUID().toString();
    protected List<VoltageLevel> voltageLevels;
    protected List<STATUS> statuses;
    protected Map<VoltageLevel, CONNECTION> connections;

    public AbstractPowerNode(PowerNodeType nodeType, int x, int y, int power, int chainLinkOrder, Collection<VoltageLevel> voltageLevels) {
        this.nodeType = nodeType;
        this.x = x;
        this.y = y;
        this.power = power;
        this.chainLinkOrder = chainLinkOrder;
        this.voltageLevels = voltageLevels.stream().toList();
        this.statuses = new ArrayList<>();
        this.connections = new HashMap<>();
        initConnections();
    }

    protected abstract void initConnections();

    public void addStatus(StatusType statusType, Collection<StatusLevelChainLinkDto> statusDtos) {
        Collection<VoltageLevel> levels = statusDtos.stream().map(StatusLevelChainLinkDto::getVoltageLevel).toList();
        Optional<STATUS> existed = statuses.stream().filter(status -> status.getType().equals(statusType)).findFirst();
        Optional<STATUS> opposite = statuses.stream()
            .filter(status -> status.getType().getNodeType().equals(statusType.getNodeType()) && !status.getType().getBlockType().equals(statusType.getBlockType()))
            .findFirst();

        if (opposite.isPresent()) {
            if (BlockType.BLOCK.equals(statusType.getBlockType())) {
                Collection<VoltageLevel> finalLevels = levels;
                opposite.ifPresent(opp -> opp.removeVoltageLevels(finalLevels.stream().toList()));
            } else {
                // SHOULD Оставляю только те уровни напряжения, которых нет в блокирующем статусе
                Collection<VoltageLevel> finalLevels1 = levels;
                levels = opposite.map(opp -> CollectionUtils.subtract(finalLevels1.stream().toList(), opp.getVoltageLevels())).orElse(List.of());
            }
        }

        if (levels.isEmpty()) return;

        Collection<VoltageLevel> finalLevels = levels;
        Collection<StatusLevelChainLinkDto> finalStatusDtos = statusDtos.stream().filter(dto -> finalLevels.contains(dto.getVoltageLevel())).toList();

        existed.ifPresentOrElse(
            ex -> updateExistedStatus(ex, finalStatusDtos),
            () -> statuses.add(getStatus(statusType, finalStatusDtos))
        );

        // Удаляем статусы, в которых нет ни одного voltageLevel-а
        statuses.removeIf(status -> status.getVoltageLevels().isEmpty());
    }

    protected void updateExistedStatus(STATUS existed, Collection<StatusLevelChainLinkDto> statusDtos) {
        for (StatusLevelChainLinkDto statusDto : statusDtos) {
            existed.getVoltageLevelChainLinkHashMap().merge(statusDto.getVoltageLevel(),
                statusDto, (s1, s2) -> s1.getChainLinkOrder() < s2.getChainLinkOrder() ? s1 : s2);
        }
//        if (existed.getChainLinkOrder() > chainLinkOrder) { // todo удалить
//            existed.setChainLinkOrder(chainLinkOrder);
//            existed.setNodeUuid(nodeUuid);
//        }
    }

    protected abstract STATUS getStatus(StatusType statusType, Collection<StatusLevelChainLinkDto> statusDtos);
}
