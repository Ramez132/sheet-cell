package management.center;

import dto.management.info.SheetBasicInfoDto;
import dto.permission.PermissionRequestDto;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import management.window.ManagementWindowController;
import okhttp3.*;
import util.Constants;
import util.http.HttpClientUtil;

import java.util.*;

import static util.Constants.GSON_INSTANCE;
import static util.Constants.REFRESH_RATE;

public class ManagementCenterController {
    private ManagementWindowController managementWindowController;
    private Timer timerForSheetsListRefresher;
    private TimerTask sheetsListRefresher;
    private String userName;
    private String currentSelectedSheetName;
    private int newSheetCurrentNumOfTotalPermissionRequests;
    private int newSheetCurrentNumOfPendingPermissionRequests;

    @FXML private TableColumn<RowInSheetTable, String> sheetNameColumn;
    @FXML private TableColumn<RowInSheetTable, String> ownerNameColumn;
    @FXML private TableColumn<RowInSheetTable, Integer> numRowsColumn;
    @FXML private TableColumn<RowInSheetTable, Integer> numColumnsColumn;
    @FXML private TableColumn<RowInSheetTable, String> permissionLevelColumn;
    @FXML private TableView<RowInSheetTable> allSheetsTableView;
    private ObservableList<RowInSheetTable> allRowsInSheetsTable;

    @FXML private TableColumn<RowInPermissionTable, String> permissionRequestUserNameColumn;
    @FXML private TableColumn<RowInPermissionTable, String> permissionLevelRequestedColumn;
    @FXML private TableColumn<RowInPermissionTable, String> permissionRequestStatusColumn;
    @FXML private TableColumn<RowInPermissionTable, Integer> permissionRequestNumColumn;
    @FXML private TableView<RowInPermissionTable> allPermissionsTableView;
    private ObservableList<RowInPermissionTable> allRowsInPermissionsTable;

    private PermissionRequestsRefresher permissionRequestsRefresher;


    @FXML
    public void initialize() {
        sheetNameColumn.setCellValueFactory(new PropertyValueFactory<>("sheetName"));
        ownerNameColumn.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
        numRowsColumn.setCellValueFactory(new PropertyValueFactory<>("numRows"));
        numColumnsColumn.setCellValueFactory(new PropertyValueFactory<>("numCols"));
        permissionLevelColumn.setCellValueFactory(new PropertyValueFactory<>("permissionLevel"));

        allRowsInSheetsTable = FXCollections.observableArrayList();
        allSheetsTableView.setItems(allRowsInSheetsTable);

        permissionRequestNumColumn.setCellValueFactory(new PropertyValueFactory<>("numOfRequestForSheet"));
        permissionRequestUserNameColumn.setCellValueFactory(new PropertyValueFactory<>("permissionRequestUserName"));
        permissionLevelRequestedColumn.setCellValueFactory(new PropertyValueFactory<>("permissionLevelRequested"));
        permissionRequestStatusColumn.setCellValueFactory(new PropertyValueFactory<>("permissionRequestStatus"));

        allRowsInPermissionsTable = FXCollections.observableArrayList();
        allPermissionsTableView.setItems(allRowsInPermissionsTable);

        permissionRequestsRefresher = new PermissionRequestsRefresher(this::updatePermissionTableView);
    }

    public String getCurrentSheetNameSupplier() {
        // Return a Supplier that fetches the latest currentSheetId value
        return currentSelectedSheetName;
    }

    public void setManagementWindowController(ManagementWindowController mainController) {
        this.managementWindowController = mainController;
    }

    public void addInfoOfUploadedSheetToTableView(ResponseBody body) {

        try {
            SheetBasicInfoDto sheetBasicInfo = GSON_INSTANCE.fromJson(body.string(), SheetBasicInfoDto.class);
            RowInSheetTable newRow = new RowInSheetTable(sheetBasicInfo.sheetName(),
                                            sheetBasicInfo.ownerUsername(),
                                            sheetBasicInfo.numOfRows(),
                                            sheetBasicInfo.numOfColumns(),
                                            sheetBasicInfo.currentUserPermissionLevel());
            allRowsInSheetsTable.add(newRow);
        } catch (Exception e) {
            managementWindowController.setMessageOfRecentActionOutcomeLabel(e.getMessage());
        }
    }

    @FXML
    private void handleRowSelectionInSheetsTableView(MouseEvent event) {
        // Get the selected row from the TableView
        RowInSheetTable selectedRow = allSheetsTableView.getSelectionModel().getSelectedItem();

        if (selectedRow != null) {
            // Access the data in the selected row
            String sheetName = selectedRow.getSheetName();
            currentSelectedSheetName = sheetName;
            managementWindowController.updateSelectedSheetFromSheetsTableView(sheetName);

            if (selectedRow.getPermissionLevel().equals("Owner")) {
                managementWindowController.disablePermissionRequestsButton();
                managementWindowController.enableButtonsToApproveOrRejectPermissionRequests();
            } else {
                managementWindowController.enablePermissionRequestsButton();
                managementWindowController.disableButtonsToApproveOrRejectPermissionRequests();
            }

            try {
                List<PermissionRequestDto> allPermissionRequestsForSheet = GetAllPermissionDataFromServer(sheetName);
                updatePermissionTableView(allPermissionRequestsForSheet);
                permissionRequestsRefresher.onSheetSelected(this::getCurrentSheetNameSupplier,
                        newSheetCurrentNumOfTotalPermissionRequests,
                        newSheetCurrentNumOfPendingPermissionRequests);
                permissionRequestsRefresher.resume();
            }
            catch (Exception e) {
                managementWindowController.setMessageOfRecentActionOutcomeLabel(e.getMessage());
            }
        }
    }

    @FXML
    public void handleRowSelectionInPermissionsTableView(MouseEvent mouseEvent) {
        RowInPermissionTable selectedRow = allPermissionsTableView.getSelectionModel().getSelectedItem();
        if (selectedRow != null) {
            managementWindowController.setSelectedRowInPermissionTable(selectedRow);
        }
    }

    public void pausePermissionRequestsRefresher() {
        permissionRequestsRefresher.pause();
    }

    private void updatePermissionTableView(List<PermissionRequestDto> allPermissionRequestsForSheet) {
        Platform.runLater(() -> {
            RowInPermissionTable selectedRow = allPermissionsTableView.getSelectionModel().getSelectedItem();
            allRowsInPermissionsTable.clear();
            for (PermissionRequestDto permissionRequestDto : allPermissionRequestsForSheet) {
                RowInPermissionTable newRow = new RowInPermissionTable(permissionRequestDto.username(),
                        permissionRequestDto.permissionLevelRequested(),
                        permissionRequestDto.permissionRequestStatus(),
                        permissionRequestDto.numOfRequestForSheet());
                if (permissionRequestDto.permissionLevelRequested().equals("Owner")) {
                    newRow.setPermissionRequestStatus("Owner - not relevant");
                }
                allRowsInPermissionsTable.add(newRow);
            }

            allRowsInPermissionsTable.sort(Comparator.comparing(RowInPermissionTable::getNumOfRequestForSheet));

            if (selectedRow != null) {
                int selectedNumOfRequest = selectedRow.getNumOfRequestForSheet();
                allPermissionsTableView.getItems().stream()
                        .filter(row -> row.getNumOfRequestForSheet().equals(selectedNumOfRequest))
                        .findFirst()
                        .ifPresent(row -> allPermissionsTableView.getSelectionModel().select(row));
            }
        });
    }

    private List<PermissionRequestDto> GetAllPermissionDataFromServer(String sheetName) {
        String finalUrl = HttpUrl
                .parse(Constants.GET_ALL_PERMISSION_REQUESTS_FOR_SHEET)
                .newBuilder()
                .addQueryParameter(Constants.SHEET_NAME, sheetName)
                .build()
                .toString();
        try {
            Response response = HttpClientUtil.runSync(finalUrl);
            if (response.isSuccessful()) {
                newSheetCurrentNumOfTotalPermissionRequests = Integer.parseInt(response.header(Constants.NUM_OF_ALL_PERMISSION_REQUESTS_FOR_SELECTED_SHEET));
                newSheetCurrentNumOfPendingPermissionRequests = Integer.parseInt(response.header(Constants.NUM_OF_PENDING_PERMISSION_REQUESTS_FOR_SELECTED_SHEET));
                String jsonArrayOfPermissionRequestDto = response.body().string();
                PermissionRequestDto[] dataOfAllPermissionRequestsForSheet = GSON_INSTANCE.fromJson(jsonArrayOfPermissionRequestDto, PermissionRequestDto[].class);
                return Arrays.asList(dataOfAllPermissionRequestsForSheet);
            } else {
                // Handle non-200 HTTP responses
                String responseBody = response.body().string();
                throw new IllegalArgumentException(responseBody);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void updateSheetsTableWithAllSheetsInSystem(List<SheetBasicInfoDto> dataOfAllSheetsInSystem) {
        Platform.runLater(() -> {
            RowInSheetTable selectedRow = allSheetsTableView.getSelectionModel().getSelectedItem();
            allRowsInSheetsTable.clear();
            for (SheetBasicInfoDto sheetBasicInfo : dataOfAllSheetsInSystem) {
                RowInSheetTable newRow = new RowInSheetTable(sheetBasicInfo.sheetName(),
                        sheetBasicInfo.ownerUsername(),
                        sheetBasicInfo.numOfRows(),
                        sheetBasicInfo.numOfColumns(),
                        sheetBasicInfo.currentUserPermissionLevel());
                allRowsInSheetsTable.add(newRow);
            }

            if (selectedRow != null) {
                String selectedSheetName = selectedRow.getSheetName();
                currentSelectedSheetName = selectedSheetName;
                allSheetsTableView.getItems().stream()
                        .filter(row -> row.getSheetName().equals(selectedSheetName))
                        .findFirst()
                        .ifPresent(row -> allSheetsTableView.getSelectionModel().select(row));

            }
        });
    }

    public void startSheetsListRefresher() {
        sheetsListRefresher = new SheetsListRefresher(
                                    userName,
                                    this::updateSheetsTableWithAllSheetsInSystem);
        timerForSheetsListRefresher = new Timer();
        timerForSheetsListRefresher.schedule(sheetsListRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public RowInPermissionTable getUpdatedSelectedRowInPermissionTable() {
        return allPermissionsTableView.getSelectionModel().getSelectedItem();
    }

    public String getPermissionLevelForSelectedSheet() {
        return allSheetsTableView.getSelectionModel().getSelectedItem().getPermissionLevel();
    }

    public void resumePermissionRequestsRefresher() {
        permissionRequestsRefresher.resume();
    }

    public void close() {
        if (sheetsListRefresher != null && timerForSheetsListRefresher != null) {
            sheetsListRefresher.cancel();
            timerForSheetsListRefresher.cancel();
        }
        permissionRequestsRefresher.stop();
    }
}