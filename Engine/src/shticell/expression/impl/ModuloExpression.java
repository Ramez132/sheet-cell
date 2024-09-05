package shticell.expression.impl;

import shticell.cell.api.CellType;
import shticell.cell.api.EffectiveValue;
import shticell.cell.impl.EffectiveValueImpl;
import shticell.expression.api.Expression;
import shticell.sheet.api.SheetReadActions;

import static java.lang.Double.NaN;

public class ModuloExpression implements Expression {
    private Expression dividend;
    private Expression divisor;

    public ModuloExpression(Expression dividend, Expression divisor) {
        this.dividend = dividend;
        this.divisor = divisor;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {
        double dividendValueResult, divisorValueResult;
        EffectiveValue dividendValue, divisorValue;

        try {
            dividendValue = dividend.eval(sheet);
            dividendValueResult = dividendValue.extractValueWithExpectation(Double.class);
        } catch (Exception e) {
            //Will get here if left expression has a reference to:
            //1. An empty cell or a cell out of sheet range 2. A cell without a number value
            dividendValueResult = NaN;
        }

        try {
            divisorValue = divisor.eval(sheet);
            divisorValueResult = divisorValue.extractValueWithExpectation(Double.class);
        }
        catch (Exception e) {
            //Will get here if right expression has a reference to:
            //1. An empty cell or a cell out of sheet range 2. A cell without a number value
            divisorValueResult = NaN;
        }
//        Double divisor = divisorValue.extractValueWithExpectation(Double.class);
        if (divisorValueResult == 0) {
            throw new ArithmeticException("Division by zero is not possible.");
        }
        double result = dividendValueResult % divisorValueResult;
        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.NUMERIC;
    }
}
