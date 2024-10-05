package shticell.row;

import shticell.cell.api.EffectiveValue;

import java.util.HashMap;
import java.util.Map;

public class RowInArea {
    private int rowNumber;
    Map<Integer, EffectiveValue> mapColumnToEffectiveValueString = new HashMap<>();

    public RowInArea(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public void setColumnWithEffectiveValue(int column, EffectiveValue effectiveValue) {
        mapColumnToEffectiveValueString.put(column, effectiveValue);
    }

    public boolean isColumnInRow(int column) {
        return mapColumnToEffectiveValueString.containsKey(column);
    }

    public EffectiveValue getEffectiveValueOfColumn(int column) {
        return mapColumnToEffectiveValueString.get(column);
    }

    public boolean isUniqueValueInThisRowAndColumn(int column, EffectiveValue effectiveValue) {
        return mapColumnToEffectiveValueString.get(column).equals(effectiveValue);
    }
}
