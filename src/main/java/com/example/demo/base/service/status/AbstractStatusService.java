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
import com.example.demo.base.service.ConfigurationStaticSupplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

@RequiredArgsConstructor
@Getter
public abstract class AbstractStatusService<PNODE extends AbstractPowerNode<? extends BaseStatus, ? extends BaseConnection>> implements StatusService<PNODE> {

    protected final Matrix<PNODE> matrix;
    protected final BaseConfiguration baseConfiguration;

    @Override
    public void setTransformerStatusToArea(PNODE powerNode, List<TransformerConfiguration> transformerConfigurations) {
        for (TransformerConfiguration configuration : transformerConfigurations) {
            // Установка BLOCK_SUBSTATION статусов

            addStatusAreaTo(
                powerNode.getX(), powerNode.getY(),
                configuration.getBoundingAreaFrom(),
                powerNode.getNodeType().getBlockingStatus(),
                configuration.getLevel(),
                configuration.isRoundedBoundingArea(), 0,
                powerNode.getUuid()
            );

            // Установка SHOULD_SUBSTATION статусов
            // Устанавливаем SHOULD_TRANSFORMER статусы только если powerNode.chainLinkOrder < configuration.maxChainLength
            // (Если = то у следующих элементов уже будет +1, что превысит лимит maxChainLength)
            // Высшему классу напряжения присваиваем номер цепочки в ноде + 1, остальным (низким) присваиваем номер цепочки = 1
            if (getChainLinkOrder(powerNode, configuration.getLevel()) < configuration.getMaxChainLength() && !configHasMinVoltage(configuration.getLevel())) {
                int chainLinkOrder = getChainLinkOrder(powerNode, configuration.getLevel()) + 1;

                addRingStatusArea(powerNode.getX(), powerNode.getY(),
                    configuration.getBoundingAreaFrom(),
                    configuration.getBoundingAreaTo(),
                    powerNode.getNodeType().getShouldStatus(),
                    configuration.getLevel(),
                    configuration.isRoundedBoundingArea(),
                    chainLinkOrder,
                    powerNode.getUuid()
                );
            }

            // Установка SHOULD_LOAD статусов

            LoadConfiguration loadConfiguration = baseConfiguration.getLoadConfiguration(configuration.getLevel());
            if (loadConfiguration != null && loadConfiguration.isEnabled()) {
                addRingStatusArea(
                    powerNode.getX(), powerNode.getY(),
                    loadConfiguration.getBoundingAreaFrom(),
                    loadConfiguration.getBoundingAreaTo(),
                    StatusType.SHOULD_LOAD,
                    loadConfiguration.getLevel(),
                    loadConfiguration.isRoundedBoundingArea(), 1,
                    powerNode.getUuid()
                );
            }

            // Установка SHOULD_GENERATOR статусов

            GeneratorConfiguration generatorConfiguration = baseConfiguration.getGeneratorConfiguration(configuration.getLevel());
            if (generatorConfiguration != null && generatorConfiguration.isEnabled()) {
                addRingStatusArea(
                    powerNode.getX(), powerNode.getY(),
                    generatorConfiguration.getBoundingAreaFrom(),
                    generatorConfiguration.getBoundingAreaTo(),
                    StatusType.SHOULD_GENERATOR,
                    generatorConfiguration.getLevel(),
                    generatorConfiguration.isRoundedBoundingArea(), 1,
                    powerNode.getUuid()
                );
            }
        }

        addBaseBlockingStatus(powerNode.getX(), powerNode.getY(), powerNode.getUuid());
    }

    private boolean configHasMinVoltage(VoltageLevel voltageLevel) {
        Integer min = baseConfiguration.getTransformerConfigurations().keySet().stream().map(VoltageLevel::getVoltageLevel).min(Comparator.comparingInt(level -> level)).orElseThrow(() -> new UnsupportedOperationException("Unable to find mimimum voltage level for transformer configurations"));
        return voltageLevel.getVoltageLevel() == min;
    }

    @Override
    public void setLoadStatusToArea(PNODE powerNode, LoadConfiguration configuration) {

        // Установка blocking статусов
        addStatusAreaTo(
            powerNode.getX(), powerNode.getY(),
            configuration.getBoundingAreaFrom(),
            powerNode.getNodeType().getBlockingStatus(),
            configuration.getLevel(),
            configuration.isRoundedBoundingArea(), 0,
            powerNode.getUuid()
        );

        // Установка should статусов
        if (getChainLinkOrder(powerNode, configuration.getLevel()) < configuration.getMaxChainLength()) {
            addRingStatusArea(
                powerNode.getX(), powerNode.getY(),
                configuration.getBoundingAreaFrom(),
                configuration.getBoundingAreaTo(),
                powerNode.getNodeType().getShouldStatus(),
                configuration.getLevel(),
                configuration.isRoundedBoundingArea(), getChainLinkOrder(powerNode, configuration.getLevel()) + 1,
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
            configuration.getBoundingAreaFrom(),
            powerNode.getNodeType().getBlockingStatus(),
            configuration.getLevel(),
            configuration.isRoundedBoundingArea(), 0,
            powerNode.getUuid()
        );

        addBaseBlockingStatus(powerNode.getX(), powerNode.getY(), powerNode.getUuid());
    }

    @Override
    public void removeStatusesByNodeUuid(String uuid) {
        matrix.toNodeList().forEach(
            node -> {
                node.getStatuses().forEach(status -> {
                        List<VoltageLevel> levelsForRemove = new ArrayList<>();
                        status.getVoltageLevelChainLinkHashMap().values().stream().forEach(
                            (meta) -> {
                                if (meta.getNodeUuid().equals(uuid)) {
                                    // Этот статус был порождён нодой uuid
                                    levelsForRemove.add(meta.getVoltageLevel());
                                }
                            }
                        );
                        status.removeVoltageLevels(levelsForRemove);
                    }
                );

                node.tryToRemoveStatuses();
            }
        );
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

    private void addStatusAreaTo(int x, int y, int boundingAreaTo, StatusType statusType, VoltageLevel voltageLevel, boolean rounded, int chainLinkOrder, String nodeUuid,
                                 Integer availablePower) {
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
        matrix.getArea(x, y, baseConfiguration.getBaseBlockingStatusConfiguration().getRadius()).forEach(node ->
            PowerNodeType.getValidValues().forEach(
                pnt -> {
                    if (baseConfiguration.getBaseBlockingStatusConfiguration().isRounded()) {
                        // Отбрасываем все ноды, которые выходят за зону
                        if (sqrt(pow(node.getX() - x, 2) + pow(node.getY() - y, 2)) > (baseConfiguration.getBaseBlockingStatusConfiguration().getRadius())) {
                            return;
                        }
                    }
                    List<StatusMetaDto> res = new ArrayList<>();
                    for (VoltageLevel level : VoltageLevel.values()) {
                        res.add(new StatusMetaDto(level, 0, nodeUuid));
                    }
                    node.addStatus(pnt.getBlockingStatus(), res);
                })
        );
    }

    private int getChainLinkOrder(PNODE node, VoltageLevel voltageLevel) {
        return node.getConnections().get(voltageLevel).getChainLinkOrder();
    }

}
