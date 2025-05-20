package dto.management.info;

import dto.sheet.SheetDto;
import java.util.List;


public record SheetAndRangesNamesDto(SheetDto sheetDto, List<String> rangesNames) {
}
