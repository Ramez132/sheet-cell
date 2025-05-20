package dto.permission;

public record PermissionRequestDto(String sheetName, String username,
                                   String permissionLevelRequested, String permissionRequestStatus,
                                   int numOfRequestForSheet) {
}
