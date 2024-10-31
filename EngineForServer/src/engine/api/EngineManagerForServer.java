package engine.api;

import shticell.cell.api.Cell;
import shticell.range.Range;
import shticell.row.RangeWithRowsInArea;
import shticell.sheet.api.Sheet;

import java.io.File;
import java.util.List;

public interface EngineManagerForServer {
    Sheet getSheetFromFile(File file) throws Exception;
    Sheet getMostRecentSheetWithSelectedName(String sheetName);
//    Sheet getMostRecentSheet();

    /**
     * Returns a cell from the most recent sheet.
     * if the cell is not present in activeCells in the sheet, it will be created as a new empty cell
     * @param row    the row of the cell
     * @param col the column of the cell
     * @return the cell from the most recent sheet
     */
    Cell getCellFromMostRecentSheetWithSelectedName(String sheetName, int row, int col);
    Sheet updateValueOfCellAndGetNewSheetWithSelectedName(String sheetName, int row, int col, String value);

    /**
     * @param version should be a number of requested version, starting from 1 (not 0)
     */
    Sheet getSheetOfSpecificVersion(int version);
    int getLatestVersionNumber();
    boolean isThereASheetLoadedToTheSystem();

    Range addRangeToMostRecentSheet(String rangeName, String leftTopStartCoordinateStr, String rightBottomEndCoordinateStr);
    Range getRangeFromMostRecentSheet(String rangeName);
    void deleteRangeFromRangeFactory(String rangeName);
    List<String> getAllRangeNamesInTheSystem();
    boolean isThereAnyRangeInRangesFactory();
    Range getRangeByItsName(String rangeName);
    boolean isSelectedRangeUsedInAnyCellWithRelevantFunction(String rangeName);
    boolean isFilteringOrSortingAreaValid(String newFilterStartCoordinateStr, String newFilterEndCoordinateStr);

    boolean isColumnLetterInFilteringOrSortingArea
            (String stringWithLetterOfSelectedColumn, String newAreaStartCoordinateStr, String newAreaEndCoordinateStr);

    List<String> getUniqueValuesForFilteringInSelectedColumnAndRelevantArea
            (char charLetterOfColumnToGetUniqueValuesToFilter, String newFilterStartCoordinateStr, String newFilterEndCoordinateStr);

    /**
    * method will be invoked only if the area and coordinates are valid
     */
    Range createRangeToSortOrFilter(String newFilterStartCoordinateStr, String newFilterEndCoordinateStr);

//    Sheet createCopyOfSheet();

    Sheet createCopyOfRecentSheet();

    RangeWithRowsInArea createFilteredRangeArea
            (Range filteringRange, char letterOfColumnToGetUniqueValuesToFilter, List<String> uniqueValuesToFilter);

    RangeWithRowsInArea createSortedRangeArea(Range newSortingRange, List<Character> listOfColumnLettersCharactersToSortBy);
}
