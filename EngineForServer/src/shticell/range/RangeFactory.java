package shticell.range;

import shticell.coordinate.Coordinate;
import shticell.coordinate.CoordinateFactory;
import shticell.sheet.api.SheetReadActions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RangeFactory {

    private final static Map<String,Range> allCachedRanges = new HashMap<>();
    private final static Map<String,Range> cachedRangesFromPreviousFile = new HashMap<>();

    public static Range createNewRangeOrGetExistingRange(String rangeName, Coordinate topLeftStartCoordinate, Coordinate bottomRightEndCoordinate) {
        if (allCachedRanges.containsKey(rangeName)) {
            return allCachedRanges.get(rangeName);
        }

        RangeImpl range = new RangeImpl(rangeName, topLeftStartCoordinate, bottomRightEndCoordinate);
        allCachedRanges.put(rangeName, range);

        return range;
    }

    public static boolean isRangeNameAlreadyExistsInTheSystem(String rangeName) {
        return allCachedRanges.containsKey(rangeName);
    }

    public static Range getRangeByItsName(String rangeName) {
        if (allCachedRanges.containsKey(rangeName)) {
            return allCachedRanges.get(rangeName);
        } else {
            throw new IllegalArgumentException("Range with name " + rangeName + " does not exist.");
        }
    }

    public static Range createRangeFromTwoCoordinateStringsAndNameString
            (SheetReadActions sheet, String rangeName, String leftTopStartCoordinateStr, String rightBottomEndCoordinateStr) {

        Coordinate topLeftStartCoordinate, bottomRightEndCoordinate;

        if (isRangeNameAlreadyExistsInTheSystem(rangeName)) {
            throw new IllegalArgumentException("Range with name " + rangeName + " already exists. The system does not allow duplicate range names.");
        }

        try {
            topLeftStartCoordinate = CoordinateFactory.getCoordinateFromStr(leftTopStartCoordinateStr, sheet);
            bottomRightEndCoordinate = CoordinateFactory.getCoordinateFromStr(rightBottomEndCoordinateStr, sheet);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error trying to create range from coordinates provided: " + e.getMessage());
        }

        if (isCoordinatesCreateValidRange(topLeftStartCoordinate, bottomRightEndCoordinate)) {
                return createNewRangeOrGetExistingRange(rangeName, topLeftStartCoordinate, bottomRightEndCoordinate);
        } else {
            throw new IllegalArgumentException
            ("Error when trying to create range " + rangeName + " - row and column of start coordinate must be smaller or equal to row and column of end coordinate");
        }
    }

    public static boolean isCoordinatesCreateValidRange(Coordinate leftTopStartCoordinate, Coordinate rightBottomEndCoordinate) {
        boolean isRowStartEqualOrSmallerThanRowEnd = leftTopStartCoordinate.getRow() <= rightBottomEndCoordinate.getRow();
        boolean isColumnStartEqualOrSmallerThanColumnEnd = leftTopStartCoordinate.getColumn() <= rightBottomEndCoordinate.getColumn();
        return isRowStartEqualOrSmallerThanRowEnd && isColumnStartEqualOrSmallerThanColumnEnd;
    }

    public static void deleteAllRangesInRangesFactoryBeforeLoadingSheetFromNewFile() {
        allCachedRanges.clear();
    }

    public static void deleteRangeFromRangesFactory(String rangeName) {
        if (allCachedRanges.containsKey(rangeName)) {
            allCachedRanges.remove(rangeName);
        } else {
            throw new IllegalArgumentException("Range with name '" + rangeName + "' does not exist.");
        }
    }

    public static boolean isThereAnyRangeInRangesFactory() {
        return !allCachedRanges.isEmpty();
    }

    public static List<String> getAllRangesNames() {
        return List.copyOf(allCachedRanges.keySet());
    }

    public static void cacheRangesBeforeLoadingNewFileInCaseFileLoadingFails() {
        cachedRangesFromPreviousFile.clear();
        cachedRangesFromPreviousFile.putAll(allCachedRanges);
        allCachedRanges.clear();
    }

    public static void restoreRangesFromPreviousFileAfterFileLoadingFailed() {
        allCachedRanges.clear();
        allCachedRanges.putAll(cachedRangesFromPreviousFile);
        cachedRangesFromPreviousFile.clear();
    }
}
