package shticell.expression.impl;

import shticell.cell.api.CellType;
import shticell.cell.api.EffectiveValue;
import shticell.cell.impl.EffectiveValueImpl;
import shticell.expression.api.Expression;
import shticell.sheet.api.SheetReadActions;

public class NotExpression implements Expression {
    private final Expression argument;

    public NotExpression(Expression argument) {
        this.argument = argument;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {
        Boolean argBoolean;

        try {
            EffectiveValue argValue = argument.eval(sheet);
            argBoolean = argValue.extractValueWithExpectation(Boolean.class);
        } catch (Exception e) {
            return new EffectiveValueImpl(CellType.BOOLEAN, "UNKNOWN");
        }

        boolean result = !argBoolean;
        return new EffectiveValueImpl(CellType.BOOLEAN, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.BOOLEAN;
    }
}