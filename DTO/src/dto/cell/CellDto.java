package dto.cell;

import dto.coordinate.CoordinateDto;

import java.util.Set;

public record CellDto(CoordinateDto coordinateDto,
                      String originalValueStr,
                      String effectiveValueStr,
                      int lastVersionInWhichCellHasChanged,
                      String userNameOfLastChange,
                      Set<CoordinateDto> dependsOnCoordinatesSet,
                      Set<CoordinateDto> influencesOnCoordinatesSet) {

}
