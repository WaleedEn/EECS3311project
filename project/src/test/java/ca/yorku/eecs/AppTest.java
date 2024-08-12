package ca.yorku.eecs;

import junit.framework.TestCase;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AppTest extends TestCase {
    private static final String BASE_URL = "http://localhost:8080"; // Update with your server's base URL

    public void testAddActorPass() throws Exception {
        URL url = new URL(BASE_URL + "/api/v1/addActor?actorId=testActorId&name=Test Actor");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");

        int responseCode = conn.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            System.out.println(inputLine);
        }
        in.close();

        assertEquals(200, responseCode);
    }

    public void testAddActorFail() throws Exception {
        URL url = new URL(BASE_URL + "/api/v1/addActor?name=Test Actor"); // Missing actorId
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");

        int responseCode = conn.getResponseCode();
        assertEquals(400, responseCode);
    }

    public void testAddMoviePass() throws Exception {
        URL url = new URL(BASE_URL + "/api/v1/addMovie?movieId=testMovieId&name=Test Movie");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");

        int responseCode = conn.getResponseCode();
        assertEquals(200, responseCode);
    }

    public void testAddMovieFail() throws Exception {
        URL url = new URL(BASE_URL + "/api/v1/addMovie?name=Test Movie"); // Missing movieId
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");

        int responseCode = conn.getResponseCode();
        assertEquals(400, responseCode);
    }

    public void testGetActorPass() throws Exception {
        URL url = new URL(BASE_URL + "/api/v1/getActor?actorId=testActorId");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        assertEquals(200, responseCode);
    }

    public void testGetActorFail() throws Exception {
        URL url = new URL(BASE_URL + "/api/v1/getActor?actorId=nonExistentActorId");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        assertEquals(404, responseCode);
    }

    public void testComputeBaconNumberPass() throws Exception {
        URL url = new URL(BASE_URL + "/api/v1/computeBaconNumber?actorId=testActorId");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        assertEquals(200, responseCode);
    }

    public void testComputeBaconNumberFail() throws Exception {
        URL url = new URL(BASE_URL + "/api/v1/computeBaconNumber?actorId=nonExistentActorId");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        assertEquals(404, responseCode);
    }

    // Add similar methods for other endpoints...

}
