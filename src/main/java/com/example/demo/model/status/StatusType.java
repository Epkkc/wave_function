package com.example.demo.model.status;

import com.example.demo.model.power.node.PowerNodeType;
import javafx.scene.paint.Paint;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StatusType {
    BLOCK_SUBSTATION(PowerNodeType.SUBSTATION, BlockType.BLOCK, Paint.valueOf("#F1C40F"), "Блокирует подстанции"),
    MUST_SUBSTATION(PowerNodeType.SUBSTATION, BlockType.MUST, Paint.valueOf("#2ECC71"), "Должны быть подстанции"),
    BLOCK_GENERATOR(PowerNodeType.GENERATOR, BlockType.BLOCK, Paint.valueOf("#E67E22"), "Блокирует генератор"),
    MUST_GENERATOR(PowerNodeType.GENERATOR, BlockType.MUST, Paint.valueOf("#1ABC9C"), "Должны быть генераторы"),
    BLOCK_LOAD(PowerNodeType.LOAD, BlockType.BLOCK, Paint.valueOf("#E74C3C"), "Блокирует нагрузку"),
    MUST_LOAD(PowerNodeType.LOAD, BlockType.MUST, Paint.valueOf("#3498DB"), "Должны быть нагрузки");

    private final PowerNodeType nodeType;
    private final BlockType blockType;
    private final Paint color;
    private final String tooltipPrefix;
}
