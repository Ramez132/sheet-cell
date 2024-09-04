package shticell.sheet.api;

import shticell.cell.api.Cell;

public interface SheetUpdateActions {
    Sheet updateCellValueAndCalculate(int row, int column, String value, boolean isUpdatePartOfSheetInitialization);
    Cell setNewEmptyCell(int row, int column);
}
