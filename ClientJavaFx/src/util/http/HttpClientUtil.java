package util.http;

import okhttp3.*;

import java.io.IOException;
import java.util.function.Consumer;

public class HttpClientUtil {

    private final static SimpleCookieManager simpleCookieManager = new SimpleCookieManager();
    private final static OkHttpClient HTTP_CLIENT =
            new OkHttpClient.Builder()
                    .cookieJar(simpleCookieManager)
                    .followRedirects(false)
                    .build();

    public static void setCookieManagerLoggingFacility(Consumer<String> logConsumer) {
        simpleCookieManager.setLogData(logConsumer);
    }

    public static void removeCookiesOf(String domain) {
        simpleCookieManager.removeCookiesOf(domain);
    }

    public static void runAsync(String finalUrl, Callback callback) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);

        call.enqueue(callback);
    }

    public static void runAsyncWithPostAndBody(String finalUrl, RequestBody body, Callback callback) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .post(body)
                .build();

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);

        call.enqueue(callback);
    }

    public static Response runSync(String finalUrl) throws Exception {
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);

        return call.execute();
    }


    public static Response runSyncWithPostAndBody(String finalUrl, RequestBody body) throws Exception {
        Request request = new Request.Builder()
                .url(finalUrl)
                .post(body)
                .build();

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);

        return call.execute();
    }

    public static Response runSyncWithDelete(String finalUrl) throws IOException {
        Request request = new Request.Builder()
                .url(finalUrl)
                .delete()
                .build();

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);

        return call.execute();
    }

    public static void shutdown() {
//        System.out.println("Shutting down HTTP CLIENT");
        HTTP_CLIENT.dispatcher().executorService().shutdown();
        HTTP_CLIENT.connectionPool().evictAll();
    }

    public static Response runSyncWithPost(String finalUrl) throws IOException {
        Request request = new Request.Builder()
                .url(finalUrl)
                .post(RequestBody.create(new byte[0]))
                .build();

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);

        return call.execute();
    }

    public static Response runSyncWithPut(String finalUrl) throws IOException {
        Request request = new Request.Builder()
                .url(finalUrl)
                .put(RequestBody.create(new byte[0]))
                .build();

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);

        return call.execute();
    }
}
