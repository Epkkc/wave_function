package com.example.demo.base.service.element;

import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.BaseLine;
import com.example.demo.base.model.power.BasePowerNode;
import com.example.demo.base.service.element.AbstractElementService;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class BaseElementService extends AbstractElementService<BasePowerNode, BaseLine> {

    public BaseElementService(Matrix<BasePowerNode> matrix) {
        super(matrix);
    }

    @Override
    protected void beforeRemovingLines(List<BaseLine> linesForRemove) {}
}
