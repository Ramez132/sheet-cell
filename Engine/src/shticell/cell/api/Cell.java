package shticell.cell.api;

import shticell.coordinate.Coordinate;

import java.util.List;

public interface Cell {
    Coordinate getCoordinate();
    String getOriginalValueStr();
    void setCellOriginalValue(String value);
    EffectiveValue getEffectiveValue();
    boolean calculateEffectiveValue();
    int getLastVersionInWhichCellHasChanged();
    List<Cell> getDependsOn();
    List<Cell> getInfluencingOn();
    boolean getIsCellEmptyBoolean();
}