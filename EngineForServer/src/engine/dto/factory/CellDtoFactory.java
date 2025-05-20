package engine.dto.factory;

import dto.cell.CellDto;
import dto.coordinate.CoordinateDto;
import shticell.cell.api.Cell;
import shticell.cell.api.EffectiveValue;
import shticell.coordinate.Coordinate;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Set;

public class CellDtoFactory {

    public static CellDto createCellDto(Cell cell) {
        CoordinateDto coordinateDto = new CoordinateDto(cell.getCoordinate().getRow(), cell.getCoordinate().getColumn());
        String originalValueStr = cell.getOriginalValueStr();
        String effectiveValueStr = getEffectiveValueAsString(cell);
        int lastVersionInWhichCellHasChanged = cell.getLastVersionInWhichCellHasChanged();
        String nameOfUserWhoCausedUpdateOfValue = cell.getNameOfUserWhoCausedUpdateOfValue();

        Set<Coordinate> dependsOnCoordinatesSet = cell.getDependsOnMap().keySet();
        Set<CoordinateDto> dependsOnCoordinatesDtoSet = CoordinateDtoFactory.createCoordinateDtoSetFromCoordinateSet(dependsOnCoordinatesSet);
        Set<Coordinate> influencesOnCoordinatesSet = cell.getInfluencingOnMap().keySet();
        Set<CoordinateDto> influencesOnCoordinatesDtoSet = CoordinateDtoFactory.createCoordinateDtoSetFromCoordinateSet(influencesOnCoordinatesSet);

        return new CellDto(coordinateDto, originalValueStr, effectiveValueStr,
                lastVersionInWhichCellHasChanged, nameOfUserWhoCausedUpdateOfValue,
                dependsOnCoordinatesDtoSet, influencesOnCoordinatesDtoSet);
    }

    private static String getEffectiveValueAsString(Cell cell) {
        String effectiveValueOfCellAsString;

        EffectiveValue effectiveValueOfCell = cell.getCurrentEffectiveValue();
        if (effectiveValueOfCell == null) {
            effectiveValueOfCellAsString = addThousandsSeparator(" ");
        } else {
            effectiveValueOfCellAsString = addThousandsSeparator(effectiveValueOfCell.getValue().toString());
        }

        return effectiveValueOfCellAsString;
    }

    private static String addThousandsSeparator(String number) throws NumberFormatException {
        try {
            return NumberFormat.getNumberInstance(Locale.US).format(Double.parseDouble(number));
        }
        catch (NumberFormatException e) {
            return number;
        }
    }
}
