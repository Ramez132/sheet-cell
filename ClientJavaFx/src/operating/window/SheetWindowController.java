package operating.window;

import dto.coordinate.CoordinateDto;
import dto.management.info.SheetAndRangesNamesDto;
import dto.range.RangeDto;
import dto.sheet.SheetDto;
import dto.sheet.SheetWithSortedOrFilteredRangeDto;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;
import management.window.ManagementWindowController;
import okhttp3.*;
import operating.top.TopPartController;
import operating.left.LeftPartController;
import operating.table.TablePartController;
import util.Constants;
import util.http.HttpClientUtil;

import java.util.List;

public class SheetWindowController {

    private Scene managementWindowScene;
    Stage primaryStage;
    private ManagementWindowController managementWindowController;
    private Response responseReceivedFromManagementWindow;
    private String userName;
    private String currentSheetName;
    private String currentUserPermissionLevel;
    private String sheetName;

    @FXML private TopPartController topPartController;
    @FXML private TablePartController tablePartController;
    @FXML private LeftPartController leftPartController;

    @FXML
    public void initialize() {
        topPartController.setMainController(this);
        tablePartController.setMainController(this);
        leftPartController.setMainController(this);
    }

    public void handleCellClick(SheetDto sheetDto, CoordinateDto coordinateDto) {
        topPartController.handleCellClick(sheetDto, coordinateDto);
    }

    public void updateCellValue(CoordinateDto currentlySelectedCoordinate, String newValueStr, int currentVersionNumInUpdatingClient) {
        try {
            SheetDto sheetDto = sendToServerNewValueForCell(currentlySelectedCoordinate, newValueStr, currentVersionNumInUpdatingClient);
            //update could fail - for example, if the version number in the client is not the most recent one
            //if the updates fails, the sendToServerNewValueForCell method will throw an exception
            topPartController.addNewVersionNumberToVersionComboBox(sheetDto.thisSheetVersion());
            loadNewSheet(sheetDto);
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
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private SheetDto sendToServerNewValueForCell(CoordinateDto currentlySelectedCoordinate, String newValueStr, int currentVersionNumInUpdatingClient) {
        try {
            String finalUrl = HttpUrl
                    .parse(Constants.UPDATE_CELL_VALUE)
                    .newBuilder()
                    .addQueryParameter(Constants.USERNAME, userName)
                    .addQueryParameter(Constants.SHEET_NAME, currentSheetName)
                    .addQueryParameter(Constants.CURRENT_VERSION_NUM_IN_UPDATING_CLIENT, String.valueOf(currentVersionNumInUpdatingClient))
                    .addQueryParameter(Constants.ROW_NUMBER, String.valueOf(currentlySelectedCoordinate.getRow()))
                    .addQueryParameter(Constants.COLUMN_NUMBER, String.valueOf(currentlySelectedCoordinate.getColumn()))
                    .build()
                    .toString();
            RequestBody body = RequestBody.create(newValueStr, MediaType.get("text/plain; charset=utf-8"));

            Response response = HttpClientUtil.runSyncWithPostAndBody(finalUrl, body);
            if (response.code() != 200) {
                String responseBody = response.body().string();
                throw new IllegalArgumentException(responseBody);
            } else {
                ResponseBody responseBody = response.body();
                return Constants.GSON_INSTANCE.fromJson(responseBody.string(), SheetDto.class);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void setNotificationMessageOfRecentActionOutcomeLabel(String message) {
        topPartController.setMessageOfRecentActionOutcomeLabel(message);
    }

    public void handleChoosingRangeAndHighlightCellsInRangeMainController(RangeDto selectedRangeDto) {
        tablePartController.highlightCellsInSelectedRange(selectedRangeDto);
    }

    public void displaySheetOfSpecificVersion(int versionNumToDisplay) {
        try {
            SheetDto sheetDto = getFromServerSheetOfSpecificVersion(versionNumToDisplay);
            loadNewSheet(sheetDto);
        } catch (Exception e) {
            topPartController.setMessageOfRecentActionOutcomeLabel(e.getMessage());
            topPartController.enableAllButtonsInScene();
        }
    }

    public void displayRecentSheetBeforeSortingOrFilteringCachedInTablePart() {
        tablePartController.displayRecentSheetBeforeSortingOrFilteringCachedInTablePart();
    }

    private SheetDto getFromServerSheetOfSpecificVersion(int versionNumToDisplay) {
        try {
            String finalUrl = HttpUrl
                    .parse(Constants.GET_SHEET_OF_SPECIFIC_VERSION)
                    .newBuilder()
                    .addQueryParameter(Constants.USERNAME, userName)
                    .addQueryParameter(Constants.SHEET_NAME, currentSheetName)
                    .addQueryParameter(Constants.VERSION_NUMBER, Integer.toString(versionNumToDisplay))
                    .build()
                    .toString();

            Response response = HttpClientUtil.runSync(finalUrl);
            if (response.code() != 200) {
                String responseBody = response.body().string();
                throw new IllegalArgumentException(responseBody);
            } else {
                ResponseBody body = response.body();
                return Constants.GSON_INSTANCE.fromJson(body.string(), SheetDto.class);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void loadSheetFromManagementWindow() {
        try {
            ResponseBody body = responseReceivedFromManagementWindow.body();
            SheetAndRangesNamesDto sheetAndRangesNamesDto = Constants.GSON_INSTANCE.fromJson(body.string(), SheetAndRangesNamesDto.class);
            SheetDto sheetDto = sheetAndRangesNamesDto.sheetDto();
            List<String> rangesNames = sheetAndRangesNamesDto.rangesNames();
            leftPartController.deleteRangesNamesFromRangesComboBox();
            leftPartController.addNewRangesNamesToRangesComboBox(rangesNames);
            topPartController.deleteAllVersionNumbersInComboBoxFromPreviousSheet();
            topPartController.initialUpdateOfVersionNumbersInVersionComboBox(sheetDto.thisSheetVersion());
            updateCurrentSheetNameAndDataForRefreshersInAllControllers(sheetDto.sheetName());
            topPartController.resumeVersionNumRefresher();
            leftPartController.resumeRangesNamesRefresher();

            if (currentUserPermissionLevel.equals(Constants.READER)) {
                topPartController.disableButtonUpdateCellValue();
                leftPartController.disableEditingButtonsInLeftPart();
            }

            loadNewSheet(sheetDto);

        } catch (Exception e) {
            topPartController.setMessageOfRecentActionOutcomeLabel(e.getMessage());
            topPartController.enableAllButtonsInScene();
        }
    }

    public void updateCurrentSheetNameAndDataForRefreshersInAllControllers(String sheetName) {
        currentSheetName = sheetName;
        topPartController.updateCurrentSheetNameAndDataForRefresherInTopPart(sheetName);
        leftPartController.updateCurrentSheetNameAndDataForRefresherInLeftPart(sheetName);
        tablePartController.updateCurrentSheetNameInTablePart(sheetName);
    }

    public void loadNewSheet(SheetDto sheetDto){
        tablePartController.loadAndDisplayNewSheet(sheetDto);
    }

    public void setManagementWindowScene(Scene managementWindowScene) {
        this.managementWindowScene = managementWindowScene;
    }

    public void setPrimaryStage(Stage primaryStage) { this.primaryStage = primaryStage; }

    public void returnToManagementWindow() {
        managementWindowController.resumePermissionRequestsRefresher();
        primaryStage.setScene(managementWindowScene);
    }

    public void setManagementWindowController(ManagementWindowController managementWindowController) {
        this.managementWindowController = managementWindowController;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setResponseFromManagementWindow(Response response) {
        this.responseReceivedFromManagementWindow = response;
    }

    public void DisplaySheetWithSortedOrFilteredLines(SheetWithSortedOrFilteredRangeDto sheetWithSortedOrFilteredRangeDto) {
        tablePartController.displaySheetWithSortedOrFilteredRange(sheetWithSortedOrFilteredRangeDto);
    }


    public void handleShowFilteredLinesButton(String currentFilteringStartCoordinate, String currentFilteringEndCoordinate,
                                              char currentColumnLetterForFiltering, List<String> selectedUniqueValuesOptions) {
        try {
            SheetWithSortedOrFilteredRangeDto sheetWithFilteredRangeDto =
                    getSheetWithFilteredAreaFromServer(currentFilteringStartCoordinate, currentFilteringEndCoordinate,
                                                       currentColumnLetterForFiltering, selectedUniqueValuesOptions);
            DisplaySheetWithSortedOrFilteredLines(sheetWithFilteredRangeDto);
            String messageToUser = "Displaying lines with the selected unique values in column " +
                    currentColumnLetterForFiltering + ", in the selected filtering area: "
                    + currentFilteringStartCoordinate +
                    " to " + currentFilteringEndCoordinate + ".";
            topPartController.setMessageOfRecentActionOutcomeLabel(messageToUser);
        } catch (Exception e) {
            topPartController.setMessageOfRecentActionOutcomeLabel(e.getMessage());
        }
    }

    private SheetWithSortedOrFilteredRangeDto getSheetWithFilteredAreaFromServer
            (String currentFilteringStartCoordinate, String currentFilteringEndCoordinate,
             char currentColumnLetterForFiltering, List<String> selectedUniqueValuesOptions) {
        try {
            String finalUrl = HttpUrl
                    .parse(Constants.GET_SHEET_WITH_FILTERED_RANGE)
                    .newBuilder()
                    .addQueryParameter(Constants.USERNAME, userName)
                    .addQueryParameter(Constants.SHEET_NAME, currentSheetName)
                    .addQueryParameter(Constants.LEFT_TOP_START_COORDINATE, currentFilteringStartCoordinate)
                    .addQueryParameter(Constants.RIGHT_BOTTOM_END_COORDINATE, currentFilteringEndCoordinate)
                    .addQueryParameter(Constants.COLUMN_CHAR_STRING, String.valueOf(currentColumnLetterForFiltering))
                    .build()
                    .toString();
            String jsonResult = Constants.GSON_INSTANCE.toJson(selectedUniqueValuesOptions);
            RequestBody body = RequestBody.create(jsonResult, MediaType.get("application/json"));

            Response response = HttpClientUtil.runSyncWithPostAndBody(finalUrl, body);
            if (response.code() != 200) {
                String responseBody = response.body().string();
                throw new IllegalArgumentException(responseBody);
            } else {
                ResponseBody responseBody = response.body();
                return Constants.GSON_INSTANCE.fromJson(responseBody.string(), SheetWithSortedOrFilteredRangeDto.class);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void handleReturnToMainWindowButton() {
        returnToManagementWindow();
    }

    public void setPermissionLevelForSelectedSheet(String permissionLevel) {
        this.currentUserPermissionLevel = permissionLevel;
        topPartController.setLabelOfPermissionLevelForSelectedSheet(permissionLevel);
    }

    public void enableAllButtonsInScene() {
        leftPartController.enableAllButtonsInScene();
    }

    public void cleanUnnecessaryStyleClassesForAllCells() {
        tablePartController.cleanUnnecessaryStyleClassesForAllCells();
    }

    public void setUserNameAndSheetNameInSheetWindow(String currentUserName, String selectedSheetName) {
        topPartController.setUserNameAndSheetNameInTopPart(currentUserName, selectedSheetName);
    }

    public void enableReturnToMainWindowButton() {
        topPartController.enableReturnToMainWindowButton();
    }

    public void close() {
        topPartController.close();
        leftPartController.close();
    }
}
