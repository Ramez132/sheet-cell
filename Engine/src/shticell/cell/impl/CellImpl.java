package shticell.cell.impl;

import shticell.cell.api.EffectiveValue;
import shticell.cell.api.Cell;
import shticell.coordinate.Coordinate;
import shticell.coordinate.CoordinateFactory;
import shticell.coordinate.CoordinateImpl;
import shticell.expression.api.Expression;
import shticell.expression.parser.FunctionParser;
import shticell.sheet.api.SheetReadActions;

import java.util.ArrayList;
import java.util.List;

public class CellImpl implements Cell {

    private final Coordinate coordinate;
    private String originalValueStr;
    private EffectiveValue effectiveValue;
    private int lastVersionInWhichCellHasChanged;
    private final List<Cell> dependsOn;
    private final List<Cell> influencingOn;
    private final SheetReadActions sheet;
    private final int versionNumForEmptyCellWithoutPreviousValues = -1;
    private boolean isCellEmptyBoolean;

    public CellImpl(int row, int column, String originalValueStr, int lastVersionInWhichCellHasChanged, SheetReadActions sheet)  {
        this.sheet = sheet;
        this.coordinate = new CoordinateImpl(row, column);
        this.originalValueStr = originalValueStr;
        this.lastVersionInWhichCellHasChanged = lastVersionInWhichCellHasChanged;
        this.dependsOn = new ArrayList<>();
        this.influencingOn = new ArrayList<>();
        isCellEmptyBoolean = originalValueStr.isEmpty();
        handleOriginalValueStrWithRef();
    }


    public void handleOriginalValueStrWithRef() {

        //List<int[]> indices = new ArrayList<>();
        List<Integer> indicesAfterRefAndComma = new ArrayList<>();
        List<String> rowAndColStringsAfterRef = new ArrayList<>();
        String refAndCommaSTR = "REF,";

        if (!originalValueStr.contains(refAndCommaSTR))
            return;

//        int startIndex = originalValueStr.indexOf(refAndCommaSTR);
//
//        while (startIndex != -1) {
//            firstIndexAfterRef = startIndex + refAndCommaSTR.length();
//            indices.add(new int[]{startIndex, firstIndexAfterRef});
//            startIndex = originalValueStr.indexOf(refAndCommaSTR, firstIndexAfterRef);
//        }

        int startIndex = originalValueStr.indexOf(refAndCommaSTR);
        int firstIndexAfterRef;

        while (startIndex != -1) {
            firstIndexAfterRef = startIndex + refAndCommaSTR.length();
            indicesAfterRefAndComma.add(firstIndexAfterRef);
            startIndex = originalValueStr.indexOf(refAndCommaSTR, firstIndexAfterRef);
        }

        for (int index : indicesAfterRefAndComma) {

            int indexAfterRefWithComma = index;
            StringBuilder rowAndColStr = new StringBuilder();

            while (indexAfterRefWithComma < originalValueStr.length()
                    && originalValueStr.charAt(indexAfterRefWithComma) != '}') {
                rowAndColStr.append(originalValueStr.charAt(indexAfterRefWithComma));
                indexAfterRefWithComma++;
            }

            rowAndColStringsAfterRef.add(rowAndColStr.toString());
        }

        for (String rowAndColStr : rowAndColStringsAfterRef) {
            Cell referencedCell;
            Coordinate referencedCoordinate = CoordinateFactory.getCoordinateFromStr(rowAndColStr);
            int currentReferencedRow = referencedCoordinate.getRow();
            int currentReferencedColumn = referencedCoordinate.getColumn();
            boolean isCellsCollectionInSheetContainsCoordinate = sheet.isCellsCollectionContainsCoordinate(currentReferencedRow, currentReferencedColumn);

            if (!isCellsCollectionInSheetContainsCoordinate){
                referencedCell = sheet.setNewEmptyCell(currentReferencedRow, currentReferencedColumn);
            } else {
                referencedCell = sheet.getCell(currentReferencedRow, currentReferencedColumn);
            }

            referencedCell.getInfluencingOn().add(this);
            this.dependsOn.add(referencedCell);
        }

    }

    @Override
    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public String getOriginalValueStr() {
        return originalValueStr;
    }

    @Override
    public void setCellOriginalValue(String value) {
        this.originalValueStr = value;
    }

    @Override
    public EffectiveValue getEffectiveValue() {
        if (effectiveValue == null) {
            calculateEffectiveValue();
        }
        return effectiveValue;
    }

    @Override
    public boolean calculateEffectiveValue() {
        // build the expression object out of the original value...
        // it can be {PLUS, 4, 5} OR {CONCAT, {ref, A4}, world}
        Expression expression = FunctionParser.parseExpression(originalValueStr);

        EffectiveValue newEffectiveValue = expression.eval(sheet);

        if (newEffectiveValue.equals(effectiveValue)) {
            return false;
        } else {
            effectiveValue = newEffectiveValue;
            return true;
        }
    }

    @Override
    public int getLastVersionInWhichCellHasChanged() {
        return lastVersionInWhichCellHasChanged;
    }

    @Override
    public List<Cell> getDependsOn() {
        return dependsOn;
    }

    @Override
    public List<Cell> getInfluencingOn() {
        return influencingOn;
    }

    @Override
    public boolean getIsCellEmptyBoolean() {
        return isCellEmptyBoolean;
    }

}
