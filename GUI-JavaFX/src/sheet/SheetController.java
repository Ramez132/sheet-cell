package sheet;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import main.MainController;
import shticell.cell.api.Cell;
import shticell.cell.api.EffectiveValue;
import shticell.coordinate.Coordinate;
import shticell.coordinate.CoordinateFactory;
import shticell.range.Range;
import shticell.row.RangeWithRowsInArea;
import shticell.sheet.api.SheetReadActions;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SheetController {

    private MainController mainController;
    private Coordinate currentlySelectedCoordinate = CoordinateFactory.getCoordinate(1, 1);

    @FXML private GridPane gridPaneColumnLetters;
    @FXML private GridPane gridPaneRowNumbers;
    @FXML private GridPane gridPaneActualCells;
    @FXML public ScrollPane upDownScroller;
    @FXML public ScrollPane rightLeftScroller;

    private final Map<Integer, SimpleIntegerProperty> heightForEachRowMapping = new HashMap<>();
    private final Map<Integer, SimpleIntegerProperty> widthForEachColumnMapping = new HashMap<>();


    public void initialize() {
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void loadAndDisplayNewSheet(SheetReadActions sheet){
        int numOfRows = sheet.getNumOfRows();
        int numOfColumns = sheet.getNumOfColumns();
        int initializedRowHeight = sheet.getRowHeight();
        int initializedColumnWidth = sheet.getColumnWidth();

        clearCurrentSheetDisplay();

        for (int i = 1; i <= numOfColumns; i++) {
            widthForEachColumnMapping.put(i,new SimpleIntegerProperty(initializedColumnWidth));
        }

        for (int i = 1; i <= numOfRows; i++) {
            heightForEachRowMapping.put(i,new SimpleIntegerProperty(initializedRowHeight));
        }

        createAndDisplayColumnsLetters(sheet);
        createAndDisplayRowNumbers(sheet);
        createAndDisplayAllCells(sheet);
    }

    private void clearCurrentSheetDisplay() {
        gridPaneColumnLetters.getChildren().clear();
        gridPaneRowNumbers.getChildren().clear();
        clearAllCellsDisplay();
    }

    public void clearAllCellsDisplay() {
        gridPaneActualCells.getChildren().clear();
    }

    public void createAndDisplayAllCells(SheetReadActions sheet) {
        int numOfRows = sheet.getNumOfRows();
        int numOfColumns = sheet.getNumOfColumns();
        int initializedRowHeight = sheet.getRowHeight();
        int initializedColumnWidth = sheet.getColumnWidth();

        for (int currentRowNum = 1; currentRowNum <= numOfRows; currentRowNum++) {
            for (int currentColumnNum = 1; currentColumnNum <= numOfColumns; currentColumnNum++) {

                Coordinate coordinate = CoordinateFactory.getCoordinate(currentRowNum, currentColumnNum);
                String effectiveValueOfCellAsString;
                if (sheet.getActiveCells().containsKey(coordinate)) {
                    EffectiveValue effectiveValueOfCell = sheet.getActiveCells().get(coordinate).getCurrentEffectiveValue();
                    if (effectiveValueOfCell == null) {
                        effectiveValueOfCellAsString = addThousandsSeparator(" ");
                    } else {
                        effectiveValueOfCellAsString = addThousandsSeparator(effectiveValueOfCell.getValue().toString());
                    }
                } else {
                    effectiveValueOfCellAsString = addThousandsSeparator(" ");
                }

                Label cellLabel = new Label(effectiveValueOfCellAsString);

                cellLabel.prefHeightProperty().set(initializedRowHeight);
                cellLabel.prefWidthProperty().set(initializedColumnWidth);

                cellLabel.setAlignment(Pos.CENTER);
                cellLabel.getStyleClass().add("single-cell");
                cellLabel.setOnMouseClicked(event -> handleCellClick(sheet, coordinate));
                gridPaneActualCells.add(cellLabel, currentColumnNum, currentRowNum);
                Insets margin = new Insets(3,3,3,3); // Define the margin (top, right, bottom, left)
                GridPane.setMargin(cellLabel, margin);
                GridPane.setHalignment(cellLabel, HPos.CENTER);
                GridPane.setValignment(cellLabel, VPos.CENTER);
            }
        }
    }

    private void handleCellClick(SheetReadActions sheet, Coordinate selectedCoordinate) {
        Coordinate previouslySelectedCoordinate = currentlySelectedCoordinate;
        currentlySelectedCoordinate = selectedCoordinate;
        Cell selectedCell = sheet.getCell(selectedCoordinate.getRow(), selectedCoordinate.getColumn());

        mainController.handleCellClick(sheet, selectedCoordinate);

        removeStyleClassForPreviouslySelectedCell(previouslySelectedCoordinate);
        addStyleClassForCurrentlySelectedCell(selectedCoordinate);
        removeStyleClassOfCellsInRange();
        removeStyleClassesInfluenceAndDependsOnFromAllCells();

        if (sheet.getActiveCells().containsKey(selectedCoordinate)) {

            for (Coordinate coordinateTheCellDependsOn : selectedCell.getDependsOnMap().keySet()) {
                gridPaneActualCells.getChildren().stream()
                        .filter(node -> GridPane.getRowIndex(node) == coordinateTheCellDependsOn.getRow() && GridPane.getColumnIndex(node) == coordinateTheCellDependsOn.getColumn())
                        .findFirst()
                        .ifPresent(node -> ((Label) node).getStyleClass().add("depends-on-cell"));
            }

            for (Coordinate coordinateTheCellInfluencesOn : selectedCell.getInfluencingOnMap().keySet()) {
                gridPaneActualCells.getChildren().stream()
                        .filter(node -> GridPane.getRowIndex(node) == coordinateTheCellInfluencesOn.getRow() && GridPane.getColumnIndex(node) == coordinateTheCellInfluencesOn.getColumn())
                        .findFirst()
                        .ifPresent(node -> ((Label) node).getStyleClass().add("influence-on-cell"));
            }
        }
    }

    //assuming rangeName parameter exists in the system - user chooses only from existing ranges
    public void highlightCellsInSelectedRange(Range selectedRange) {
        cleanUnnecessaryStyleClassesForAllCells();

        int rowStart = selectedRange.getRowStart();
        int rowEnd = selectedRange.getRowEnd();
        int columnStart = selectedRange.getColumnStart();
        int columnEnd = selectedRange.getColumnEnd();

        for (Node node : gridPaneActualCells.getChildren()) {
            int currentNodeRowIndex = GridPane.getRowIndex(node);
            int currentNodeColumnIndex = GridPane.getColumnIndex(node);
            if (currentNodeRowIndex >= rowStart && currentNodeRowIndex <= rowEnd
                    && currentNodeColumnIndex >= columnStart && currentNodeColumnIndex <= columnEnd) {
                ((Label) node).getStyleClass().add("cell-from-selected-range");
            }
        }
    }



    private void cleanUnnecessaryStyleClassesForAllCells() {
        removeStyleClassForPreviouslySelectedCell(currentlySelectedCoordinate);
        removeStyleClassesInfluenceAndDependsOnFromAllCells();
        removeStyleClassOfCellsInRange();
    }

    private void removeStyleClassForPreviouslySelectedCell(Coordinate previouslySelectedCoordinate) {
        gridPaneActualCells.getChildren().stream()
                .filter(node -> GridPane.getRowIndex(node) == previouslySelectedCoordinate.getRow() && GridPane.getColumnIndex(node) == previouslySelectedCoordinate.getColumn())
                .findFirst()
                .ifPresent(node -> ((Label) node).getStyleClass().remove("selected-cell"));
    }

    private void addStyleClassForCurrentlySelectedCell(Coordinate selectedCoordinate) {
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

    public void createAndDisplayRowNumbers(SheetReadActions sheet){
        int numOfRows = sheet.getNumOfRows();
        int initializedRowHeightWidth = sheet.getRowHeight();
        int initializedColumnWidth = sheet.getColumnWidth();

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

    public void createAndDisplayColumnsLetters(SheetReadActions sheet){
        int numOfColumns = sheet.getNumOfColumns();
        int initializedRowHeight = sheet.getRowHeight();
        int initializedColumnWidth = sheet.getColumnWidth();

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


    public void displaySheetWithFilteredOrSortedRange(SheetReadActions sheet, RangeWithRowsInArea filteredRangeArea) {
        int numOfRows = sheet.getNumOfRows();
        int numOfColumns = sheet.getNumOfColumns();
        int initializedRowHeight = sheet.getRowHeight();
        int initializedColumnWidth = sheet.getColumnWidth();


        Range selectedRange = filteredRangeArea.getRange();
        int filteredRangeRowStart = selectedRange.getRowStart();
        int filteredRangeRowEnd = selectedRange.getRowEnd();
        int filteredRangeColumnStart = selectedRange.getColumnStart();
        int filteredRangeColumnEnd = selectedRange.getColumnEnd();

        clearAllCellsDisplay();

        for (int currentRowNum = 1; currentRowNum <= numOfRows; currentRowNum++) {
            for (int currentColumnNum = 1; currentColumnNum <= numOfColumns; currentColumnNum++) {
                Coordinate coordinate = CoordinateFactory.getCoordinate(currentRowNum, currentColumnNum);
                String effectiveValueOfCellAsString;

                //check if the cell is in the filtered range
                if (currentRowNum >= filteredRangeRowStart && currentRowNum <= filteredRangeRowEnd
                        && currentColumnNum >= filteredRangeColumnStart && currentColumnNum <= filteredRangeColumnEnd) {

                    effectiveValueOfCellAsString = filteredRangeArea.getEffectiveValueOfCellAsString(currentRowNum, currentColumnNum);
//                    effectiveValueOfCellAsString = addThousandsSeparator(effectiveValueOfCellAsString);
                } else { //cell is not in the filtered range - display the original value
                    if (sheet.getActiveCells().containsKey(coordinate)) {
                        EffectiveValue effectiveValueOfCell = sheet.getActiveCells().get(coordinate).getCurrentEffectiveValue();
                        if (effectiveValueOfCell == null) {
                            effectiveValueOfCellAsString = addThousandsSeparator(" ");
                        } else {
                            effectiveValueOfCellAsString = addThousandsSeparator(effectiveValueOfCell.getValue().toString());
                        }
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
