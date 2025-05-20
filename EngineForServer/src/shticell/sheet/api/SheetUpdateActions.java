package shticell.sheet.api;

import shticell.cell.api.Cell;
import shticell.range.Range;
import shticell.sheet.impl.SheetImpl;

public interface SheetUpdateActions {
    Sheet updateCellValueAndCalculate(int row, int column, String value, boolean isUpdatePartOfSheetInitialization, String nameOfUserWhoCausedUpdateOfValue);
    Cell setNewEmptyCell(int row, int column);
    void addRangeToAllRangesReferencedInSheet(String rangeName, Range range);
    void increaseCounterOfReferencesToSelectedRange(String rangeName);
    public SheetImpl createCopyOfSheet();
}
