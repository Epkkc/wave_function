package com.example.demo.java.fx.model.visual;

import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.status.BlockType;
import com.example.demo.java.fx.model.status.FxStatus;
import com.example.demo.base.model.status.StatusType;
import com.example.demo.base.model.enums.VoltageLevel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Data
public class StatusPane {

    private HBox hbox;

//    private Matrix<FxStatus> statusMatrix;

    private int limitInOneRow = 7;

    private int numberOfElements = 0;

    private final double size;

    public StatusPane(double size) {
        createHBox();
        this.size = size * 15 / 200;
//        this.statusMatrix = new Matrix<>(1, limitInOneRow);
    }

    private void createHBox() {
        // TODO перенести в параметры конструктора и привязать к параметру size
        double value = 0.5d;
        hbox = new HBox();
        hbox.setAlignment(Pos.BOTTOM_LEFT);
        hbox.setPadding(new Insets(value));
        hbox.setSpacing(value);
//        hbox.setVgap(value);
//        hbox.setHgap(value);

    }

    public void refreshStatuses(List<FxStatus> statuses) {
        hbox.getChildren().reA
    }


//    public Collection<Runnable> addStatusP(StatusType type, VoltageLevel... voltageLevel) {
//        Collection<VoltageLevel> levels = List.of(voltageLevel);
//        Collection<Runnable> runnables = new ArrayList<>();
//
//        Optional<FxStatus> existed = statusMatrix.get(s -> type.equals(s.getType()));
//        Optional<FxStatus> opposite = statusMatrix.get(s -> type.getNodeType().equals(s.getType().getNodeType()) && !s.getType().getBlockType().equals(type.getBlockType()));
//
//        // Если статус такой уже есть, то проверяем два сценария:
//        //  1. BlockType = Block -> добавляем в существующий статус новые voltageLevel-ы, если в матрцие есть
//        //      статус с таким же nodeType, но с BlockType.Should, то убираем из него пришедшие voltageLevel-ы
//        //  2. BlockType = Should -> проверяем, если есть в матрице статус с таким же nodeType, но с BlockType.Block, то ничего не делаем,
//        //      если такого нет, то просто добавляем новые voltageLevel-ы в существующий статус
//        // Если статуса такого ещё нет, то алгоритм такой же, только вместо добавления voltageLevel-ов в существующие статусы мы создаём новый статус
//
//
//        if (opposite.isPresent()) {
//
//            if (BlockType.BLOCK.equals(type.getBlockType())) {
//                Collection<VoltageLevel> finalLevels = levels;
//                opposite.ifPresent(opp -> opp.removeVoltageLevel(finalLevels.stream().toList()));
//            } else {
//                // SHOULD Оставляю только те уровни напряжения, которых нет в блокирующем статусе
//                Collection<VoltageLevel> finalLevels1 = levels;
//                levels = opposite.map(opp -> CollectionUtils.subtract(finalLevels1, opp.getVoltageLevels())).orElse(List.of());
//            }
//
//        }
//
//        if (levels.isEmpty()) return List.of();
//
//        if (existed.isPresent()) {
//
//            existed.get().addVoltageLevel(levels);
//
//        } else {
//            int x = numberOfElements / limitInOneRow;
//            int y = numberOfElements - x * limitInOneRow;
//            if (y == 0) statusMatrix.addRow();
//
//            FxStatus status = new FxStatus(type, x, y, size, voltageLevel);
//            statusMatrix.fill(status);
//
//            runnables.add(() -> hbox.add(status.getShape(), status.getY(), status.getX()));
//
//            numberOfElements++;
//        }
//
//        // todo необходимо удалить статусы, которые не имеют voltageLevel-ов
//        List<FxStatus> statusesWithoutVoltageLevels = statusMatrix.getAll(s -> s.getVoltageLevels().isEmpty());
//
//        for (FxStatus status : statusesWithoutVoltageLevels) {
//            // Сдвигаем элементы после status влево
//            for (int i = status.getX(); i < (numberOfElements / limitInOneRow) + 1; i++) {
//                if (i == status.getX()) {
//                    loop(status, i, status.getY(), runnables);
//                } else {
//                    for (int j = 0; j < numberOfElements - 1; j++) {
//                        loop(status, i, 0, runnables);
//                    }
//                }
//            }
//
////            statusMatrix.remove(status.getX(), status.getY());
//
//
//            int xcord = numberOfElements / limitInOneRow;
//            int ycord = numberOfElements - xcord * limitInOneRow - 1;
//            statusMatrix.remove(xcord, ycord);
//            numberOfElements--;
//            runnables.add(() -> hbox.getChildren().remove(status.getShape()));
//        }
//
//
//        return runnables;
//    }
//
//    private void loop(FxStatus status, int i, int y, Collection<Runnable> runnables) {
//        for (int j = y; j < Math.min(limitInOneRow, numberOfElements); j++) {
//            Optional<FxStatus> node;
//            if (j == (limitInOneRow - 1)) {
//                node = statusMatrix.getNode(i + 1, 0);
//                int finalJ = j;
//                node.ifPresent(n -> {
//                    n.setX(n.getX() - 1);
//                    n.setY(finalJ);
//                });
//            } else if ((i * numberOfElements + j) >= (numberOfElements - 1)) {
//                continue;
//            } else {
//                node = statusMatrix.getNode(i, j + 1);
//                int finalJ1 = j;
//                node.ifPresent(n -> n.setY(finalJ1));
//            }
//            node.ifPresent(n -> {
//                    statusMatrix.add(n);
//                    //todo Удалить элемент справа ПРОВЕРИТЬ ЭТО
//                    runnables.add(() -> {
//                        hbox.getChildren().remove(n.getShape());
//                        hbox.add(n.getShape(), n.getY(), n.getX());
//                    });
//                }
//            );
//
//        }
//    }
//
//
//    public List<FxStatus> getStatuses() {
//        return statusMatrix.toNodeList();
//    }
}
