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

//    int getRowCount();
//
//    int getColumnCount();
//
//    boolean contains(int row, int col);
//
//    boolean contains(Range range);
//
//    boolean isIntersecting(Range range);
//
//    Range getIntersection(Range range);
//
//    Range getUnion(Range range);
//
//    Range getDifference(Range range);
//
//    Range getCopy();
//
//    boolean equals(Object obj);
//
//    int hashCode();
//
//    String toString();
}
