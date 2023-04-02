package com.example.demo.model.status;

import com.example.demo.model.power.node.PowerNodeType;
import javafx.scene.paint.Paint;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StatusType {
    BLOCK_SUBSTATION(BlockType.BLOCK, Paint.valueOf("#F1C40F"), "Блокирует подстанции"),
    SHOULD_SUBSTATION(BlockType.SHOULD, Paint.valueOf("#2ECC71"), "Может быть подстанция"),
    BLOCK_GENERATOR(BlockType.BLOCK, Paint.valueOf("#E67E22"), "Блокирует генератор"),
    SHOULD_GENERATOR(BlockType.SHOULD, Paint.valueOf("#1ABC9C"), "Может быть генератор"),
    BLOCK_LOAD(BlockType.BLOCK, Paint.valueOf("#E74C3C"), "Блокирует нагрузку"),
    SHOULD_LOAD(BlockType.SHOULD, Paint.valueOf("#3498DB"), "Может быть нагрузка");

    static {
        BLOCK_SUBSTATION.nodeType = PowerNodeType.SUBSTATION;
        BLOCK_GENERATOR.nodeType = PowerNodeType.GENERATOR;
        BLOCK_LOAD.nodeType = PowerNodeType.LOAD;
        SHOULD_SUBSTATION.nodeType = PowerNodeType.SUBSTATION;
        SHOULD_GENERATOR.nodeType = PowerNodeType.GENERATOR;
        SHOULD_LOAD.nodeType = PowerNodeType.LOAD;
    }

    private PowerNodeType nodeType;
    private final BlockType blockType;
    private final Paint color;
    private final String tooltipPrefix;
}
