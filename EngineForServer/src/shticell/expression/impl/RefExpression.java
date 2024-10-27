package shticell.expression.impl;

import shticell.cell.api.CellType;
import shticell.cell.api.EffectiveValue;
import shticell.coordinate.Coordinate;
import shticell.expression.api.Expression;
import shticell.sheet.api.SheetReadActions;

public class RefExpression implements Expression {

    private final Coordinate coordinate;

    public RefExpression(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {

        try {
            boolean isCoordinateInSheetRange = sheet.isCoordinateInSheetRange(coordinate.getRow(), coordinate.getColumn());
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The referenced cell is not in the sheet range" + e.getMessage());
        }

        if (sheet.isCellEmpty(coordinate.getRow(), coordinate.getColumn())) { //change it because implemented logic for empty cell?
            //throw new IllegalArgumentException("The referencedCell is empty");
        }

        sheet.getCell(coordinate.getRow(), coordinate.getColumn()).calculateNewEffectiveValueAndDetermineIfItChanged();

        // error handling if the referencedCell is empty or not found
        return sheet.getCell(coordinate.getRow(), coordinate.getColumn()).getCurrentEffectiveValue();
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.UNKNOWN;
    }
}
