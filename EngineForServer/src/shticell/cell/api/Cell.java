package shticell.cell.api;

import shticell.coordinate.Coordinate;
import shticell.range.Range;

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
    void insertDependsOnMapFromCellBeforeUpdate(Map<Coordinate, Cell> dependsOnMapOfCopiedCell);
    void removeSelectedCoordinateFromInfluencingOnMap(Coordinate selectedCoordinate);
    Map<String, Range> getRangesReferencedInCell();
    void removeRangeFromRangesReferencedInCell(String rangeName);
    int getCounterOfReferencesToSelectedRange(String rangeName);
    String getNameOfUserWhoCausedUpdateOfValue();
    void setNameOfUserWhoCausedUpdateOfValue(String nameOfUserWhoCausedUpdateOfValue);
}