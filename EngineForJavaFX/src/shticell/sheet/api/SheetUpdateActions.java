package shticell.sheet.api;

import shticell.cell.api.Cell;
import shticell.range.Range;

public interface SheetUpdateActions {
    Sheet updateCellValueAndCalculate(int row, int column, String value, boolean isUpdatePartOfSheetInitialization);
    Cell setNewEmptyCell(int row, int column);
    void addRangeToAllRangesReferencedInSheet(String rangeName, Range range);
}
