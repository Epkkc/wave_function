package com.example.demo.model.power.node;

import com.example.demo.model.status.StatusType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Getter
public enum PowerNodeType {
    EMPTY(0, 0),
    SUBSTATION(1, 60),
    GENERATOR(2, 30),
    LOAD(1, 10);

    static {
        SUBSTATION.blockingStatus = StatusType.BLOCK_SUBSTATION;
        GENERATOR.blockingStatus = StatusType.BLOCK_GENERATOR;
        LOAD.blockingStatus = StatusType.BLOCK_LOAD;

        SUBSTATION.shouldStatus = StatusType.SHOULD_SUBSTATION;
        GENERATOR.shouldStatus = StatusType.SHOULD_GENERATOR;
        LOAD.shouldStatus = StatusType.SHOULD_LOAD;

        double counter = 0;
        for (PowerNodeType value : values()) {
            counter += value.getGenerationRate();
        }
        assert Double.compare(1.0,counter) == 0;
    }

    private StatusType blockingStatus;
    private StatusType shouldStatus;
    private final int boundingAreaKoef;
    private final double generationRate;

    public static Collection<PowerNodeType> getValidValues() {
        return List.of(SUBSTATION, GENERATOR, LOAD);
    }

}
