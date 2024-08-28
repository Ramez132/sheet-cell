package shticell.expression.impl;
import shticell.cell.api.CellType;
import shticell.cell.api.EffectiveValue;
import shticell.cell.impl.EffectiveValueImpl;
import shticell.expression.api.Expression;
import shticell.sheet.api.SheetReadActions;

public class PowExpression implements Expression {
    private final Expression base;
    private final Expression exponent;

    public PowExpression(Expression base, Expression exponent) {
        this.base = base;
        this.exponent = exponent;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {
        EffectiveValue baseValue = base.eval(sheet);
        EffectiveValue exponentValue = exponent.eval(sheet);
        double base = baseValue.extractValueWithExpectation(Double.class);
        double exponent = exponentValue.extractValueWithExpectation(Double.class);
        double result = Math.pow(base, exponent);
        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.NUMERIC;
    }
}
