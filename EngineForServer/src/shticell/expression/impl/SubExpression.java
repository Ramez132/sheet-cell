package shticell.expression.impl;

import shticell.cell.api.CellType;
import shticell.cell.api.EffectiveValue;
import shticell.cell.impl.EffectiveValueImpl;
import shticell.expression.api.Expression;
import shticell.sheet.api.SheetReadActions;

import static java.lang.Double.NaN;

public class SubExpression implements Expression {
    private final Expression sourceStrExpression;
    private final Expression startIndex;
    private final Expression endIndex;

    public SubExpression(Expression sourceStrExpression, Expression startIndex, Expression endIndex) {
        this.sourceStrExpression = sourceStrExpression;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {

        EffectiveValue sourceValue;
        String originalValueToSubFrom;
        try {
            sourceValue = sourceStrExpression.eval(sheet);
            originalValueToSubFrom = sourceValue.extractValueWithExpectation(String.class);
        } catch (Exception e) {
            //Will get here if left expression has a reference to:
            //1. An empty cell or a cell out of sheet range 2. A cell without a String value
            originalValueToSubFrom = "!UNDEFINED!";
        }

        Double startIndexDoubleValue, endIndexDoubleValue;
        EffectiveValue startIndexEffectiveValue, endIndexEffectiveValue;

        try {
            startIndexEffectiveValue = startIndex.eval(sheet);
            startIndexDoubleValue = startIndexEffectiveValue.extractValueWithExpectation(Double.class);
        } catch (Exception e) {
            //Will get here if left expression has a reference to:
            //1. An empty cell or a cell out of sheet range 2. A cell without a number value
            startIndexDoubleValue = NaN;
        }

        try {
            endIndexEffectiveValue = endIndex.eval(sheet);
            endIndexDoubleValue = endIndexEffectiveValue.extractValueWithExpectation(Double.class);
        }
        catch (Exception e) {
            //Will get here if right expression has a reference to:
            //1. An empty cell or a cell out of sheet range 2. A cell without a number value
            endIndexDoubleValue = NaN;
        }

        if (originalValueToSubFrom.equals("!UNDEFINED!") || startIndexDoubleValue == NaN || endIndexDoubleValue == NaN) {
            return new EffectiveValueImpl(CellType.STRING, "!UNDEFINED!");
        }

        if (startIndexDoubleValue % 1 != 0 || endIndexDoubleValue % 1 != 0) {
            return new EffectiveValueImpl(CellType.STRING, "!UNDEFINED!");
        }
        if (startIndexDoubleValue < 0 || endIndexDoubleValue < 0 || startIndexDoubleValue > endIndexDoubleValue ) {
            return new EffectiveValueImpl(CellType.STRING, "!UNDEFINED!");
        }
        if (!(startIndexDoubleValue < originalValueToSubFrom.length() && endIndexDoubleValue < originalValueToSubFrom.length())) {
            return new EffectiveValueImpl(CellType.STRING, "!UNDEFINED!");
        }

        int startIndex = startIndexDoubleValue.intValue();
        int endIndex = endIndexDoubleValue.intValue();

        String result = originalValueToSubFrom.substring(startIndex - 1, endIndex);
        return new EffectiveValueImpl(CellType.STRING, result);

    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.STRING;
    }
}
