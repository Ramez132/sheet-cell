package dto.range;

import dto.coordinate.CoordinateDto;

import java.util.HashSet;
import java.util.Set;

public record RangeDto(String rangeName,
                        CoordinateDto topLeftStartCoordinate,
                        CoordinateDto bottomRightEndCoordinate,
                        Set<CoordinateDto> allCoordinatesDtoThatBelongToThisRange,
                        int rowStart,
                        int rowEnd,
                        int columnStart,
                        int columnEnd) {}
