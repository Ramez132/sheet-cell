package shticell.expression.impl;

import shticell.cell.api.CellType;
import shticell.cell.api.EffectiveValue;
import shticell.cell.impl.EffectiveValueImpl;
import shticell.expression.api.Expression;
import shticell.sheet.api.SheetReadActions;

public class ModuleExpression implements Expression {
    private Expression e1;
    private Expression e2;

    public ModuleExpression(Expression e1, Expression e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {
        EffectiveValue left = e1.eval(sheet);
        EffectiveValue right = e2.eval(sheet);
        Double divisor = right.extractValueWithExpectation(Double.class);
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        double result = left.extractValueWithExpectation(Double.class) % right.extractValueWithExpectation(Double.class);
        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.NUMERIC;
    }
}
