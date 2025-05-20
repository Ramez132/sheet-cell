package engine.dto.factory;

import dto.coordinate.CoordinateDto;
import shticell.coordinate.Coordinate;

import java.util.Set;
import java.util.stream.Collectors;

public class CoordinateDtoFactory {

    public static Set<CoordinateDto> createCoordinateDtoSetFromCoordinateSet(Set<Coordinate> coordinatesSet) {
        return coordinatesSet.stream()
                .map(coordinate -> new CoordinateDto(coordinate.getRow(), coordinate.getColumn()))
                .collect(Collectors.toSet());
    }

}
