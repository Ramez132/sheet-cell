package shticell.cell.impl;

import shticell.cell.api.CellType;
import shticell.cell.api.EffectiveValue;
import shticell.cell.api.Cell;
import shticell.coordinate.Coordinate;
import shticell.coordinate.CoordinateFactory;
import shticell.coordinate.CoordinateImpl;
import shticell.expression.api.Expression;
import shticell.expression.parser.FunctionParser;
import shticell.sheet.api.Sheet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CellImpl implements Cell, Serializable {

    private final Coordinate coordinate;
    private String originalValueStr;
    private EffectiveValue previousEffectiveValue;
    private EffectiveValue currentEffectiveValue; //null means: no effective value - empty cell or reference to an empty cell
    private int lastVersionInWhichCellHasChanged = -1;
    private Map<Coordinate, Cell> dependsOnMap;
    private Map<Coordinate, Cell> influencingOnMap;
    private final Sheet sheet;
    private static final int versionNumForEmptyCellWithoutPreviousValues = -1;
    private boolean isCellEmptyBoolean;

    public CellImpl(int row, int column, String originalValueStr,
                    int lastVersionInWhichCellHasChanged, Sheet sheet)  {
            this.sheet = sheet;
            this.coordinate = new CoordinateImpl(row, column);
            this.originalValueStr = originalValueStr;
            this.lastVersionInWhichCellHasChanged = lastVersionInWhichCellHasChanged;
            this.dependsOnMap = new HashMap<>();
            this.influencingOnMap = new HashMap<>();
            isCellEmptyBoolean = originalValueStr.isEmpty();
            handleOriginalValueStrWithRef();
    }


    public void handleOriginalValueStrWithRef() {

        try {
            List<Integer> indicesAfterRefAndComma = new ArrayList<>();
            List<String> rowAndColStringsAfterRef = new ArrayList<>();
            String upperCaseRefAndCommaSTR = "REF,";
            String upperCaseOriginalStr = originalValueStr.toUpperCase();

            if (!upperCaseOriginalStr.contains(upperCaseRefAndCommaSTR))
                return;

            int startIndex = upperCaseOriginalStr.indexOf(upperCaseRefAndCommaSTR);
            int firstIndexAfterRef;

            while (startIndex != -1) {
                firstIndexAfterRef = startIndex + upperCaseRefAndCommaSTR.length();
                indicesAfterRefAndComma.add(firstIndexAfterRef);
                startIndex = upperCaseOriginalStr.indexOf(upperCaseRefAndCommaSTR, firstIndexAfterRef);
            }

            for (int index : indicesAfterRefAndComma) {

                int indexAfterRefWithComma = index;
                StringBuilder rowAndColStr = new StringBuilder();

                while (indexAfterRefWithComma < upperCaseOriginalStr.length()
                        && upperCaseOriginalStr.charAt(indexAfterRefWithComma) != '}') {
                    rowAndColStr.append(upperCaseOriginalStr.charAt(indexAfterRefWithComma));
                    indexAfterRefWithComma++;
                }

                rowAndColStringsAfterRef.add(rowAndColStr.toString());
            }

            for (String rowAndColStr : rowAndColStringsAfterRef) {
                Cell referencedCell;
                try {
                    Coordinate referencedCoordinate = CoordinateFactory.getCoordinateFromStr(rowAndColStr, sheet);

                    if (referencedCoordinate.getRow() == this.coordinate.getRow() && referencedCoordinate.getColumn() == this.coordinate.getColumn()) {
                        throw new IllegalArgumentException("A cell can't reference itself. Cell " + rowAndColStr + " tried to reference " + rowAndColStr);
                    }

                    int currentReferencedRow = referencedCoordinate.getRow();
                    int currentReferencedColumn = referencedCoordinate.getColumn();
                    boolean isCellsCollectionInSheetContainsCoordinate = sheet.isCellsCollectionContainsCoordinate(currentReferencedRow, currentReferencedColumn);

                    if (!isCellsCollectionInSheetContainsCoordinate) {
                        referencedCell = sheet.setNewEmptyCell(currentReferencedRow, currentReferencedColumn);
                    } else {
                        referencedCell = sheet.getCell(currentReferencedRow, currentReferencedColumn);
                    }

                    referencedCell.getInfluencingOnMap().put(this.getCoordinate(), this);
                    this.dependsOnMap.put(referencedCell.getCoordinate(), referencedCell);
                }

                catch(IllegalArgumentException e){
                    throw new IllegalArgumentException("Error trying to reference " + rowAndColStr + ": " + e.getMessage());
                }
            }
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void insertInfluencingOnMapFromCellBeforeUpdate(Map<Coordinate, Cell> influencingOnMapFromCellBeforeUpdate) {
        influencingOnMap = influencingOnMapFromCellBeforeUpdate;
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
    public EffectiveValue getCurrentEffectiveValue() {
        return currentEffectiveValue;
    }

    @Override
    public EffectiveValue getPreviousEffectiveValue() {
        return previousEffectiveValue;
    }

    @Override
    public void setPreviousEffectiveValue(EffectiveValue effectiveValue) {
        previousEffectiveValue = effectiveValue;
    }

    @Override
    public boolean calculateNewEffectiveValueAndDetermineIfItChanged() {
        try {
            Expression expression = FunctionParser.parseExpression(originalValueStr, sheet);

            EffectiveValue newEffectiveValue = expression.eval(sheet);

            //if the received newEffectiveValue is not already null (indicating no effective value) and the cell is empty or references an empty cell
            if (newEffectiveValue != null && newEffectiveValue.getCellType() == CellType.Empty) {
                //null for effectiveValue means: no effective value - empty cell or reference to an empty cell
                newEffectiveValue = null;
            }
            if (newEffectiveValue != null && newEffectiveValue.equals(previousEffectiveValue)) {
                return false;
            }

            if (newEffectiveValue == null) {  //no effectiveValue
                if (currentEffectiveValue == null) { //the cell did not have an effective value and stays like that
                    return false; // return false to indicate no change
                } else {  //the cell had an effective value before, now it doesn't have
                    currentEffectiveValue = null;
                    return true; // return true to indicate a change
                }
            } else if (currentEffectiveValue == null) {  //the cell did not have an effective value before, now it has
                currentEffectiveValue = newEffectiveValue; //update to the new value
                return true; //indicate a change
            } else if (newEffectiveValue.equals(currentEffectiveValue)) { //the cell had an effective value before and now
                return false; // the effective value stayed the same - indicating no change
            } else {  // the effective value has changed - update and indicate change
                currentEffectiveValue = newEffectiveValue;
                return true;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error - The original value string is not in the correct format: " + e.getMessage());
        }
    }

    @Override
    public int getLastVersionInWhichCellHasChanged() {
        return lastVersionInWhichCellHasChanged;
    }

    @Override
    public void setLastVersionInWhichCellHasChanged(int newVersionNum) {
        lastVersionInWhichCellHasChanged = newVersionNum;
    }

    @Override
    public Map<Coordinate, Cell> getDependsOnMap() {
        return dependsOnMap;
    }

    @Override
    public Map<Coordinate, Cell> getInfluencingOnMap() {
        return influencingOnMap;
    }

    /**
     @return true if the cell is empty, false otherwise
    */
    @Override
    public boolean getIsCellEmptyBoolean() {
        return isCellEmptyBoolean;
    }

}
