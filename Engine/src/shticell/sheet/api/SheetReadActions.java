package shticell.sheet.api;

import shticell.cell.api.Cell;
import shticell.coordinate.Coordinate;

import java.util.Map;

public interface SheetReadActions {
    int getVersion();
    Cell getCell(int row, int column);
    String getNameOfSheet();
    int getNumOfRows();
    int getNumOfColumns();
    int getRowHeight();
    int getColumnWidth();
    boolean isCoordinateInSheetRange(int row, int column);
    boolean isCellEmpty(int row, int column);
    boolean isCellsCollectionContainsCoordinate(int row, int column);
    Map<Coordinate, Cell> getActiveCells();
}
