package ru.evotor.app.api;

import org.json.JSONException;
import org.json.JSONObject;

import ru.evotor.app.Application;
import ru.evotor.framework.users.UserApi;

public class ReceptModel {

    public int id;
    public String user_id;

    public ReceptModel(int id) {
        this.user_id = UserApi.getAuthenticatedUser(Application.context).getUuid();
        this.id = id;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject item = new JSONObject();

        return item;
    }

    @Override
    public String toString() {
        try {
            return String.valueOf(toJSONObject());
        } catch (Throwable e) {
            return "{}";
        }
    }
}
