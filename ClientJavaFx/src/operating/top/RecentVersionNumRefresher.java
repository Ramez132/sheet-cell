package operating.top;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static util.Constants.REFRESH_RATE;

public class RecentVersionNumRefresher {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Consumer<Integer> recentVersionNumConsumer;
    private Supplier<String> currentSheetNameSupplier;
    private int currentMaxVersionNumInClient;
    private volatile boolean isPaused = true; // New flag to control pausing

    public RecentVersionNumRefresher(Consumer<Integer> recentVersionNumConsumer) {
        this.recentVersionNumConsumer = recentVersionNumConsumer;
        startFetchingRecentVersionNum();
    }

    public void onSheetSelected(Supplier<String> newSheetNameSupplier,
                                int currentMaxVersionNumInClient) {
        this.currentSheetNameSupplier = newSheetNameSupplier;
        this.currentMaxVersionNumInClient = currentMaxVersionNumInClient;
    }

    public void updateCurrentMaxVersionNumInClient(int currentMaxVersionNumInClient) {
        this.currentMaxVersionNumInClient = currentMaxVersionNumInClient;
    }

    private void startFetchingRecentVersionNum() {
        scheduler.scheduleAtFixedRate(() -> {
            if (isPaused) return; // Skip fetching if paused

            String sheetName = (currentSheetNameSupplier != null) ? currentSheetNameSupplier.get() : null;
            if (sheetName != null) {
                fetchRecentVersionNum(sheetName);
            }
        }, REFRESH_RATE, REFRESH_RATE, TimeUnit.MILLISECONDS); // Adjust interval as needed
    }

    private void fetchRecentVersionNum(String sheetName) {
        System.out.println("Fetching RecentVersionNum for sheet: " + sheetName);
        // Make HTTP request and process response here

        String finalUrl = HttpUrl
                .parse(Constants.GET_VERSION_NUM_OF_RECENT_SELECTED_SHEET)
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
                int recentVersionNumInServer = Integer.parseInt(responseBody);
                if (recentVersionNumInServer != currentMaxVersionNumInClient) {
                    currentMaxVersionNumInClient = recentVersionNumInServer;
                    recentVersionNumConsumer.accept(recentVersionNumInServer);
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