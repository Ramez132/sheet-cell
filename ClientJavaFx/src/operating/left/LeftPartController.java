package operating.left;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.util.Callback;
import operating.window.SheetWindowController;
import shticell.range.Range;
import shticell.sheet.api.Sheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeftPartController {
    private SheetWindowController sheetWindowController;
    private Range currentFilteringRange;
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


    @FXML
    public void initialize() {
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
            String newFilterStartCoordinateStr = newFilterStartCoordinateTextField.getText().trim();
            String newFilterEndCoordinateStr = newFilterEndCoordinateTextField.getText().trim();
            String stringWithLetterOfColumnToGetUniqueValuesToFilter = selectColumnLetterForFilteringTextField.getText().trim().toUpperCase();
            newFilterStartCoordinateTextField.clear();
            newFilterEndCoordinateTextField.clear();
            selectColumnLetterForFilteringTextField.clear();
            Range updatedFilteringRange;

            boolean isFilteringAreaValid = sheetWindowController.isFilteringOrSortingAreaValid(newFilterStartCoordinateStr, newFilterEndCoordinateStr);
            if (!isFilteringAreaValid) {
                sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel
                    ("The filtering area " + newFilterStartCoordinateStr + " to" + newFilterEndCoordinateStr +
                        " is not valid. Please enter valid area for filtering and column letter in this area.");
                return;
            } else {
                //method will be invoked only if the area and coordinates are valid
                updatedFilteringRange = sheetWindowController.createRangeToSortOrFilter(newFilterStartCoordinateStr, newFilterEndCoordinateStr);
            }

            boolean isColumnLetterInFilteringArea = sheetWindowController.isColumnLetterInFilteringOrSortingArea(stringWithLetterOfColumnToGetUniqueValuesToFilter, newFilterStartCoordinateStr, newFilterEndCoordinateStr);
            if (!isColumnLetterInFilteringArea) {
                sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel
                    ("The column letter is not in selected filtering area - please enter valid area for filtering and column letter in this area.");
                return;
            }

            char charLetterOfColumnToGetUniqueValuesToFilter = stringWithLetterOfColumnToGetUniqueValuesToFilter.charAt(0);

            //if got here, all fields are filled and valid
            List<String> uniqueValuesInSelectedColumn = sheetWindowController.getUniqueValuesForFilteringInSelectedColumnAndRelevantArea(charLetterOfColumnToGetUniqueValuesToFilter, newFilterStartCoordinateStr, newFilterEndCoordinateStr);
            if (uniqueValuesInSelectedColumn.isEmpty()) {
                sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel("No unique values found in selected column '" + charLetterOfColumnToGetUniqueValuesToFilter + "'.");
                return;
            }

            updateFilteringOptionsListViewWithUniqueValuesFroSelectedColumnAndArea(uniqueValuesInSelectedColumn);

            //updating for handleShowFilteredLinesButton method
            isUniqueValuesUpdatedFromRecentFilteringAreaAndColumnLetter = true;
            currentFilteringRange = updatedFilteringRange;
            currentColumnLetterForFiltering = charLetterOfColumnToGetUniqueValuesToFilter;

            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel("Please select one or more unique values to filter in column '" + stringWithLetterOfColumnToGetUniqueValuesToFilter + "'.");
        } catch (Exception e) {
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel(e.getMessage());
        }
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
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel("No unique values were selected to filter. Please select one or more unique values to filter.");
            return;
        }

        disableAllButtonsInSceneExceptOne(undoFilteringButton);
        sheetWindowController.handleShowFilteredLinesButton(currentFilteringRange, currentColumnLetterForFiltering, selectedUniqueValuesOptions);

        listViewOptionsForFiltering.clear();
        selectionMapOfFilteringListView.clear();
        listViewWithCheckBoxesContainingPossibleUniqueValuesToFilter.setItems(listViewOptionsForFiltering);

        isUniqueValuesUpdatedFromRecentFilteringAreaAndColumnLetter = false;
    }

    @FXML
    public void handleUndoFilteringButton() {
        enableAllButtonsInScene();
        sheetWindowController.displaySheetBeforeSortingOrFiltering();
    }


    @FXML
    public void handleAddNewRangeButtonAndClearRelevantTextFields() {
        String rangeName = newRangeNameTextField.getText().trim();
        String leftTopStartCoordinateStr = newRangeStartCoordinateTextField.getText().trim();
        String rightBottomEndCoordinateStr = newRangeEndCoordinateTextField.getText().trim();
        if (sheetWindowController.getMostRecentSheetFromEngine() == null) {
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel("No sheet is loaded - the system can not create a new range. Please load a sheet first.");
        } else if (rangeName.isEmpty() || leftTopStartCoordinateStr.isEmpty() || rightBottomEndCoordinateStr.isEmpty()) {
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel("One or more fields are empty - the system can not create a new range. Please fill all fields.");
        } else {
            sheetWindowController.handleCreatingNewRange(rangeName, leftTopStartCoordinateStr, rightBottomEndCoordinateStr);
            newRangeNameTextField.clear(); //will get here even if the range was not created and error caught
            newRangeStartCoordinateTextField.clear();
            newRangeEndCoordinateTextField.clear();
        }
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
        } else if (sheetWindowController.isSelectedRangeUsedInAnyCellWithRelevantFunction(selectRangeComboBox.getValue())) {
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel("The selected range is used in a cell/cells with relevant functions - can not delete it.");
            return;
        }
        try {
            String rangeName = selectRangeComboBox.getValue();
            sheetWindowController.deleteRangeFromRangeFactoryMainController(rangeName);
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel("Range '" + rangeName + "' was deleted successfully.");
            selectRangeComboBox.getItems().remove(rangeName);
        } catch (Exception e) {
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel(e.getMessage());
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
            Range range = sheetWindowController.getRangeByItsName(rangeName);
            String topLeftStartCoordinateStr = range.getTopLeftStartCoordinate().toString();
            String bottomRightEndCoordinateStr = range.getBottomRightEndCoordinate().toString();
            sheetWindowController.handleChoosingRangeAndHighlightCellsInRangeMainController(range);
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel("Cells in selected range '" + rangeName +
                    "' are now highlighted with purple border. The range is from "
                    + topLeftStartCoordinateStr + " to " + bottomRightEndCoordinateStr +".");
        } catch (Exception e) {
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel(e.getMessage());
        }
    }

    public void handleInitialRangesFromNewSheet(Sheet sheet) {
        selectRangeComboBox.getItems().clear();
        if (sheetWindowController.isThereAnyRangeInRangesFactory()) {
            selectRangeComboBox.getItems().addAll(sheetWindowController.getAllRangeNamesInTheSystem());
        }
        selectRangeComboBox.setPromptText("Select range");
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
            List<String> allColumnLettersToSortByAsString = fillArrayListWithColumnLettersToSortBy (firstColumnTextFieldEmpty, secondColumnTextFieldEmpty,
                                                             thirdColumnTextFieldEmpty, fourthColumnTextFieldEmpty, fifthColumnTextFieldEmpty);
            clearAllTextFieldsRelatedToSorting();

            Range newSortingRange;

            boolean isSortingAreaValid = sheetWindowController.isFilteringOrSortingAreaValid(newSortStartCoordinateStr, newSortEndCoordinateStr);
            if (!isSortingAreaValid) {
                sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel
                        ("The sorting area " + newSortStartCoordinateStr + " to" + newSortEndCoordinateStr +
                                " is not valid. Please enter valid area for sorting and column letters in this area.");
                return;
            } else {
                //method will be invoked only if the area and coordinates are valid
                newSortingRange = sheetWindowController.createRangeToSortOrFilter(newSortStartCoordinateStr, newSortEndCoordinateStr);
            }

            boolean areAllColumnLetterInSortingArea = checkIfAllColumnLettersInSortingArea(allColumnLettersToSortByAsString, newSortStartCoordinateStr, newSortEndCoordinateStr);
            if (!areAllColumnLetterInSortingArea) {
                sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel
                        ("One or more column letters is not in selected sorting area - please enter valid area and column letters in this area.");
                return;
            }

            List<Character> listOfColumnLettersCharactersToSortBy = convertArrayOfColumnLettersStringsToArrayOfCharacters(allColumnLettersToSortByAsString);
            //if got here, all fields are filled and valid

        disableAllButtonsInSceneExceptOne(undoSortingButton);
        sheetWindowController.handleShowSortedLinesButton(newSortingRange, listOfColumnLettersCharactersToSortBy);

        } catch (Exception e) {
            sheetWindowController.setNotificationMessageOfRecentActionOutcomeLabel(e.getMessage());
        }
    }

    @FXML
    public void handleUndoSortingButton() {
        enableAllButtonsInScene();
        sheetWindowController.displaySheetBeforeSortingOrFiltering();
    }

    private List<Character> convertArrayOfColumnLettersStringsToArrayOfCharacters(List<String> allColumnLettersToSortByAsString) {
        List<Character> allColumnLettersToSortByAsCharacters = new ArrayList<>();

        for (String currentString : allColumnLettersToSortByAsString) {
            allColumnLettersToSortByAsCharacters.add(currentString.charAt(0));
        }

        return allColumnLettersToSortByAsCharacters;
    }

    private boolean checkIfAllColumnLettersInSortingArea(List<String> allColumnLettersToSortByAsString, String newSortStartCoordinateStr, String newSortEndCoordinateStr) {
        boolean allColumnLettersInSortingArea = true;
        for (String currentColumnLetterString : allColumnLettersToSortByAsString) {
            boolean currentColumnInSortingArea = sheetWindowController.isColumnLetterInFilteringOrSortingArea(currentColumnLetterString, newSortStartCoordinateStr, newSortEndCoordinateStr);
            if (!currentColumnInSortingArea) {
                allColumnLettersInSortingArea = false;
                break;
            }
        }

        return allColumnLettersInSortingArea;
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


    public void disableAllButtonsInSceneExceptOne(Button buttonToKeepEnabled) {
        Scene scene = buttonToKeepEnabled.getScene();
        if (scene != null) {
            for (Node node : scene.getRoot().lookupAll(".button")) {
                if (node instanceof Button) {
                    Button button = (Button) node;
                    button.setDisable(button != buttonToKeepEnabled);
                }
            }
        }
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

    }



