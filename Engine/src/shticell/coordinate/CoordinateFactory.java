package shticell.coordinate;

import java.util.HashMap;
import java.util.Map;

public class CoordinateFactory {

    private static Map<String, Coordinate> cachedCoordinates = new HashMap<>();

    public static Coordinate createCoordinate(int row, int column) {

        String key = row + ":" + column;
        if (cachedCoordinates.containsKey(key)) {
            return cachedCoordinates.get(key);
        }

        CoordinateImpl coordinate = new CoordinateImpl(row, column);
        cachedCoordinates.put(key, coordinate);

        return coordinate;
    }

//    public static Coordinate from(String trim) {
//        try {
//            cell_id = trim.toUpperCase();
//            int[] parts = convertCellIdToIndex(cell_id);
//            return createCoordinate(parts[0], parts[1]);
//        } catch (NumberFormatException e) {
//            return null;
//        }
//    }

    public static Coordinate getCoordinate(int row, int column) {
        return createCoordinate(row, column);
    }

    public static Coordinate getCoordinateFromStr(String rowAndColStr) {

        int columnNumFromChar, rowNum, firstIndex = 0;
        char letterChar = rowAndColStr.toUpperCase().charAt(firstIndex);

        if (rowAndColStr.length() <= 1) {
            throw new IllegalArgumentException("Row and column string must be longer than 1 character.");
        }

        if (letterChar >= 'A' && letterChar <= 'T') {
            columnNumFromChar = letterChar - 'A' + 1;
        } else {
            throw new IllegalArgumentException
                ("First character, representing a column number, must be a letter from A to T (for columns 1 to max 20)");
        }

        String numberPart = rowAndColStr.substring(1);
        try {
            rowNum = Integer.parseInt(numberPart);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The part after the first character must be a number.");
        }

        if (rowNum < 1 || rowNum > 50) {
            throw new IllegalArgumentException("The number must be between 1 and 50.");
        }

        return createCoordinate(rowNum,columnNumFromChar);
    }
}
