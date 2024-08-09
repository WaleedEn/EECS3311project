package ca.yorku.eecs.utils;

import org.neo4j.driver.v1.Driver;

public class Utils {
    public static String getQueryParameter(String query, String param) {
        if (query != null) {
            for (String pair : query.split("&")) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2 && keyValue[0].equals(param)) {
                    return keyValue[1];
                }
            }
        }
        return null;
    }

}
