package engine.api;

import dto.cell.CellDto;
import dto.management.info.SheetBasicInfoDto;
import dto.permission.PermissionRequestDto;
import dto.range.RangeDto;
import dto.sheet.SheetWithSortedOrFilteredRangeDto;
import dto.sheet.SheetDto;

import java.io.File;
import java.util.List;

public interface EngineManagerForServer {
    SheetBasicInfoDto tryToExtractSheetFromFileAndReturnBasicInfo(File file, String ownerUsername) throws Exception;

    List<PermissionRequestDto> getPendingAndDecidedPermissionRequestsDtoList(String sheetName);

    void addNewPendingPermissionRequest(String sheetName, String username, String permissionLevelRequested);

    void approveSelectedPermissionRequest(String sheetName, String username, String permissionLevelRequested, int numOfRequestForSheet);

    void rejectSelectedPermissionRequest(String sheetName, String username, String permissionLevelRequested, int numOfRequestForSheet);

    SheetDto getLastVersionSheetDto(String sheetName);
    CellDto getCellDtoFromRecentSelectedSheet(String sheetName, int rowNum, int columnNum);
    RangeDto getRangeDtoFromSelectedSheet(String selectedSheetName, String rangeName);
    List<String> getAllRangeNamesForSelectedSheet(String sheetName);
    SheetDto updateCellValueInSelectedSheet(String sheetName, int rowNumber, int columnNumber,
                                            String newOriginalValueStr, String username, int currentVersionNumInUpdatingClient);
    int getVersionNumOfRecentSelectedSheet(String selectedSheetName);
    SheetDto getSheetOfSpecificVersion(String selectedSheetName, int versionNum);
    void deleteRangeFromSelectedSheet(String selectedSheetName, String rangeName);
    boolean isSelectedRangeUsedInAnyCellWithRelevantFunction(String selectedSheetName, String rangeName);
    void addNewRangeToSelectedSheet(String selectedSheetName, String rangeName, String leftTopStartCoordinateStr, String rightBottomEndCoordinateStr);

    SheetWithSortedOrFilteredRangeDto getSheetDtoWithSortedArea(String selectedSheetName,
                                                                String newSortStartCoordinateStr,
                                                                String newSortEndCoordinateStr,
                                                                List<String> allColumnLettersToSortBy);

    List<String> getUniqueValuesForFilteringInSelectedColumnAndRelevantArea(String selectedSheetName, String filterAreaStartCoordinateStr, String filterAreaEndCoordinateStr, String stringWithLetterOfColumnToGetUniqueValuesToFilter);

    SheetWithSortedOrFilteredRangeDto getSheetDtoWithFilteredArea(String selectedSheetName, String filterAreaStartCoordinateStr, String filterAreaEndCoordinateStr, char columnLetterForFiltering, List<String> uniqueValuesInSelectedColumn);

    List<SheetBasicInfoDto> getDataOfAllSheetsInSystem(String username);

    int getNumOfPendingPermissionRequestsForSelectedSheet(String sheetName);

    boolean wasThereAChangeInSelectedUserPermissions(String username);

    String getPermissionLevelForSelectedUserAndSheet(String selectedSheetName, String userName);

    List<String> getAllRangesNamesFromSelectedSheet(String selectedSheetName);

    SheetDto getTempSheetForDynamicAnalysis(String selectedSheetName, int rowNumber, int columnNumber, String newOriginalValueStr, String username);

}
