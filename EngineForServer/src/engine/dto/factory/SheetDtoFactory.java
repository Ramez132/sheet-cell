package engine.dto.factory;

import dto.cell.CellDto;
import dto.coordinate.CoordinateDto;
import dto.sheet.SheetDto;
import shticell.sheet.api.Sheet;

import java.util.HashMap;
import java.util.Map;

public class SheetDtoFactory {

    public static SheetDto createSheetDtoFromSheet(Sheet sheet) {

        Map<CoordinateDto, CellDto> coordinateToCellDtoMap = sheet.getActiveCells().entrySet().stream()
                .collect(
                        HashMap::new,
                        (map, entry) -> map.put(new CoordinateDto(entry.getKey().getRow(), entry.getKey().getColumn()),
                                CellDtoFactory.createCellDto(entry.getValue())),
                        Map::putAll
                );
        return new SheetDto(sheet.getNameOfSheet(),
                            coordinateToCellDtoMap,
                            sheet.getNumOfRows(), sheet.getNumOfColumns(),
                            sheet.getRowHeight(), sheet.getColumnWidth(),
                            sheet.getVersion());
    }
}
