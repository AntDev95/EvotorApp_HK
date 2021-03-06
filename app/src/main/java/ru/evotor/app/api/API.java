package ru.evotor.app.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.zip.GZIPInputStream;

import ru.evotor.app.Application;
import ru.evotor.app.DispatchQueue;
import ru.evotor.app.Helper;
import ru.evotor.devices.commons.DeviceServiceConnector;
import ru.evotor.devices.commons.printer.PrinterDocument;
import ru.evotor.devices.commons.printer.printable.PrintableImage;


public class API {

    private static volatile API Instance = null;
    private final static DispatchQueue thread = new DispatchQueue("vk");
    private final SharedPreferences preferences;
    public String shop_id, user_id;

    API() {
        preferences = Application.context.getSharedPreferences("current_info", Context.MODE_PRIVATE);
        shop_id = preferences.getString("shop_id", null);
        user_id = preferences.getString("user_id", null);
    }

    public JSONObject request(String method) {
        Params params = new Params();
        params.addValue("version_api", "1");
        return request(method, params);
    }

    public JSONObject request(String method, Params params) {
        try {
            String result = httpEngine("http://78.155.199.101/" + method, String.valueOf(params), 0);
            return new JSONObject(result);
        } catch (Throwable ignored) { }
        return new JSONObject();
    }

    public void shorMethod(final String link) {
        thread.postRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    Params params = new Params();
                    params.addValue("url", link);
                    request("/short/", params);
                } catch (final Throwable ignored) {}
            }
        });
    }

    public void startCrutch() {
        thread.postRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    final JSONObject result = API.getInstance().request("evotor/crutch/").getJSONObject("result");
                    shop_id = result.getString("shop_id");
                    user_id = result.getString("user_id");
                    preferences.edit().putString("shop_id", shop_id).putString("user_id", user_id).apply();
                } catch (Throwable ignored) { }
            }
        });
    }

    private String httpEngine(final String url, String body, int attempt) {
        attempt++;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(true);
            OutputStream os = connection.getOutputStream();
            os.write(body.getBytes("UTF-8"));
            os.close();
            connection.connect();
            InputStream is = new BufferedInputStream(connection.getResponseCode() == 200 ? connection.getInputStream() : connection.getErrorStream(), 8192);
            String enc = connection.getHeaderField("Content-Encoding");
            if (enc != null && enc.equalsIgnoreCase("gzip")) {
                is = new GZIPInputStream(is);
            }
            final String result = streamToString(is);
            if (TextUtils.isEmpty(result)) {
                try { Thread.sleep(3000); } catch (Throwable ignored) { }
                return httpEngine(url, body, 0);
            }
            return result;
        } catch (final Throwable e) {
            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Throwable ignore){ }
            }
            return attempt >= 7 ? null : httpEngine(url, body, attempt);
        }
    }

    private String streamToString(InputStream is) throws IOException {
        InputStreamReader r = new InputStreamReader(is);
        StringWriter sw = new StringWriter();
        char[] buffer = new char[1024 * 8];
        try {
            for (int n; (n = r.read(buffer)) != -1;)
                sw.write(buffer, 0, n);
        } finally {
            try {
                is.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return String.valueOf(sw);
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
