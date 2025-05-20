package engine.dto.factory;

import dto.permission.PermissionRequestDto;
import engine.permissions.PermissionRequest;

public class PermissionRequestDtoFactory {

    public static PermissionRequestDto createNewPermissionRequestDto(PermissionRequest permissionRequest) {
        return new PermissionRequestDto(permissionRequest.getSheetName(), permissionRequest.getUsername(),
                permissionRequest.getPermissionLevel().toString(), permissionRequest.getRequestStatus().toString(),
                permissionRequest.getNumOfRequestForSheet());
    }
}
