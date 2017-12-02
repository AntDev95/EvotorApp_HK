package ru.evotor.app.api;

import org.json.JSONObject;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ru.evotor.app.DispatchQueue;


public class API {

    private static volatile API Instance = null;
    private final DispatchQueue thread = new DispatchQueue("vk");
    private final static OkHttpClient client = new OkHttpClient();

    public JSONObject request(String method) {
        return request(method, new Params());
    }

    public JSONObject request(String method, Params params) {
        try {
            String result = httpEngine("hhttp://78.155.199.101/" + method, String.valueOf(params), 0);
            return new JSONObject(result);
        } catch (Throwable ignored) { }
        return new JSONObject();
    }

    public String httpEngine(String url, String params, int attempt) {
        attempt++;
        if (attempt > 5) {
            return null;
        }
        try {
            RequestBody body = RequestBody.create(MultipartBody.FORM, params);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return String.valueOf(response.body().string());
        } catch (Throwable e) {
            return httpEngine(url, params, attempt);
        }
    }

    public static API getInstance() {
        API localInstance = Instance;
        if (localInstance == null) {
            synchronized (API.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new API();
                }
            }
        }
        return localInstance;
    }

}
