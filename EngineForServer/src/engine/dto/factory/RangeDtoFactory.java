package engine.dto.factory;

import dto.coordinate.CoordinateDto;
import dto.range.RangeDto;
import shticell.range.Range;

import java.util.Set;

public class RangeDtoFactory {

    public static RangeDto createRangeDtoFromRange(Range range) {
        CoordinateDto topLeftStartCoordinateDto = new CoordinateDto(range.getTopLeftStartCoordinate().getRow(),
                                                                 range.getTopLeftStartCoordinate().getColumn());
        CoordinateDto bottomRightEndCoordinateDto = new CoordinateDto(range.getBottomRightEndCoordinate().getRow(),
                                                                   range.getBottomRightEndCoordinate().getColumn());
        Set<CoordinateDto> allCoordinatesDtoThatBelongToThisRange =
                CoordinateDtoFactory.createCoordinateDtoSetFromCoordinateSet(range.getAllCoordinatesThatBelongToThisRange());

        return new RangeDto(range.getRangeName(),
                            topLeftStartCoordinateDto,
                            bottomRightEndCoordinateDto,
                            allCoordinatesDtoThatBelongToThisRange,
                            range.getRowStart(),
                            range.getRowEnd(),
                            range.getColumnStart(),
                            range.getColumnEnd());
    }
}
