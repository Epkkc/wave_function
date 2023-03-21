package com.example.demo.model.power.node;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ConnectionPoint {
    /**
     * Координата x на сетке
     */
    private double x;
    /**
     * Координата y на сетке
     */
    private double y;
    /**
     * Уровень напряжения точки присоединения
     */
    private VoltageLevel voltageLevel;
    /**
     * Число присоединений
     */
    private int connections;
    /**
     * ограничение на максимальное количество присоединений
     */
    private int limit;

    public boolean addConnection() {
        if (connections < limit) {
            connections++;
            return true;
        }
        return false;
    }

}
