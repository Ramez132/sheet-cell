package engine.impl;

import engine.api.EngineManagerForServer;
import shticell.cell.api.Cell;
import shticell.coordinate.Coordinate;
import shticell.coordinate.CoordinateFactory;
import shticell.jaxb.SheetFromFilesFactory;
import shticell.range.Range;
import shticell.range.RangeFactory;
import shticell.range.RangeImpl;
import shticell.row.RangeWithRowsInArea;
import shticell.sheet.api.Sheet;
import shticell.sorter.RangeSorter;

import java.io.File;
import java.util.*;

public class EngineManagerForServerImpl implements EngineManagerForServer {

    private List<Sheet> sheetVersionsArray;
    Map<String, List<Sheet>> mapSheetNameToSheetVersionsArray;

    public EngineManagerForServerImpl() {
        //this.sheetVersionsArray = new ArrayList<Sheet>();
        this.mapSheetNameToSheetVersionsArray = new HashMap<>();
    }

    @Override
    public Sheet getSheetFromFile(File file) throws Exception {
        try {
            Sheet currentSheet;

            currentSheet = SheetFromFilesFactory.CreateSheetObjectFromXmlFile(file);
            //if the code gets here, the file is valid and new Sheet object was created - clear all previous versions
//            sheetVersionsArray.clear();
            String newSheetName = currentSheet.getNameOfSheet();
            if (mapSheetNameToSheetVersionsArray.containsKey(newSheetName)) {
                throw new IllegalArgumentException("Sheet with the same name already exists in the system.");
            }
            else {
                mapSheetNameToSheetVersionsArray.put(newSheetName, new ArrayList<>());
            }
            List<Sheet> sheetVersionsArray = mapSheetNameToSheetVersionsArray.get(newSheetName);
            sheetVersionsArray.add(currentSheet);
        }
        catch (Exception e) {
            throw e;
        }

        return sheetVersionsArray.getLast();
    }

    @Override
    public Sheet getMostRecentSheetWithSelectedName(String sheetName) throws NoSuchElementException {
        if (mapSheetNameToSheetVersionsArray.containsKey(sheetName)) {
            List<Sheet> sheetVersionsArray = mapSheetNameToSheetVersionsArray.get(sheetName);
            return sheetVersionsArray.getLast();
        }
        else {
            throw new NoSuchElementException("There is no sheet with the requested name in the system.");
        }

//
//        if (!sheetVersionsArray.isEmpty()) {
//            return sheetVersionsArray.getLast();
//        }
//        else {
//            throw new NoSuchElementException("There is no sheet in the system.");
//        }
    }

    @Override
    public Cell getCellFromMostRecentSheetWithSelectedName(String sheetName, int row, int column) {
        List<Sheet> sheetVersionsArray = mapSheetNameToSheetVersionsArray.get(sheetName);
        return sheetVersionsArray.getLast().getCell(row, column); //is it the expected behavior?
    }

    @Override
    public Sheet updateValueOfCellAndGetNewSheetWithSelectedName(String sheetName, int row, int col, String value) throws RuntimeException {
        Sheet possibleNewSheet;
        boolean isUpdatePartOfSheetInitialization = sheetVersionsArray.isEmpty();
        try {
            Sheet latestSheet = sheetVersionsArray.getLast();
            possibleNewSheet = latestSheet.updateCellValueAndCalculate(row, col, value, isUpdatePartOfSheetInitialization);
            if (possibleNewSheet != sheetVersionsArray.getLast()) { //the sheet has changed
                sheetVersionsArray.add(possibleNewSheet); //add the new sheet to the list
            }

            return sheetVersionsArray.getLast();
        }
        catch (Exception e) { //should we catch a more specific Exception? could there be a few types?
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @param version should be a number of requested version, starting from 1 (not 0)
     */
    @Override
    public Sheet getSheetOfSpecificVersion(int version) throws NoSuchElementException, IndexOutOfBoundsException {
        try {
            if (!sheetVersionsArray.isEmpty()) {
                return sheetVersionsArray.get(version - 1);
            }
            else {
                throw new NoSuchElementException("There are no sheets in the system.");
            }
        }
        catch(IndexOutOfBoundsException e){
            throw new IndexOutOfBoundsException("The requested version is not available.");
        }
    }

    /**
     * Returns a counter that starts from 1 (not 0).
     *
     * @return the number of the latest version, starting from 1
     */
    @Override
    public int getLatestVersionNumber() throws NoSuchElementException {
        if (!sheetVersionsArray.isEmpty()) {
            return sheetVersionsArray.size();
        }
        else {
            throw new NoSuchElementException("There is no sheet loaded to the system.");
        }
    }

    @Override
    public boolean isThereASheetLoadedToTheSystem() {  //returns true if there is a sheet loaded to the system
        if (sheetVersionsArray.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Range addRangeToMostRecentSheet(String rangeName, String leftTopStartCoordinateStr, String rightBottomEndCoordinateStr) {
        return RangeFactory.createRangeFromTwoCoordinateStringsAndNameString(sheetVersionsArray.getLast(), rangeName, leftTopStartCoordinateStr, rightBottomEndCoordinateStr);
    }

    @Override
    public Range getRangeFromMostRecentSheet(String rangeName) {
        return RangeFactory.getRangeByItsName(rangeName);
    }

    @Override
    public boolean isSelectedRangeUsedInAnyCellWithRelevantFunction(String rangeName) {
        return sheetVersionsArray.getLast().isSelectedRangeIsUsedInSheet(rangeName);
    }

    @Override
    public boolean isFilteringOrSortingAreaValid(String newFilterStartCoordinateStr, String newFilterEndCoordinateStr) {
        Sheet currentSheet = sheetVersionsArray.getLast();
        Coordinate topLeftStartCoordinate, bottomRightEndCoordinate;
        try {
            topLeftStartCoordinate = CoordinateFactory.getCoordinateFromStr(newFilterStartCoordinateStr, currentSheet);
            bottomRightEndCoordinate = CoordinateFactory.getCoordinateFromStr(newFilterEndCoordinateStr, currentSheet);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error trying to create filtering area from coordinates provided: " + e.getMessage());
        }
        return RangeFactory.isCoordinatesCreateValidRange(topLeftStartCoordinate, bottomRightEndCoordinate);
    }

    @Override
    public Range createRangeToSortOrFilter(String newFilterStartCoordinateStr, String newFilterEndCoordinateStr) {
        // method will be invoked only if the area and coordinates are valid
        Sheet currentSheet = sheetVersionsArray.getLast();
        Coordinate topLeftStartCoordinate = CoordinateFactory.getCoordinateFromStr(newFilterStartCoordinateStr, currentSheet);
        Coordinate bottomRightEndCoordinate = CoordinateFactory.getCoordinateFromStr(newFilterEndCoordinateStr, currentSheet);

        return new RangeImpl("CurrentRangeToFilterOrSort", topLeftStartCoordinate, bottomRightEndCoordinate);
    }

    @Override
    public Sheet createCopyOfRecentSheet() {
        return sheetVersionsArray.getLast().createCopyOfSheet();
    }

    @Override
    public RangeWithRowsInArea createFilteredRangeArea(Range rangeToFilter, char letterOfColumnToGetUniqueValuesToFilter, List<String> uniqueValuesToFilter) {
        try {
            if (rangeToFilter == null) {
                throw new IllegalArgumentException("Filtering range is null.");
            }
            if (uniqueValuesToFilter == null) {
                throw new IllegalArgumentException("List of unique values to filter is null.");
            }
            if (uniqueValuesToFilter.isEmpty()) {
                throw new IllegalArgumentException("List of unique values to filter is empty.");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        try {
            Sheet copyOfRecentSheetWithAreaToFilter = sheetVersionsArray.getLast().createCopyOfSheet();
            RangeWithRowsInArea rangeWithRowsInArea = new RangeWithRowsInArea(copyOfRecentSheetWithAreaToFilter, rangeToFilter);
            rangeWithRowsInArea.buildRangeFromAllRelevantCellsInRange();
            int columnNumFromChar = CoordinateFactory.getColumnNumberFromChar(letterOfColumnToGetUniqueValuesToFilter, copyOfRecentSheetWithAreaToFilter);
            int numOfIterationsLeftToPreform = rangeToFilter.getRowEnd() - rangeToFilter.getRowStart() + 1;
            int currentRowToCheck = rangeWithRowsInArea.getFirstRowNumToCheck();
            int lastRowToCheck = rangeWithRowsInArea.getLastRowNumToCheck();
//
//            int rowEnd = rangeToFilter.getRowEnd();
            int numOfRowsToCheck = rangeWithRowsInArea.getCurrentNumOfRows();
            int numOfRowsAlreadyChecked = 0;


            while (currentRowToCheck <= lastRowToCheck && numOfRowsAlreadyChecked < numOfRowsToCheck) {
                String currentEffectiveValueOfCellAsString = rangeWithRowsInArea.getEffectiveValueOfCellAsString(currentRowToCheck, columnNumFromChar);
                if (!uniqueValuesToFilter.contains(currentEffectiveValueOfCellAsString)) {
                    rangeWithRowsInArea.removeCurrentRowAndMoveAllRowsOneRowUp(currentRowToCheck);
                    currentRowToCheck--; //to check the next row that moved up
                }
                currentRowToCheck++; //even if the row was removed, the next row moved up ?
                numOfRowsAlreadyChecked++;
            }

            return rangeWithRowsInArea;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Error trying to create a copy of the sheet with filtered area: " + e.getMessage());
        }
    }

    @Override
    public RangeWithRowsInArea createSortedRangeArea(Range newSortingRange, List<Character> listOfColumnLettersCharactersToSortBy) {
        Sheet copyOfRecentSheetWithAreaToSort = sheetVersionsArray.getLast().createCopyOfSheet();
        RangeSorter rangeToSort = new RangeSorter(copyOfRecentSheetWithAreaToSort, newSortingRange,listOfColumnLettersCharactersToSortBy);
        rangeToSort.sortRange();

        return rangeToSort.getSortedRangeWithRows();
    }


    @Override
    public boolean isColumnLetterInFilteringOrSortingArea(String stringWithLetterOfSelectedColumn, String newAreaStartCoordinateStr, String newAreaEndCoordinateStr) {
        Sheet currentSheet = sheetVersionsArray.getLast();
        int columnNumFromChar;

        //getting here only if isFilteringOrSortingAreaValid() returned true,
        //so the coordinates are valid and will not throw an exception
        Coordinate topLeftStartCoordinate = CoordinateFactory.getCoordinateFromStr(newAreaStartCoordinateStr, currentSheet);
        Coordinate bottomRightEndCoordinate = CoordinateFactory.getCoordinateFromStr(newAreaEndCoordinateStr, currentSheet);

        if (stringWithLetterOfSelectedColumn.length() != 1) {
            throw new IllegalArgumentException("Column letter must be exactly one character long.");
        }
        try {
            columnNumFromChar = CoordinateFactory.getColumnNumberFromChar(stringWithLetterOfSelectedColumn.charAt(0), currentSheet);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error trying to reach requested column " + stringWithLetterOfSelectedColumn +
                    " in selected filtering area" + e.getMessage());
        }

        return columnNumFromChar >= topLeftStartCoordinate.getColumn() && columnNumFromChar <= bottomRightEndCoordinate.getColumn();
    }

    @Override
    public List<String> getUniqueValuesForFilteringInSelectedColumnAndRelevantArea
            (char charLetterOfColumnToGetUniqueValuesToFilter,
             String newFilterStartCoordinateStr,
             String newFilterEndCoordinateStr) {
        return sheetVersionsArray.getLast().getUniqueValuesForFilteringInSelectedColumnAndRelevantArea(charLetterOfColumnToGetUniqueValuesToFilter, newFilterStartCoordinateStr, newFilterEndCoordinateStr);
    }

    @Override
    public void deleteRangeFromRangeFactory(String rangeName) {
        try {
            RangeFactory.deleteRangeFromRangesFactory(rangeName);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public List<String> getAllRangeNamesInTheSystem() {
        return RangeFactory.getAllRangesNames();
    }

    @Override
    public Range getRangeByItsName(String rangeName) { return RangeFactory.getRangeByItsName(rangeName); }

    @Override
    public boolean isThereAnyRangeInRangesFactory() {
        return RangeFactory.isThereAnyRangeInRangesFactory();
    }


}
