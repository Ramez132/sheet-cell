package dto.management.info;

import java.util.List;

public record SheetBasicInfoDto(String sheetName,
                                String ownerUsername,
                                String currentUserPermissionLevel,
                                int numOfRows,
                                int numOfColumns,
                                int numOfTotalPermissionRequests,
                                int numOfPendingPermissionRequests,
                                List<String> rangeNamesInUploadedSheet) {
}
