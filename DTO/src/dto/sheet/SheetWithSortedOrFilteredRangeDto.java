package dto.sheet;

import dto.range.RangeWithEffectiveValuesDto;

public record SheetWithSortedOrFilteredRangeDto
        (SheetDto sheetDto,
         RangeWithEffectiveValuesDto rangeWithEffectiveValuesDto ) {
}
