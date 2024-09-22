package shticell.expression.impl;

import shticell.cell.api.CellType;
import shticell.cell.api.EffectiveValue;
import shticell.cell.impl.EffectiveValueImpl;
import shticell.expression.api.Expression;
import shticell.sheet.api.SheetReadActions;

import static java.lang.Double.NaN;

public class TimesExpression implements Expression {
    private Expression left;
    private Expression right;

    public TimesExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {

        double leftValueResult, rightValueResult;
        EffectiveValue leftValue, rightValue;
        try {
            leftValue = left.eval(sheet);
            leftValueResult = leftValue.extractValueWithExpectation(Double.class);
        } catch (Exception e) {
            //Will get here if left expression has a reference to:
            //1. An empty cell or a cell out of sheet range 2. A cell without a number value
            leftValueResult = NaN;
        }

        try {
            rightValue = right.eval(sheet);
            rightValueResult = rightValue.extractValueWithExpectation(Double.class);
        }
        catch (Exception e) {
            //Will get here if right expression has a reference to:
            //1. An empty cell or a cell out of sheet range 2. A cell without a number value
            rightValueResult = NaN;
        }

        double result = leftValueResult * rightValueResult;

        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.NUMERIC;
    }
}
