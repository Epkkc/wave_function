package com.example.demo.base.model.enums;

import com.example.demo.base.model.status.StatusType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Getter
public enum PowerNodeType {
    EMPTY(),
    SUBSTATION(),
    GENERATOR(),
    LOAD();

    static {
        SUBSTATION.blockingStatus = StatusType.BLOCK_SUBSTATION;
        GENERATOR.blockingStatus = StatusType.BLOCK_GENERATOR;
        LOAD.blockingStatus = StatusType.BLOCK_LOAD;

        SUBSTATION.shouldStatus = StatusType.SHOULD_SUBSTATION;
        GENERATOR.shouldStatus = StatusType.SHOULD_GENERATOR;
        LOAD.shouldStatus = StatusType.SHOULD_LOAD;
    }

    private StatusType blockingStatus;
    private StatusType shouldStatus;

    public static Collection<PowerNodeType> getValidValues() {
        return List.of(SUBSTATION, GENERATOR, LOAD);
    }

}
