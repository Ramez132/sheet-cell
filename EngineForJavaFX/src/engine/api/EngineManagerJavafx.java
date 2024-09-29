package engine.api;

import shticell.cell.api.Cell;
import shticell.range.Range;
import shticell.sheet.api.Sheet;

import java.io.File;

public interface EngineManagerJavafx {
    Sheet getSheetFromFile(File file) throws Exception;
    Sheet getMostRecentSheet();

    /**
     * Returns a cell from the most recent sheet.
     * if the cell is not present in activeCells in the sheet, it will be created as a new empty cell
     * @param row    the row of the cell
     * @param col the column of the cell
     * @return the cell from the most recent sheet
     */
    Cell getCellFromMostRecentSheet(int row, int col);
    Sheet updateValueOfCellAndGetNewSheet(int row, int col, String value);

    /**
     * @param version should be a number of requested version, starting from 1 (not 0)
     */
    Sheet getSheetOfSpecificVersion(int version);
    int getLatestVersionNumber();
    boolean isThereASheetLoadedToTheSystem();

    Range addRangeToMostRecentSheet(String rangeName, String leftTopStartCoordinateStr, String rightBottomEndCoordinateStr);
    Range getRangeFromMostRecentSheet(String rangeName);
    void deleteRangeFromMostRecentSheet(String rangeName);
}
