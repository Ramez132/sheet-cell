package shticell.manager.api;

import shticell.cell.api.Cell;
import shticell.sheet.api.Sheet;

public interface Manager {
    Sheet getSheetFromFile(String fileName) throws Exception;
    Sheet getMostRecentSheet();
    //Sheet displaySheet(Sheet sheet);
    Cell getCellFromSheet(Sheet sheet, int row, int col);
    Sheet updateValueOfCellAndDisplayNewSheet(Sheet sheet, int row, int col, String value);
    Sheet getSheetOfSpecificVersion(int version);
    int getLatestVersionNumber();
}
