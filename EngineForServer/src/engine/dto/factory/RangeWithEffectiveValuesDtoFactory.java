package engine.dto.factory;

import dto.coordinate.CoordinateDto;
import dto.range.RangeDto;
import dto.range.RangeWithEffectiveValuesDto;
import shticell.coordinate.Coordinate;
import shticell.range.Range;
import shticell.row.RangeWithRowsInArea;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RangeWithEffectiveValuesDtoFactory {

    public static RangeWithEffectiveValuesDto createRangeWithEffectiveValuesDto(RangeWithRowsInArea rangeWithRowsInArea) {
        Range range = rangeWithRowsInArea.getRange();
        RangeDto rangeDto = RangeDtoFactory.createRangeDtoFromRange(range);

        Set<Coordinate> allCoordinatesThatBelongToThisRange = range.getAllCoordinatesThatBelongToThisRange();
        Map<CoordinateDto, String> allEffectiveValues = new HashMap<>();

        for (Coordinate coordinate : allCoordinatesThatBelongToThisRange) {
            CoordinateDto coordinateDto = new CoordinateDto(coordinate.getRow(), coordinate.getColumn());
            String effectiveValueString = rangeWithRowsInArea.getEffectiveValueForCoordinate(coordinate);
            allEffectiveValues.put(coordinateDto, effectiveValueString);
        }

        return new RangeWithEffectiveValuesDto(rangeDto, allEffectiveValues);
    }
}
