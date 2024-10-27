package shticell.coordinate;

import shticell.sheet.api.SheetReadActions;

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

    public static Coordinate getCoordinate(int row, int column) {
        return createCoordinate(row, column);
    }

    public static Coordinate getCoordinateFromStr(String rowAndColStr, SheetReadActions sheet) {

        int columnNumFromChar, rowNum, firstIndex = 0;
        char letterChar = rowAndColStr.toUpperCase().charAt(firstIndex);

        if (rowAndColStr.length() <= 1) {
            throw new IllegalArgumentException("Tried to reach " + rowAndColStr + ", but row and column string must be longer than 1 character.");
        }
        try {
            columnNumFromChar = getColumnNumberFromChar(letterChar, sheet);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tried to reach " + rowAndColStr + ", but " + e.getMessage());
        }

        String numberPart = rowAndColStr.substring(1);
        try {
            rowNum = Integer.parseInt(numberPart);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Tried to reach " + rowAndColStr + ", but the part after the first character must be a valid number.");
        }

        if (rowNum > sheet.getNumOfRows()) {
            throw new IllegalArgumentException("Tried to reach " + rowAndColStr +
                    ", but row number provided (" + rowNum + ") is greater than the maximum row number of the sheet (" + sheet.getNumOfRows() + ")");
        }
        if (rowNum < 1) {
            throw new IllegalArgumentException("Tried to reach " + rowAndColStr + ", but row number provided is less than 1 - not possible");
        }

        return createCoordinate(rowNum,columnNumFromChar);
    }

    public static int getColumnNumberFromChar(char letterChar, SheetReadActions sheet) {
        int columnNumFromChar;
        if (letterChar >= 'A' && letterChar <= 'T') {
            columnNumFromChar = letterChar - 'A' + 1;
        } else {
            throw new IllegalArgumentException ("character representing a column number must be a letter from A to T (for columns 1 to max 20)");
        }
        if (columnNumFromChar > sheet.getNumOfColumns()) {
            throw new IllegalArgumentException(" column character provided (" + letterChar + "), which represents column number "
                    + columnNumFromChar + ", is greater than the maximum column number of the sheet (" + sheet.getNumOfColumns() + ")");
        }
        return columnNumFromChar;
    }
}
