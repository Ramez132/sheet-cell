package shticell.expression.impl;

import shticell.cell.api.CellType;
import shticell.cell.api.EffectiveValue;
import shticell.cell.impl.EffectiveValueImpl;
import shticell.expression.api.Expression;
import shticell.sheet.api.SheetReadActions;

import static java.lang.Double.NaN;

public class PercentExpression implements Expression {

    private Expression partExpression;
    private Expression wholeExpression;

    public PercentExpression(Expression part, Expression whole) {
        this.partExpression = part;
        this.wholeExpression = whole;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {
        double partValueResult, wholeValueResult;
        EffectiveValue partValue, wholeValue;
        try {
            partValue = partExpression.eval(sheet);
            partValueResult = partValue.extractValueWithExpectation(Double.class);
        } catch (Exception e) {
            //Will get here if left expression has a reference to:
            //1. An empty cell or a cell out of sheet range 2. A cell without a number value
            partValueResult = NaN;
        }

        try {
            wholeValue = wholeExpression.eval(sheet);
            wholeValueResult = wholeValue.extractValueWithExpectation(Double.class);
        }
        catch (Exception e) {
            //Will get here if right expression has a reference to:
            //1. An empty cell or a cell out of sheet range 2. A cell without a number value
            wholeValueResult = NaN;
        }

        double result = (partValueResult * wholeValueResult) / 100;

        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.NUMERIC;
    }
}
