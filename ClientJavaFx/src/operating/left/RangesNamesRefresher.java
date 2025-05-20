package operating.left;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static util.Constants.REFRESH_RATE;

public class RangesNamesRefresher {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Consumer<List<String>> recentRangesNamesConsumer;
    private Supplier<String> currentSheetNameSupplier;
    private  List<String> currentRangesNamesInClient;
    private volatile boolean isPaused = true; // New flag to control pausing

    public RangesNamesRefresher(Consumer<List<String>> recentRangesNamesConsumer) {
        this.recentRangesNamesConsumer = recentRangesNamesConsumer;
        startFetchingRecentRangesNames();
    }

    public void onSheetSelected(Supplier<String> newSheetNameSupplier,
                                List<String> newSheetCurrentRangesNames) {
        this.currentSheetNameSupplier = newSheetNameSupplier;
        this.currentRangesNamesInClient = newSheetCurrentRangesNames;
    }

    public void updateCurrentRangesNamesInClient(List<String> newSheetCurrentRangesNames) {
        this.currentRangesNamesInClient = newSheetCurrentRangesNames;
    }

    private void startFetchingRecentRangesNames() {
        scheduler.scheduleAtFixedRate(() -> {
            if (isPaused) return; // Skip fetching if paused

            String sheetName = (currentSheetNameSupplier != null) ? currentSheetNameSupplier.get() : null;
            if (sheetName != null) {
                fetchRecentRangesNames(sheetName);
            }
        }, REFRESH_RATE, REFRESH_RATE, TimeUnit.MILLISECONDS); // Adjust interval as needed
    }

    private void fetchRecentRangesNames(String sheetName) {
        System.out.println("Fetching RangesNames for sheet: " + sheetName);
        // Make HTTP request and process response here

        String finalUrl = HttpUrl
                .parse(Constants.GET_ALL_RANGES_NAMES_FOR_SELECTED_SHEET)
                .newBuilder()
                .addQueryParameter(Constants.SHEET_NAME, sheetName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("RecentVersionNum Request | Ended with failure...");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                String[] rangesNames = Constants.GSON_INSTANCE.fromJson(responseBody, String[].class);
                List<String> rangesNamesListFromServer = List.of(rangesNames);
                boolean rangesNamesChanged = false;

                //Check if server contains new ranges
                for (String rangeNameFromServer : rangesNamesListFromServer) {
                    if (!currentRangesNamesInClient.contains(rangeNameFromServer)) {
                        rangesNamesChanged = true;
                        break;
                    }
                }

                //Check if client contains ranges that were deleted from server
                for (String rangeNameInClient : currentRangesNamesInClient) {
                    if (!rangesNamesListFromServer.contains(rangeNameInClient)) {
                        rangesNamesChanged = true;
                        break;
                    }
                }

                if (rangesNamesChanged) {
                    currentRangesNamesInClient = rangesNamesListFromServer;
                    recentRangesNamesConsumer.accept(rangesNamesListFromServer);
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