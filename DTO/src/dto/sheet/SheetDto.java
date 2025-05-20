package dto.sheet;

import dto.cell.CellDto;
import dto.coordinate.CoordinateDto;

import java.util.Map;

public record SheetDto( String sheetName,
                        Map<CoordinateDto, CellDto> coordinateToCellDtoMap,
                        int numOfRows, int numOfColumns,
                        int rowHeight, int columnWidth,
                        int thisSheetVersion ) {
}
