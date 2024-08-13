package ca.yorku.eecs;

import junit.framework.TestCase;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;

public class AppTest extends TestCase {
    private static final String serverURL = "http://localhost:8080";

    private HttpURLConnection sendRequest(String endpoint, String method) throws IOException{
        URL url = new URL(serverURL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        return connection;
    }
    public void testAddActorPass() throws Exception {
        HttpURLConnection connection = sendRequest("/api/v1/addActor", "PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.getOutputStream().write("{\"name\": \"ActorName\", \"actorId\": \"actor12345\"}".getBytes());

        int statusCode = connection.getResponseCode();
        assertEquals(200, statusCode);
        connection.disconnect();
    }

    public void testAddActorFail() throws Exception {
        HttpURLConnection connection = sendRequest("/api/v1/addActor", "PUT");
        connection.setDoOutput(true);
        connection.getOutputStream().write("{\"actorId\": \"\", \"name\": \"\"}".getBytes());

        int responseCode = connection.getResponseCode();
        assertEquals(400, responseCode);
        connection.disconnect();
    }

    public void testAddMoviePass() throws Exception {
        HttpURLConnection connection = sendRequest("/api/v1/addMovie", "PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        String requestBody = "{\"movieId\": \"movie123\", \"name\": \"MovieTitle\"}";
        connection.getOutputStream().write(requestBody.getBytes());

        int statusCode = connection.getResponseCode();
        assertEquals(200, statusCode);
        connection.disconnect();
    }

    public void testAddMovieFail() throws Exception {
        URL url = new URL(serverURL + "/api/v1/addMovie?name=Test Movie"); // Missing movieId
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");

        int responseCode = conn.getResponseCode();
        assertEquals(400, responseCode);
    }

    public void testGetActorPass() throws Exception {
        URL url = new URL(serverURL + "/api/v1/getActor?actorId=actor12345");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        assertEquals(200, responseCode);
    }

    public void testGetActorFail() throws Exception {
        URL url = new URL(serverURL + "/api/v1/getActor?actorId=nonExistentActorId");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        assertEquals(404, responseCode);
    }

    public void testComputeBaconNumberPass() throws Exception {
        URL url = new URL(serverURL + "/api/v1/computeBaconNumber?actorId=actor12345");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        assertEquals(200, responseCode);
    }

    public void testComputeBaconNumberFail() throws Exception {
        URL url = new URL(serverURL + "/api/v1/computeBaconNumber?actorId=nonExistentActorId");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        assertEquals(404, responseCode);
    }
    

}
