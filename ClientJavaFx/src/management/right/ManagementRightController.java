package management.right;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import management.center.RowInPermissionTable;
import management.window.ManagementWindowController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.Objects;

public class ManagementRightController {

    @FXML private Button requestReadingPermissionButton;
    @FXML private  Button requestWritingPermissionButton;
    @FXML private  Button approveSelectedRequestButton;
    @FXML private  Button denySelectedRequestButton;
    private ManagementWindowController managementWindowController;
    private String currentUserName;
    private String selectedSheetName;
    private RowInPermissionTable selectedRowInPermissionTable;

    public void setManagementWindowController(ManagementWindowController managementWindowController) {
        this.managementWindowController = managementWindowController;
    }

    public void setUserName(String userName) {
        this.currentUserName = userName;
    }

    //add logic in case there is no selected sheet in the table view
    @FXML
    public void handlePressingViewSelectedSheetButton() {
        if(selectedSheetName == null) {
            managementWindowController.setMessageOfRecentActionOutcomeLabel("Please select a sheet first");
            return;
        }
        String permissionLevel = managementWindowController.getPermissionLevelForSelectedSheet();
        if (Objects.equals(permissionLevel, Constants.None)) {
            managementWindowController.setMessageOfRecentActionOutcomeLabel("You don't have permission to view this sheet");
            return;
        }
        String finalUrl = HttpUrl
                .parse(Constants.GET_SHEET_AND_RANGES_NAMES_DTO)
                .newBuilder()
                .addQueryParameter(Constants.USERNAME, currentUserName)
                .addQueryParameter(Constants.SHEET_NAME, selectedSheetName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        managementWindowController.setMessageOfRecentActionOutcomeLabel("Something went wrong: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            managementWindowController.setMessageOfRecentActionOutcomeLabel(responseBody)
                    );
                } else {
                    Platform.runLater(() -> {
                        managementWindowController.setPermissionLevelForSelectedSheet(permissionLevel);
                        managementWindowController.setUserNameAndSheetNameInSheetWindow(currentUserName, selectedSheetName);
                        managementWindowController.setDataInSheetWindowFromResponse(response);
                        managementWindowController.pausePermissionRequestsRefresher();
                        managementWindowController.goToSheetWindow();
                    });
                }
            }
        });
    }

    @FXML
    public void handlePressingRequestReadingPermissionButton() {
        if (selectedSheetName == null) {
            managementWindowController.setMessageOfRecentActionOutcomeLabel("Please select a sheet first");
            return;
        }

        String finalUrl = HttpUrl
                .parse(Constants.ADD_PENDING_PERMISSION_REQUEST)
                .newBuilder()
                .addQueryParameter(Constants.USERNAME, currentUserName)
                .addQueryParameter(Constants.SHEET_NAME, selectedSheetName)
                .addQueryParameter(Constants.PERMISSION_LEVEL_REQUESTED, Constants.READER)
                .build()
                .toString();
        try {
            Response response = HttpClientUtil.runSyncWithPost(finalUrl);
            if (response.isSuccessful()) {
                managementWindowController.setMessageOfRecentActionOutcomeLabel("Request has been sent");
            } else {
                // Handle non-200 HTTP responses
                String responseBody = response.body().string();
                throw new IllegalArgumentException(responseBody);
            }
        } catch (Exception e) {
            managementWindowController.setMessageOfRecentActionOutcomeLabel(e.getMessage());
        }
    }

    @FXML
    public void handlePressingRequestWritingPermissionButton() {
        if (selectedSheetName == null) {
            managementWindowController.setMessageOfRecentActionOutcomeLabel("Please select a sheet first");
            return;
        }

        String finalUrl = HttpUrl
                .parse(Constants.ADD_PENDING_PERMISSION_REQUEST)
                .newBuilder()
                .addQueryParameter(Constants.USERNAME, currentUserName)
                .addQueryParameter(Constants.SHEET_NAME, selectedSheetName)
                .addQueryParameter(Constants.PERMISSION_LEVEL_REQUESTED, Constants.WRITER)
                .build()
                .toString();
        try {
            Response response = HttpClientUtil.runSyncWithPost(finalUrl);
            if (response.isSuccessful()) {
                managementWindowController.setMessageOfRecentActionOutcomeLabel("Request has been sent");
            } else {
                // Handle non-200 HTTP responses
                String responseBody = response.body().string();
                throw new IllegalArgumentException(responseBody);
            }
        } catch (Exception e) {
            managementWindowController.setMessageOfRecentActionOutcomeLabel(e.getMessage());
        }
    }

    @FXML
    public void handlePressingApproveSelectedRequestButton() {
        if (selectedRowInPermissionTable == null) {
            managementWindowController.setMessageOfRecentActionOutcomeLabel("Please select a request first");
            return;
        }
        selectedRowInPermissionTable = managementWindowController.getUpdatedSelectedRowInPermissionTable();
        if (!Objects.equals(selectedRowInPermissionTable.getPermissionRequestStatus().toUpperCase(), Constants.PENDING.toUpperCase())) {
            managementWindowController.setMessageOfRecentActionOutcomeLabel("This request has already been handled");
            return;
        }

        String finalUrl = HttpUrl
                .parse(Constants.APPROVE_PENDING_PERMISSION_REQUEST)
                .newBuilder()
                .addQueryParameter(Constants.SHEET_NAME, selectedSheetName)
                .addQueryParameter(Constants.USERNAME, selectedRowInPermissionTable.getPermissionRequestUserName())
                .addQueryParameter(Constants.PERMISSION_LEVEL_REQUESTED, selectedRowInPermissionTable.getPermissionLevelRequested())
                .addQueryParameter(Constants.NUM_OF_REQUEST_FOR_SHEET, String.valueOf(selectedRowInPermissionTable.getNumOfRequestForSheet()))
                .build()
                .toString();

        try {
            Response response = HttpClientUtil.runSyncWithPut(finalUrl);
            if (response.isSuccessful()) {
                managementWindowController.setMessageOfRecentActionOutcomeLabel("Request has been approved");
            } else {
                // Handle non-200 HTTP responses
                String responseBody = response.body().string();
                throw new IllegalArgumentException(responseBody);
            }
        } catch (Exception e) {
            managementWindowController.setMessageOfRecentActionOutcomeLabel(e.getMessage());
        }
    }

    @FXML
    public void handlePressingDenySelectedRequestButton() {
        if (selectedRowInPermissionTable == null) {
            managementWindowController.setMessageOfRecentActionOutcomeLabel("Please select a request first");
            return;
        }
        selectedRowInPermissionTable = managementWindowController.getUpdatedSelectedRowInPermissionTable();
        if (!Objects.equals(selectedRowInPermissionTable.getPermissionRequestStatus().toUpperCase(), Constants.PENDING.toUpperCase())) {
            managementWindowController.setMessageOfRecentActionOutcomeLabel("This request has already been handled");
            return;
        }

        String finalUrl = HttpUrl
                .parse(Constants.REJECT_PENDING_PERMISSION_REQUEST)
                .newBuilder()
                .addQueryParameter(Constants.SHEET_NAME, selectedSheetName)
                .addQueryParameter(Constants.USERNAME, selectedRowInPermissionTable.getPermissionRequestUserName())
                .addQueryParameter(Constants.PERMISSION_LEVEL_REQUESTED, selectedRowInPermissionTable.getPermissionLevelRequested())
                .addQueryParameter(Constants.NUM_OF_REQUEST_FOR_SHEET, String.valueOf(selectedRowInPermissionTable.getNumOfRequestForSheet()))
                .build()
                .toString();
        try {
            Response response = HttpClientUtil.runSyncWithPut(finalUrl);
            if (response.isSuccessful()) {
                managementWindowController.setMessageOfRecentActionOutcomeLabel("Request has been denied");
            } else {
                // Handle non-200 HTTP responses
                String responseBody = response.body().string();
                throw new IllegalArgumentException(responseBody);
            }
        } catch (Exception e) {
            managementWindowController.setMessageOfRecentActionOutcomeLabel(e.getMessage());
        }
    }

    public void updateSelectedSheetFromSheetsTableView(String sheetName) {
        selectedSheetName = sheetName;
    }

    public void disablePermissionRequestsButton() {
        requestReadingPermissionButton.setDisable(true);
        requestWritingPermissionButton.setDisable(true);
    }

    public void enableButtonsToApproveOrRejectPermissionRequests() {
        approveSelectedRequestButton.setDisable(false);
        denySelectedRequestButton.setDisable(false);
    }

    public void enablePermissionRequestsButton() {
        requestReadingPermissionButton.setDisable(false);
        requestWritingPermissionButton.setDisable(false);
    }

    public void disableButtonsToApproveOrRejectPermissionRequests() {
        approveSelectedRequestButton.setDisable(true);
        denySelectedRequestButton.setDisable(true);
    }

    public void setSelectedRowInPermissionTable(RowInPermissionTable selectedRow) {
        selectedRowInPermissionTable = selectedRow;
    }
}
