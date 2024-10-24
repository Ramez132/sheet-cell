package main;

import engine.api.EngineManagerJavafx;
import javafx.fxml.FXML;
import left.LeftPartController;
import sheet.SheetController;
import shticell.coordinate.Coordinate;
import shticell.range.Range;
import shticell.range.RangeFactory;
import shticell.row.RangeWithRowsInArea;
import shticell.sheet.api.Sheet;
import shticell.sheet.api.SheetReadActions;
import top.TopPartController;

import java.util.List;

public class MainController {

    private EngineManagerJavafx engineManager;

    @FXML private SheetController sheetPartController;
    @FXML private TopPartController topPartController;
    @FXML private LeftPartController leftPartController;

    @FXML
    public void initialize() {
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

    public void loadNewSheet(SheetReadActions sheet){
        sheetPartController.loadAndDisplayNewSheet(sheet);
    }

    public void displaySheetBeforeSortingOrFiltering() {
        sheetPartController.clearAllCellsDisplay();
        sheetPartController.createAndDisplayAllCells(engineManager.getMostRecentSheet());
    }

    public void handleCellClick(SheetReadActions sheet, Coordinate selectedCoordinate) {
        topPartController.handleCellClick(sheet, selectedCoordinate);
    }

    public void updateCellValue(Coordinate currentlySelectedCoordinate, String newValueStr) {
        try {
            SheetReadActions sheet = engineManager.updateValueOfCellAndGetNewSheet
                    (currentlySelectedCoordinate.getRow(), currentlySelectedCoordinate.getColumn(), newValueStr);
            topPartController.addNewVersionNumberToVersionComboBox(sheet.getVersion());
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
            String messageToUser = "Displaying lines with the selected unique values in column " +
                    currentColumnLetterForFiltering + ", in the selected filtering area: "
                    + currentFilteringRange.getTopLeftStartCoordinate() +
                    " to " + currentFilteringRange.getBottomRightEndCoordinate() + ".";
            topPartController.setMessageOfRecentActionOutcomeLabel(messageToUser);
            sheetPartController.displaySheetWithFilteredOrSortedRange(sheet, filteredRangeArea);
        }
        catch (Exception e) {
            topPartController.setMessageOfRecentActionOutcomeLabel(e.getMessage());
        }
    }

    public void handleShowSortedLinesButton(Range newSortingRange, List<Character> listOfColumnLettersCharactersToSortBy) {
        try {
            RangeWithRowsInArea sortedRangeArea = engineManager.createSortedRangeArea(newSortingRange, listOfColumnLettersCharactersToSortBy);
            Sheet sheet = engineManager.createCopyOfRecentSheet();

            String messageToUser = "Displaying the sorted area: "
                    + newSortingRange.getTopLeftStartCoordinate() +
                    " to " + newSortingRange.getBottomRightEndCoordinate() + ", sorted in the desired columns and provided order.";
            topPartController.setMessageOfRecentActionOutcomeLabel(messageToUser);
            sheetPartController.displaySheetWithFilteredOrSortedRange(sheet, sortedRangeArea);

        } catch (Exception e) {
            topPartController.setMessageOfRecentActionOutcomeLabel(e.getMessage());
        }

    }

    public void displaySheetOfSpecificVersion(int versionNumToDisplay) {
        try {
            SheetReadActions sheet = engineManager.getSheetOfSpecificVersion(versionNumToDisplay);
            loadNewSheet(sheet);
        } catch (Exception e) {
            topPartController.setMessageOfRecentActionOutcomeLabel(e.getMessage());
            topPartController.enableAllButtonsInScene();
        }

    }

    public void displaySheetOfMostRecentVersion() {
        try {
            SheetReadActions sheet = engineManager.getMostRecentSheet();
            loadNewSheet(sheet);
        } catch (Exception e) {
            topPartController.setMessageOfRecentActionOutcomeLabel(e.getMessage());
            topPartController.enableAllButtonsInScene();
        }
    }
}
