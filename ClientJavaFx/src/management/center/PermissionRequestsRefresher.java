package management.center;

import dto.permission.PermissionRequestDto;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static util.Constants.GSON_INSTANCE;
import static util.Constants.REFRESH_RATE;

public class PermissionRequestsRefresher {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Consumer<List<PermissionRequestDto>> permissionRequestsConsumer;
    private Supplier<String> currentSheetNameSupplier;
    private int newSheetCurrentNumOfTotalPermissionRequests;
    private int newSheetCurrentNumOfPendingPermissionRequests;
    private volatile boolean isPaused = true; // New flag to control pausing

    public PermissionRequestsRefresher(Consumer<List<PermissionRequestDto>> permissionRequestsConsumer) {
        this.permissionRequestsConsumer = permissionRequestsConsumer;
        startPermissionRequestFetching();
    }

    public void onSheetSelected(Supplier<String> newSheetNameSupplier,
                                int newSheetCurrentNumOfTotalPermissionRequests,
                                int newSheetCurrentNumOfPendingPermissionRequests) {
        this.currentSheetNameSupplier = newSheetNameSupplier;
        this.newSheetCurrentNumOfTotalPermissionRequests = newSheetCurrentNumOfTotalPermissionRequests;
        this.newSheetCurrentNumOfPendingPermissionRequests = newSheetCurrentNumOfPendingPermissionRequests;
    }

    private void startPermissionRequestFetching() {
        scheduler.scheduleAtFixedRate(() -> {
            if (isPaused) return; // Skip fetching if paused

            String sheetName = (currentSheetNameSupplier != null) ? currentSheetNameSupplier.get() : null;
            if (sheetName != null) {
                fetchPermissionRequests(sheetName);
            }
        }, REFRESH_RATE, REFRESH_RATE, TimeUnit.MILLISECONDS); // Adjust interval as needed
    }

    private void fetchPermissionRequests(String sheetName) {
        System.out.println("Fetching permissions for sheet ID: " + sheetName);
        // Make HTTP request and process response here

        String finalUrl = HttpUrl
                .parse(Constants.GET_ALL_PERMISSION_REQUESTS_FOR_SHEET)
                .newBuilder()
                .addQueryParameter(Constants.SHEET_NAME, sheetName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Permissions Request | Ended with failure...");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                int currentNumOfTotalPermissionRequests = Integer.parseInt(response.header(Constants.NUM_OF_ALL_PERMISSION_REQUESTS_FOR_SELECTED_SHEET));
                int currentNumOfPendingPermissionRequests = Integer.parseInt(response.header(Constants.NUM_OF_PENDING_PERMISSION_REQUESTS_FOR_SELECTED_SHEET));
                if (currentNumOfTotalPermissionRequests != newSheetCurrentNumOfTotalPermissionRequests
                    || currentNumOfPendingPermissionRequests != newSheetCurrentNumOfPendingPermissionRequests) {
                    newSheetCurrentNumOfTotalPermissionRequests = currentNumOfTotalPermissionRequests;
                    newSheetCurrentNumOfPendingPermissionRequests = currentNumOfPendingPermissionRequests;

                    String jsonArrayOfPermissionRequestDto = response.body().string();
                    PermissionRequestDto[] dataOfAllPermissionRequestsForSheet = GSON_INSTANCE.fromJson(jsonArrayOfPermissionRequestDto, PermissionRequestDto[].class);
                    permissionRequestsConsumer.accept(Arrays.asList(dataOfAllPermissionRequestsForSheet));
                    response.close();
                }
            }
        });
    }

    // Method to pause fetching
    public void pause() {
        isPaused = true;
    }

    // Method to resume fetching
    public void resume() {
        isPaused = false;
    }

    public void stop() {
        scheduler.shutdown();
    }
}