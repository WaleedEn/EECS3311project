package ca.yorku.eecs;


import com.sun.net.httpserver.HttpServer;
import junit.framework.TestCase;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class AppTest extends TestCase {

    private static final int PORT = 8080;
    private static HttpServer server;
    private static final String serverURL = "http://localhost:8080";

    public void setUp() throws Exception {

        Neo4jConfig neo4jConfig = new Neo4jConfig();
        server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
        server.createContext("/api/v1", new APIHandler(neo4jConfig));
        server.start();
    }

    public void tearDown() throws Exception {
        if (server != null) {
            server.stop(0);
        }
    }
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

        JSONObject jr = new JSONObject();
        jr.put("name", "Actor");
        jr.put("actorId", "actror123");

        OutputStream os = connection.getOutputStream();
        os.write(jr.toString().getBytes(StandardCharsets.UTF_8));
        os.flush();
        os.close();

        int statusCode = connection.getResponseCode();
        assertEquals(200, statusCode);
        connection.disconnect();
        resetDatabase();
    }

    public void testAddActorFail() throws Exception {
        HttpURLConnection connection = sendRequest("/api/v1/addActor", "PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.getOutputStream().write("{\"name\": \"\", \"actorId\": \"\"}".getBytes());

        int statusCode = connection.getResponseCode();
        assertEquals(400, statusCode);
        connection.disconnect();
    }

    public void testAddMoviePass() throws Exception {
        HttpURLConnection connection = sendRequest("/api/v1/addMovie", "PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        JSONObject jr = new JSONObject();
        jr.put("name", "Movie");
        jr.put("movieId", "movie123");

        OutputStream os = connection.getOutputStream();
        os.write(jr.toString().getBytes(StandardCharsets.UTF_8));
        os.flush();
        os.close();

        int statusCode = connection.getResponseCode();
        assertEquals(200, statusCode);
        connection.disconnect();
        resetDatabase();
    }

    public void testAddMovieFail() throws Exception {
        HttpURLConnection connection = sendRequest("/api/v1/addMovie", "PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.getOutputStream().write("{\"name\": \"\", \"movieId\": \"\"}".getBytes());

        int statusCode = connection.getResponseCode();
        assertEquals(400, statusCode);
        connection.disconnect();
    }

    public void testAddRelationshipPass() throws IOException {
        HttpURLConnection connection = sendRequest("/api/v1/addActor", "PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        String requestBody = "{\"name\": \"Actor\", \"actorId\": \"actor123\"}";
        connection.getOutputStream().write(requestBody.getBytes());
        connection.getResponseCode();
        connection.disconnect();

        connection = sendRequest("/api/v1/addMovie", "PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        String requestBody2 = "{\"name\": \"Movie\", \"movieId\": \"movie123\"}";
        connection.getOutputStream().write(requestBody2.getBytes());
        connection.getResponseCode();
        connection.disconnect();

        connection = sendRequest("/api/v1/addRelationship", "PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        String requestBody3 = "{\"actorId\": \"actor123\", \"movieId\": \"movie123\"}";
        connection.getOutputStream().write(requestBody3.getBytes());

        int statusCode = connection.getResponseCode();
        assertEquals(200, statusCode);
        resetDatabase();
        connection.disconnect();
    }

    public void testAddRelationshipFail() throws IOException {
        HttpURLConnection connection = sendRequest("/api/v1/addRelationship", "PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.getOutputStream().write("{\"actorId\": \"\", \"movieId\": \"\"}".getBytes());

        int statusCode = connection.getResponseCode();
        assertEquals(400, statusCode);
        connection.disconnect();
        resetDatabase();
    }

    public void testGetActorPass() throws Exception {
        HttpURLConnection connection = sendRequest("/api/v1/addActor", "PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        String requestBody = "{\"name\": \"Actor One\", \"actorId\": \"actor123\"}";
        connection.getOutputStream().write(requestBody.getBytes());
        connection.getResponseCode();
        connection.disconnect();

        connection = sendRequest("/api/v1/getActor?actorId=actor123", "GET");

        int statusCode = connection.getResponseCode();
        assertEquals(200, statusCode);
        connection.disconnect();
        resetDatabase();
    }

    public void testGetActorFail() throws Exception {
        HttpURLConnection connection = sendRequest("/api/v1/getActor?actorId=000", "GET");

        int statusCode = connection.getResponseCode();
        assertEquals(404, statusCode);
        connection.disconnect();
    }

    public void testGetMoviePass() throws IOException, JSONException {
        HttpURLConnection connection = sendRequest("/api/v1/addMovie", "PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        String requestBody2 = "{\"name\": \"Movie One\", \"movieId\": \"movie123\"}";
        connection.getOutputStream().write(requestBody2.getBytes());
        connection.getResponseCode();
        connection.disconnect();

        connection = sendRequest("/api/v1/getMovie?movieId=movie123", "GET");

        int statusCode = connection.getResponseCode();
        assertEquals(200, statusCode);
        connection.disconnect();
        resetDatabase();
    }

    public void testGetMovieFail() throws IOException {
        HttpURLConnection connection = sendRequest("/api/v1/getMovie?movieId=movie000", "GET");

        int statusCode = connection.getResponseCode();
        assertEquals(404, statusCode);
        connection.disconnect();
    }

    public void testHasRelationshipPass() throws IOException {
        HttpURLConnection connection = sendRequest("/api/v1/addActor", "PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        String requestBody = "{\"name\": \"Actor\", \"actorId\": \"actor123\"}";
        connection.getOutputStream().write(requestBody.getBytes());
        connection.getResponseCode();
        connection.disconnect();

        connection = sendRequest("/api/v1/addMovie", "PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        String requestBody2 = "{\"name\": \"Movie\", \"movieId\": \"movie123\"}";
        connection.getOutputStream().write(requestBody2.getBytes());
        connection.getResponseCode();
        connection.disconnect();

        connection = sendRequest("/api/v1/addRelationship", "PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        String requestBody3 = "{\"actorId\": \"actor123\", \"movieId\": \"movie123\"}";
        connection.getOutputStream().write(requestBody3.getBytes());

        int statusCode = connection.getResponseCode();
        assertEquals(200, statusCode);
        connection.disconnect();

        resetDatabase();
    }
    public void testHasRelationshipFail() throws IOException {
        HttpURLConnection connection = sendRequest("/api/v1/hasRelationship?actorId=&movieId=movie000", "GET");

        int statusCode = connection.getResponseCode();
        assertEquals(400, statusCode);
        connection.disconnect();
    }

    public void testComputeBaconNumberPass() throws Exception {
        // add kevin bacon to compute the bacon number
        HttpURLConnection connection = sendRequest("/api/v1/addActor", "PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.getOutputStream().write("{\"name\": \"Kevin Bacon\", \"actorId\": \"nm0000102\"}".getBytes());
        connection.getResponseCode();
        connection.disconnect();

        // add an actor to the database
        connection = sendRequest("/api/v1/addActor", "PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.getOutputStream().write("{\"name\": \"Actor Name\", \"actorId\": \"actor123\"}".getBytes());
        connection.getResponseCode();
        connection.disconnect();

        // add a movie to the database
        connection = sendRequest("/api/v1/addMovie", "PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.getOutputStream().write("{\"name\": \"Some Movie\", \"movieId\": \"movie123\"}".getBytes());
        connection.getResponseCode();
        connection.disconnect();

        // add the relationship between Kevin bacon and movie
        connection = sendRequest("/api/v1/addRelationship", "PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.getOutputStream().write("{\"actorId\": \"nm0000102\", \"movieId\": \"movie123\"}".getBytes());
        connection.getResponseCode();
        connection.disconnect();

        // add the relationship between actor and the movie
        connection = sendRequest("/api/v1/addRelationship", "PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.getOutputStream().write("{\"actorId\": \"actor123\", \"movieId\": \"movie123\"}".getBytes());
        connection.getResponseCode();
        connection.disconnect();

        connection = sendRequest("/api/v1/computeBaconNumber?actorId=actor123", "GET");

        int statusCode = connection.getResponseCode();
        assertEquals(200, statusCode);
        connection.disconnect();
        resetDatabase();
    }

    public void testComputeBaconNumberFail() throws Exception {
        HttpURLConnection connection = sendRequest("/api/v1/computeBaconNumber?actorId=actor000", "GET");
        int statusCode = connection.getResponseCode();
        assertEquals(404, statusCode);

        connection.disconnect();
    }

    public void testComputeBaconPathPass() throws IOException {
        // add Kevin Bacon to the database to compute the bacon number
        HttpURLConnection connection = sendRequest("/api/v1/addActor", "PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.getOutputStream().write("{\"name\": \"Kevin Bacon\", \"actorId\": \"nm0000102\"}".getBytes());
        connection.getResponseCode();
        connection.disconnect();

        // add an actor to the database
        connection = sendRequest("/api/v1/addActor", "PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.getOutputStream().write("{\"name\": \"Actor Name\", \"actorId\": \"actor123\"}".getBytes());
        connection.getResponseCode();
        connection.disconnect();

        // add a movie to the database
        connection = sendRequest("/api/v1/addMovie", "PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.getOutputStream().write("{\"name\": \"Some Movie\", \"movieId\": \"movie123\"}".getBytes());
        connection.getResponseCode();
        connection.disconnect();

        // add the relationship between Kevin bacon and movie
        connection = sendRequest("/api/v1/addRelationship", "PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.getOutputStream().write("{\"actorId\": \"nm0000102\", \"movieId\": \"movie123\"}".getBytes());
        connection.getResponseCode();
        connection.disconnect();

        // add the relationship between actor and the movie
        connection = sendRequest("/api/v1/addRelationship", "PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.getOutputStream().write("{\"actorId\": \"actor123\", \"movieId\": \"movie123\"}".getBytes());
        connection.getResponseCode();
        connection.disconnect();

        int responseCode = connection.getResponseCode();
        assertEquals(200, responseCode);
        connection.disconnect();
        resetDatabase();
    }

    public void testComputeBaconPathFail() throws IOException {
        URL url = new URL(serverURL + "/api/v1/computeBaconPath?actorId=actor000");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        assertEquals(404, responseCode);
        conn.disconnect();
    }

    private void resetDatabase() throws IOException {
        HttpURLConnection connection = sendRequest("/api/v1/deleteAll", "DELETE");
        connection.setDoOutput(true);
        int statusCode = connection.getResponseCode();
        assertEquals(200, statusCode);
        connection.disconnect();
    }
}