package shticell.expression.impl;

import shticell.cell.api.Cell;
import shticell.cell.api.CellType;
import shticell.cell.api.EffectiveValue;
import shticell.coordinate.Coordinate;
import shticell.expression.api.Expression;
import shticell.expression.parser.FunctionParser;
import shticell.sheet.api.SheetReadActions;

public class RefExpression implements Expression {

    private final Coordinate coordinate;

    public RefExpression(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {

        //boolean isWantedCellReferencesThisCell = ??
        // sheet.getCell(coordinate.getRow(), coordinate.getColumn()).getCoordinate().equals(sheet.getCell(coordinate.getRow(), coordinate.getColumn()).getCoordinate());

        //boolean isCellReferencesItself =
        // sheet.getCell(coordinate.getRow(), coordinate.getColumn()).getCoordinate().equals(sheet.getCell(coordinate.getRow(), coordinate.getColumn()).getCoordinate());

        //handle throwing exceptions if referencedCell references itself or the wanted referencedCell references this referencedCell?

        Cell referencedCell = sheet.getCell(coordinate.getRow(), coordinate.getColumn());
        Expression expression = FunctionParser.parseExpression(referencedCell.getOriginalValueStr());
        //need to check if the referencedCell is also of type refExpression
        if (expression instanceof RefExpression) {
            RefExpression refExpression = (RefExpression) expression;
            if (refExpression.coordinate.equals(coordinate)) {
                throw new IllegalArgumentException("The referencedCell references itself");
            }
        }

        try {
            boolean isCoordinateInSheetRange = sheet.isCoordinateInSheetRange(coordinate.getRow(), coordinate.getColumn());
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The referencedCell is not in the sheet range" + e.getMessage());
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
