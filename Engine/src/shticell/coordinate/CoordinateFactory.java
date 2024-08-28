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

    public static Coordinate from(String trim) {
        try {
            cell_id = trim.toUpperCase();
            int[] parts = convertCellIdToIndex(cell_id);
            return createCoordinate(parts[0], parts[1]);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
