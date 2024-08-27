package shticell.expression.impl;

import shticell.cell.api.CellType;
import shticell.cell.api.EffectiveValue;
import shticell.cell.impl.EffectiveValueImpl;
import shticell.expression.api.Expression;
import shticell.sheet.api.SheetReadActions;

public class AbsExpression implements Expression {
    private Expression val;

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {
        EffectiveValue only_value = val.eval(sheet);
        double result = Math.abs(only_value.extractValueWithExpectation(Double.class));
        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.NUMERIC;
    }
}
