package engine.permissions;

public class PermissionRequest {
    private final String username;
    private final String sheetName;
    private final PermissionLevel permissionLevel;
    private PermissionRequestStatus status;
    private int numOfRequestForSheet;

    public PermissionRequest(String sheetName,String username, PermissionLevel requestedPermissionLevel, int numOfRequestForSheet) {
        this.username = username;
        this.sheetName = sheetName;
        this.permissionLevel = requestedPermissionLevel;
        this.status = PermissionRequestStatus.PENDING;
        this.numOfRequestForSheet = numOfRequestForSheet;
    }

    //how to handle owner?
    //how to handle multiple requests for the same user? how to hold his current PermissionLevel?

    public String getUsername() {
        return username;
    }

    public String getSheetName() {
        return sheetName;
    }

    public PermissionLevel getPermissionLevel() {
        return permissionLevel;
    }

    public PermissionRequestStatus getRequestStatus() {
        return status;
    }

    public int getNumOfRequestForSheet() {
        return numOfRequestForSheet;
    }

    public void approve() {
        status = PermissionRequestStatus.APPROVED;
    }

    public void reject() {
        status = PermissionRequestStatus.REJECTED;
    }


}
