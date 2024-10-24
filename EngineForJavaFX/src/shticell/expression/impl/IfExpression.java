package shticell.expression.impl;

import shticell.cell.api.CellType;
import shticell.cell.api.EffectiveValue;
import shticell.cell.impl.EffectiveValueImpl;
import shticell.expression.api.Expression;
import shticell.sheet.api.SheetReadActions;

public class IfExpression implements Expression {
    private final Expression condition;
    private final Expression thenExpression;
    private final Expression elseExpression;

    public IfExpression(Expression condition, Expression thenExpression, Expression elseExpression) {
        this.condition = condition;
        this.thenExpression = thenExpression;
        this.elseExpression = elseExpression;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {
        EffectiveValue conditionValue = condition.eval(sheet);
        EffectiveValue resultValue;
        EffectiveValue unknownResult = new EffectiveValueImpl(CellType.UNKNOWN,"UNKNOWN");
        Boolean conditionBoolean;

        try {
            conditionBoolean = conditionValue.extractValueWithExpectation(Boolean.class);
        } catch (Exception e) {
            return unknownResult;
        }

        EffectiveValue thenValue = thenExpression.eval(sheet);
        EffectiveValue elseValue = elseExpression.eval(sheet);

        try {
            boolean isThenDouble = thenValue.getCellType().isAssignableFrom(Double.class);
            boolean isElseDouble = elseValue.getCellType().isAssignableFrom(Double.class);

            if (isThenDouble && isElseDouble) {
                double thenResult = thenValue.extractValueWithExpectation(Double.class);
                double elseResult = elseValue.extractValueWithExpectation(Double.class);
                if (conditionBoolean) {
                    resultValue = new EffectiveValueImpl(CellType.NUMERIC, thenResult);
                } else {
                    resultValue = new EffectiveValueImpl(CellType.NUMERIC, elseResult);
                }
            } else {
                boolean isThenString = thenValue.getCellType().isAssignableFrom(String.class);
                boolean isElseString = elseValue.getCellType().isAssignableFrom(String.class);

                if (isThenString && isElseString) {
                    String thenResult = thenValue.extractValueWithExpectation(String.class);
                    String elseResult = elseValue.extractValueWithExpectation(String.class);
                    if (conditionBoolean) {
                        resultValue = new EffectiveValueImpl(CellType.STRING, thenResult);
                    } else {
                        resultValue = new EffectiveValueImpl(CellType.STRING, elseResult);
                    }
                } else {
                    boolean isThenBoolean = thenValue.getCellType().isAssignableFrom(Boolean.class);
                    boolean isElseBoolean = elseValue.getCellType().isAssignableFrom(Boolean.class);

                    if (isThenBoolean && isElseBoolean) {
                        boolean thenResult = thenValue.extractValueWithExpectation(Boolean.class);
                        boolean elseResult = elseValue.extractValueWithExpectation(Boolean.class);
                        if (conditionBoolean) {
                            resultValue = new EffectiveValueImpl(CellType.BOOLEAN, thenResult);
                        } else {
                            resultValue = new EffectiveValueImpl(CellType.BOOLEAN, elseResult);
                        }
                    } else {
                        resultValue = unknownResult;
                    }
                }
            }
            return resultValue;
        }
        catch (Exception e) {
            return unknownResult;
        }
    }

    @Override
    public CellType getFunctionResultType() {
        CellType thenType = thenExpression.getFunctionResultType();
        CellType elseType = elseExpression.getFunctionResultType();

        if (!thenType.equals(elseType) && !thenType.equals(CellType.UNKNOWN) && !elseType.equals(CellType.UNKNOWN)) {
            return CellType.UNKNOWN;
        }

        return thenType.equals(CellType.UNKNOWN) ? elseType : thenType;
    }
}