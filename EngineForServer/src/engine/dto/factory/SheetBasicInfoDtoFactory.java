package engine.dto.factory;

import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIConversion;
import dto.management.info.SheetBasicInfoDto;

import java.util.List;

public class SheetBasicInfoDtoFactory {

    public static SheetBasicInfoDto createSheetBasicInfoDto
            (String sheetName, String ownerUsername, String currentUserPermissionLevel,
             int numOfRows, int numOfColumns,
             int numOfTotalPermissionRequests, int numOfPendingPermissionRequests,
             List<String> rangeNamesInUploadedSheet) {

        return new SheetBasicInfoDto(sheetName, ownerUsername, currentUserPermissionLevel,
                                    numOfRows, numOfColumns,
                                    numOfTotalPermissionRequests, numOfPendingPermissionRequests,
                                    rangeNamesInUploadedSheet);
    }
}
