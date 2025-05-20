package engine.impl;

import dto.management.info.SheetBasicInfoDto;
import engine.api.SingleSheetManager;
import engine.dto.factory.SheetBasicInfoDtoFactory;
import engine.permissions.PermissionLevel;
import engine.permissions.PermissionRequest;
import engine.permissions.PermissionRequestStatus;
import shticell.cell.api.Cell;
import shticell.coordinate.Coordinate;
import shticell.coordinate.CoordinateFactory;
import shticell.range.Range;
import shticell.range.RangesManager;
import shticell.range.RangeImpl;
import shticell.row.RangeWithRowsInArea;
import shticell.sheet.api.Sheet;
import shticell.sorter.RangeSorter;

import java.util.*;

public class SingleSheetManagerImpl implements SingleSheetManager {
    private List<Sheet> sheetVersionsArray;
    private String sheetName;
    private String ownerUsername;
    private final Map<String, PermissionLevel> mapUsernameToPermissionLevel = new HashMap<>();
    private final List<PermissionRequest> pendingPermissionRequestsList = new ArrayList<>();
    private final List<PermissionRequest> decidedPermissionRequestsList = new ArrayList<>();
    private int numOfTotalPermissionRequests;
    private Map<String, Boolean> mapUsernameToBooleanWasThereAChangeInApprovedPermissions = new HashMap<>();

    public SingleSheetManagerImpl() {
        this.sheetVersionsArray = new ArrayList<>();
    }

    @Override
    public SheetBasicInfoDto addNewSheetToNewManagerAndReturnBasicInfo(Sheet newSheet, String ownerUsername) throws Exception {
        this.sheetVersionsArray.add(newSheet);
        this.sheetName = newSheet.getNameOfSheet();
        this.ownerUsername = ownerUsername;
        this.numOfTotalPermissionRequests = 1;
        PermissionRequest ownerPermissionRequest = new PermissionRequest(sheetName, ownerUsername, PermissionLevel.OWNER, numOfTotalPermissionRequests);
        ownerPermissionRequest.approve();
        mapUsernameToBooleanWasThereAChangeInApprovedPermissions.put(ownerUsername, true);
        decidedPermissionRequestsList.add(ownerPermissionRequest);
        mapUsernameToPermissionLevel.put(ownerUsername, PermissionLevel.OWNER);
        int numOfTotalPermissionRequests = decidedPermissionRequestsList.size() + pendingPermissionRequestsList.size();
        int numOfPendingPermissionRequests = pendingPermissionRequestsList.size();

        RangesManager rangesManager = newSheet.getRangesManager();
        List<String> rangeNamesInUploadedSheet = rangesManager.getAllRangesNames();
        return SheetBasicInfoDtoFactory.createSheetBasicInfoDto(sheetName, ownerUsername,
                PermissionLevel.OWNER.getPermissionLevel(),
                newSheet.getNumOfRows(), newSheet.getNumOfColumns(),
                numOfTotalPermissionRequests, numOfPendingPermissionRequests,
                rangeNamesInUploadedSheet);
    }

    @Override
    public List<PermissionRequest> getPendingAndDecidedPermissionRequestsList() {
        List<PermissionRequest> allPermissionRequests = new ArrayList<>();
        allPermissionRequests.addAll(pendingPermissionRequestsList);
        allPermissionRequests.addAll(decidedPermissionRequestsList);

        return allPermissionRequests;
    }

    @Override
    public int getNumOfPendingPermissionRequests() {
        return pendingPermissionRequestsList.size();
    }

    @Override
    public boolean wasThereAChangeInSelectedUserPermissions(String username) {

       if (mapUsernameToBooleanWasThereAChangeInApprovedPermissions.containsKey(username)) {
           if (mapUsernameToBooleanWasThereAChangeInApprovedPermissions.get(username)) {
               mapUsernameToBooleanWasThereAChangeInApprovedPermissions.put(username, false);
               return true;
           }
           else {
               return false;
           }
        }
        else {
            return false;
        }
    }

    @Override
    public String getPermissionLevelForSelectedUser(String userName) {
        return mapUsernameToPermissionLevel.get(userName).getPermissionLevel();
    }

    @Override
    public Sheet getTempSheetForDynamicAnalysis(int rowNumber, int columnNumber, String newOriginalValueStr, String username) {
        Sheet copyOfRecentSheet = sheetVersionsArray.getLast().createCopyOfSheet();
        Sheet tempSheetForDynamicAnalysis = copyOfRecentSheet.updateCellValueAndCalculate(rowNumber, columnNumber, newOriginalValueStr, false, username);

        return tempSheetForDynamicAnalysis;
    }

    @Override
    public void addNewPendingPermissionRequest(String username, PermissionLevel requestedPermissionLevel) {
        this.numOfTotalPermissionRequests++;
        PermissionRequest newPermissionRequest = new PermissionRequest(sheetName, username, requestedPermissionLevel, numOfTotalPermissionRequests);
        pendingPermissionRequestsList.add(newPermissionRequest);
    }

    @Override
    public void approvePermissionRequest(String userName, PermissionLevel permissionLevelRequested, int numOfRequestForSheet) {
        PermissionRequest permissionRequestToApprove = pendingPermissionRequestsList.stream()
                .filter(permissionRequest -> permissionRequest.getUsername().equals(userName))
                .filter(permissionRequest -> permissionRequest.getPermissionLevel().equals(permissionLevelRequested))
                .filter(permissionRequest -> permissionRequest.getNumOfRequestForSheet() == numOfRequestForSheet)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException
                                        ("No permission request found for user " + userName +
                                        " with requested permission level " + permissionLevelRequested));

        if (permissionRequestToApprove.getRequestStatus().equals(PermissionRequestStatus.APPROVED)) {
            throw new IllegalArgumentException("Permission request for " + userName +
                    " with requested permission level " + permissionLevelRequested +
                    " and request number " + numOfRequestForSheet + " was already approved.");
        }

        permissionRequestToApprove.approve();
        mapUsernameToPermissionLevel.put(userName, permissionLevelRequested);
        decidedPermissionRequestsList.add(permissionRequestToApprove);
        pendingPermissionRequestsList.remove(permissionRequestToApprove);
        mapUsernameToBooleanWasThereAChangeInApprovedPermissions.put(userName, true);
    }

    @Override
    public void rejectPermissionRequest(String userName, PermissionLevel permissionLevelRequested, int numOfRequestForSheet) {

        PermissionRequest permissionRequestToReject = pendingPermissionRequestsList.stream()
                .filter(permissionRequest -> permissionRequest.getUsername().equals(userName))
                .filter(permissionRequest -> permissionRequest.getPermissionLevel().equals(permissionLevelRequested))
                .filter(permissionRequest -> permissionRequest.getNumOfRequestForSheet() == numOfRequestForSheet)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException
                        ("No permission request found for user " + userName +
                                " with requested permission level " + permissionLevelRequested));


        if (permissionRequestToReject.getRequestStatus().equals(PermissionRequestStatus.REJECTED)) {
            throw new IllegalArgumentException("Permission request for " + userName +
                    " with requested permission level " + permissionLevelRequested +
                    " and request number " + numOfRequestForSheet + " was already rejected.");
        }
        permissionRequestToReject.reject();
        decidedPermissionRequestsList.add(permissionRequestToReject);
        pendingPermissionRequestsList.remove(permissionRequestToReject);
    }

    @Override
    public SheetBasicInfoDto getSheetBasicInfoDtoForSelectedUserWithItsPermissionLevel(String username) {
        PermissionLevel currentUserPermissionLevel = mapUsernameToPermissionLevel.get(username);
        int numOfTotalPermissionRequests = decidedPermissionRequestsList.size() + pendingPermissionRequestsList.size();
        int numOfPendingPermissionRequests = pendingPermissionRequestsList.size();

        return  SheetBasicInfoDtoFactory.createSheetBasicInfoDto
                (sheetName, ownerUsername, currentUserPermissionLevel.getPermissionLevel(),
                sheetVersionsArray.getLast().getNumOfRows(), sheetVersionsArray.getLast().getNumOfColumns(),
                numOfTotalPermissionRequests, numOfPendingPermissionRequests,
                getAllRangeNamesRelatedToThisSheet());
    }

    @Override
    public void trySetPermissionLevelForSelectedUser(String username) {
        if (!mapUsernameToPermissionLevel.containsKey(username)) {
            PermissionLevel currentUserPermissionLevel = PermissionLevel.NONE;
            mapUsernameToPermissionLevel.put(username, currentUserPermissionLevel);
        }
    }

    @Override
    public String getSheetName() {
        return sheetName;
    }

    @Override
    public Sheet getMostRecentSheet() throws NoSuchElementException {
        if (!sheetVersionsArray.isEmpty()) {
            return sheetVersionsArray.getLast();
        }
        else {
            throw new NoSuchElementException("There is no sheet in the system.");
        }
    }

    @Override
    public Cell getCellFromMostRecentSheet(int row, int column) {
        return sheetVersionsArray.getLast().getCell(row, column); //is it the expected behavior?
    }

    @Override
    public Sheet updateValueOfCellAndGetNewSheet(int row, int col, String value, String nameOfUserWhoCausedUpdateOfValue, int currentVersionNumInUpdatingClient) throws RuntimeException {
        Sheet possibleNewSheet;
        boolean isUpdatePartOfSheetInitialization = sheetVersionsArray.isEmpty();
        try {
            if (!sheetVersionsArray.isEmpty() && currentVersionNumInUpdatingClient != sheetVersionsArray.size()) {
                throw new IllegalArgumentException
                        ("Cell update is possible only on recent sheet version. Please refresh the sheet and try again.");
            }
            Sheet latestSheet = sheetVersionsArray.getLast();
            possibleNewSheet = latestSheet.updateCellValueAndCalculate(row, col, value, isUpdatePartOfSheetInitialization,nameOfUserWhoCausedUpdateOfValue);
            if (possibleNewSheet != sheetVersionsArray.getLast()) { //the sheet has changed
                sheetVersionsArray.add(possibleNewSheet); //add the new sheet to the list
            }

            return sheetVersionsArray.getLast();
        }
        catch (Exception e) { //should we catch a more specific Exception? could there be a few types?
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @param version should be a number of requested version, starting from 1 (not 0)
     */
    @Override
    public Sheet getSheetOfSpecificVersion(int version) throws NoSuchElementException, IndexOutOfBoundsException {
        try {
            if (!sheetVersionsArray.isEmpty()) {
                return sheetVersionsArray.get(version - 1);
            }
            else {
                throw new NoSuchElementException("There are no sheets in the system.");
            }
        }
        catch(IndexOutOfBoundsException e){
            throw new IndexOutOfBoundsException("The requested version is not available.");
        }
    }

    /**
     * Returns a counter that starts from 1 (not 0).
     *
     * @return the number of the latest version, starting from 1
     */
    @Override
    public int getLatestVersionNumber() throws NoSuchElementException {
        if (!sheetVersionsArray.isEmpty()) {
            return sheetVersionsArray.size();
        }
        else {
            throw new NoSuchElementException("There is no sheet loaded to the system.");
        }
    }

    @Override
    public boolean isThereASheetLoadedToTheSystem() {  //returns true if there is a sheet loaded to the system
        if (sheetVersionsArray.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Range addRangeToMostRecentSheet(String rangeName, String leftTopStartCoordinateStr, String rightBottomEndCoordinateStr) {
        RangesManager rangesManager = sheetVersionsArray.getLast().getRangesManager();
        return rangesManager.createRangeFromTwoCoordinateStringsAndNameString(sheetVersionsArray.getLast(), rangeName, leftTopStartCoordinateStr, rightBottomEndCoordinateStr);
    }

    @Override
    public Range getRangeFromMostRecentSheet(String rangeName) {
        RangesManager rangesManager = sheetVersionsArray.getLast().getRangesManager();
        return rangesManager.getRangeByItsName(rangeName);
    }

    @Override
    public boolean isSelectedRangeUsedInAnyCellWithRelevantFunction(String rangeName) {
        return sheetVersionsArray.getLast().isSelectedRangeIsUsedInSheet(rangeName);
    }

    @Override
    public boolean isFilteringOrSortingAreaValid(String newFilterStartCoordinateStr, String newFilterEndCoordinateStr) {
        Sheet currentSheet = sheetVersionsArray.getLast();
        Coordinate topLeftStartCoordinate, bottomRightEndCoordinate;
        RangesManager rangesManager = currentSheet.getRangesManager();
        try {
            topLeftStartCoordinate = CoordinateFactory.getCoordinateFromStr(newFilterStartCoordinateStr, currentSheet);
            bottomRightEndCoordinate = CoordinateFactory.getCoordinateFromStr(newFilterEndCoordinateStr, currentSheet);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error trying to create filtering area from coordinates provided: " + e.getMessage());
        }
        return rangesManager.isCoordinatesCreateValidRange(topLeftStartCoordinate, bottomRightEndCoordinate);
    }

    @Override
    public Range createRangeToSortOrFilter(String newFilterStartCoordinateStr, String newFilterEndCoordinateStr) {
        // method will be invoked only if the area and coordinates are valid
        Sheet currentSheet = sheetVersionsArray.getLast();
        Coordinate topLeftStartCoordinate = CoordinateFactory.getCoordinateFromStr(newFilterStartCoordinateStr, currentSheet);
        Coordinate bottomRightEndCoordinate = CoordinateFactory.getCoordinateFromStr(newFilterEndCoordinateStr, currentSheet);

        return new RangeImpl("CurrentRangeToFilterOrSort", topLeftStartCoordinate, bottomRightEndCoordinate);
    }

    @Override
    public Sheet createCopyOfRecentSheet() {
        return sheetVersionsArray.getLast().createCopyOfSheet();
    }

    @Override
    public RangeWithRowsInArea createFilteredRangeArea(Range rangeToFilter, char letterOfColumnToGetUniqueValuesToFilter, List<String> uniqueValuesToFilter) {
        try {
            if (rangeToFilter == null) {
                throw new IllegalArgumentException("Filtering range is null.");
            }
            if (uniqueValuesToFilter == null) {
                throw new IllegalArgumentException("List of unique values to filter is null.");
            }
            if (uniqueValuesToFilter.isEmpty()) {
                throw new IllegalArgumentException("List of unique values to filter is empty.");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        try {
            Sheet copyOfRecentSheetWithAreaToFilter = sheetVersionsArray.getLast().createCopyOfSheet();
            RangeWithRowsInArea rangeWithRowsInArea = new RangeWithRowsInArea(copyOfRecentSheetWithAreaToFilter, rangeToFilter);
            rangeWithRowsInArea.buildRangeFromAllRelevantCellsInRange();
            int columnNumFromChar = CoordinateFactory.getColumnNumberFromChar(letterOfColumnToGetUniqueValuesToFilter, copyOfRecentSheetWithAreaToFilter);
            int numOfIterationsLeftToPreform = rangeToFilter.getRowEnd() - rangeToFilter.getRowStart() + 1;
            int currentRowToCheck = rangeWithRowsInArea.getFirstRowNumToCheck();
            int lastRowToCheck = rangeWithRowsInArea.getLastRowNumToCheck();
            int numOfRowsToCheck = rangeWithRowsInArea.getCurrentNumOfRows();
            int numOfRowsAlreadyChecked = 0;


            while (currentRowToCheck <= lastRowToCheck && numOfRowsAlreadyChecked < numOfRowsToCheck) {
                String currentEffectiveValueOfCellAsString = rangeWithRowsInArea.getEffectiveValueOfCellAsString(currentRowToCheck, columnNumFromChar);
                if (!uniqueValuesToFilter.contains(currentEffectiveValueOfCellAsString)) {
                    rangeWithRowsInArea.removeCurrentRowAndMoveAllRowsOneRowUp(currentRowToCheck);
                    currentRowToCheck--; //to check the next row that moved up
                }
                currentRowToCheck++; //even if the row was removed, the next row moved up ?
                numOfRowsAlreadyChecked++;
            }

            return rangeWithRowsInArea;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Error trying to create a copy of the sheet with filtered area: " + e.getMessage());
        }
    }

    @Override
    public RangeWithRowsInArea createSortedRangeArea(Range newSortingRange, List<Character> listOfColumnLettersCharactersToSortBy) {
        Sheet copyOfRecentSheetWithAreaToSort = sheetVersionsArray.getLast().createCopyOfSheet();
        RangeSorter rangeToSort = new RangeSorter(copyOfRecentSheetWithAreaToSort, newSortingRange,listOfColumnLettersCharactersToSortBy);
        rangeToSort.sortRange();

        return rangeToSort.getSortedRangeWithRows();
    }

    @Override
    public boolean isColumnLetterInFilteringOrSortingArea(String stringWithLetterOfSelectedColumn, String newAreaStartCoordinateStr, String newAreaEndCoordinateStr) {
        Sheet currentSheet = sheetVersionsArray.getLast();
        int columnNumFromChar;

        //getting here only if isFilteringOrSortingAreaValid() returned true,
        //so the coordinates are valid and will not throw an exception
        Coordinate topLeftStartCoordinate = CoordinateFactory.getCoordinateFromStr(newAreaStartCoordinateStr, currentSheet);
        Coordinate bottomRightEndCoordinate = CoordinateFactory.getCoordinateFromStr(newAreaEndCoordinateStr, currentSheet);

        if (stringWithLetterOfSelectedColumn.length() != 1) {
            throw new IllegalArgumentException("Column letter must be exactly one character long.");
        }
        try {
            columnNumFromChar = CoordinateFactory.getColumnNumberFromChar(stringWithLetterOfSelectedColumn.charAt(0), currentSheet);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error trying to reach requested column " + stringWithLetterOfSelectedColumn +
                    " in selected filtering area" + e.getMessage());
        }

        return columnNumFromChar >= topLeftStartCoordinate.getColumn() && columnNumFromChar <= bottomRightEndCoordinate.getColumn();
    }

    @Override
    public List<String> getUniqueValuesForFilteringInSelectedColumnAndRelevantArea
            (char charLetterOfColumnToGetUniqueValuesToFilter,
             String newFilterStartCoordinateStr,
             String newFilterEndCoordinateStr) {
        return sheetVersionsArray.getLast().getUniqueValuesForFilteringInSelectedColumnAndRelevantArea(charLetterOfColumnToGetUniqueValuesToFilter, newFilterStartCoordinateStr, newFilterEndCoordinateStr);
    }

    @Override
    public void deleteRangeRelatedToThisSheet(String rangeName) {
        try {
            RangesManager rangesManager = sheetVersionsArray.getLast().getRangesManager();
            rangesManager.deleteRangeFromRangesManager(rangeName);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public List<String> getAllRangeNamesRelatedToThisSheet() {
        RangesManager rangesManager = sheetVersionsArray.getLast().getRangesManager();
        return rangesManager.getAllRangesNames();
    }

    @Override
    public Range getRangeByItsName(String rangeName) {
        RangesManager rangesManager = sheetVersionsArray.getLast().getRangesManager();
        return rangesManager.getRangeByItsName(rangeName);
    }

    @Override
    public boolean isThereAnyRangeInRangesFactory() {
        RangesManager rangesManager = sheetVersionsArray.getLast().getRangesManager();
        return rangesManager.isThereAnyRangeInRangesManager();
    }
}