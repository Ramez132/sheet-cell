package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.range.RangeWithEffectiveValuesDto;
import dto.sheet.SheetDto;

import java.io.Writer;

public class Constants {

    // global constants
    public final static int REFRESH_RATE = 500;

    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/SheetCellServer";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";
    public final static String UPLOAD_NEW_FILE = FULL_SERVER_PATH + "/UploadNewFile";
    public final static String GET_SHEET_AND_RANGES_NAMES_DTO = FULL_SERVER_PATH + "/GetSheetAndRangesNamesDto";
    public final static String GET_RANGE_DTO_FROM_SELECTED_SHEET = FULL_SERVER_PATH + "/GetRangeDtoFromSelectedSheet";
    public final static String UPDATE_CELL_VALUE = FULL_SERVER_PATH + "/UpdateCellValue";
    public final static String GET_VERSION_NUM_OF_RECENT_SELECTED_SHEET = FULL_SERVER_PATH + "/GetVersionNumOfRecentSelectedSheet";
    public static final String GET_SHEET_OF_SPECIFIC_VERSION = FULL_SERVER_PATH + "/GetSheetOfSpecificVersion";
    public final static String DELETE_RANGE = FULL_SERVER_PATH + "/DeleteRange";
    public static final String ADD_NEW_RANGE = FULL_SERVER_PATH + "/AddNewRange";
    public static final String GET_SHEET_WITH_SORTED_RANGE = FULL_SERVER_PATH + "/GetSheetWithSortedRange";
    public static final String GET_UNIQUE_VALUES_FOR_FILTERING = FULL_SERVER_PATH + "/GetUniqueValuesForFiltering";
    public static final String GET_SHEET_WITH_FILTERED_RANGE = FULL_SERVER_PATH + "/GetSheetWithFilteredRange";
    public static final String GET_ALL_SHEETS_IN_SYSTEM = FULL_SERVER_PATH + "/GetAllSheetsInSystem";
    public static final String GET_ALL_PERMISSION_REQUESTS_FOR_SHEET = FULL_SERVER_PATH + "/GetAllPermissionRequestsForSheet";
    public static final String ADD_PENDING_PERMISSION_REQUEST = FULL_SERVER_PATH + "/AddPendingPermissionRequest";
    public static final String APPROVE_PENDING_PERMISSION_REQUEST = FULL_SERVER_PATH + "/ApprovePendingPermissionRequest";
    public static final String REJECT_PENDING_PERMISSION_REQUEST = FULL_SERVER_PATH + "/RejectPendingPermissionRequest";
    public static final String GET_ALL_RANGES_NAMES_FOR_SELECTED_SHEET = FULL_SERVER_PATH + "/GetAllRangesNamesForSelectedSheet";
    public static final String GET_TEMP_SHEET_FOR_DYNAMIC_ANALYSIS = FULL_SERVER_PATH + "/GetTempSheetForDynamicAnalysis";

    public static final String USERNAME = "username";
    public static final String USER_NAME_ERROR = "username_error";

    public static final String SHEET_NAME = "sheetName";
    public static final String ROW_NUMBER = "rowNumber";
    public static final String COLUMN_NUMBER = "columnNumber";
    public static final String RANGE_NAME = "rangeName";
    public static final String VERSION_NUMBER = "versionNumber";
    public static final String LEFT_TOP_START_COORDINATE = "leftTopStartCoordinate";
    public static final String RIGHT_BOTTOM_END_COORDINATE = "rightBottomEndCoordinate";
    public static final String COLUMN_CHAR_STRING = "columnCharString";
    public static final String NUM_OF_SHEETS_IN_SERVER = "numOfSheetsInServer";
    public static final String WAS_THERE_A_CHANGE_IN_PERMISSIONS = "wasThereAChangeInPermissions";
    public static final String NUM_OF_ALL_PERMISSION_REQUESTS_FOR_SELECTED_SHEET = "numOfAllPermissionRequestsForSelectedSheet";
    public static final String NUM_OF_PENDING_PERMISSION_REQUESTS_FOR_SELECTED_SHEET = "numOfPendingPermissionRequestsForSelectedSheet";
    public static final String PENDING = "Pending";
    public static final String WRITER = "Writer";
    public static final String READER = "Reader";
    public static final String None = "None";
    public static final String NUM_OF_REQUEST_FOR_SHEET = "numOfRequestForSheet";
    public static final String CURRENT_VERSION_NUM_IN_UPDATING_CLIENT = "currentVersionNumInUpdatingClient";

    public static final String PERMISSION_LEVEL_REQUESTED = "permissionLevelRequested";

    // GSON instance
    public final static Gson GSON_INSTANCE =
            new GsonBuilder()
                .registerTypeAdapter(SheetDto.class, new SheetDtoDeserializer())
                .registerTypeAdapter(RangeWithEffectiveValuesDto.class, new RangeWithEffectiveValuesDtoDeserializer())
                .create();
}
