package shticell.expression.impl;
import shticell.cell.api.CellType;
import shticell.cell.api.EffectiveValue;
import shticell.cell.impl.EffectiveValueImpl;
import shticell.expression.api.Expression;
import shticell.sheet.api.SheetReadActions;

import static java.lang.Double.NaN;

public class PowExpression implements Expression {
    private final Expression base;
    private final Expression exponent;

    public PowExpression(Expression base, Expression exponent) {
        this.base = base;
        this.exponent = exponent;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {
        double baseValueResult, exponentValueResult;
        EffectiveValue baseValue ,exponentValue;

        try {
            baseValue = base.eval(sheet);
            baseValueResult = baseValue.extractValueWithExpectation(Double.class);
        } catch (Exception e) {
            //Will get here if left expression has a reference to:
            //1. An empty cell or a cell out of sheet range 2. A cell without a number value
            baseValueResult = NaN;
        }

        try {
            exponentValue = exponent.eval(sheet);
            exponentValueResult = exponentValue.extractValueWithExpectation(Double.class);
        }
        catch (Exception e) {
            //Will get here if right expression has a reference to:
            //1. An empty cell or a cell out of sheet range 2. A cell without a number value
            exponentValueResult = NaN;
        }

        double result = Math.pow(baseValueResult, exponentValueResult);
        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.NUMERIC;
    }
}
