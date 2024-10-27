package shticell.range;

import shticell.coordinate.Coordinate;

import java.util.Set;

public interface Range {
    String getRangeName();

    int getRowStart();

    int getRowEnd();

    int getColumnStart();

    int getColumnEnd();

    Coordinate getTopLeftStartCoordinate();

    Coordinate getBottomRightEndCoordinate();

    Set<Coordinate> getAllCoordinatesThatBelongToThisRange();

    int getNumOfRowsInRange();

}
