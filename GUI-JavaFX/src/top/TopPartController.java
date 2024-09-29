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
import shticell.range.RangeFactory;
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
                //update UI with possible ranges from the new sheet
                selectRangeComboBox.getItems().clear();
                if (RangeFactory.isThereAnyRangeInRangesFactory()) {
                    selectRangeComboBox.getItems().addAll(RangeFactory.getAllRangesNames());
                }
                mainController.loadNewSheetFromNewFile(sheet);
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
        String messageRegardingCellsItDependsOn = thereAreCellsThatSelectedCellDependsOn ? " The cell depends on the blue cells." : "";
        String messageRegardingCellsInInfluencesOn = thereAreCellsThatSelectedCellInfluenceOn ? " The cell influences the green cells." : "";
        if (selectedCell != null) {
            currentlySelectedCoordinate = selectedCoordinate;
            String coordinateStr = selectedCoordinate.toString();
            idOfSelectedCell.setText(coordinateStr);
            originalValueStrOfSelectedCell.setText(selectedCell.getOriginalValueStr());
            versionNumOfLastChange.setText(String.valueOf(selectedCell.getLastVersionInWhichCellHasChanged()));
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

    private void clearDataInTopPartRegardingSelectedCell() {
        idOfSelectedCell.setText("");
        originalValueStrOfSelectedCell.setText("");
        versionNumOfLastChange.setText("");
    }

    public void handleCreatingNewRange(SheetReadActions sheet, String rangeName, String leftTopStartCoordinateStr, String rightBottomEndCoordinateStr) {
//        mainController.handleCreatingNewRange(sheet, rangeName, leftTopStartCoordinateStr, rightBottomEndCoordinateStr);
        Range range;
        try {
            range = RangeFactory.createRangeFromTwoCoordinateStringsAndNameString(sheet, rangeName, leftTopStartCoordinateStr, rightBottomEndCoordinateStr);
            //update ui with new range
            messageOfRecentActionOutcomeLabel.setText("New range created successfully: " + rangeName + " from " + leftTopStartCoordinateStr + " to " + rightBottomEndCoordinateStr);
        } catch (Exception e) {
            messageOfRecentActionOutcomeLabel.setText(e.getMessage());
        }
    }

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
            Range range = RangeFactory.getRangeByItsName(rangeName);
            mainController.handleChoosingRangeAndHighlightCellsInRangeMainController(range);
//            sheet.highlightCellsInRange(range);
            messageOfRecentActionOutcomeLabel.setText("Cells in selected range " + rangeName + " are now highlighted in the sheet.");
        } catch (Exception e) {
            messageOfRecentActionOutcomeLabel.setText(e.getMessage());
        }
    }

    public void setMessageOfRecentActionOutcomeLabel(String message) {
        messageOfRecentActionOutcomeLabel.setText(message);
    }

    //need to implement
    public void deleteAllVersionNumbersFromPreviousSheet() {
    }

    //need to implement
    public void deleteAllRangesFromPreviousSheet() {
    }
}

