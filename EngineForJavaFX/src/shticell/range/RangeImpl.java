package shticell.range;

import shticell.coordinate.Coordinate;
import shticell.coordinate.CoordinateFactory;

import java.util.HashSet;
import java.util.Set;

public class RangeImpl implements Range {
    private final String rangeName;
    private final Coordinate topLeftStartCoordinate;
    private final Coordinate bottomRightEndCoordinate;
    private Set<Coordinate> allCoordinatesThatBelongToThisRange = new HashSet<>();
    private final int rowStart;
    private final int rowEnd;
    private final int columnStart;
    private final int columnEnd;


    public RangeImpl(String rangeName, Coordinate topLeftStartCoordinate, Coordinate bottomRightEndCoordinate) {
        this.rangeName = rangeName;
        this.topLeftStartCoordinate = topLeftStartCoordinate;
        this.bottomRightEndCoordinate = bottomRightEndCoordinate;
        this.rowStart = topLeftStartCoordinate.getRow();
        this.rowEnd = bottomRightEndCoordinate.getRow();
        this.columnStart = topLeftStartCoordinate.getColumn();
        this.columnEnd = bottomRightEndCoordinate.getColumn();
        updateAllCoordinatesThatBelongToThisRange();
    }

    private void updateAllCoordinatesThatBelongToThisRange() {
//        allCoordinatesThatBelongToThisRange = Set.of(topLeftStartCoordinate, bottomRightEndCoordinate);
//        rowStart = topLeftStartCoordinate.getRow();
//        rowEnd = bottomRightEndCoordinate.getRow();
//        columnStart = topLeftStartCoordinate.getColumn();
//        columnEnd = bottomRightEndCoordinate.getColumn();

        for (int currentRow = rowStart; currentRow <= rowEnd; currentRow++) {
            for (int currentColumn = columnStart; currentColumn <= columnEnd; currentColumn++) {
                allCoordinatesThatBelongToThisRange.add(CoordinateFactory.getCoordinate(currentRow, currentColumn));
            }
        }
    }

    @Override
    public String getRangeName() {
        return rangeName;
    }

    @Override
    public int getRowStart() {
        return rowStart;
    }

    @Override
    public int getRowEnd() {
        return rowEnd;
    }

    @Override
    public int getColumnStart() {
        return columnStart;
    }

    @Override
    public int getColumnEnd() {
        return columnEnd;
    }

    @Override
    public Coordinate getTopLeftStartCoordinate() {
        return topLeftStartCoordinate;
    }

    @Override
    public Coordinate getBottomRightEndCoordinate() {
        return bottomRightEndCoordinate;
    }

    @Override
    public Set<Coordinate> getAllCoordinatesThatBelongToThisRange() {
        return allCoordinatesThatBelongToThisRange;
    }

//    @Override
//    public boolean contains(Coordinate coordinate) {
//        return coordinate.row() >= topLeftStartCoordinate.row() && coordinate.row() <= bottomRightEndCoordinate.row() &&
//                coordinate.col() >= topLeftStartCoordinate.col() && coordinate.col() <= bottomRightEndCoordinate.col();
//    }

//    @Override
//    public int getRowCount() {
//        return 0;
//    }
//
//    @Override
//    public int getColumnCount() {
//        return 0;
//    }
//
//    @Override
//    public boolean contains(int row, int col) {
//        return false;
//    }
//
//    @Override
//    public boolean contains(Range range) {
//        return false;
//    }
//
//    @Override
//    public boolean isIntersecting(Range range) {
//        return false;
//    }
//
//    @Override
//    public Range getIntersection(Range range) {
//        return null;
//    }
//
//    @Override
//    public Range getUnion(Range range) {
//        return null;
//    }
//
//    @Override
//    public Range getDifference(Range range) {
//        return null;
//    }
//
//    @Override
//    public Range getCopy() {
//        return null;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (obj instanceof Range) {
//            return topLeftStartCoordinate.equals(((Range) obj).topLeft()) && bottomRightEndCoordinate.equals(((Range) obj).bottomRight());
//        }
//
//        return false;
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(topLeftStartCoordinate, bottomRightEndCoordinate);
//    }
//
//    @Override
//    public String toString() {
//        return topLeftStartCoordinate.toString() + ":" + bottomRightEndCoordinate.toString();
//    }
}
