package shticell.row;

import shticell.cell.api.Cell;
import shticell.cell.api.EffectiveValue;
import shticell.coordinate.Coordinate;
import shticell.range.Range;
import shticell.sheet.api.Sheet;

import java.text.NumberFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RangeWithRowsInArea {
    Sheet sheet;
    Range range;
    Map<Integer, RowInArea> mapRowNumberToRowInArea = new HashMap<>();

    public RangeWithRowsInArea(Sheet sheet, Range range) {
        this.sheet = sheet;
        this.range = range;
//        buildRowsInAreaList();
        buildRangeWithRowsInAreaMapCollection();
    }

    public RangeWithRowsInArea(Sheet sheet) {
        this.sheet = sheet;
    }

    public RangeWithRowsInArea(Range range) {
        this.range = range;
    }

    public RangeWithRowsInArea(){}  ;

    public Map<Integer, RowInArea> getMapRowNumberToRowInArea() {
        return mapRowNumberToRowInArea;
    }

    public void addRowInAreaToMap(int rowNumber, RowInArea rowInArea) {
        mapRowNumberToRowInArea.put(rowNumber, rowInArea);
    }

    public RowInArea getSelectedRowInAreaFromMap(int rowNumber) {
        return mapRowNumberToRowInArea.get(rowNumber);
    }

    public int getCurrentNumOfRows() {
        return mapRowNumberToRowInArea.size();
    }

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    public Coordinate getTopLeftStartCoordinate() {
        return range.getTopLeftStartCoordinate();
    }

    public Coordinate getBottomRightEndCoordinate() {
        return range.getBottomRightEndCoordinate();
    }

    public void buildRangeWithRowsInAreaMapCollection() {
        for (int i = range.getRowStart(); i <= range.getRowEnd(); i++) {
            mapRowNumberToRowInArea.put(i, new RowInArea(i));
        }
    }

    public void buildRangeFromAllRelevantCellsInRange() {
        for (int currentRow = range.getRowStart(); currentRow <= range.getRowEnd(); currentRow++) {
            for (int currentColumn = range.getColumnStart(); currentColumn <= range.getColumnEnd(); currentColumn++) {

                Cell currentCell = sheet.getCell(currentRow, currentColumn);
                addEffectiveValueToSelectedRowAndColumnInArea(currentRow, currentColumn, currentCell.getCurrentEffectiveValue());
            }
        }
    }

    public void addEffectiveValueToSelectedRowAndColumnInArea(int rowNumber, int column, EffectiveValue effectiveValue) {

        if (mapRowNumberToRowInArea.containsKey(rowNumber)) {
            mapRowNumberToRowInArea.get(rowNumber).setColumnWithEffectiveValue(column, effectiveValue);
        } else {

            RowInArea rowInArea = new RowInArea(rowNumber);
            rowInArea.setColumnWithEffectiveValue(column, effectiveValue);
            mapRowNumberToRowInArea.put(rowNumber, rowInArea);
        }

    }

    public void removeCurrentRowAndMoveAllRowsOneRowUp(int rowNumberToRemove) {
        int numOfRowsLeftInTheRange = mapRowNumberToRowInArea.size();
        int lastRowInMap = mapRowNumberToRowInArea.keySet().stream().max(Comparator.naturalOrder()).orElse(0);
        int firstRowInMap = mapRowNumberToRowInArea.keySet().stream().min(Comparator.naturalOrder()).orElse(0);

        Map<Integer, RowInArea> newMapRowNumberToRowInArea = new HashMap<>();
        for (int i = firstRowInMap; i < rowNumberToRemove; i++) {
            newMapRowNumberToRowInArea.put(i, mapRowNumberToRowInArea.get(i));
        }

        for (int currentRowToMoveUp = rowNumberToRemove + 1; currentRowToMoveUp <= lastRowInMap; currentRowToMoveUp++) {
            if (mapRowNumberToRowInArea.containsKey(currentRowToMoveUp)) {
                int updatedRowNumber = currentRowToMoveUp - 1;
                RowInArea currentRowInArea = mapRowNumberToRowInArea.get(currentRowToMoveUp);
                currentRowInArea.setRowNumber(updatedRowNumber);
                newMapRowNumberToRowInArea.put(updatedRowNumber, currentRowInArea);
            }
        }

        mapRowNumberToRowInArea.clear();
        mapRowNumberToRowInArea = newMapRowNumberToRowInArea;
    }

    public int getFirstRowNumToCheck() {
        return mapRowNumberToRowInArea.keySet().stream().min(Comparator.naturalOrder()).orElse(0);
    }

    public int getLastRowNumToCheck() {
        return mapRowNumberToRowInArea.keySet().stream().max(Comparator.naturalOrder()).orElse(0);
    }



    public String getEffectiveValueOfCellAsString(int selectedRow, int selectedColumn) {
        if (!mapRowNumberToRowInArea.containsKey(selectedRow)) {
            return "";
        }
        if (mapRowNumberToRowInArea.get(selectedRow).isColumnInRow(selectedColumn)) {
            EffectiveValue effectiveValueOfCell = mapRowNumberToRowInArea.get(selectedRow).getEffectiveValueOfColumn(selectedColumn);
            String effectiveValueOfCellAsString;
                if (effectiveValueOfCell == null) {
                    effectiveValueOfCellAsString = addThousandsSeparator("");
                } else {
                    effectiveValueOfCellAsString = addThousandsSeparator(effectiveValueOfCell.getValue().toString());
                }

            return effectiveValueOfCellAsString;
        }
        return "";
    }

    private String addThousandsSeparator(String number) throws NumberFormatException {
        try {
            return NumberFormat.getNumberInstance(Locale.US).format(Double.parseDouble(number));
        }
        catch (NumberFormatException e) {
            return number;
        }
    }

    public double getEffectiveValueOfCellAsDouble(int currentRow, int columnNumberFromChar) {
        return Double.parseDouble(getEffectiveValueOfCellAsString(currentRow, columnNumberFromChar));
    }
}
