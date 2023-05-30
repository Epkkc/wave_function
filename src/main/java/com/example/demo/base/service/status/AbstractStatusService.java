package com.example.demo.base.service.status;

import com.example.demo.base.model.configuration.GeneratorConfiguration;
import com.example.demo.base.model.configuration.LoadConfiguration;
import com.example.demo.base.model.configuration.TransformerConfiguration;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.AbstractPowerNode;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.status.BaseStatus;
import com.example.demo.base.model.status.StatusMetaDto;
import com.example.demo.base.model.status.StatusType;
import com.example.demo.base.service.BaseConfiguration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

@RequiredArgsConstructor
@Getter
public abstract class AbstractStatusService<PNODE extends AbstractPowerNode<? extends BaseStatus, ? extends BaseConnection>> implements StatusService<PNODE> {

    protected final Matrix<PNODE> matrix;
    protected final BaseConfiguration baseConfiguration;
    protected final boolean roundedArea;

    @Override
    public void setTransformerStatusToArea(PNODE powerNode, List<TransformerConfiguration> transformerConfigurations) {
        int maxLevel = transformerConfigurations.stream()
            .map(TransformerConfiguration::getLevel)
            .map(VoltageLevel::getVoltageLevel)
            .reduce(Integer::max)
            .orElseThrow(
                () -> new UnsupportedOperationException(String.format("Unable to find max voltage level among transformer configurations=%s", transformerConfigurations))
            );

        for (TransformerConfiguration configuration : transformerConfigurations) {
            // Установка blocking статусов
            addStatusAreaTo(
                powerNode.getX(), powerNode.getY(),
                configuration.getBoundingAreaFrom(),
                powerNode.getNodeType().getBlockingStatus(),
                configuration.getLevel(),
                true, 0,
                powerNode.getUuid()
            );

            // Установка should статусов
            // Устанавливаем SHOULD_TRANSFORMER статусы только если powerNode.chainLinkOrder < configuration.maxChainLength
            // (Если = то у следующих элементов уже будет +1, что превысит лимит maxChainLength)
            // Высшему классу напряжения присваиваем номер цепочки в ноде + 1, остальным (низким) присваиваем номер цепочки = 1
            if (powerNode.getChainLinkOrder() < configuration.getMaxChainLength()) {
                int chainLinkOrder = configuration.getLevel().getVoltageLevel() == maxLevel ? powerNode.getChainLinkOrder() + 1 : 1;

                addRingStatusArea(powerNode.getX(), powerNode.getY(),
                    configuration.getBoundingAreaFrom(),
                    configuration.getBoundingAreaTo(),
                    powerNode.getNodeType().getShouldStatus(),
                    configuration.getLevel(),
                    true, chainLinkOrder,
                    powerNode.getUuid()
                );
            }

            // Установка SHOULD_LOAD статусов
            baseConfiguration.getLoadConfigurations()
                .stream()
                .filter(cfg -> configuration.getLevel().equals(cfg.getLevel()))
                .findFirst()
                .ifPresent(loadConfiguration -> {
                    addRingStatusArea(
                        powerNode.getX(), powerNode.getY(),
                        loadConfiguration.getBoundingAreaFrom(),
                        loadConfiguration.getBoundingAreaTo(),
                        StatusType.SHOULD_LOAD,
                        loadConfiguration.getLevel(),
                        true, 1,
                        powerNode.getUuid());
                });
        }

        addBaseBlockingStatus(powerNode.getX(), powerNode.getY(), powerNode.getUuid());
    }

    @Override
    public void setLoadStatusToArea(PNODE powerNode, LoadConfiguration configuration) {

        // Установка blocking статусов
        addStatusAreaTo(
            powerNode.getX(), powerNode.getY(),
            configuration.getBoundingAreaFrom(),
            powerNode.getNodeType().getBlockingStatus(),
            configuration.getLevel(),
            true, 0,
            powerNode.getUuid()
        );

        // Установка should статусов
        if (powerNode.getChainLinkOrder() < configuration.getMaxChainLength()) {
            addRingStatusArea(
                powerNode.getX(), powerNode.getY(),
                configuration.getBoundingAreaFrom(),
                configuration.getBoundingAreaTo(),
                powerNode.getNodeType().getShouldStatus(),
                configuration.getLevel(),
                true, powerNode.getChainLinkOrder() + 1,
                powerNode.getUuid()
            );
        }

        addBaseBlockingStatus(powerNode.getX(), powerNode.getY(), powerNode.getUuid());
    }

    @Override
    public void setGeneratorStatusToArea(PNODE powerNode, GeneratorConfiguration configuration) {
        // Установка blocking статусов
        addStatusAreaTo(
            powerNode.getX(), powerNode.getY(),
            configuration.getBoundingArea(),
            powerNode.getNodeType().getBlockingStatus(),
            configuration.getLevel(),
            true, 0,
            powerNode.getUuid()
        );

        addBaseBlockingStatus(powerNode.getX(), powerNode.getY(), powerNode.getUuid());
    }


    private void addRingStatusArea(int x, int y, int boundingAreaFrom, int boundingAreaTo, StatusType statusType, VoltageLevel voltageLevel, boolean rounded, int chainLinkOrder,
                                   String nodeUuid, Integer availablePower) {
        matrix.getArea(x, y, boundingAreaTo).stream()
            .filter(node -> node.getNodeType().equals(PowerNodeType.EMPTY))
            .forEach(node -> {
                if (rounded) {
                    // Отбрасываем все ноды, которые выходят за зону
                    if (sqrt(pow(node.getX() - x, 2) + pow(node.getY() - y, 2)) <= (boundingAreaFrom) ||
                        sqrt(pow(node.getX() - x, 2) + pow(node.getY() - y, 2)) > (boundingAreaTo)
                    ) {
                        return;
                    }
                } else {
                    if ((Math.abs(node.getX() - x) <= boundingAreaFrom || Math.abs(node.getX() - x) > boundingAreaTo) &&
                        (Math.abs(node.getY() - y) <= boundingAreaFrom || Math.abs(node.getY() - y) > boundingAreaTo)
                    ) {
                        return;
                    }
                }
                node.addStatus(statusType, List.of(new StatusMetaDto(voltageLevel, chainLinkOrder, nodeUuid, availablePower)));
            });
    }

    private void addRingStatusArea(int x, int y, int boundingAreaFrom, int boundingAreaTo, StatusType statusType, VoltageLevel voltageLevel, boolean rounded, int chainLinkOrder,
                                   String nodeUuid) {
        addRingStatusArea(x, y, boundingAreaFrom, boundingAreaTo, statusType, voltageLevel, rounded, chainLinkOrder, nodeUuid, null);
    }

    // todo доработать этот метод, используя StatusDto
    private void addStatusAreaTo(int x, int y, int boundingAreaTo, StatusType statusType, VoltageLevel voltageLevel, boolean rounded, int chainLinkOrder, String nodeUuid, Integer availablePower) {
        matrix.getArea(x, y, boundingAreaTo).stream()
            .filter(node -> node.getNodeType().equals(PowerNodeType.EMPTY))
            .forEach(node -> {
                if (rounded) {
                    // Отбрасываем все ноды, которые выходят за зону
                    if (sqrt(pow(node.getX() - x, 2) + pow(node.getY() - y, 2)) > (boundingAreaTo)) {
                        return;
                    }
                }
                node.addStatus(statusType, List.of(new StatusMetaDto(voltageLevel, chainLinkOrder, nodeUuid, availablePower)));
            });
    }

    private void addStatusAreaTo(int x, int y, int boundingAreaTo, StatusType statusType, VoltageLevel voltageLevel, boolean rounded, int chainLinkOrder, String nodeUuid) {
        addStatusAreaTo(x, y, boundingAreaTo, statusType, voltageLevel, rounded, chainLinkOrder, nodeUuid, null);
    }

        private void addBaseBlockingStatus(int x, int y, String nodeUuid) {
        matrix.getArea(x, y).forEach(node -> PowerNodeType.getValidValues().forEach(
            pnt -> {
                List<StatusMetaDto> res = new ArrayList<>();
                for (VoltageLevel level : VoltageLevel.values()) {
                    res.add(new StatusMetaDto(level, 0, nodeUuid));
                }
                node.addStatus(pnt.getBlockingStatus(), res);
            })
        );
    }


    private void findStatusesWithEmptyVoltages() {
        List<PNODE> powerNodes = matrix.toNodeList()
            .stream()
            .filter(node -> node.getStatuses().stream().anyMatch(status -> status.getVoltageLevels().isEmpty()))
            .toList();
        System.out.println(powerNodes);
    }

}
