package com.example.demo.base.model.power;

import com.example.demo.base.model.enums.VoltageLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseConnection implements Connection {

    /**
     * Уровень напряжения точки присоединения
     */
    protected VoltageLevel voltageLevel;
    /**
     * Число присоединений
     */
    protected int connections;
    /**
     * ограничение на максимальное количество присоединений
     */
    protected int limit;

    public boolean addConnection() {
        if (connections < limit) {
            connections++;
            return true;
        }
        return false;
    }

}
