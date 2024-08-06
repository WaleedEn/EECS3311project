package ca.yorku.eecs.utils;

import ca.yorku.eecs.model.Actor;

import org.json.JSONException;
import org.json.JSONObject;

public class Utils {
    public static Actor parseActorFromJson(String requestBody) throws JSONException {
        JSONObject json = new JSONObject(requestBody);
        String name = json.optString("name");
        String actorId = json.optString("actorId");
        return new Actor(name, actorId);
    }
}
