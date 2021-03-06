package ru.evotor.app.api;

import android.support.v4.util.ArrayMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Map;

public class Params {

    private final ArrayMap<String, String> params = new ArrayMap<>();

    public Params addValue(String name, Object value) {
        params.put(name, String.valueOf(value));
        return this;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject result = new JSONObject();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                result.put(entry.getKey(), URLEncoder.encode(entry.getValue(), "utf-8"));
            }
        } catch (Throwable ignored) {}
        return result;
    }

    public static Params newInstance() {
        return new Params();
    }

    @Override
    public String toString() {
        String result = "";
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                result += "&" + entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "utf-8");
            }
        } catch (Throwable ignored) {}
        return String.valueOf(result.subSequence(1, result.length()));
    }
}