package shticell.range;

import shticell.coordinate.Coordinate;
import shticell.coordinate.CoordinateFactory;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class RangeImpl implements Range, Serializable {
    private String rangeName;
    private final Coordinate topLeftStartCoordinate;
    private final Coordinate bottomRightEndCoordinate;
    private Set<Coordinate> allCoordinatesThatBelongToThisRange = new HashSet<>();
    private final int rowStart;
    private final int rowEnd;
    private final int columnStart;
    private final int columnEnd;

    public RangeImpl(Coordinate topLeftStartCoordinate, Coordinate bottomRightEndCoordinate) {
        this.topLeftStartCoordinate = topLeftStartCoordinate;
        this.bottomRightEndCoordinate = bottomRightEndCoordinate;
        this.rowStart = topLeftStartCoordinate.getRow();
        this.rowEnd = bottomRightEndCoordinate.getRow();
        this.columnStart = topLeftStartCoordinate.getColumn();
        this.columnEnd = bottomRightEndCoordinate.getColumn();
        updateAllCoordinatesThatBelongToThisRange();
    }

    public RangeImpl(String rangeName, Coordinate topLeftStartCoordinate, Coordinate bottomRightEndCoordinate) {
        this(topLeftStartCoordinate,bottomRightEndCoordinate);
        this.rangeName = rangeName;
    }

    private void updateAllCoordinatesThatBelongToThisRange() {
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

    @Override
    public int getNumOfRowsInRange() {
        return rowEnd - rowStart + 1;
    }
}
