package dto.sort;

import java.util.List;

public record SortParametersDto(String newSortStartCoordinateStr,
                                String newSortEndCoordinateStr,
                                List<String> allColumnLettersToSortByAsStrings) {
}
