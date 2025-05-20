package shticell.expression.impl;

import shticell.cell.api.Cell;
import shticell.cell.api.CellType;
import shticell.cell.api.EffectiveValue;
import shticell.cell.impl.EffectiveValueImpl;
import shticell.coordinate.Coordinate;
import shticell.expression.api.Expression;
import shticell.expression.parser.FunctionParser;
import shticell.range.Range;
import shticell.range.RangesManager;
import shticell.sheet.api.SheetReadActions;

import static java.lang.Double.NaN;

public class SumExpression implements Expression {

    private final Expression rangeNameExpression;

    public SumExpression(Expression rangeNameExpression) {
        this.rangeNameExpression = rangeNameExpression;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {

        double resultOfSumFunction = 0;
        EffectiveValue rangeNameValue;
        String rangeNameString;
        try {
            rangeNameValue = rangeNameExpression.eval(sheet);
            rangeNameString = rangeNameValue.extractValueWithExpectation(String.class);
        } catch (Exception e) { // ???????
            //Will get here if expression has a reference to:
            //1. An empty cell or a cell out of sheet range 2. A cell without a String value
            rangeNameString = "!UNDEFINED!";
        }

        RangesManager rangesManager = sheet.getRangesManager();

        if (!rangesManager.isThereAnyRangeInRangesManager() || !rangesManager.isRangeNameAlreadyExistsForThisSheet(rangeNameString)) {
            return new EffectiveValueImpl(CellType.NUMERIC, NaN);
        } else { //rangeNameString is a valid range name
            Range selectedRange;
            try {
                selectedRange = rangesManager.getRangeByItsName(rangeNameString);
            } catch (Exception e) {
                return new EffectiveValueImpl(CellType.NUMERIC, NaN);
            }
            Cell currentReferenceCell;
            EffectiveValue currentReferencedCellEffectiveValue;
            double currentDoubleValueResult;
            for (Coordinate currentCoordinate : selectedRange.getAllCoordinatesThatBelongToThisRange()) {
                try {
                    currentReferenceCell = sheet.getCell(currentCoordinate.getRow(), currentCoordinate.getColumn());
                    if (currentReferenceCell.getIsCellEmptyBoolean()) {
                        continue; //system ignores the empty cell and continues with the next one
                    }
                    Expression currentReferencedExpression = FunctionParser.parseExpression(currentReferenceCell.getOriginalValueStr(), sheet);
                    currentReferencedCellEffectiveValue = currentReferencedExpression.eval(sheet);
                    currentDoubleValueResult = currentReferencedCellEffectiveValue.extractValueWithExpectation(Double.class);
                    resultOfSumFunction += currentDoubleValueResult;
                } catch (Exception e) {
                    //Will get here if cell has a reference to:
                    //1. An empty cell or a cell out of sheet range - ?? what happens in an empty cell?
                    // 2. A cell without a number value
                    //system ignores the cell and continues with the next one
                    resultOfSumFunction += 0;
                }
            }
        }

        return new EffectiveValueImpl(CellType.NUMERIC, resultOfSumFunction);
    }

    @Override
    public CellType getFunctionResultType() {
        return null;
    }
}
