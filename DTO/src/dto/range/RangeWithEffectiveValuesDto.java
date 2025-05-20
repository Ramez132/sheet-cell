package dto.range;

import dto.coordinate.CoordinateDto;

import java.util.Map;

public record RangeWithEffectiveValuesDto(RangeDto rangeDto,
                                          Map<CoordinateDto, String> allEffectiveValuesStrings) {
}
