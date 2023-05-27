package com.example.demo.base.service.element;

import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.BaseLine;
import com.example.demo.base.model.power.BasePowerNode;
import com.example.demo.base.service.element.AbstractElementService;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class BaseElementService extends AbstractElementService<BasePowerNode, BaseLine> {

    public BaseElementService(Matrix<BasePowerNode> matrix) {
        super(matrix);
    }

}
