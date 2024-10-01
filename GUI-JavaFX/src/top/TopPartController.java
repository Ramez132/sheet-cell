package top;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import main.MainController;
import shticell.cell.api.Cell;
import shticell.coordinate.Coordinate;
import shticell.range.Range;
import shticell.sheet.api.Sheet;
import shticell.sheet.api.SheetReadActions;

import java.io.File;

public class TopPartController {
    private MainController mainController;
    private Coordinate currentlySelectedCoordinate;
    @FXML
    private Button loadNewFileButton;
    @FXML
    private Label filePathLabel;
    @FXML
    private Label messageOfRecentActionOutcomeLabel;
    //    @FXML
//    private Label notificationHeadlineOfRecentActionOutcomeLabel;
    @FXML
    private Label idOfSelectedCell;
    @FXML
    private Label originalValueStrOfSelectedCell;
    @FXML
    private Label versionNumOfLastChange;
    @FXML
    private TextField newValueToCellTextField;
    @FXML
    private ComboBox<String> selectRangeComboBox;
    @FXML
    private Button displayCellsInSelectedRangeButton;
    @FXML
    private TextField newRangeNameTextField;
    @FXML
    private TextField newRangeStartCoordinateTextField;
    @FXML
    private TextField newRangeEndCoordinateTextField;
    @FXML
    private ComboBox<Integer> selectVersionNumberComboBox;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void handleLoadNewFile() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML Files", "*.xml")
        );

        // Show the file chooser and wait for user selection
        File selectedFile = fileChooser.showOpenDialog(loadNewFileButton.getScene().getWindow());

        // If a file is selected, display its name in the label
        if (selectedFile != null) {
            try {
                Sheet sheet = mainController.getEngineManager().getSheetFromFile(selectedFile);
                messageOfRecentActionOutcomeLabel.setText("Recent file loaded successfully");
                filePathLabel.setText(selectedFile.getAbsoluteFile().toString());
//                mainController.deleteAllRangesInRangeFactoryBeforeLoadingNewSheet();
                //update UI with possible ranges from the new sheet
                selectRangeComboBox.getItems().clear();
                if (mainController.isThereAnyRangeInRangesFactory()) {
                    selectRangeComboBox.getItems().addAll(mainController.getAllRangeNamesInTheSystem());
                }
                selectRangeComboBox.setPromptText("Select range");
                mainController.displayNewSheetFromNewFile(sheet);
            } catch (Exception e) {
                messageOfRecentActionOutcomeLabel.setText(e.getMessage());
            }
        }
    }

    public void handleCellClick(SheetReadActions sheet, Coordinate selectedCoordinate) {
        Cell selectedCell = sheet.getActiveCells().get(selectedCoordinate);
//        if (selectedCell != null) {
//            notificationHeadlineOfRecentActionOutcomeLabel.setText("Selected cell:");
//            messageOfRecentActionOutcomeLabel.setText(selectedCell.getEffectiveValue().toString());
//        }
        boolean thereAreCellsThatSelectedCellDependsOn = sheet.getActiveCells().containsKey(selectedCoordinate) && !sheet.getActiveCells().get(selectedCoordinate).getDependsOnMap().isEmpty();
        boolean thereAreCellsThatSelectedCellInfluenceOn = sheet.getActiveCells().containsKey(selectedCoordinate) && !sheet.getActiveCells().get(selectedCoordinate).getInfluencingOnMap().isEmpty();
        String messageRegardingCellsItDependsOn = thereAreCellsThatSelectedCellDependsOn ? " The cell depends on cells with blue border blue." : "";
        String messageRegardingCellsInInfluencesOn = thereAreCellsThatSelectedCellInfluenceOn ? " The cell influences cells with green border." : "";
        if (selectedCell != null) {
            currentlySelectedCoordinate = selectedCoordinate;
            String coordinateStr = selectedCoordinate.toString();
            idOfSelectedCell.setText(coordinateStr);
            originalValueStrOfSelectedCell.setText(selectedCell.getOriginalValueStr());
            int lastVersionInWhichCellHasChanged = selectedCell.getLastVersionInWhichCellHasChanged();
            if (lastVersionInWhichCellHasChanged == -1) {
                versionNumOfLastChange.setText("Empty cell - No change yet.");
            } else {
                versionNumOfLastChange.setText(String.valueOf(lastVersionInWhichCellHasChanged));
            }
            messageOfRecentActionOutcomeLabel.setText("Displaying data of cell " + selectedCoordinate.toString() + " (highlighted in the sheet)."
                    + messageRegardingCellsItDependsOn + messageRegardingCellsInInfluencesOn);
        }
    }

    @FXML
    public void handleUpdateCellValueButtonAndClearTextField() {
        String newValueStr = newValueToCellTextField.getText().trim();
        //in case no cell is selected or no value was entered - should we pop a message to the user before preforming the action?

        if (mainController.getMostRecentSheetFromEngine() == null) {
            messageOfRecentActionOutcomeLabel.setText("No sheet is loaded - the system can not update a value. Please load a sheet first.");
        } else if (currentlySelectedCoordinate == null) {
            messageOfRecentActionOutcomeLabel.setText("No cell is selected - the system can not update a value. Please select a cell first.");
        } else if (newValueStr != null && newValueStr.isEmpty()) {
            messageOfRecentActionOutcomeLabel.setText("No value was entered - the system updated the selected cell " +
                    currentlySelectedCoordinate +" to be an empty cell.");
            mainController.updateCellValue(currentlySelectedCoordinate, "");
            clearDataInTopPartRegardingSelectedCell();
        } else if (newValueStr != null) {
            mainController.updateCellValue(currentlySelectedCoordinate, newValueStr);
            newValueToCellTextField.clear();
            clearDataInTopPartRegardingSelectedCell();
        }
    }

    @FXML
    public void handleAddNewRangeButtonAndClearRelevantTextFields() {
        String rangeName = newRangeNameTextField.getText().trim();
        String leftTopStartCoordinateStr = newRangeStartCoordinateTextField.getText().trim();
        String rightBottomEndCoordinateStr = newRangeEndCoordinateTextField.getText().trim();
        if (mainController.getMostRecentSheetFromEngine() == null) {
            messageOfRecentActionOutcomeLabel.setText("No sheet is loaded - the system can not create a new range. Please load a sheet first.");
        } else if (rangeName.isEmpty() || leftTopStartCoordinateStr.isEmpty() || rightBottomEndCoordinateStr.isEmpty()) {
            messageOfRecentActionOutcomeLabel.setText("One or more fields are empty - the system can not create a new range. Please fill all fields.");
        } else {
            mainController.handleCreatingNewRange(rangeName, leftTopStartCoordinateStr, rightBottomEndCoordinateStr);
            newRangeNameTextField.clear(); //will get here even if the range was not created and error caught
            newRangeStartCoordinateTextField.clear();
            newRangeEndCoordinateTextField.clear();
        }
    }

    public void addNewRangeNameToRangesComboBox(String rangeName) {
        selectRangeComboBox.getItems().add(rangeName);
//        selectRangeComboBox.getItems().clear();
//        if (mainController.isThereAnyRangeInRangesFactory()) {
//            selectRangeComboBox.getItems().addAll(mainController.getAllRangeNamesInTheSystem());
//        }
    }

    @FXML
    public void handleDeleteSelectedRangeButton() {
        if (selectRangeComboBox.getItems().isEmpty()) {
            messageOfRecentActionOutcomeLabel.setText("No ranges are available to delete.");
            return;
        } else if (selectRangeComboBox.getValue() == null) {
            messageOfRecentActionOutcomeLabel.setText("There is nothing to delete - no range is selected.");
            return;
        } else if (mainController.isSelectedRangeUsedInAnyCellWithRelevantFunction(selectRangeComboBox.getValue())) {
            messageOfRecentActionOutcomeLabel.setText("The selected range is used in a cell/cells with relevant functions - can not delete it.");
            return;
        }
        try {
            String rangeName = selectRangeComboBox.getValue();
            mainController.deleteRangeFromRangeFactoryMainController(rangeName);
            messageOfRecentActionOutcomeLabel.setText("Range '" + rangeName + "' was deleted successfully.");
            selectRangeComboBox.getItems().remove(rangeName);
        } catch (Exception e) {
            messageOfRecentActionOutcomeLabel.setText(e.getMessage());
        }
    }

    private void clearDataInTopPartRegardingSelectedCell() {
        idOfSelectedCell.setText("");
        originalValueStrOfSelectedCell.setText("");
        versionNumOfLastChange.setText("");
    }

//    public void handleCreatingNewRange(SheetReadActions sheet, String rangeName, String leftTopStartCoordinateStr, String rightBottomEndCoordinateStr) {
//        Range range;
//        try {
//            range = RangeFactory.createRangeFromTwoCoordinateStringsAndNameString(sheet, rangeName, leftTopStartCoordinateStr, rightBottomEndCoordinateStr);
//            //update ui with new range
//            messageOfRecentActionOutcomeLabel.setText("New range created successfully: " + rangeName + " from " + leftTopStartCoordinateStr + " to " + rightBottomEndCoordinateStr);
//        } catch (Exception e) {
//            messageOfRecentActionOutcomeLabel.setText(e.getMessage());
//        }
//    }

    @FXML
    public void handleDisplayCellsInSelectedRangeButton() {
        if (selectRangeComboBox.getItems().isEmpty()) {
            messageOfRecentActionOutcomeLabel.setText("No ranges are available to display.");
            return;
        } else if (selectRangeComboBox.getValue() == null) {
            messageOfRecentActionOutcomeLabel.setText("There is nothing to display - no range is selected.");
            return;
        }
        try {
            String rangeName = selectRangeComboBox.getValue();
            Range range = mainController.getRangeByItsName(rangeName);
            mainController.handleChoosingRangeAndHighlightCellsInRangeMainController(range);
//            sheet.highlightCellsInRange(range);
            messageOfRecentActionOutcomeLabel.setText("Cells in selected range '" + rangeName + "' are now highlighted with purple border.");
        } catch (Exception e) {
            messageOfRecentActionOutcomeLabel.setText(e.getMessage());
        }
    }

    @FXML
    public void handleDisplaySelectedVersionButton() {
        if (selectVersionNumberComboBox.getItems().isEmpty()) {
            messageOfRecentActionOutcomeLabel.setText("No versions are available to display.");
            return;
        } else if (selectVersionNumberComboBox.getValue() == null) {
            messageOfRecentActionOutcomeLabel.setText("There is nothing to display - no version is selected.");
            return;
        }
        try {
            int version = selectVersionNumberComboBox.getValue();
//            Sheet sheet = mainController.getSheetOfSpecificVersion(version);
//            mainController.displayNewSheetFromNewFile(sheet);
            messageOfRecentActionOutcomeLabel.setText("Version " + version + " is now displayed.");
        } catch (Exception e) {
            messageOfRecentActionOutcomeLabel.setText(e.getMessage());
        }
    }

    @FXML
    public void handleReturnToRecentSheetButton(){

    }

    public void setMessageOfRecentActionOutcomeLabel(String message) {
        messageOfRecentActionOutcomeLabel.setText(message);
    }

    //need to implement
    public void deleteAllVersionNumbersFromPreviousSheet() {
    }

}

