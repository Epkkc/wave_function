package com.example.demo.java.fx.model.grid;

import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.power.BaseConnection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ConnectionPoint extends BaseConnection {
    /**
     * Координата x на сетке
     */
    protected double x;
    /**
     * Координата y на сетке
     */
    protected double y;

    public ConnectionPoint(double x, double y, VoltageLevel voltageLevel, int limit, int chainLinkOrder) {
        super(voltageLevel, limit, chainLinkOrder);
        this.x = x;
        this.y = y;
    }

}
