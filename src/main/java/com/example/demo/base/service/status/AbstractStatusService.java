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
import com.example.demo.base.model.status.StatusType;
import com.example.demo.base.service.BaseConfiguration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
    public void setTransformerStatusToArea(PNODE powerNode, TransformerConfiguration... configurations) {

        for (TransformerConfiguration configuration : configurations) {
            // Установка blocking статусов
            addStatusAreaTo(powerNode.getX(), powerNode.getY(), configuration.getBoundingAreaFrom(), powerNode.getNodeType().getBlockingStatus(), configuration.getLevel(), true);
            // Установка should статусов
            addRingStatusArea(powerNode.getX(), powerNode.getY(), configuration.getBoundingAreaFrom(), configuration.getBoundingAreaTo(), powerNode.getNodeType().getShouldStatus(), configuration.getLevel(), true);
            // Установка should load статусов
            baseConfiguration.getLoadConfigurations().stream().filter(cfg -> configuration.getLevel().equals(cfg.getLevel())).findFirst().ifPresent(loadConfiguration -> {
                addRingStatusArea(powerNode.getX(), powerNode.getY(), loadConfiguration.getBoundingAreaFrom(), loadConfiguration.getBoundingAreaTo(), StatusType.SHOULD_LOAD, loadConfiguration.getLevel(), true);
            });
        }

        addBaseBlockingStatus(powerNode.getX(), powerNode.getY());
    }

    @Override
    public void setLoadStatusToArea(PNODE powerNode, LoadConfiguration configuration) {

        // Установка blocking статусов
        addStatusAreaTo(powerNode.getX(), powerNode.getY(), configuration.getBoundingAreaFrom(), powerNode.getNodeType().getBlockingStatus(), configuration.getLevel(), true);
        // Установка should статусов
        addRingStatusArea(powerNode.getX(), powerNode.getY(), configuration.getBoundingAreaFrom(), configuration.getBoundingAreaTo(), powerNode.getNodeType().getShouldStatus(), configuration.getLevel(), true);

        addBaseBlockingStatus(powerNode.getX(), powerNode.getY());
    }

    @Override
    public void setGeneratorStatusToArea(PNODE powerNode, GeneratorConfiguration configuration) {
        // Установка blocking статусов
        addStatusAreaTo(powerNode.getX(), powerNode.getY(), configuration.getBoundingArea(), powerNode.getNodeType().getBlockingStatus(), configuration.getLevel(), true);

        addBaseBlockingStatus(powerNode.getX(), powerNode.getY());
    }


    // todo доработать этот метод, используя StatusDto
    private void addRingStatusArea(int x, int y, int boundingAreaFrom, int boundingAreaTo, StatusType statusType, VoltageLevel voltageLevel, boolean rounded) {
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
                node.addStatus(statusType, voltageLevel);
            });
    }

    // todo доработать этот метод, используя StatusDto
    private void addStatusAreaTo(int x, int y, int boundingAreaTo, StatusType statusType, VoltageLevel voltageLevel, boolean rounded) {
        matrix.getArea(x, y, boundingAreaTo).stream()
            .filter(node -> node.getNodeType().equals(PowerNodeType.EMPTY))
            .forEach(node -> {
                if (rounded) {
                    // Отбрасываем все ноды, которые выходят за зону
                    if (sqrt(pow(node.getX() - x, 2) + pow(node.getY() - y, 2)) > (boundingAreaTo)) {
                        return;
                    }
                }
                node.addStatus(statusType, voltageLevel);
            });
    }

    private void addBaseBlockingStatus(int x, int y) {
        // Добавляем запрет на расстановку рядом любых объектов
        matrix.getArea(x, y).forEach(node -> PowerNodeType.getValidValues().forEach(
            pnt -> node.addStatus(pnt.getBlockingStatus(), VoltageLevel.values()))
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
