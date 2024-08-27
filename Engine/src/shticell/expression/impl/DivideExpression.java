package shticell.expression.impl;

import shticell.cell.api.CellType;
import shticell.cell.api.EffectiveValue;
import shticell.cell.impl.EffectiveValueImpl;
import shticell.expression.api.Expression;
import shticell.sheet.api.SheetReadActions;

public class DivideExpression implements Expression {
    private Expression dividend;
    private Expression divisor;

    public DivideExpression(Expression dividend, Expression divisor) {
        this.dividend = dividend;
        this.divisor = divisor;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {
        EffectiveValue dividendValue = dividend.eval(sheet);
        EffectiveValue divisorValue = divisor.eval(sheet);
        Double divisor = divisorValue.extractValueWithExpectation(Double.class);
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero is not possible.");
        }
        double result = dividendValue.extractValueWithExpectation(Double.class) / divisor;
        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.NUMERIC;
    }
}
