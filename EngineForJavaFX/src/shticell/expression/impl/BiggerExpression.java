package shticell.expression.impl;

import shticell.cell.api.CellType;
import shticell.cell.api.EffectiveValue;
import shticell.cell.impl.EffectiveValueImpl;
import shticell.expression.api.Expression;
import shticell.sheet.api.SheetReadActions;

public class BiggerExpression implements Expression {
    private final Expression left;
    private final Expression right;

    public BiggerExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {
        EffectiveValue leftValue = left.eval(sheet);
        EffectiveValue rightValue = right.eval(sheet);

        Double leftNumber;
        Double rightNumber;

        try {
            leftNumber = leftValue.extractValueWithExpectation(Double.class);
        } catch (Exception e) {
            return new EffectiveValueImpl(CellType.BOOLEAN, "UNKNOWN");
        }

        try {
            rightNumber = rightValue.extractValueWithExpectation(Double.class);
        } catch (Exception e) {
            return new EffectiveValueImpl(CellType.BOOLEAN, "UNKNOWN");
        }

        boolean result = leftNumber >= rightNumber;
        return new EffectiveValueImpl(CellType.BOOLEAN, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.BOOLEAN;
    }
}