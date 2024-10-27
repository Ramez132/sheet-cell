package shticell.expression.impl;

import shticell.cell.api.CellType;
import shticell.cell.api.EffectiveValue;
import shticell.cell.impl.EffectiveValueImpl;
import shticell.expression.api.Expression;
import shticell.sheet.api.SheetReadActions;

import static java.lang.Double.NaN;

public class AbsExpression implements Expression {
    private final Expression valueToPreformAbsOn;

    public AbsExpression(Expression argument) {
        this.valueToPreformAbsOn = argument;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {
        double valueResult;
        EffectiveValue effectiveValue;
        try {
            effectiveValue = valueToPreformAbsOn.eval(sheet);
            valueResult = effectiveValue.extractValueWithExpectation(Double.class);
        } catch (Exception e) {
            //Will get here if left expression has a reference to:
            //1. An empty cell or a cell out of sheet range 2. A cell without a number value
            valueResult = NaN;
        }

        double result = Math.abs(valueResult);
        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.NUMERIC;
    }
}
