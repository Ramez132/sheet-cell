package shticell.sheet.api;

import shticell.cell.api.Cell;
import shticell.coordinate.Coordinate;
import shticell.range.RangesManager;

import java.util.List;
import java.util.Map;

public interface SheetReadActions {
    int getVersion();
    Cell getCell(int row, int column);
    String getNameOfSheet();
    int getNumOfRows();
    int getNumOfColumns();
    int getRowHeight();
    int getColumnWidth();
    RangesManager getRangesManager();
    boolean isCoordinateInSheetRange(int row, int column);
    boolean isCellEmpty(int row, int column);
    boolean isCellsCollectionContainsCoordinate(int row, int column);
    Map<Coordinate, Cell> getActiveCells();
    int getVersionNumForEmptyCellWithoutPreviousValues();
    int getNumOfCellsWhichEffectiveValueChangedInNewVersion();
    boolean isSelectedRangeIsUsedInSheet(String rangeName);
    List<String> getUniqueValuesForFilteringInSelectedColumnAndRelevantArea(char charLetterOfColumnToGetUniqueValuesToFilter, String newFilterStartCoordinateStr, String newFilterEndCoordinateStr);
}
