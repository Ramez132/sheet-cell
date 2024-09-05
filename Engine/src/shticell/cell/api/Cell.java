package shticell.cell.api;

import shticell.coordinate.Coordinate;

import java.util.Map;

public interface Cell {
    Coordinate getCoordinate();
    String getOriginalValueStr();
    void setCellOriginalValue(String value);
    EffectiveValue getCurrentEffectiveValue();
    EffectiveValue getPreviousEffectiveValue();
    void setPreviousEffectiveValue(EffectiveValue effectiveValue);
    boolean calculateNewEffectiveValueAndDetermineIfItChanged();
    int getLastVersionInWhichCellHasChanged();
    void setLastVersionInWhichCellHasChanged(int version);
    Map<Coordinate, Cell> getDependsOnMap();
    Map<Coordinate, Cell> getInfluencingOnMap();
    boolean getIsCellEmptyBoolean();
    void insertInfluencingOnMapFromCellBeforeUpdate(Map<Coordinate, Cell> influencingOnMap);
}