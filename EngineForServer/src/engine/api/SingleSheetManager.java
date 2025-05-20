package engine.api;

import dto.management.info.SheetBasicInfoDto;
import engine.permissions.PermissionLevel;
import engine.permissions.PermissionRequest;
import shticell.cell.api.Cell;
import shticell.range.Range;
import shticell.row.RangeWithRowsInArea;
import shticell.sheet.api.Sheet;

import java.util.List;

public interface SingleSheetManager {
    SheetBasicInfoDto addNewSheetToNewManagerAndReturnBasicInfo(Sheet newSheet, String ownerUsername) throws Exception;
    Sheet getMostRecentSheet();
    String getSheetName();
    /**
     * Returns a cell from the most recent sheet.
     * if the cell is not present in activeCells in the sheet, it will be created as a new empty cell
     * @param row    the row of the cell
     * @param col the column of the cell
     * @return the cell from the most recent sheet
     */
    Cell getCellFromMostRecentSheet(int row, int col);
    Sheet updateValueOfCellAndGetNewSheet(int row, int col, String value,
                                          String nameOfUserWhoCausedUpdateOfValue,
                                          int currentVersionNumInUpdatingClient);

    /**
     * @param version should be a number of requested version, starting from 1 (not 0)
     */
    Sheet getSheetOfSpecificVersion(int version);
    int getLatestVersionNumber();
    boolean isThereASheetLoadedToTheSystem();

    Range addRangeToMostRecentSheet(String rangeName, String leftTopStartCoordinateStr, String rightBottomEndCoordinateStr);
    Range getRangeFromMostRecentSheet(String rangeName);
    void deleteRangeRelatedToThisSheet(String rangeName);
    List<String> getAllRangeNamesRelatedToThisSheet();
    boolean isThereAnyRangeInRangesFactory();
    Range getRangeByItsName(String rangeName);
    boolean isSelectedRangeUsedInAnyCellWithRelevantFunction(String rangeName);
    boolean isFilteringOrSortingAreaValid(String newFilterStartCoordinateStr, String newFilterEndCoordinateStr);

    boolean isColumnLetterInFilteringOrSortingArea
            (String stringWithLetterOfSelectedColumn, String newAreaStartCoordinateStr, String newAreaEndCoordinateStr);

    List<String> getUniqueValuesForFilteringInSelectedColumnAndRelevantArea
            (char charLetterOfColumnToGetUniqueValuesToFilter, String newFilterStartCoordinateStr, String newFilterEndCoordinateStr);

    /**
     * method will be invoked only if the area and coordinates are valid
     */
    Range createRangeToSortOrFilter(String newFilterStartCoordinateStr, String newFilterEndCoordinateStr);

    Sheet createCopyOfRecentSheet();

    RangeWithRowsInArea createFilteredRangeArea
            (Range filteringRange, char letterOfColumnToGetUniqueValuesToFilter, List<String> uniqueValuesToFilter);

    RangeWithRowsInArea createSortedRangeArea(Range newSortingRange, List<Character> listOfColumnLettersCharactersToSortBy);

    void addNewPendingPermissionRequest(String username, PermissionLevel requestedPermissionLevel);

    void approvePermissionRequest(String userName, PermissionLevel permissionLevelRequested, int numOfRequestForSheet);

    void rejectPermissionRequest(String userName, PermissionLevel permissionLevelRequested, int numOfRequestForSheet);

    SheetBasicInfoDto getSheetBasicInfoDtoForSelectedUserWithItsPermissionLevel(String username);

    void trySetPermissionLevelForSelectedUser(String username);

    List<PermissionRequest> getPendingAndDecidedPermissionRequestsList();

    int getNumOfPendingPermissionRequests();

    boolean wasThereAChangeInSelectedUserPermissions(String username);

    String getPermissionLevelForSelectedUser(String userName);

    Sheet getTempSheetForDynamicAnalysis(int rowNumber, int columnNumber, String newOriginalValueStr, String username);
}
