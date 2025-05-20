package operating.left;

import com.google.gson.reflect.TypeToken;
import dto.range.RangeDto;
import dto.sheet.SheetWithSortedOrFilteredRangeDto;
import dto.sort.SortParametersDto;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.util.Callback;
import okhttp3.*;
import operating.window.SheetWindowController;
import util.Constants;
import util.http.HttpClientUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static util.Constants.GSON_INSTANCE;

public class LeftPartController {
    private SheetWindowController sheetWindowController;
    private boolean isUniqueValuesUpdatedFromRecentFilteringAreaAndColumnLetter = false;
    private char currentColumnLetterForFiltering;
    @FXML private ComboBox<String> selectRangeComboBox;
    @FXML private Button displayCellsInSelectedRangeButton;
    @FXML private TextField newRangeNameTextField;
    @FXML private TextField newRangeStartCoordinateTextField;
    @FXML private TextField newRangeEndCoordinateTextField;
    @FXML private TextField newFilterStartCoordinateTextField;
    @FXML private TextField newFilterEndCoordinateTextField;
    @FXML private TextField selectColumnLetterForFilteringTextField;
    @FXML private ListView<String> listViewWithCheckBoxesContainingPossibleUniqueValuesToFilter;
    private ObservableList<String> listViewOptionsForFiltering = FXCollections.observableArrayList();

    // Map to track which items are selected to filter
    private Map<String, Boolean> selectionMapOfFilteringListView = new HashMap<>();

    @FXML private TextField newSortStartCoordinateTextField;
    @FXML private TextField newSortEndCoordinateTextField;
    @FXML private TextField firstColumnLetterToSortByTextField;
    @FXML private TextField secondColumnLetterToSortByTextField;
    @FXML private TextField thirdColumnLetterToSortByTextField;
    @FXML private TextField fourthColumnLetterToSortByTextField;
    @FXML private TextField fifthColumnLetterToSortByTextField;
    @FXML private Button undoFilteringButton;
    @FXML private Button undoSortingButton;
    @FXML private Button deleteSelectedRangeButton;
    @FXML private Button addNewRangeButton;

    private String currentSheetName;
    private RangeDto currentRangeDto;
    private String currentFilteringStartCoordinate;
    private String currentFilteringEndCoordinate;
    private RangesNamesRefresher rangesNamesRefresher;
    private List<String> currentRangesNamesInClient;


    @FXML
    public void initialize() {

        rangesNamesRefresher = new RangesNamesRefresher(this::updateRangesNamesFromRefresher);

        // Set a custom cell factory to render CheckBox for each item
        listViewWithCheckBoxesContainingPossibleUniqueValuesToFilter.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<>() {
                    private final CheckBox checkBox = new CheckBox();

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            checkBox.setText(item);
                            checkBox.setSelected(selectionMapOfFilteringListView.getOrDefault(item, false));

                            // Handle checkbox state change
                            checkBox.setOnAction(event -> {
                                selectionMapOfFilteringListView.put(item, checkBox.isSelected());
                            });

                            setGraphic(checkBox);
                        }
                    }
                };
            }
        });
    }

    public void updateRangesNamesFromRefresher(List<String> rangesNamesListFromServer) {
        Platform.runLater(() -> {
            selectRangeComboBox.getItems().clear();
            selectRangeComboBox.getItems().addAll(rangesNamesListFromServer);
            selectRangeComboBox.setPromptText("Select range");
            currentRangesNamesInClient = rangesNamesListFromServer;
            sheetWindowController.cleanUnnecessaryStyleClassesForAllCells();
        });
    }

    public void setMainController(SheetWindowController sheetWindowController) {
        this.sheetWindowController = sheetWindowController;
    }

    // Example of fetching and updating ComboBox dynamically
    public void updateFilteringOptionsListViewWithUniqueValuesFroSelectedColumnAndArea(List<String> uniqueValuesInSelectedColumn) {
        List<String> observableListOfUniqueValuesToFilter = FXCollections.observableArrayList(uniqueValuesInSelectedColumn);

        // Update ObservableList
        listViewOptionsForFiltering.clear();
        listViewOptionsForFiltering.addAll(observableListOfUniqueValuesToFilter);

        // Bind the data to the ComboBox
        listViewWithCheckBoxesContainingPossibleUniqueValuesToFilter.setItems(listViewOptionsForFiltering);

        // Initialize the selection map
        selectionMapOfFilteringListView.clear();
        listViewOptionsForFiltering.forEach(item -> selectionMapOfFilteringListView.put(item, false));
    }

    @FXML
    public void handleGetUniqueValuesToFilterButton() {
        if (newFilterStartCoordinateTextField.getText().isEmpty() || newFilterEndCoordinateTextField.getText().isEmpty()) {
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel("One or more coordinate fields are empty - the system can not get unique values to filter. Please fill all fields.");
            return;
        } else if (selectColumnLetterForFilteringTextField.getText().isEmpty()) {
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel("The column letter is empty - the system can not get unique values to filter. Please fill the column letter.");
            return;
        }
        try {
            String filterAreaStartCoordinateStr = newFilterStartCoordinateTextField.getText().trim();
            String filterAreaEndCoordinateStr = newFilterEndCoordinateTextField.getText().trim();
            String stringWithLetterOfColumnToGetUniqueValuesToFilter = selectColumnLetterForFilteringTextField.getText().trim().toUpperCase();
            newFilterStartCoordinateTextField.clear();
            newFilterEndCoordinateTextField.clear();
            selectColumnLetterForFilteringTextField.clear();

            List<String> uniqueValuesInSelectedColumn = getUniqueValuesForFilteringFromServer(filterAreaStartCoordinateStr, filterAreaEndCoordinateStr, stringWithLetterOfColumnToGetUniqueValuesToFilter);
            updateFilteringOptionsListViewWithUniqueValuesFroSelectedColumnAndArea(uniqueValuesInSelectedColumn);

            //updating for handleShowFilteredLinesButton method
            isUniqueValuesUpdatedFromRecentFilteringAreaAndColumnLetter = true;
            currentFilteringStartCoordinate = filterAreaStartCoordinateStr;
            currentFilteringEndCoordinate = filterAreaEndCoordinateStr;
            currentColumnLetterForFiltering = stringWithLetterOfColumnToGetUniqueValuesToFilter.charAt(0);
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel("Please select one or more unique values to filter in column '" + stringWithLetterOfColumnToGetUniqueValuesToFilter + "'.");
        } catch (Exception e) {
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel(e.getMessage());
        }
    }

    private List<String> getUniqueValuesForFilteringFromServer(String filterAreaStartCoordinateStr, String filterAreaEndCoordinateStr, String stringWithLetterOfColumnToGetUniqueValuesToFilter) {
        List<String> uniqueValuesInSelectedColumn = new ArrayList<>();
        String finalUrl = HttpUrl
                .parse(Constants.GET_UNIQUE_VALUES_FOR_FILTERING)
                .newBuilder()
                .addQueryParameter(Constants.SHEET_NAME, currentSheetName)
                .addQueryParameter(Constants.LEFT_TOP_START_COORDINATE, filterAreaStartCoordinateStr)
                .addQueryParameter(Constants.RIGHT_BOTTOM_END_COORDINATE, filterAreaEndCoordinateStr)
                .addQueryParameter(Constants.COLUMN_CHAR_STRING, stringWithLetterOfColumnToGetUniqueValuesToFilter)
                .build()
                .toString();

        try {
            Response response = HttpClientUtil.runSync(finalUrl);
            if (response.isSuccessful()) {
                // Parse the response into List<String> using GSON
                Type listType = new TypeToken<List<String>>(){}.getType();
                uniqueValuesInSelectedColumn = GSON_INSTANCE.fromJson(response.body().string(), listType);
            } else {
                // Handle non-200 HTTP responses
                String responseBody = response.body().string();
                throw new IllegalArgumentException(responseBody);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return uniqueValuesInSelectedColumn;
    }

    @FXML
    // Handle button click event to get selected options
    private void handleShowFilteredLinesButton() {
        if (listViewWithCheckBoxesContainingPossibleUniqueValuesToFilter.getItems().isEmpty()) {
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel("No unique values to filter - first enter filtering coordinates and column letter then press \"Get values to filter\".");
            return;
        }

        // Collect the selected options
        List<String> selectedUniqueValuesOptions = selectionMapOfFilteringListView.entrySet().stream()
                .filter(Map.Entry::getValue) // Only get selected options (true)
                .map(Map.Entry::getKey)      // Extract the keys (option names)
                .toList();

        if (selectedUniqueValuesOptions.isEmpty()) {
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel
                    ("No unique values were selected to filter. Please select one or more unique values to filter.");
            return;
        }

        disableAllButtonsInSceneExceptSelectedAndReturnToMainWindowButton(undoFilteringButton);

        sheetWindowController.handleShowFilteredLinesButton(currentFilteringStartCoordinate, currentFilteringEndCoordinate, currentColumnLetterForFiltering, selectedUniqueValuesOptions);

        listViewOptionsForFiltering.clear();
        selectionMapOfFilteringListView.clear();
        listViewWithCheckBoxesContainingPossibleUniqueValuesToFilter.setItems(listViewOptionsForFiltering);

        isUniqueValuesUpdatedFromRecentFilteringAreaAndColumnLetter = false;
    }

    @FXML
    public void handleUndoFilteringButton() {
        enableAllButtonsInScene();
        sheetWindowController.displayRecentSheetBeforeSortingOrFilteringCachedInTablePart();
    }


    @FXML
    public void handleAddNewRangeButtonAndClearRelevantTextFields() {
        String rangeName = newRangeNameTextField.getText().trim();
        String leftTopStartCoordinateStr = newRangeStartCoordinateTextField.getText().trim();
        String rightBottomEndCoordinateStr = newRangeEndCoordinateTextField.getText().trim();

        if (rangeName.isEmpty() || leftTopStartCoordinateStr.isEmpty() || rightBottomEndCoordinateStr.isEmpty()) {
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel("One or more fields are empty - the system can not create a new range. Please fill all fields.");
        } else {
            handleCreatingNewRangeInServer(rangeName, leftTopStartCoordinateStr, rightBottomEndCoordinateStr);
            newRangeNameTextField.clear(); //will get here even if the range was not created and error caught
            newRangeStartCoordinateTextField.clear();
            newRangeEndCoordinateTextField.clear();
        }
    }

    public void handleCreatingNewRangeInServer(String rangeName, String leftTopStartCoordinateStr, String rightBottomEndCoordinateStr) {
        try {
            rangesNamesRefresher.pause();
            sheetWindowController.cleanUnnecessaryStyleClassesForAllCells();
            boolean isRangeCreated = tryCreateNewRangeInServer(rangeName, leftTopStartCoordinateStr, rightBottomEndCoordinateStr);
            //if the range was not created, the method will throw an exception
            if (isRangeCreated) {
                addNewRangeNameToRangesComboBox(rangeName);
                rangesNamesRefresher.resume();
                sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel("New range created successfully: '" + rangeName + "' . The range is from cell "
                        + leftTopStartCoordinateStr.toUpperCase() + " to cell " + rightBottomEndCoordinateStr.toUpperCase());
            }
        } catch (Exception e) {
            rangesNamesRefresher.resume();
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel(e.getMessage());
        }
    }

    private boolean tryCreateNewRangeInServer(String rangeName, String leftTopStartCoordinateStr, String rightBottomEndCoordinateStr) {

        String finalUrl = HttpUrl
                .parse(Constants.ADD_NEW_RANGE)
                .newBuilder()
                .addQueryParameter(Constants.SHEET_NAME, currentSheetName)
                .addQueryParameter(Constants.RANGE_NAME, rangeName)
                .addQueryParameter(Constants.LEFT_TOP_START_COORDINATE, leftTopStartCoordinateStr)
                .addQueryParameter(Constants.RIGHT_BOTTOM_END_COORDINATE, rightBottomEndCoordinateStr)
                .build()
                .toString();

        try {
            Response response = HttpClientUtil.runSyncWithPost(finalUrl);
            //if the range was created successfully, the response will be 201 and the method will return true
            //if the range was not created, the response will be 500 and the method will throw an exception
            if (response.isSuccessful()) {
                return true;
            } else {
                // Handle non-200 HTTP responses
                String responseBody = response.body().string();
                throw new IllegalArgumentException(responseBody);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    public void deleteRangesNamesFromRangesComboBox() {
        selectRangeComboBox.getItems().clear();
    }

    public void addNewRangesNamesToRangesComboBox(List<String> rangesNames) {
        selectRangeComboBox.getItems().addAll(rangesNames);
        currentRangesNamesInClient = rangesNames;
    }

    public void addNewRangeNameToRangesComboBox(String rangeName) {
        selectRangeComboBox.getItems().add(rangeName);
    }

    @FXML
    public void handleDeleteSelectedRangeButton() {
        if (selectRangeComboBox.getItems().isEmpty()) {
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel("No ranges are available to delete.");
            return;
        } else if (selectRangeComboBox.getValue() == null) {
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel("There is nothing to delete - no range is selected.");
            return;
        }

        try {
            rangesNamesRefresher.pause();
            sheetWindowController.cleanUnnecessaryStyleClassesForAllCells();
            String rangeName = selectRangeComboBox.getValue();
            boolean isRangeDeleted = deleteRangeFromRangesManagerInServer(rangeName);
            if (isRangeDeleted) {
                sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel("Range '" + rangeName + "' was deleted successfully.");
                selectRangeComboBox.getItems().remove(rangeName);
            }
            rangesNamesRefresher.resume();
        } catch (Exception e) {
            rangesNamesRefresher.resume();
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel(e.getMessage());
        }
    }

    private boolean deleteRangeFromRangesManagerInServer(String rangeName) {
        String finalUrl = HttpUrl
                .parse(Constants.DELETE_RANGE)
                .newBuilder()
                .addQueryParameter(Constants.SHEET_NAME, currentSheetName)
                .addQueryParameter(Constants.RANGE_NAME, rangeName)
                .build()
                .toString();

        try {
            Response response = HttpClientUtil.runSyncWithDelete(finalUrl);
            if (response.isSuccessful()) {
                return true;
            } else {
                // Handle non-200 HTTP responses
                String responseBody = response.body().string();
                throw new IllegalArgumentException(responseBody);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error: " + e.getMessage());
        }
    }

    @FXML
    public void handleDisplayCellsInSelectedRangeButton() {
        if (selectRangeComboBox.getItems().isEmpty()) {
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel("No ranges are available to display.");
            return;
        } else if (selectRangeComboBox.getValue() == null) {
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel("There is nothing to display - no range is selected.");
            return;
        }
        try {
            String rangeName = selectRangeComboBox.getValue();
            RangeDto rangeDto = getRangeDtoFromServer(rangeName); //if there's an error, the method will set the notification message
            if (rangeDto != null) {
            String topLeftStartCoordinateStr = rangeDto.topLeftStartCoordinate().toString();
            String bottomRightEndCoordinateStr = rangeDto.bottomRightEndCoordinate().toString();
            sheetWindowController.handleChoosingRangeAndHighlightCellsInRangeMainController(rangeDto);
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel("Cells in selected range '" + rangeName +
                    "' are now highlighted with purple border. The range is from "
                    + topLeftStartCoordinateStr + " to " + bottomRightEndCoordinateStr +".");
            }
        } catch (Exception e) {
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel(e.getMessage());
        }
    }

    private RangeDto getRangeDtoFromServer(String rangeName) {
        RangeDto rangeDto = null;
        String finalUrl = HttpUrl
                .parse(Constants.GET_RANGE_DTO_FROM_SELECTED_SHEET)
                .newBuilder()
                .addQueryParameter(Constants.SHEET_NAME, currentSheetName)
                .addQueryParameter(Constants.RANGE_NAME, rangeName)
                .build()
                .toString();

        try {
            Response response = HttpClientUtil.runSync(finalUrl);
            if (response.isSuccessful()) {
                // Parse the response into RangeDto using GSON
                rangeDto = GSON_INSTANCE.fromJson(response.body().string(), RangeDto.class);
            } else {
                // Handle non-200 HTTP responses
                String responseBody = response.body().string();
                sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel(responseBody);
                if (responseBody.contains("does not exist")) {
                    selectRangeComboBox.getItems().remove(rangeName);
                    sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel
                    ("Range '" + rangeName + "' does not exist anymore. This name was removed from the list of ranges.");
                }
            }
        } catch (Exception e) {
            // Handle exceptions
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel("Something went wrong: " + e.getMessage());
        }
        return rangeDto;
    }

    @FXML
    public void handleShowSortedLinesButton() {
        boolean firstColumnTextFieldEmpty = firstColumnLetterToSortByTextField.getText().isEmpty();
        boolean secondColumnTextFieldEmpty = secondColumnLetterToSortByTextField.getText().isEmpty();
        boolean thirdColumnTextFieldEmpty = thirdColumnLetterToSortByTextField.getText().isEmpty();
        boolean fourthColumnTextFieldEmpty = fourthColumnLetterToSortByTextField.getText().isEmpty();
        boolean fifthColumnTextFieldEmpty = fifthColumnLetterToSortByTextField.getText().isEmpty();

        if (newSortStartCoordinateTextField.getText().isEmpty() || newSortEndCoordinateTextField.getText().isEmpty()) {
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel("Please enter both start and end coordinates of area to sort.");
            return;
        } else {
            try {
                boolean areAllTextFieldsFilledOrEmptyProperlyForSorting = isCombinationOfEmptyAndFilledTextFieldsValidForSorting(firstColumnTextFieldEmpty, secondColumnTextFieldEmpty, thirdColumnTextFieldEmpty, fourthColumnTextFieldEmpty, fifthColumnTextFieldEmpty);
            } catch (Exception e) {
                sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel(e.getMessage());
                return;
            }
        }
        //if got here, combination of empty and filled text fields is valid for sorting, and range coordinates are filled
        try {
            String newSortStartCoordinateStr = newSortStartCoordinateTextField.getText().trim();
            String newSortEndCoordinateStr = newSortEndCoordinateTextField.getText().trim();
            List<String> allColumnLettersToSortByAsStrings = fillArrayListWithColumnLettersToSortBy (firstColumnTextFieldEmpty, secondColumnTextFieldEmpty,
                                                             thirdColumnTextFieldEmpty, fourthColumnTextFieldEmpty, fifthColumnTextFieldEmpty);
            clearAllTextFieldsRelatedToSorting();

            SheetWithSortedOrFilteredRangeDto sheetWithSortedRangeDto =
            getFromServerSheetWithSortedRangeDto(newSortStartCoordinateStr, newSortEndCoordinateStr, allColumnLettersToSortByAsStrings);

            disableAllButtonsInSceneExceptSelectedAndReturnToMainWindowButton(undoSortingButton);
            sheetWindowController.DisplaySheetWithSortedOrFilteredLines(sheetWithSortedRangeDto);

        } catch (Exception e) {
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel(e.getMessage());
        }
    }

    private SheetWithSortedOrFilteredRangeDto getFromServerSheetWithSortedRangeDto(String newSortStartCoordinateStr, String newSortEndCoordinateStr, List<String> allColumnLettersToSortByAsStrings) {
        SheetWithSortedOrFilteredRangeDto sheetWithSortedRangeDto;
        String finalUrl = HttpUrl
                .parse(Constants.GET_SHEET_WITH_SORTED_RANGE)
                .newBuilder()
                .addQueryParameter(Constants.SHEET_NAME, currentSheetName)
                .build()
                .toString();

        SortParametersDto sortParametersDto = new SortParametersDto(newSortStartCoordinateStr, newSortEndCoordinateStr, allColumnLettersToSortByAsStrings);
        String jsonResult = GSON_INSTANCE.toJson(sortParametersDto);
        RequestBody body = RequestBody.create(GSON_INSTANCE.toJson(sortParametersDto), MediaType.get("application/json"));

        try {
            Response response = HttpClientUtil.runSyncWithPostAndBody(finalUrl, body);
            if (response.isSuccessful()) {
                // Parse the response into SheetWithSortedOrFilteredRangeDto using GSON
                sheetWithSortedRangeDto = GSON_INSTANCE.fromJson(response.body().string(), SheetWithSortedOrFilteredRangeDto.class);
            } else {
                // Handle non-200 HTTP responses
                String responseBody = response.body().string();
                throw new IllegalArgumentException(responseBody);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return sheetWithSortedRangeDto;
    }

    @FXML
    public void handleUndoSortingButton() {
        enableAllButtonsInScene();
        sheetWindowController.displayRecentSheetBeforeSortingOrFilteringCachedInTablePart();
    }

    private void clearAllTextFieldsRelatedToSorting() {
        newSortStartCoordinateTextField.clear();
        newSortEndCoordinateTextField.clear();
        firstColumnLetterToSortByTextField.clear();
        secondColumnLetterToSortByTextField.clear();
        thirdColumnLetterToSortByTextField.clear();
        fourthColumnLetterToSortByTextField.clear();
        fifthColumnLetterToSortByTextField.clear();
    }

    private List<String> fillArrayListWithColumnLettersToSortBy(boolean firstColumnTextFieldEmpty, boolean secondColumnTextFieldEmpty, boolean thirdColumnTextFieldEmpty, boolean fourthColumnTextFieldEmpty, boolean fifthColumnTextFieldEmpty) {
        List<String> columnLettersToSortBy = new ArrayList<>();
        String stringLetterOfFirstColumnToSortBy = firstColumnLetterToSortByTextField.getText().trim().toUpperCase();
        columnLettersToSortBy.add(stringLetterOfFirstColumnToSortBy);

        int numOfLastTextFieldWhichIsFilled = getNumOfLastTextFieldWhichIsFilled(firstColumnTextFieldEmpty, secondColumnTextFieldEmpty, thirdColumnTextFieldEmpty, fourthColumnTextFieldEmpty, fifthColumnTextFieldEmpty);

        String stringLetterOfSecondColumnToSortBy = secondColumnLetterToSortByTextField.getText().trim().toUpperCase();
        String stringLetterOfThirdColumnToSortBy = thirdColumnLetterToSortByTextField.getText().trim().toUpperCase();
        String stringLetterOfFourthColumnToSortBy = fourthColumnLetterToSortByTextField.getText().trim().toUpperCase();
        String stringLetterOfFifthColumnToSortBy = fifthColumnLetterToSortByTextField.getText().trim().toUpperCase();

        boolean thereIsColumnLetterThatAppearsMoreThanOneTime;
        if (numOfLastTextFieldWhichIsFilled >= 2) {
            thereIsColumnLetterThatAppearsMoreThanOneTime = stringLetterOfFirstColumnToSortBy.equals(stringLetterOfSecondColumnToSortBy);
            if (thereIsColumnLetterThatAppearsMoreThanOneTime) {
                throw new IllegalArgumentException("Column letter '" + stringLetterOfFirstColumnToSortBy + "' appears more than one time - please enter different column letters to sort by.");
            }
        }
        if (numOfLastTextFieldWhichIsFilled >= 3) {
            thereIsColumnLetterThatAppearsMoreThanOneTime = stringLetterOfFirstColumnToSortBy.equals(stringLetterOfThirdColumnToSortBy)
                    || stringLetterOfSecondColumnToSortBy.equals(stringLetterOfThirdColumnToSortBy);
            if (thereIsColumnLetterThatAppearsMoreThanOneTime) {
                throw new IllegalArgumentException("Column letter '" + stringLetterOfThirdColumnToSortBy + "' appears more than one time - please enter different column letters to sort by.");
            }
        }
        if (numOfLastTextFieldWhichIsFilled >= 4) {
            thereIsColumnLetterThatAppearsMoreThanOneTime = stringLetterOfFirstColumnToSortBy.equals(stringLetterOfFourthColumnToSortBy)
                    || stringLetterOfSecondColumnToSortBy.equals(stringLetterOfFourthColumnToSortBy)
                    || stringLetterOfThirdColumnToSortBy.equals(stringLetterOfFourthColumnToSortBy);
            if (thereIsColumnLetterThatAppearsMoreThanOneTime) {
                throw new IllegalArgumentException("Column letter '" + stringLetterOfFourthColumnToSortBy + "' appears more than one time - please enter different column letters to sort by.");
            }
        }
        if (numOfLastTextFieldWhichIsFilled == 5) {
            thereIsColumnLetterThatAppearsMoreThanOneTime = stringLetterOfFirstColumnToSortBy.equals(stringLetterOfFifthColumnToSortBy)
                    || stringLetterOfSecondColumnToSortBy.equals(stringLetterOfFifthColumnToSortBy)
                    || stringLetterOfThirdColumnToSortBy.equals(stringLetterOfFifthColumnToSortBy)
                    || stringLetterOfFourthColumnToSortBy.equals(stringLetterOfFifthColumnToSortBy);
            if (thereIsColumnLetterThatAppearsMoreThanOneTime) {
                throw new IllegalArgumentException("Column letter '" + stringLetterOfFifthColumnToSortBy + "' appears more than one time - please enter different column letters to sort by.");
            }
        }

        //add letters to sort by to the list, according to the number of filled text fields
        if (numOfLastTextFieldWhichIsFilled >= 2) {
            columnLettersToSortBy.add(stringLetterOfSecondColumnToSortBy);
        }
        if (numOfLastTextFieldWhichIsFilled >= 3) {
            columnLettersToSortBy.add(stringLetterOfThirdColumnToSortBy);
        }
        if (numOfLastTextFieldWhichIsFilled >= 4) {
            columnLettersToSortBy.add(stringLetterOfFourthColumnToSortBy);
        }
        if (numOfLastTextFieldWhichIsFilled == 5) {
            columnLettersToSortBy.add(stringLetterOfFifthColumnToSortBy);
        }

        return columnLettersToSortBy;
    }

    private int getNumOfLastTextFieldWhichIsFilled(boolean firstColumnTextFieldEmpty, boolean secondColumnTextFieldEmpty, boolean thirdColumnTextFieldEmpty, boolean fourthColumnTextFieldEmpty, boolean fifthColumnTextFieldEmpty) {
        int numOfLastTextFieldWhichIsFilled = 0;
        if (!fifthColumnTextFieldEmpty) {
            numOfLastTextFieldWhichIsFilled = 5;
        } else if (!fourthColumnTextFieldEmpty) {
            numOfLastTextFieldWhichIsFilled = 4;
        } else if (!thirdColumnTextFieldEmpty) {
            numOfLastTextFieldWhichIsFilled = 3;
        } else if (!secondColumnTextFieldEmpty) {
            numOfLastTextFieldWhichIsFilled = 2;
        } else if (!firstColumnTextFieldEmpty) {
            numOfLastTextFieldWhichIsFilled = 1;
        }
        return numOfLastTextFieldWhichIsFilled;
    }

    private boolean isCombinationOfEmptyAndFilledTextFieldsValidForSorting
            (boolean firstColumnTextFieldEmpty, boolean secondColumnTextFieldEmpty,
             boolean thirdColumnTextFieldEmpty, boolean fourthColumnTextFieldEmpty, boolean fifthColumnTextFieldEmpty) {
        boolean combinationOfEmptyAndFilledTextFieldsValid = false;
        boolean fourthToFifthColumnTextFieldEmpty = fourthColumnTextFieldEmpty && fifthColumnTextFieldEmpty;
        boolean thirdToFifthColumnTextFieldEmpty = thirdColumnTextFieldEmpty && fourthToFifthColumnTextFieldEmpty;
        boolean secondToFifthColumnTextFieldEmpty = secondColumnTextFieldEmpty && thirdToFifthColumnTextFieldEmpty;

        boolean firstAndSecondColumnAreFilled = !firstColumnTextFieldEmpty && !secondColumnTextFieldEmpty;
        boolean firstToThirdColumnAreFilled = firstAndSecondColumnAreFilled && !thirdColumnTextFieldEmpty;
        boolean firstToFourthColumnAreFilled = firstToThirdColumnAreFilled && !fourthColumnTextFieldEmpty;
        boolean allColumnTextFieldsFilled = firstToFourthColumnAreFilled && !fifthColumnTextFieldEmpty;

        boolean firstColumnTextFieldFilled = !firstColumnTextFieldEmpty;
        boolean secondColumnTextFieldFilled = !secondColumnTextFieldEmpty;
        boolean thirdColumnTextFieldFilled = !thirdColumnTextFieldEmpty;
        boolean fourthColumnTextFieldFilled = !fourthColumnTextFieldEmpty;
        boolean fifthColumnTextFieldFilled = !fifthColumnTextFieldEmpty;

        boolean oneOfColumnsFromFourthToFifthAreFilled = fourthColumnTextFieldFilled || fifthColumnTextFieldFilled;
        boolean oneOfColumnsFromThirdToFifthAreFilled = thirdColumnTextFieldFilled || oneOfColumnsFromFourthToFifthAreFilled;
        boolean oneOfColumnsFromSecondToFifthAreFilled = secondColumnTextFieldFilled || oneOfColumnsFromThirdToFifthAreFilled;

        boolean allColumnTextFieldsEmpty = firstColumnTextFieldEmpty && secondToFifthColumnTextFieldEmpty;
        boolean firstEmptyAndOneOfColumnsFromSecondToFifthAreFilled = firstColumnTextFieldEmpty && oneOfColumnsFromSecondToFifthAreFilled;
        boolean firstFilledSecondEmptyAndOneOfColumnsFromThirdToFifthAreFilled = firstColumnTextFieldFilled && secondColumnTextFieldEmpty && oneOfColumnsFromThirdToFifthAreFilled;
        boolean firstAndSecondFilledThirdEmptyAndOneOfColumnsFromFourthToFifthAreFilled = firstAndSecondColumnAreFilled && thirdColumnTextFieldEmpty && oneOfColumnsFromFourthToFifthAreFilled;
        boolean firstToThirdFilledFourthEmptyAndFifthFilled = firstToThirdColumnAreFilled && fourthColumnTextFieldEmpty && fifthColumnTextFieldFilled;

        String enterInConsecutiveOrderMessage = "Combination of filled and empty text fields is not valid for sorting - please enter only letters in consecutive order.";

        if (allColumnTextFieldsEmpty) {
            throw new IllegalArgumentException("Please enter at least one column letter to sort by - in the 1st column text field.");
        }
        if (firstEmptyAndOneOfColumnsFromSecondToFifthAreFilled
            || firstFilledSecondEmptyAndOneOfColumnsFromThirdToFifthAreFilled
            || firstAndSecondFilledThirdEmptyAndOneOfColumnsFromFourthToFifthAreFilled
            || firstToThirdFilledFourthEmptyAndFifthFilled) {
            throw new IllegalArgumentException(enterInConsecutiveOrderMessage);
        }

        //will get here if (firstColumnTextFieldFilled && secondToFifthColumnTextFieldEmpty)
        //                || (firstAndSecondColumnAreFilled && thirdToFifthColumnTextFieldEmpty)
        //                || (firstToThirdColumnAreFilled && fourthToFifthColumnTextFieldEmpty)
        //                || (firstToFourthColumnAreFilled && fifthColumnTextFieldEmpty)
        //                || allColumnTextFieldsFilled;
        combinationOfEmptyAndFilledTextFieldsValid = true;
        return combinationOfEmptyAndFilledTextFieldsValid;
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

        sheetWindowController.enableReturnToMainWindowButton();
    }

    public void enableAllButtonsInScene() {
        Scene scene = undoFilteringButton.getScene();
        if (scene != null) {
            for (Node node : scene.getRoot().lookupAll(".button")) {
                if (node instanceof Button) {
                    Button button = (Button) node;
                    button.setDisable(false);
                }
            }
        }
    }

    public void updateCurrentSheetNameAndDataForRefresherInLeftPart(String sheetName) {
        currentSheetName = sheetName;
        rangesNamesRefresher.onSheetSelected(() -> currentSheetName, currentRangesNamesInClient);
    }

    public void disableEditingButtonsInLeftPart() {
        deleteSelectedRangeButton.setDisable(true);
        addNewRangeButton.setDisable(true);
    }

    public void resumeRangesNamesRefresher() {
        rangesNamesRefresher.resume();
    }

    public void close() {
        rangesNamesRefresher.stop();
    }
}