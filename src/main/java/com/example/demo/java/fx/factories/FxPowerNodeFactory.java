package com.example.demo.java.fx.factories;

import com.example.demo.java.fx.service.FxElementService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class FxPowerNodeFactory implements FxNodeFactory{
    protected final FxElementService elementsService;

}
