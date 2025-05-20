package operating.top;

import dto.cell.CellDto;
import dto.coordinate.CoordinateDto;
import dto.sheet.SheetDto;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import okhttp3.*;
import operating.window.SheetWindowController;
import util.Constants;
import util.http.HttpClientUtil;


public class TopPartController {
    private SheetWindowController sheetWindowController;
    private CoordinateDto currentlySelectedCoordinateDto;
    @FXML
    private Button returnToMainWindowButton;
    @FXML
    private Label messageOfRecentActionOutcomeLabel;
    @FXML
    private Label idOfSelectedCell;
    @FXML
    private Label originalValueStrOfSelectedCell;
    @FXML
    private Label versionNumOfLastChange;
    @FXML Label userNameOfLastChangeLabel;
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
    @FXML
    private Button displayRecentSheetButton;
    @FXML private Button displaySelectedVersionButton;

    @FXML private Button updateValueButton;

    @FXML private Label userNameLabel;
    @FXML private Label sheetNameLabel;
    @FXML private Label currentPermissionLabel;

    @FXML private TextField minValueForDynamicAnalysisTextField;
    @FXML private TextField maxValueForDynamicAnalysisTextField;
    @FXML private TextField stepSizeForDynamicAnalysisTextField;
    @FXML private Button updateSliderForDynamicAnalysisButton;
    @FXML private Slider sliderForDynamicAnalysis;
    @FXML private Button stopDynamicAnalysisButton;
    private boolean isSliderUpdated = false;

    private boolean displayingMostRecentSheetVersion = true;
    private String currentSheetName;
    private int currentMaxVersionNumInUpdatingClient;
    private RecentVersionNumRefresher recentVersionNumRefresher;
    private String currentUserNameInClient;

    public void setMainController(SheetWindowController sheetWindowController) {
        this.sheetWindowController = sheetWindowController;
    }

    @FXML
    public void initialize() {
        recentVersionNumRefresher = new RecentVersionNumRefresher(this::updateVersionNumbersInVersionComboBoxAndIndicateChangeToUser);
    }

    public void handleCellClick(SheetDto sheetDto, CoordinateDto selectedCoordinate) {
        CellDto selectedCellDto;
        boolean thereAreCellsThatSelectedCellDependsOn = false;
        boolean thereAreCellsThatSelectedCellInfluenceOn = false;

        if (sheetDto.coordinateToCellDtoMap().containsKey(selectedCoordinate)){
            selectedCellDto = sheetDto.coordinateToCellDtoMap().get(selectedCoordinate);
            thereAreCellsThatSelectedCellDependsOn = !selectedCellDto.dependsOnCoordinatesSet().isEmpty();
            thereAreCellsThatSelectedCellInfluenceOn = !selectedCellDto.influencesOnCoordinatesSet().isEmpty();

        } else {
            selectedCellDto = new CellDto(selectedCoordinate, "","", -1,"", null,null);
        }

        String messageRegardingCellsItDependsOn = thereAreCellsThatSelectedCellDependsOn ? " The cell depends on cells with blue border blue." : "";
        String messageRegardingCellsInInfluencesOn = thereAreCellsThatSelectedCellInfluenceOn ? " The cell influences cells with green border." : "";
        currentlySelectedCoordinateDto = selectedCoordinate;
        String coordinateStr = selectedCoordinate.toString();
        idOfSelectedCell.setText(coordinateStr);
        originalValueStrOfSelectedCell.setText(selectedCellDto.originalValueStr());
        int lastVersionInWhichCellHasChanged = selectedCellDto.lastVersionInWhichCellHasChanged();
        if (lastVersionInWhichCellHasChanged == -1) {
            versionNumOfLastChange.setText("Empty cell - No change yet.");
            userNameOfLastChangeLabel.setText("Empty cell - No change yet.");
        } else {
            versionNumOfLastChange.setText(String.valueOf(lastVersionInWhichCellHasChanged));
            userNameOfLastChangeLabel.setText(selectedCellDto.userNameOfLastChange());
        }
        messageOfRecentActionOutcomeLabel.setText("Displaying data of cell " + selectedCoordinate.toString() + " (highlighted in the sheet)."
                + messageRegardingCellsItDependsOn + messageRegardingCellsInInfluencesOn);

    }

    @FXML
    public void handleUpdateCellValueButtonAndClearTextField() {
        try {
            String newValueStr = newValueToCellTextField.getText().trim();
            //in case no cell is selected or no value was entered - should we pop a message to the user before preforming the action?
            if (currentlySelectedCoordinateDto == null) {
                messageOfRecentActionOutcomeLabel.setText("No cell is selected - the system can not update a value. Please select a cell first.");
            } else {
                recentVersionNumRefresher.pause();

                if (newValueStr.isEmpty()) {
                    sheetWindowController.updateCellValue(currentlySelectedCoordinateDto, "", currentMaxVersionNumInUpdatingClient);
                    messageOfRecentActionOutcomeLabel.setText("No value was entered - the system updated the selected cell " +
                            currentlySelectedCoordinateDto + " to be an empty cell.");
                    clearDataInTopPartRegardingSelectedCell();
                } else {
                    sheetWindowController.updateCellValue(currentlySelectedCoordinateDto, newValueStr, currentMaxVersionNumInUpdatingClient);
                    newValueToCellTextField.clear();
                    clearDataInTopPartRegardingSelectedCell();
                }

                currentlySelectedCoordinateDto = null;
                recentVersionNumRefresher.resume();
            }
        } catch (Exception e) {
            sheetWindowController.cleanUnnecessaryStyleClassesForAllCells();
            clearDataInTopPartRegardingSelectedCell();
            currentlySelectedCoordinateDto = null;
            recentVersionNumRefresher.resume();
        }
    }

    private void clearDataInTopPartRegardingSelectedCell() {
        idOfSelectedCell.setText("");
        originalValueStrOfSelectedCell.setText("");
        versionNumOfLastChange.setText("");
        userNameOfLastChangeLabel.setText("");
    }

    public void deleteAllVersionNumbersInComboBoxFromPreviousSheet() {
        selectVersionNumberComboBox.getItems().clear();
    }

    public void addNewVersionNumberToVersionComboBox(int newVersionNumber) {
        selectVersionNumberComboBox.getItems().add(newVersionNumber);
        currentMaxVersionNumInUpdatingClient = newVersionNumber;
        recentVersionNumRefresher.updateCurrentMaxVersionNumInClient(newVersionNumber);
    }

    @FXML
    public void handleDisplaySelectedVersionButton() {
        if (selectVersionNumberComboBox.getValue() == null) {
            messageOfRecentActionOutcomeLabel.setText("Pressed button 'Display selected version', but no version is selected. Please select a version.");
            return;
        }
        try {
            int versionNumToDisplay = selectVersionNumberComboBox.getValue();
            if (versionNumToDisplay == getFromServerVersionNumOfRecentSelectedSheet()) {
                messageOfRecentActionOutcomeLabel.setText("The most recent version is already displayed.");
                return;
            }
            displayingMostRecentSheetVersion = false;
            disableAllButtonsInSceneExceptSelectedAndReturnToMainWindowButton(displayRecentSheetButton);
            sheetWindowController.displaySheetOfSpecificVersion(versionNumToDisplay);
            messageOfRecentActionOutcomeLabel.setText("Version " + versionNumToDisplay + " is now displayed.");
        } catch (Exception e) {
            messageOfRecentActionOutcomeLabel.setText(e.getMessage());
        }
    }

    private int getFromServerVersionNumOfRecentSelectedSheet() {
        int versionNum = -1;
        String finalUrl = HttpUrl
                .parse(Constants.GET_VERSION_NUM_OF_RECENT_SELECTED_SHEET)
                .newBuilder()
                .addQueryParameter(Constants.SHEET_NAME, currentSheetName)
                .build()
                .toString();

        try {
            Response response = HttpClientUtil.runSync(finalUrl);
            if (response.isSuccessful()) {
                // Parse the response into RangeDto using GSON
                String responseBody = response.body().string();
                versionNum = Integer.parseInt(responseBody);
            } else {
                // Handle non-200 HTTP responses
                String responseBody = response.body().string();
                Platform.runLater(() ->
                        sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel(responseBody)
                );
            }
        } catch (Exception e) {
            // Handle exceptions
            Platform.runLater(() ->
                    sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel("Something went wrong: " + e.getMessage())
            );
        }

        return versionNum;
    }

    @FXML
    public void handleDisplayRecentSheetButton(){
        displayRecentSheetButton.getStyleClass().remove("version-number-changed");
        int versionNumOfRecentSelectedSheet = getFromServerVersionNumOfRecentSelectedSheet();
        Integer selectedItem = selectVersionNumberComboBox.getSelectionModel().getSelectedItem();
        int currentSelectedVersionNum = (selectedItem != null) ? selectedItem : -1; // or any default value
        selectVersionNumberComboBox.getSelectionModel().clearSelection();
        if (versionNumOfRecentSelectedSheet == currentSelectedVersionNum) {
            messageOfRecentActionOutcomeLabel.setText("The most recent version is already displayed.");
        } else {
            clearDataInTopPartRegardingSelectedCell();
            currentlySelectedCoordinateDto = null;
            displayingMostRecentSheetVersion = true;
            enableAllButtonsInScene();
            sheetWindowController.displaySheetOfSpecificVersion(versionNumOfRecentSelectedSheet);
            currentMaxVersionNumInUpdatingClient = versionNumOfRecentSelectedSheet;
            messageOfRecentActionOutcomeLabel.setText("Returned to the most recent version of the sheet - version " + versionNumOfRecentSelectedSheet + ".");
        }
    }

    public void setMessageOfRecentActionOutcomeLabel(String message) {
        messageOfRecentActionOutcomeLabel.setText(message);
    }

    public void disableAllButtonsInSceneExceptSelectedAndReturnToMainWindowButton(Button buttonToKeepEnabled) {
        Scene scene = buttonToKeepEnabled.getScene();
        if (scene != null) {
            for (Node node : scene.getRoot().lookupAll(".button")) {
                if (node instanceof Button) {
                    Button button = (Button) node;
                    button.setDisable(button != buttonToKeepEnabled);
                }
            }
        }

        returnToMainWindowButton.setDisable(false);
    }

    public void enableAllButtonsInScene() {
        Scene scene = displayRecentSheetButton.getScene();
        if (scene != null) {
            for (Node node : scene.getRoot().lookupAll(".button")) {
                if (node instanceof Button) {
                    Button button = (Button) node;
                    button.setDisable(false);
                }
            }
        }
    }

    public void updateCurrentSheetNameAndDataForRefresherInTopPart(String sheetName) {
        currentSheetName = sheetName;
        recentVersionNumRefresher.onSheetSelected(() -> currentSheetName, currentMaxVersionNumInUpdatingClient);
    }

    public void updateVersionNumbersInVersionComboBoxAndIndicateChangeToUser(int currentVersionNumberFromServer) {
        Platform.runLater(() -> {
            int firstIndexAfterPreviousMaxVersion = currentMaxVersionNumInUpdatingClient + 1;
            for (int i = firstIndexAfterPreviousMaxVersion; i <= currentVersionNumberFromServer; i++) {
                selectVersionNumberComboBox.getItems().add(i);
            }

            displaySelectedVersionButton.setDisable(true);
            displayRecentSheetButton.getStyleClass().add("version-number-changed");
            messageOfRecentActionOutcomeLabel.setText("The server has an updated sheet version(s). You can press 'Display recent sheet' to see the most recent version.");
        });
    }

    public void initialUpdateOfVersionNumbersInVersionComboBox(int currentVersionNumberFromServer) {
        for (int i = 1 ; i <= currentVersionNumberFromServer ; i++) {
            selectVersionNumberComboBox.getItems().add(i);
        }

        currentMaxVersionNumInUpdatingClient = currentVersionNumberFromServer;
    }

    public void disableButtonUpdateCellValue() {
        updateValueButton.setDisable(true);
    }

    @FXML
    public void handleReturnToMainWindowButton() {
        sliderForDynamicAnalysis.setShowTickMarks(false);  // Hide tick marks
        sliderForDynamicAnalysis.setShowTickLabels(false);  // Hide tick labels
        recentVersionNumRefresher.pause();
        sheetWindowController.enableAllButtonsInScene();
        sheetWindowController.handleReturnToMainWindowButton();
    }

    public void resumeVersionNumRefresher() {
        recentVersionNumRefresher.resume();
    }

    public void setLabelOfPermissionLevelForSelectedSheet(String permissionLevel) {
        currentPermissionLabel.setText(permissionLevel);
    }

    public void setUserNameAndSheetNameInTopPart(String currentUserName, String selectedSheetName) {
        currentUserNameInClient = currentUserName;
        userNameLabel.setText(currentUserName);
        sheetNameLabel.setText(selectedSheetName);
        messageOfRecentActionOutcomeLabel.setText("Welcome " + currentUserName + ", you are viewing sheet with name: " + selectedSheetName + ".");
    }

    public void enableReturnToMainWindowButton() {
        returnToMainWindowButton.setDisable(false);
    }

    @FXML
    public void handleUpdateSliderForDynamicAnalysisButton() {
        String minValueStr = minValueForDynamicAnalysisTextField.getText();
        String maxValueStr = maxValueForDynamicAnalysisTextField.getText();
        String stepSizeStr = stepSizeForDynamicAnalysisTextField.getText();
        double minValue = 0, maxValue = 0, stepSize = 0;
        if (minValueStr.isEmpty() || maxValueStr.isEmpty() || stepSizeStr.isEmpty()) {
            messageOfRecentActionOutcomeLabel.setText("Please fill all fields before updating the slider.");
            return;
        }
        if (currentlySelectedCoordinateDto == null) {
            messageOfRecentActionOutcomeLabel.setText("Please select a cell before updating the slider.");
            return;
        }
        try { //try to extract the values from the text fields - and handle exceptions
            minValue = Double.parseDouble(minValueStr);
            maxValue = Double.parseDouble(maxValueStr);
            stepSize = Double.parseDouble(stepSizeStr);
        }
        catch (Exception e) {
            messageOfRecentActionOutcomeLabel.setText("Failed to update slider. Please enter valid numbers.");
        }
        //if no exception was thrown - update the slider

        sliderForDynamicAnalysis.setMin(minValue);
        sliderForDynamicAnalysis.setMax(maxValue);
        sliderForDynamicAnalysis.setBlockIncrement(stepSize);
        sliderForDynamicAnalysis.setMajorTickUnit(stepSize);
        sliderForDynamicAnalysis.setMinorTickCount(0);
        sliderForDynamicAnalysis.setShowTickMarks(true);
        sliderForDynamicAnalysis.setShowTickLabels(true);
        sliderForDynamicAnalysis.setSnapToTicks(true);
        isSliderUpdated = true;

        messageOfRecentActionOutcomeLabel.setText
            ("Slider updated. You can use it for dynamic analysis.");

        disableAllButtonsInSceneExceptSelectedAndReturnToMainWindowButton(stopDynamicAnalysisButton);
    }

    private void displaySheetAfterDynamicAnalysis() {
        double newValue = sliderForDynamicAnalysis.getValue();
        try {
            SheetDto sheetDto = getTempSheetForDynamicAnalysisFromServer(newValue);
            messageOfRecentActionOutcomeLabel.setText("Sheet updated for dynamic analysis with value: " + newValue + " in cell " + currentlySelectedCoordinateDto + ".");
            sheetWindowController.loadNewSheet(sheetDto);
        } catch (Exception e) {
            messageOfRecentActionOutcomeLabel.setText("Failed to update the sheet for dynamic analysis with value: " + newValue + ". Please try again.");
        }
    }

    private SheetDto getTempSheetForDynamicAnalysisFromServer(double newTempValue) {
        String finalUrl = HttpUrl
                .parse(Constants.GET_TEMP_SHEET_FOR_DYNAMIC_ANALYSIS)
                .newBuilder()
                .addQueryParameter(Constants.SHEET_NAME, currentSheetName)
                .addQueryParameter(Constants.USERNAME, currentUserNameInClient)
                .addQueryParameter(Constants.ROW_NUMBER, String.valueOf(currentlySelectedCoordinateDto.getRow()))
                .addQueryParameter(Constants.COLUMN_NUMBER, String.valueOf(currentlySelectedCoordinateDto.getColumn()))
                .build()
                .toString();
        RequestBody body = RequestBody.create(String.valueOf(newTempValue), MediaType.get("text/plain; charset=utf-8"));

        try {
            Response response = HttpClientUtil.runSyncWithPostAndBody(finalUrl, body);
            if (response.isSuccessful()) {
                return Constants.GSON_INSTANCE.fromJson(response.body().string(), SheetDto.class);
            } else {
                String responseBody = response.body().string();
                throw new IllegalArgumentException(responseBody);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @FXML
    public void handleMouseDragReleasedOnSlider() {
        displaySheetAfterDynamicAnalysis();
    }

    @FXML
    public void handleMouseReleasedOnSlider() {
        displaySheetAfterDynamicAnalysis();
    }

    @FXML
    public void handleStopDynamicAnalysisButton() {
        if (!isSliderUpdated) {
            messageOfRecentActionOutcomeLabel.setText("The slider was not updated. Dynamic analysis was not started.");
            return;
        }
        isSliderUpdated = false;
        sliderForDynamicAnalysis.setShowTickMarks(false);  // Hide tick marks
        sliderForDynamicAnalysis.setShowTickLabels(false);  // Hide tick labels
        minValueForDynamicAnalysisTextField.clear();
        maxValueForDynamicAnalysisTextField.clear();
        stepSizeForDynamicAnalysisTextField.clear();
        selectVersionNumberComboBox.getSelectionModel().clearSelection();
        handleDisplayRecentSheetButton();
        messageOfRecentActionOutcomeLabel.setText("Dynamic analysis stopped. Returned to the most recent version of the sheet.");
    }

    public void close() {
        recentVersionNumRefresher.stop();
    }
}

