package shticell.cell.api;

import shticell.coordinate.Coordinate;

import java.util.List;
import java.util.Map;

public interface Cell {
    Coordinate getCoordinate();
    String getOriginalValueStr();
    void setCellOriginalValue(String value);
    EffectiveValue getEffectiveValue();
    boolean calculateEffectiveValue();
    int getLastVersionInWhichCellHasChanged();
    Map<Coordinate, Cell> getDependsOnMap();
    Map<Coordinate, Cell> getInfluencingOnMap();
    //List<Cell> getDependsOnMap();
    //List<Cell> getInfluencingOnMap();
    boolean getIsCellEmptyBoolean();
    void insertInfluencingOnMapFromCellBeforeUpdate(Map<Coordinate, Cell> influencingOnMap);
}