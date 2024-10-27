package shticell.expression.impl;

import shticell.cell.api.CellType;
import shticell.cell.api.EffectiveValue;
import shticell.cell.impl.EffectiveValueImpl;
import shticell.expression.api.Expression;
import shticell.sheet.api.SheetReadActions;

public class ConcatExpression implements Expression {
    private Expression left;
    private Expression right;

    public ConcatExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {

        String leftValueStr, rightValueStr, resultStr;
        EffectiveValue leftValue, rightValue;

        try {
            leftValue = left.eval(sheet);
            leftValueStr = leftValue.extractValueWithExpectation(String.class);
        } catch (Exception e) {
            //Will get here if left expression has a reference to:
            //1. An empty cell or a cell out of sheet range 2. A cell without a String value
            leftValueStr = "!UNDEFINED!";
        }

        try {
            rightValue = right.eval(sheet);
            rightValueStr = rightValue.extractValueWithExpectation(String.class);
        } catch (Exception e) {
            //Will get here if right expression has a reference to:
            //1. An empty cell or a cell out of sheet range 2. A cell without a String value
            rightValueStr = "!UNDEFINED!";
        }

        //EffectiveValue leftValue = left.eval(sheet);
        //EffectiveValue rightValue = right.eval(sheet);

        //String resultStr = leftValue.extractValueWithExpectation(String.class) + rightValue.extractValueWithExpectation(String.class);
        if (leftValueStr.equals("!UNDEFINED!") || rightValueStr.equals("!UNDEFINED!")) {
            resultStr = "!UNDEFINED!";
        } else {
            resultStr = leftValueStr + rightValueStr;
        }

        return new EffectiveValueImpl(CellType.STRING, resultStr);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.STRING;
    }
}
