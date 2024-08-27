package shticell.expression.impl;

import shticell.cell.api.CellType;
import shticell.cell.api.EffectiveValue;
import shticell.cell.impl.EffectiveValueImpl;
import shticell.expression.api.Expression;
import shticell.sheet.api.SheetReadActions;

public class TimesExpression implements Expression {
    private Expression e1;
    private Expression e2;

    public TimesExpression(Expression e1, Expression e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {
        EffectiveValue baseValue = e1.eval(sheet);
        EffectiveValue exponentValue = e2.eval(sheet);
        double left = baseValue.extractValueWithExpectation(Double.class);
        double right = exponentValue.extractValueWithExpectation(Double.class);
        double result = left * right;
        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.NUMERIC;
    }
}
