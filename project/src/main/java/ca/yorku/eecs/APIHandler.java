package ca.yorku.eecs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.yorku.eecs.controller.ActorController;
import ca.yorku.eecs.controller.MovieController;
import ca.yorku.eecs.model.Actor;
import ca.yorku.eecs.model.Movie;
import ca.yorku.eecs.utils.Utils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.v1.Driver;

public class APIHandler implements HttpHandler {

	private Neo4jConfig neo4jConfig;
	private ActorController actorController;
	private MovieController movieController;

	public APIHandler(Neo4jConfig neo4jConfig){
		this.neo4jConfig = neo4jConfig;
		Driver driver = neo4jConfig.getDriver();
		this.actorController = new ActorController(driver);
		this.movieController = new MovieController(driver);
	}
	@Override
	public void handle(HttpExchange request) throws IOException {
		// TODO Auto-generated method stub
		String method = request.getRequestMethod();
		String path = request.getRequestURI().getPath();
		OutputStream os = request.getResponseBody();

		Map<String, String> response = new HashMap<>();

		try {
			if (path.startsWith("/api/v1/addActor") && method.equals("PUT")) {
				handleAddActor(request);
			} else if (path.startsWith("/api/v1/addMovie") && method.equals("PUT")) {
				handleAddMovie(request);
			} else if (path.startsWith("/api/v1/addRelationship") && method.equals("PUT")) {
				handleAddRelationship(request);
			} else if (path.startsWith("/api/v1/getActor") && method.equals("GET")) {
				handleGetActor(request);
			} else if (path.startsWith("/api/v1/getMovie") && method.equals("GET")) {
				handleGetMovie(request);
			} else if (path.startsWith("/api/v1/hasRelationship") && method.equals("GET")) {
				handleHasRelationship(request);
			} else if (path.startsWith("/api/v1/computeBaconNumber") && method.equals("GET")) {
				handleComputeBaconNumber(request);
			} else if (path.startsWith("/api/v1/computeBaconPath") && method.equals("GET")) {
				handleComputeBaconPath(request);
			} else if (path.startsWith("api/v1/addMovieRating") && method.equals("PUT")) {
				handleaddMovieRating(request);
			} else if (path.startsWith("/api/v1/getAverageRating") && method.equals("GET")) {
				handleGetAverageRating(request);
			} else if (path.startsWith("/api/v1/addMovieBoxRevenue") && method.equals("PUT")) {
				handleAddMovieBoxRevenue(request);
			} else if (path.startsWith("/api/v1/getActorMoviesByBoxRevenue") && method.equals("GET")) {
				handleGetActorMoviesByBoxRevenue(request);
			} else {
				this.response(request, 404, "Not Found");
			}
		} catch (Exception e){
			e.printStackTrace();
			this.response(request, 500, "Internal Server Error");
		} finally {
			os.close();
		}
	}

	private void handleAddActor(HttpExchange request) throws IOException, JSONException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getRequestBody(), StandardCharsets.UTF_8));
		StringBuilder requestBody = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			requestBody.append(line);
		}
		reader.close();

		JSONObject json = new JSONObject(requestBody.toString());
		String name = json.getString("name");
		String id = json.getString("id");

		Actor actor = new Actor(id, name);
		boolean response = actorController.addActor(actor);

		if (response) {
			this.response(request, 200, "Actor added Succesfully");
		} else {
			this.response(request, 400, "Failed to add Actor");
		}
	}

	private void handleAddMovie(HttpExchange request) throws IOException, JSONException {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getRequestBody(), StandardCharsets.UTF_8));
		StringBuilder requestBody = new StringBuilder();
		String line;

		reader.lines().forEach(requestBody::append);

		reader.close();

		JSONObject json = new JSONObject(requestBody.toString());
		String name = json.getString("name");
		String id = json.getString("movieId");

		Movie movie = new Movie(id, name);

		boolean response = movieController.addMovie(movie);

		this.response(request, response ? 200 : 400, response ? "Movie added Successfully" : "Failed to add Movie");

	}

	private void handleAddRelationship(HttpExchange request) throws IOException, JSONException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getRequestBody(), StandardCharsets.UTF_8));
		StringBuilder requestBody = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			requestBody.append(line);
		}
		reader.close();

		JSONObject json = new JSONObject(requestBody.toString());
		String actorId = json.getString("actorId");
		String movieId = json.getString("movieId");

		boolean response = actorController.addRelationship(actorId, movieId);

		if (response) {
			this.response(request, 200, "Relationship [ACTED IN] added Successfully");
		} else {
			this.response(request, 400,"Failed to add Relationship");
		}
	}

	private void handleGetActor(HttpExchange request) throws IOException, JSONException {
		String query = request.getRequestURI().getQuery();
		String actorId = Utils.getQueryParameter(query, "actorId");

		if (actorId.isEmpty()) {
			this.response(request, 400, "Missing actorId");
			return;
		}

		Actor actor = actorController.getActor(actorId);

		if (actor != null) {
			JSONObject json = new JSONObject();
			json.put("id", actor.getActorId());
			json.put("name", actor.getName());
			json.put("movies", new JSONArray(actor.getMovies()));

			this.response(request, 200, json.toString());
		} else {
			this.response(request, 404, "Actor not found");
		}
	}

	private void handleGetMovie(HttpExchange request) throws IOException{
	}

	private void handleHasRelationship(HttpExchange request) throws IOException{
	}

	private void handleComputeBaconNumber(HttpExchange request) throws IOException {
		String query = request.getRequestURI().getQuery();
		String actorId = Utils.getQueryParameter(query, "actorId");

		if(actorId.isEmpty()){
			response(request, 400, "Missing actorId");
		}

		String response = actorController.computeBaconNumber(actorId);

		if(response == null || response.contains("not found")){
			this.response(request, 404, "Bacon number not found");
		}else{
			this.response(request, 200, response);
		}

	}

	private void handleComputeBaconPath(HttpExchange request) throws IOException, JSONException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getRequestBody(), StandardCharsets.UTF_8));
		StringBuilder requestBody = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			requestBody.append(line);
		}
		reader.close();


		JSONObject json;
		try {
			json = new JSONObject(requestBody.toString());
		} catch (JSONException e) {
			this.response(request, 400, "Invalid JSON format");
			return;
		}

		String actorId = json.optString("actorId", "").trim();

		// Validate actorId
		if (actorId.isEmpty()) {
			this.response(request, 400, "Missing actorId");
			return;
		}

		// Compute the Bacon Path
		List<String> baconPath;
		try {
			baconPath = actorController.computeBaconPath(actorId, "nm0000102");
		} catch (Exception e) {
			e.printStackTrace();
			this.response(request,500, "Internal Server Error");  // Internal Server Error
			return;
		}

		// Prepare the response
		if(baconPath==null || baconPath.isEmpty()) {
			this.response(request, 404, "No path Found");
		} else {
			JSONObject responseJson = new JSONObject();
			responseJson.put("baconPath", new JSONArray(baconPath));
			this.response(request, 200, responseJson.toString());
		}
	}

	private void handleaddMovieRating(HttpExchange request) throws IOException{
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getRequestBody(), StandardCharsets.UTF_8));
    	StringBuilder requestBody = new StringBuilder();
    	String line;
    	reader.lines().forEach(requestBody::append);

    	reader.close();

    // request body turned into JSON object: 
    	JSONObject json = new JSONObject(requestBody.toString());
    	String movieId = json.getString("movieId");
    	double rating = json.getDouble("rating");

    // MovieController being called
   		boolean success = movieController.addMovieRating(movieId, rating);

    // send response to client
	this.response(request, response ? 200 : 400, response ? "Movie added Successfully" : "Failed to add Movie");
	
	}

	private void handleGetAverageRating(HttpExchange request) throws IOException{
	}

	private void handleAddMovieBoxRevenue(HttpExchange request) throws IOException{
	}

	private void handleGetActorMoviesByBoxRevenue(HttpExchange request) throws IOException{
	}

	public void response(HttpExchange request, int statusCode, String response) throws IOException {
		request.sendResponseHeaders(statusCode, response.length());
		OutputStream os = request.getResponseBody();
		os.write(response.getBytes());
		os.close();
		}

	}
