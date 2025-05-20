package management.window;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;
import management.center.RowInPermissionTable;
import management.top.ManagementTopController;
import management.center.ManagementCenterController;
import management.right.ManagementRightController;
import okhttp3.Response;
import okhttp3.ResponseBody;
import operating.window.SheetWindowController;

import java.io.Closeable;
import java.io.IOException;

public class ManagementWindowController implements Closeable {

    Scene sheetWindowScene;
    Stage primaryStage;
    String userName;

    @FXML private ManagementTopController managementTopController;
    @FXML private ManagementCenterController managementCenterController;
    @FXML private ManagementRightController managementRightController;
    private SheetWindowController sheetWindowController;

    @FXML
    public void initialize() {
        managementTopController.setManagementWindowController(this);
        managementCenterController.setManagementWindowController(this);
        managementRightController.setManagementWindowController(this);
    }

    public void setUserName(String userName) {
        this.userName = userName;
        managementTopController.setUserName(userName);
        managementRightController.setUserName(userName);
        managementCenterController.setUserName(userName);
        sheetWindowController.setUserName(userName);
        setTextInUserNameLabel();
    }

    public void setActive() {
        managementCenterController.startSheetsListRefresher();
    }

    public void setTextInUserNameLabel() {
        managementTopController.setTextInUserNameLabel(userName);
    }

    public void setSheetWindowScene(Scene sheetWindowScene) {
        this.sheetWindowScene = sheetWindowScene;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setDataInSheetWindowFromResponse(Response response) {
        sheetWindowController.setResponseFromManagementWindow(response);
        sheetWindowController.loadSheetFromManagementWindow();
    }

    public void goToSheetWindow() {
        primaryStage.setScene(sheetWindowScene);
    }

    @Override
    public void close() throws IOException {
        managementCenterController.close();
    }

    public void setSheetWindowController(SheetWindowController sheetWindowController) {
        this.sheetWindowController = sheetWindowController;
    }

    public void addInfoOfUploadedSheetToTableView(ResponseBody body) {
        managementCenterController.addInfoOfUploadedSheetToTableView(body);
    }

    public void updateSelectedSheetFromSheetsTableView(String sheetName) {
        managementRightController.updateSelectedSheetFromSheetsTableView(sheetName);
    }

    public void setMessageOfRecentActionOutcomeLabel(String message) {
        managementTopController.setMessageOfRecentActionOutcomeLabel(message);
    }

    public void pausePermissionRequestsRefresher() {
        managementCenterController.pausePermissionRequestsRefresher();
    }

    public void disablePermissionRequestsButton() {
        managementRightController.disablePermissionRequestsButton();
    }

    public void enableButtonsToApproveOrRejectPermissionRequests() {
        managementRightController.enableButtonsToApproveOrRejectPermissionRequests();
    }

    public void enablePermissionRequestsButton() {
        managementRightController.enablePermissionRequestsButton();
    }

    public void disableButtonsToApproveOrRejectPermissionRequests() {
        managementRightController.disableButtonsToApproveOrRejectPermissionRequests();
    }

    public void setSelectedRowInPermissionTable(RowInPermissionTable selectedRow) {
        managementRightController.setSelectedRowInPermissionTable(selectedRow);
    }

    public RowInPermissionTable getUpdatedSelectedRowInPermissionTable() {
        return managementCenterController.getUpdatedSelectedRowInPermissionTable();
    }

    public String getPermissionLevelForSelectedSheet() {
        return managementCenterController.getPermissionLevelForSelectedSheet();
    }

    public void setPermissionLevelForSelectedSheet(String permissionLevel) {
        sheetWindowController.setPermissionLevelForSelectedSheet(permissionLevel);
    }

    public void resumePermissionRequestsRefresher() {
        managementCenterController.resumePermissionRequestsRefresher();
    }

    public void setUserNameAndSheetNameInSheetWindow(String currentUserName, String selectedSheetName) {
        sheetWindowController.setUserNameAndSheetNameInSheetWindow(currentUserName, selectedSheetName);
    }
}
