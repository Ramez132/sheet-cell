package main;

import engine.api.EngineManagerJavafx;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import left.LeftPartController;
import sheet.SheetController;
import shticell.coordinate.Coordinate;
import shticell.range.Range;
import shticell.range.RangeFactory;
import shticell.row.RangeWithRowsInArea;
import shticell.sheet.api.Sheet;
import shticell.sheet.api.SheetReadActions;
import top.TopPartController;

import java.net.URL;
import java.util.List;

public class MainController {

    private EngineManagerJavafx engineManager;

    @FXML private SheetController sheetPartController;
    @FXML private TopPartController topPartController;
    @FXML private LeftPartController leftPartController;

//    @FXML
//    private Button loadNewFileButton;
//    @FXML
//    private Label filePathLabel;
//    @FXML
//    private Label notificationMessageOfRecentActionOutcomeLabel;
//    @FXML
//    private Label notificationHeadlineOfRecentActionOutcomeLabel;

    @FXML
    public void initialize() {
        //topComponentController.setAppController(this);
        topPartController.setMainController(this);
        sheetPartController.setMainController(this);
        leftPartController.setMainController(this);
    }

    public void setEngineManager(EngineManagerJavafx engineManager) {
        this.engineManager = engineManager;
    }

    public EngineManagerJavafx getEngineManager() {
        return engineManager;
    }

    public SheetReadActions getMostRecentSheetFromEngine() {
        try {
            return engineManager.getMostRecentSheet();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isSelectedRangeUsedInAnyCellWithRelevantFunction(String rangeName) {
        return engineManager.isSelectedRangeUsedInAnyCellWithRelevantFunction(rangeName);
    }

    public void deleteRangeFromRangeFactoryMainController(String rangeName) {
        try {
            engineManager.deleteRangeFromRangeFactory(rangeName);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void displayNewSheetFromNewFile(SheetReadActions sheet) {
        sheetPartController.loadAndDisplayNewSheet(sheet);
    }

    public void deleteAllRangesInRangeFactoryBeforeLoadingNewSheet() {
        RangeFactory.deleteAllRangesInRangesFactoryBeforeLoadingSheetFromNewFile();
    }
    private void deleteAllVersionsBeforeLoadingNewSheet() {
        topPartController.deleteAllVersionNumbersFromPreviousSheet();
    }

    public void loadNewSheet(SheetReadActions sheet){
        sheetPartController.loadAndDisplayNewSheet(sheet);
    }

    public void handleCellClick(SheetReadActions sheet, Coordinate selectedCoordinate) {
        topPartController.handleCellClick(sheet, selectedCoordinate);
    }

    public void updateCellValue(Coordinate currentlySelectedCoordinate, String newValueStr) {
        try {
            SheetReadActions sheet = engineManager.updateValueOfCellAndGetNewSheet
                    (currentlySelectedCoordinate.getRow(), currentlySelectedCoordinate.getColumn(), newValueStr);
            loadNewSheet(sheet);
            if (newValueStr.isEmpty()) {
                topPartController.setMessageOfRecentActionOutcomeLabel
                        ("No value was entered - the system updated the selected cell " +
                          currentlySelectedCoordinate + " to be an empty cell. Showing the new version of the sheet.");
            } else {
                topPartController.setMessageOfRecentActionOutcomeLabel
                        ("The original value of cell " + currentlySelectedCoordinate
                            + " was successfully updated to " + newValueStr
                            + ". Showing the new version of the sheet.");
            }
        } catch (Exception e) {
            topPartController.setMessageOfRecentActionOutcomeLabel(e.getMessage());
        }
    }

    public void handleCreatingNewRange(String rangeName, String leftTopStartCoordinateStr, String rightBottomEndCoordinateStr) {
        try {
            RangeFactory.createRangeFromTwoCoordinateStringsAndNameString(engineManager.getMostRecentSheet(), rangeName, leftTopStartCoordinateStr, rightBottomEndCoordinateStr);
            leftPartController.addNewRangeNameToRangesComboBox(rangeName);
            topPartController.setMessageOfRecentActionOutcomeLabel("New range created successfully: '" + rangeName + "' . The range is from cell "
                    + leftTopStartCoordinateStr.toUpperCase() + " to cell " + rightBottomEndCoordinateStr.toUpperCase());
        } catch (Exception e) {
            topPartController.setMessageOfRecentActionOutcomeLabel(e.getMessage());
        }
    }

    public void setNotificationMessageOfRecentActionOutcomeLabel(String message) {
        topPartController.setMessageOfRecentActionOutcomeLabel(message);
    }

    public void handleChoosingRangeAndHighlightCellsInRangeMainController(Range selectedRange) {
        sheetPartController.highlightCellsInSelectedRange(selectedRange);
    }

    public boolean isThereAnyRangeInRangesFactory() {
        return engineManager.isThereAnyRangeInRangesFactory();
    }

    public List<String> getAllRangeNamesInTheSystem() {
        return engineManager.getAllRangeNamesInTheSystem();
    }

    public Range getRangeByItsName(String rangeName) {
        return engineManager.getRangeByItsName(rangeName);
    }

    public void handleInitialRangesFromNewSheet(Sheet sheet) {
        leftPartController.handleInitialRangesFromNewSheet(sheet);
    }

    public boolean isFilteringOrSortingAreaValid(String newFilterStartCoordinateStr, String newFilterEndCoordinateStr) {
        return engineManager.isFilteringOrSortingAreaValid(newFilterStartCoordinateStr, newFilterEndCoordinateStr);
    }

    public Range createRangeToSortOrFilter(String newFilterStartCoordinateStr, String newFilterEndCoordinateStr) {
        return engineManager.createRangeToSortOrFilter(newFilterStartCoordinateStr, newFilterEndCoordinateStr);
    }

    public boolean isColumnLetterInFilteringOrSortingArea(String stringWithLetterOfSelectedColumn, String newAreaStartCoordinateStr, String newAreaEndCoordinateStr) {
        return engineManager.isColumnLetterInFilteringOrSortingArea(stringWithLetterOfSelectedColumn, newAreaStartCoordinateStr, newAreaEndCoordinateStr);
    }

    public List<String> getUniqueValuesForFilteringInSelectedColumnAndRelevantArea(char charLetterOfColumnToGetUniqueValuesToFilter, String newFilterStartCoordinateStr, String newFilterEndCoordinateStr) {
        //if got here, all fields are valid
        return engineManager.getUniqueValuesForFilteringInSelectedColumnAndRelevantArea(charLetterOfColumnToGetUniqueValuesToFilter, newFilterStartCoordinateStr, newFilterEndCoordinateStr);
    }

    public void handleShowFilteredLinesButton(Range currentFilteringRange, char currentColumnLetterForFiltering, List<String> selectedUniqueValuesOptions) {
        try {
            RangeWithRowsInArea filteredRangeArea = engineManager.createFilteredRangeArea(currentFilteringRange, currentColumnLetterForFiltering, selectedUniqueValuesOptions);
            Sheet sheet = engineManager.createCopyOfRecentSheet();
            setNotificationMessageOfRecentActionOutcomeLabel("Opened pop-up window - displaying lines with the selected unique values in column " +
                                                                currentColumnLetterForFiltering + ", in the selected filtering area: "
                                                                + currentFilteringRange.getTopLeftStartCoordinate() +
                                                                " to " + currentFilteringRange.getBottomRightEndCoordinate() + ".");
            displaySheetInPopUpWithSortedOrFilteredRange(sheet, filteredRangeArea);

            sheetPartController.displayFilteredLines(sheet, filteredRangeArea);
//            topPartController.setMessageOfRecentActionOutcomeLabel("Filtered lines are displayed");
        }
        catch (Exception e) {
            topPartController.setMessageOfRecentActionOutcomeLabel(e.getMessage());
        }
//        sheetPartController.loadAndDisplayNewSheet(sheet);
    }

    private void displaySheetInPopUpWithSortedOrFilteredRange(Sheet sheet, RangeWithRowsInArea filteredRangeArea) {
    }

    public void handleShowSortedLinesButton(Range newSortingRange, List<Character> listOfColumnLettersCharactersToSortBy) {
        try {
            RangeWithRowsInArea sortedRangeArea = engineManager.createSortedRangeArea(newSortingRange, listOfColumnLettersCharactersToSortBy);
            Sheet sheet = engineManager.createCopyOfRecentSheet();
            sheetPartController.displayFilteredLines(sheet, sortedRangeArea);
        } catch (Exception e) {
            topPartController.setMessageOfRecentActionOutcomeLabel(e.getMessage());
        }

    }


//    @FXML
//    public void handleLoadNewFile() {
//        FileChooser fileChooser = new FileChooser();
//
//        fileChooser.getExtensionFilters().add(
//                new FileChooser.ExtensionFilter("XML Files", "*.xml")
//        );
//
//        // Show the file chooser and wait for user selection
//        File selectedFile = fileChooser.showOpenDialog(loadNewFileButton.getScene().getWindow());
//
//        // If a file is selected, display its name in the label
//        if (selectedFile != null) {
//            notificationHeadlineOfRecentActionOutcomeLabel.setText("Message of recent attempt to upload a file:");
//            try {
//                engineManager.getSheetFromFile(selectedFile);
//                notificationMessageOfRecentActionOutcomeLabel.setText("Recent file loaded successfully");
//                filePathLabel.setText(selectedFile.getAbsoluteFile().toString());
//            } catch (Exception e) {
//                notificationMessageOfRecentActionOutcomeLabel.setText(e.getMessage());
//            }
//        }
//    }
}
