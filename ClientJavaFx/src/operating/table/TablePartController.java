package operating.table;

import dto.cell.CellDto;
import dto.coordinate.CoordinateDto;
import dto.range.RangeDto;
import dto.range.RangeWithEffectiveValuesDto;
import dto.sheet.SheetDto;
import dto.sheet.SheetWithSortedOrFilteredRangeDto;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import operating.window.SheetWindowController;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class TablePartController {

    private SheetWindowController sheetWindowController;
    private CoordinateDto currentlySelectedCoordinate = new CoordinateDto(1, 1);
    SheetDto recentSheetDto;

    @FXML private GridPane gridPaneColumnLetters;
    @FXML private GridPane gridPaneRowNumbers;
    @FXML private GridPane gridPaneActualCells;
    private String currentSheetName;


    private final Map<Integer, SimpleIntegerProperty> heightForEachRowMapping = new HashMap<>();
    private final Map<Integer, SimpleIntegerProperty> widthForEachColumnMapping = new HashMap<>();

    public void setMainController(SheetWindowController sheetWindowController) {
        this.sheetWindowController = sheetWindowController;
    }

    public void loadAndDisplayNewSheet(SheetDto sheetDto){
        int numOfRows = sheetDto.numOfRows();
        int numOfColumns = sheetDto.numOfColumns();
        int initializedRowHeight = sheetDto.rowHeight();
        int initializedColumnWidth = sheetDto.columnWidth();

        cacheRecentSheetDto(sheetDto);
        clearCurrentSheetDisplay();

        for (int i = 1; i <= numOfColumns; i++) {
            widthForEachColumnMapping.put(i,new SimpleIntegerProperty(initializedColumnWidth));
        }

        for (int i = 1; i <= numOfRows; i++) {
            heightForEachRowMapping.put(i,new SimpleIntegerProperty(initializedRowHeight));
        }

        createAndDisplayColumnsLetters(sheetDto);
        createAndDisplayRowNumbers(sheetDto);
        createAndDisplayAllCells(sheetDto);
    }

    public void displayRecentSheetBeforeSortingOrFilteringCachedInTablePart() {
        loadAndDisplayNewSheet(recentSheetDto);
    }

    private void cacheRecentSheetDto(SheetDto sheetDto) {
        recentSheetDto = sheetDto;
    }

    private void clearCurrentSheetDisplay() {
        gridPaneColumnLetters.getChildren().clear();
        gridPaneRowNumbers.getChildren().clear();
        clearAllCellsDisplay();
    }

    public void clearAllCellsDisplay() {
        gridPaneActualCells.getChildren().clear();
    }

    public void createAndDisplayAllCells(SheetDto sheetDto) {
        int numOfRows = sheetDto.numOfRows();
        int numOfColumns = sheetDto.numOfColumns();
        int initializedRowHeight = sheetDto.rowHeight();
        int initializedColumnWidth = sheetDto.columnWidth();

        Map<CoordinateDto, CellDto> coordinateToCellDtoMap = sheetDto.coordinateToCellDtoMap();

        for (int currentRowNum = 1; currentRowNum <= numOfRows; currentRowNum++) {
            for (int currentColumnNum = 1; currentColumnNum <= numOfColumns; currentColumnNum++) {

                String effectiveValueOfCellAsString;
                CoordinateDto coordinateDto = new CoordinateDto(currentRowNum, currentColumnNum);
                CoordinateDto possibleCoordinateDtoFromKeySet = tryGetCoordinateDtoFromKeySet(coordinateToCellDtoMap.keySet(), coordinateDto);
                if (possibleCoordinateDtoFromKeySet != null) {
                    coordinateDto = possibleCoordinateDtoFromKeySet; //update the coordinateDto to the object from the keySet - for later use
                    CellDto cellDto = coordinateToCellDtoMap.get(possibleCoordinateDtoFromKeySet);
                    effectiveValueOfCellAsString = cellDto.effectiveValueStr();
                } else {
                    effectiveValueOfCellAsString = addThousandsSeparator(" ");
                }

                Label cellLabel = new Label(effectiveValueOfCellAsString);

                cellLabel.prefHeightProperty().set(initializedRowHeight);
                cellLabel.prefWidthProperty().set(initializedColumnWidth);
                cellLabel.setAlignment(Pos.CENTER);
                cellLabel.getStyleClass().add("single-cell");
                CoordinateDto finalCoordinateDto = coordinateDto;
                cellLabel.setOnMouseClicked(event -> handleCellClick(sheetDto, finalCoordinateDto));
                gridPaneActualCells.add(cellLabel, currentColumnNum, currentRowNum);
                Insets margin = new Insets(3,3,3,3); // Define the margin (top, right, bottom, left)
                GridPane.setMargin(cellLabel, margin);
                GridPane.setHalignment(cellLabel, HPos.CENTER);
                GridPane.setValignment(cellLabel, VPos.CENTER);
            }
        }
    }

    private CoordinateDto tryGetCoordinateDtoFromKeySet(Set<CoordinateDto> coordinateDtoSet,CoordinateDto coordinateDto) {
        int selectedRow = coordinateDto.getRow();
        int selectedColumn = coordinateDto.getColumn();

        for (CoordinateDto currentCoordinateDto : coordinateDtoSet) {
            if (currentCoordinateDto.getRow() == selectedRow && currentCoordinateDto.getColumn() == selectedColumn) {
                return currentCoordinateDto;
            }
        }
        return null;
    }

    private void handleCellClick(SheetDto sheetDto, CoordinateDto selectedCoordinateDto) {
        CoordinateDto previouslySelectedCoordinate = currentlySelectedCoordinate;
        currentlySelectedCoordinate = selectedCoordinateDto;
        sheetWindowController.handleCellClick(sheetDto, selectedCoordinateDto);

        removeStyleClassForPreviouslySelectedCell(previouslySelectedCoordinate);
        addStyleClassForCurrentlySelectedCell(selectedCoordinateDto);
        removeStyleClassOfCellsInRange();
        removeStyleClassesInfluenceAndDependsOnFromAllCells();

        CoordinateDto coordinateDto = tryGetCoordinateDtoFromKeySet(sheetDto.coordinateToCellDtoMap().keySet(), selectedCoordinateDto);

        if (coordinateDto != null) {
            CellDto selectedCell = sheetDto.coordinateToCellDtoMap().get(coordinateDto);

            for (CoordinateDto coordinateTheCellDependsOn : selectedCell.dependsOnCoordinatesSet()) {
                gridPaneActualCells.getChildren().stream()
                        .filter(node -> GridPane.getRowIndex(node) == coordinateTheCellDependsOn.getRow() && GridPane.getColumnIndex(node) == coordinateTheCellDependsOn.getColumn())
                        .findFirst()
                        .ifPresent(node -> ((Label) node).getStyleClass().add("depends-on-cell"));
            }

            for (CoordinateDto coordinateTheCellInfluencesOn : selectedCell.influencesOnCoordinatesSet()) {
                gridPaneActualCells.getChildren().stream()
                        .filter(node -> GridPane.getRowIndex(node) == coordinateTheCellInfluencesOn.getRow() && GridPane.getColumnIndex(node) == coordinateTheCellInfluencesOn.getColumn())
                        .findFirst()
                        .ifPresent(node -> ((Label) node).getStyleClass().add("influence-on-cell"));
            }
        }
    }

//    assuming rangeName parameter exists in the system - user chooses only from existing ranges
    public void highlightCellsInSelectedRange(RangeDto selectedRangeDto) {
        cleanUnnecessaryStyleClassesForAllCells();

        int rowStart = selectedRangeDto.rowStart();
        int rowEnd = selectedRangeDto.rowEnd();
        int columnStart = selectedRangeDto.columnStart();
        int columnEnd = selectedRangeDto.columnEnd();

        for (Node node : gridPaneActualCells.getChildren()) {
            int currentNodeRowIndex = GridPane.getRowIndex(node);
            int currentNodeColumnIndex = GridPane.getColumnIndex(node);
            if (currentNodeRowIndex >= rowStart && currentNodeRowIndex <= rowEnd
                    && currentNodeColumnIndex >= columnStart && currentNodeColumnIndex <= columnEnd) {
                ((Label) node).getStyleClass().add("cell-from-selected-range");
            }
        }
    }

    public void cleanUnnecessaryStyleClassesForAllCells() {
        removeStyleClassForPreviouslySelectedCell(currentlySelectedCoordinate);
        removeStyleClassesInfluenceAndDependsOnFromAllCells();
        removeStyleClassOfCellsInRange();
    }

    private void removeStyleClassForPreviouslySelectedCell(CoordinateDto previouslySelectedCoordinate) {
        gridPaneActualCells.getChildren().stream()
                .filter(node -> GridPane.getRowIndex(node) == previouslySelectedCoordinate.getRow() && GridPane.getColumnIndex(node) == previouslySelectedCoordinate.getColumn())
                .findFirst()
                .ifPresent(node -> ((Label) node).getStyleClass().remove("selected-cell"));
    }

    private void addStyleClassForCurrentlySelectedCell(CoordinateDto selectedCoordinate) {
        gridPaneActualCells.getChildren().stream()
                .filter(node -> GridPane.getRowIndex(node) == selectedCoordinate.getRow() && GridPane.getColumnIndex(node) == selectedCoordinate.getColumn())
                .findFirst()
                .ifPresent(node -> ((Label) node).getStyleClass().add("selected-cell"));
    }

    private void removeStyleClassOfCellsInRange() {
        gridPaneActualCells.getChildren().stream()
                .map(node -> (Label) node)
                .forEach(cell -> cell.getStyleClass().remove("cell-from-selected-range"));
    }

    private void removeStyleClassesInfluenceAndDependsOnFromAllCells() {
        gridPaneActualCells.getChildren().stream()
                .map(node -> (Label) node)
                .forEach(cell -> cell.getStyleClass().remove("depends-on-cell"));

        gridPaneActualCells.getChildren().stream()
                .map(node -> (Label) node)
                .forEach(cell -> cell.getStyleClass().remove("influence-on-cell"));
    }

    private String addThousandsSeparator(String number) throws NumberFormatException {
        try {
            return NumberFormat.getNumberInstance(Locale.US).format(Double.parseDouble(number));
        }
        catch (NumberFormatException e) {
            return number;
        }
    }

    public void createAndDisplayRowNumbers(SheetDto sheet){
        int numOfRows = sheet.numOfRows();
        int initializedRowHeightWidth = sheet.rowHeight();
        int initializedColumnWidth = sheet.columnWidth();

        for (int currentRowNum = 1; currentRowNum <= numOfRows; currentRowNum++) {
            Label rowLabel = new Label(String.valueOf(currentRowNum));
            rowLabel.prefWidthProperty().set(initializedColumnWidth);
            rowLabel.prefHeightProperty().set(initializedRowHeightWidth);
            rowLabel.setAlignment(Pos.CENTER);
            rowLabel.getStyleClass().add("single-cell");
            gridPaneRowNumbers.add(rowLabel, 0, currentRowNum);
            Insets margin = new Insets(3,3,3,3);
            GridPane.setMargin(rowLabel, margin);
            GridPane.setHalignment(rowLabel, HPos.CENTER);
            GridPane.setValignment(rowLabel, VPos.CENTER);
        }
    }

    public void createAndDisplayColumnsLetters(SheetDto sheet){
        int numOfColumns = sheet.numOfColumns();
        int initializedRowHeight = sheet.rowHeight();
        int initializedColumnWidth = sheet.columnWidth();

        Label leftTopCornerLabel = new Label("");
        leftTopCornerLabel.prefHeightProperty().set(initializedRowHeight);
        leftTopCornerLabel.prefWidthProperty().set(initializedColumnWidth);
        leftTopCornerLabel.getStyleClass().add("single-cell");
        gridPaneColumnLetters.add(leftTopCornerLabel, 0, 0);
        Insets margin = new Insets(3,3,3,3); // Define the margin (top, right, bottom, left)
        GridPane.setMargin(leftTopCornerLabel, margin);

        for (int currentColumnNum = 1; currentColumnNum <= numOfColumns; currentColumnNum++) {
            Label columnLabel = new Label(String.valueOf((char) ('A' + currentColumnNum - 1)));
            columnLabel.prefHeightProperty().set(initializedRowHeight);
            columnLabel.prefWidthProperty().set(initializedColumnWidth);
            columnLabel.setAlignment(Pos.CENTER);
            columnLabel.getStyleClass().add("single-cell");
            gridPaneColumnLetters.add(columnLabel, currentColumnNum, 0);
            GridPane.setMargin(columnLabel, margin);
            GridPane.setHalignment(columnLabel, HPos.CENTER);
            GridPane.setValignment(columnLabel, VPos.CENTER);
        }
    }

    public void updateCurrentSheetNameInTablePart(String sheetName) {
        currentSheetName = sheetName;
    }

    public void displaySheetWithSortedOrFilteredRange(SheetWithSortedOrFilteredRangeDto sheetWithSortedOrFilteredRangeDto) {
        clearAllCellsDisplay();

        SheetDto sheetDto = sheetWithSortedOrFilteredRangeDto.sheetDto();
        Map<CoordinateDto, CellDto> coordinateToCellDtoMap = sheetDto.coordinateToCellDtoMap();

        RangeWithEffectiveValuesDto rangeWithEffectiveValuesDto = sheetWithSortedOrFilteredRangeDto.rangeWithEffectiveValuesDto();
        RangeDto sortedRangeDto = sheetWithSortedOrFilteredRangeDto.rangeWithEffectiveValuesDto().rangeDto();
        Map<CoordinateDto, String> allEffectiveValuesStringsInRange = rangeWithEffectiveValuesDto.allEffectiveValuesStrings();

        int numOfRows = sheetDto.numOfRows();
        int numOfColumns = sheetDto.numOfColumns();
        int initializedRowHeight = sheetDto.rowHeight();
        int initializedColumnWidth = sheetDto.columnWidth();

        int filteredRangeRowStart = sortedRangeDto.rowStart();
        int filteredRangeRowEnd = sortedRangeDto.rowEnd();
        int filteredRangeColumnStart = sortedRangeDto.columnStart();
        int filteredRangeColumnEnd = sortedRangeDto.columnEnd();

        String effectiveValueOfCellAsString;

        for (int currentRowNum = 1; currentRowNum <= numOfRows; currentRowNum++) {
            for (int currentColumnNum = 1; currentColumnNum <= numOfColumns; currentColumnNum++) {

                CoordinateDto coordinateDto = new CoordinateDto(currentRowNum, currentColumnNum);

                //check if the cell is in the filtered range
                if (currentRowNum >= filteredRangeRowStart && currentRowNum <= filteredRangeRowEnd
                        && currentColumnNum >= filteredRangeColumnStart && currentColumnNum <= filteredRangeColumnEnd) {

                    CoordinateDto coordinateDtoFromRange = tryGetCoordinateDtoFromKeySet(allEffectiveValuesStringsInRange.keySet(), coordinateDto);
                    effectiveValueOfCellAsString = allEffectiveValuesStringsInRange.get(coordinateDtoFromRange);
                } else { //cell is not in the filtered range - display the original value
                    CoordinateDto possibleCoordinateDtoFromKeySet = tryGetCoordinateDtoFromKeySet(coordinateToCellDtoMap.keySet(), coordinateDto);
                    if (possibleCoordinateDtoFromKeySet != null) {
                        coordinateDto = possibleCoordinateDtoFromKeySet; //update the coordinateDto to the object from the keySet - for later use
                        CellDto cellDto = coordinateToCellDtoMap.get(possibleCoordinateDtoFromKeySet);
                        effectiveValueOfCellAsString = cellDto.effectiveValueStr();
                    } else {
                        effectiveValueOfCellAsString = addThousandsSeparator(" ");
                    }
                }

                javafx.scene.control.Label cellLabel = new javafx.scene.control.Label(effectiveValueOfCellAsString);

                cellLabel.setMinHeight(initializedRowHeight);
                cellLabel.setMinWidth(initializedColumnWidth);

                cellLabel.setAlignment(Pos.CENTER);
                cellLabel.getStyleClass().add("single-cell");
                gridPaneActualCells.add(cellLabel, currentColumnNum, currentRowNum);
                javafx.geometry.Insets margin = new javafx.geometry.Insets(3,3,3,3); // Define the margin (top, right, bottom, left)
                GridPane.setMargin(cellLabel, margin);
                GridPane.setHalignment(cellLabel, HPos.CENTER);
                GridPane.setValignment(cellLabel, VPos.CENTER);
            }
        }
    }

}