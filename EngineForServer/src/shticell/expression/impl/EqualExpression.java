package shticell.expression.impl;

import shticell.cell.api.CellType;
import shticell.cell.api.EffectiveValue;
import shticell.cell.impl.EffectiveValueImpl;
import shticell.expression.api.Expression;
import shticell.sheet.api.SheetReadActions;

public class EqualExpression implements Expression {
    private final Expression left;
    private final Expression right;

    public EqualExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {
        EffectiveValue leftValue = left.eval(sheet);
        EffectiveValue rightValue = right.eval(sheet);

        boolean result;
        try {
            Double leftNumeric = leftValue.extractValueWithExpectation(Double.class);
            Double rightNumeric = rightValue.extractValueWithExpectation(Double.class);
            result = leftNumeric.equals(rightNumeric);
        } catch (Exception e1) {
            try {
                String leftString = leftValue.extractValueWithExpectation(String.class);
                String rightString = rightValue.extractValueWithExpectation(String.class);
                result = leftString.equals(rightString);
            } catch (Exception e2) {
                try {
                    Boolean leftBoolean = leftValue.extractValueWithExpectation(Boolean.class);
                    Boolean rightBoolean = rightValue.extractValueWithExpectation(Boolean.class);
                    result = leftBoolean.equals(rightBoolean);
                } catch (Exception e3) {
                    result = false;
                }
            }
        }

        return new EffectiveValueImpl(CellType.BOOLEAN, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.BOOLEAN;
    }
}