package main;

import engine.api.EngineManagerJavafx;
import javafx.fxml.FXML;
import sheet.SheetController;
import shticell.coordinate.Coordinate;
import shticell.range.Range;
import shticell.range.RangeFactory;
import shticell.sheet.api.SheetReadActions;
import top.TopPartController;

import java.util.List;

public class MainController {

    private EngineManagerJavafx engineManager;

    @FXML private SheetController sheetPartController;
    @FXML private TopPartController topPartController;

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
            topPartController.addNewRangeNameToRangesComboBox(rangeName);
            topPartController.setMessageOfRecentActionOutcomeLabel("New range created successfully: '" + rangeName + "' . The range is from cell "
                    + leftTopStartCoordinateStr.toUpperCase() + " to cell " + rightBottomEndCoordinateStr.toUpperCase());
        } catch (Exception e) {
            topPartController.setMessageOfRecentActionOutcomeLabel(e.getMessage());
        }
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
