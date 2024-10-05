package shticell.expression.impl;

import shticell.cell.api.CellType;
import shticell.cell.api.EffectiveValue;
import shticell.cell.impl.EffectiveValueImpl;
import shticell.expression.api.Expression;
import shticell.sheet.api.SheetReadActions;

public class PercentExpression implements Expression {
    private final Expression part;
    private final Expression whole;

    public PercentExpression(Expression part, Expression whole) {
        this.part = part;
        this.whole = whole;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {
        double partValueResult, wholeValueResult;
        EffectiveValue partValue, wholeValue;

        try {
            partValue = part.eval(sheet);
            partValueResult = partValue.extractValueWithExpectation(Double.class);
        } catch (Exception e) {
            partValueResult = Double.NaN;
        }

        try {
            wholeValue = whole.eval(sheet);
            wholeValueResult = wholeValue.extractValueWithExpectation(Double.class);
        } catch (Exception e) {
            wholeValueResult = Double.NaN;
        }

        double result = (partValueResult * wholeValueResult) / 100;
        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.NUMERIC;
    }
}