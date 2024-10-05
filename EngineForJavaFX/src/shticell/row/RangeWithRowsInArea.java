package shticell.row;

import shticell.cell.api.Cell;
import shticell.cell.api.EffectiveValue;
import shticell.coordinate.Coordinate;
import shticell.range.Range;
import shticell.sheet.api.Sheet;

import java.text.NumberFormat;
import java.util.*;

public class RangeWithRowsInArea {
    Sheet sheet;
    Range range;
    List<RowInArea> rowsInAreaList = new ArrayList<>();
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

//    private void buildRowsInAreaList() {
//        for (int i = range.getRowStart(); i <= range.getRowEnd(); i++) {
//            rowsInAreaList.add(new RowInArea(i));
//        }
//    }

    public void buildRangeFromAllRelevantCellsInRange() {
        for (int currentRow = range.getRowStart(); currentRow <= range.getRowEnd(); currentRow++) {
            for (int currentColumn = range.getColumnStart(); currentColumn <= range.getColumnEnd(); currentColumn++) {

                Cell currentCell = sheet.getCell(currentRow, currentColumn);
//                String effectiveValueOfCellAsString = "";
//
//                if (!currentCell.getIsCellEmptyBoolean()) {
//                    EffectiveValue effectiveValueOfCell = currentCell.getCurrentEffectiveValue();
//                    if (effectiveValueOfCell != null) {
//                        effectiveValueOfCellAsString = effectiveValueOfCell.getValue().toString();
//                    }
//                }
                addEffectiveValueToSelectedRowAndColumnInArea(currentRow, currentColumn, currentCell.getCurrentEffectiveValue());
            }
        }
    }

    public void addEffectiveValueToSelectedRowAndColumnInArea(int rowNumber, int column, EffectiveValue effectiveValue) {
//        if (rowsInAreaSet.contains(rowNumber)) {
//            rowsInAreaSet.add(new RowInArea(rowNumber));
//        }
        if (mapRowNumberToRowInArea.containsKey(rowNumber)) {
            mapRowNumberToRowInArea.get(rowNumber).setColumnWithEffectiveValue(column, effectiveValue);
        } else {

            RowInArea rowInArea = new RowInArea(rowNumber);
            rowInArea.setColumnWithEffectiveValue(column, effectiveValue);
//            rowsInAreaList.add(rowInArea);
            mapRowNumberToRowInArea.put(rowNumber, rowInArea);
        }

    }

//    public void removeFirstRowAndMoveAllRowsOneRowUp() {
//        mapRowNumberToRowInArea.remove(range.getRowStart());
//        for (int i = range.getRowStart() + 1; i <= range.getRowEnd(); i++) {
//            mapRowNumberToRowInArea.get(i).setRowNumber(i - 1);
//        }
//    }

    public void removeCurrentRowAndMoveAllRowsOneRowUp(int rowNumberToRemove) {
//        for (int i = 0 ; i < rowsInAreaList.size(); i++) {
//            if (rowsInAreaList.get(i).getRowNumber() == rowNumberToRemove) {
//                rowsInAreaList.remove(rowInArea);
//                break;
//            }
//        }
//        int numOfRowsLeftInTheRange = mapRowNumberToRowInArea.size();
        int numOfRowsLeftInTheRange = mapRowNumberToRowInArea.size();
//        mapRowNumberToRowInArea.remove(rowNumberToRemove);
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
//                mapRowNumberToRowInArea.put(currentRowToMoveUp - 1, currentRowInArea); // move row one row up in map
//                mapRowNumberToRowInArea.get(currentRowToMoveUp).setRowNumber(currentRowToMoveUp - 1);
//                mapRowNumberToRowInArea.put(currentRowToMoveUp - 1, mapRowNumberToRowInArea.get(currentRowToMoveUp)); // move row one row up in map
            }
        }
//        //checking if the last row in the map is the row that was removed
//        if (lastRowInMap != rowNumberToRemove) {
//            mapRowNumberToRowInArea.remove(lastRowInMap);
//        }

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
//            return mapRowNumberToRowInArea.get(selectedRow).getEffectiveValueOfColumn(selectedColumn);
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

//    public boolean isThereUniqueValueInSelectedRowAndColumn(int row, int column, List<String> effectiveValue) {
//        boolean isThereUniqueValue = false;
//        if (!mapRowNumberToRowInArea.containsKey(row)) {
//            return false;
//        }
//        for (String value : effectiveValue) {
//            if (mapRowNumberToRowInArea.get(row).isUniqueValueInThisRowAndColumn(column, value)) {
//                isThereUniqueValue = true;
//                break;
//            }
//        }
//        return isThereUniqueValue;
//    }
}
