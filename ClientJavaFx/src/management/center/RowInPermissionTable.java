package management.center;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class RowInPermissionTable {
    private final SimpleStringProperty permissionRequestUserName;
    private final SimpleStringProperty permissionLevelRequested;
    private final SimpleStringProperty permissionRequestStatus;
    private final SimpleIntegerProperty numOfRequestForSheet;

    public RowInPermissionTable(String userName, String permissionLevelRequested,
                                String permissionRequestStatus, int numOfRequestForSheet) {
        this.permissionRequestUserName = new SimpleStringProperty(userName);
        this.permissionLevelRequested = new SimpleStringProperty(permissionLevelRequested);
        this.permissionRequestStatus = new SimpleStringProperty(permissionRequestStatus);
        this.numOfRequestForSheet = new SimpleIntegerProperty(numOfRequestForSheet);
    }

    public void setPermissionRequestStatus(String permissionRequestStatus) {
        this.permissionRequestStatus.set(permissionRequestStatus);
    }

    public String getPermissionRequestUserName() { return permissionRequestUserName.get(); }
    public String getPermissionLevelRequested() { return permissionLevelRequested.get(); }
    public String getPermissionRequestStatus() { return permissionRequestStatus.get(); }
    public Integer getNumOfRequestForSheet() { return numOfRequestForSheet.get(); }
}
