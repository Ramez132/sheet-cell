package shticell.cell.impl;

import shticell.cell.api.CellType;
import shticell.cell.api.EffectiveValue;
import shticell.cell.api.Cell;
import shticell.coordinate.Coordinate;
import shticell.coordinate.CoordinateFactory;
import shticell.coordinate.CoordinateImpl;
import shticell.expression.api.Expression;
import shticell.expression.parser.FunctionParser;
import shticell.range.Range;
import shticell.range.RangeFactory;
import shticell.sheet.api.Sheet;

import java.io.Serializable;
import java.util.*;

//add data members and methods regarding range
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
            handleOriginalValueStrWithRangeNameInRelevantFunctions();
    }

    private void handleOriginalValueStrWithRangeNameInRelevantFunctions() {
        try {
            List<Integer> indicesAfterFunctionUsingRangeAndComma = new ArrayList<>();
            List<String> rangeNamesAfterFunctionUsingRange = new ArrayList<>();
            String upperCaseSumAndCommaSTR = "SUM,";
            String upperCaseAverageAndCommaSTR = "AVERAGE,";
            String upperCaseOriginalStr = originalValueStr.toUpperCase();

            if (!upperCaseOriginalStr.contains(upperCaseSumAndCommaSTR) && !upperCaseOriginalStr.contains(upperCaseAverageAndCommaSTR))
                return;

            int startIndexAfterSum = upperCaseOriginalStr.indexOf(upperCaseSumAndCommaSTR);
            int firstIndexAfterSum;

            while (startIndexAfterSum != -1) {
                firstIndexAfterSum = startIndexAfterSum + upperCaseSumAndCommaSTR.length();
                indicesAfterFunctionUsingRangeAndComma.add(firstIndexAfterSum);
                startIndexAfterSum = upperCaseOriginalStr.indexOf(upperCaseSumAndCommaSTR, firstIndexAfterSum);
            }

            int startIndexAfterAverage = upperCaseOriginalStr.indexOf(upperCaseAverageAndCommaSTR);
            int firstIndexAfterAverage;

            while (startIndexAfterAverage != -1) {
                firstIndexAfterAverage = startIndexAfterAverage + upperCaseAverageAndCommaSTR.length();
                indicesAfterFunctionUsingRangeAndComma.add(firstIndexAfterAverage);
                startIndexAfterAverage = upperCaseOriginalStr.indexOf(upperCaseAverageAndCommaSTR, firstIndexAfterAverage);
            }

            for (int index : indicesAfterFunctionUsingRangeAndComma) {

                int indexAfterFunctionUsingRangeAndComma = index;
                StringBuilder rowAndColStr = new StringBuilder();

                //extracting range name from the original value string,
                //not using the upper case string because range names are case-sensitive
                while (indexAfterFunctionUsingRangeAndComma < originalValueStr.length()
                        && originalValueStr.charAt(indexAfterFunctionUsingRangeAndComma) != '}') {
                    rowAndColStr.append(originalValueStr.charAt(indexAfterFunctionUsingRangeAndComma));
                    indexAfterFunctionUsingRangeAndComma++;
                }

                rangeNamesAfterFunctionUsingRange.add(rowAndColStr.toString());
            }

            for (String rangeName : rangeNamesAfterFunctionUsingRange) {
                try {
                    Range referencedRange = RangeFactory.getRangeByItsName(rangeName);
                    Set<Coordinate> allCoordinatesInThisRange = RangeFactory.getRangeByItsName(rangeName).getAllCoordinatesThatBelongToThisRange();

                    if (allCoordinatesInThisRange.contains(this.coordinate)) {
                        throw new IllegalArgumentException("A cell with function using range can't reference itself. Cell " + this.coordinate + " tried to reference " + rangeName);
                    }

                    for (Coordinate currentReferencedCoordinate : allCoordinatesInThisRange) {
                        try {
                            Cell currentReferencedCell;
                            int currentReferencedRow = currentReferencedCoordinate.getRow();
                            int currentReferencedColumn = currentReferencedCoordinate.getColumn();
                            boolean isCellsCollectionInSheetContainsCoordinate = sheet.isCellsCollectionContainsCoordinate(currentReferencedRow, currentReferencedColumn);

                            if (!isCellsCollectionInSheetContainsCoordinate) {
                                currentReferencedCell = sheet.setNewEmptyCell(currentReferencedRow, currentReferencedColumn);
                            } else {
                                currentReferencedCell = sheet.getCell(currentReferencedRow, currentReferencedColumn);
                            }

                            currentReferencedCell.getInfluencingOnMap().put(this.getCoordinate(), this);
                            this.dependsOnMap.put(currentReferencedCell.getCoordinate(), currentReferencedCell);
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Error trying to reference cell " + this.coordinate + " in range " + rangeName + ": " + e.getMessage());
                        }
                    }

                    this.sheet.addRangeToAllRangesReferencedInSheet(rangeName, referencedRange);

                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            }

        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
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
