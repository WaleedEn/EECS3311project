package ca.yorku.eecs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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
		//this.movieController = new MovieController(driver);
	}
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		// TODO Auto-generated method stub
		String method = exchange.getRequestMethod();
		String path = exchange.getRequestURI().getPath();
		OutputStream os = exchange.getResponseBody();

		Map<String, String> response = new HashMap<>();

		try {
			if (path.startsWith("/api/v1/addActor") && method.equals("PUT")) {
				handleAddActor(exchange);
			} else if (path.startsWith("/api/v1/addMovie") && method.equals("PUT")) {
				handleAddMovie(exchange);
			} else if (path.startsWith("/api/v1/addRelationship") && method.equals("PUT")) {
				handleAddRelationship(exchange);
			} else if (path.startsWith("/api/v1/getActor") && method.equals("GET")) {
				handleGetActor(exchange);
			} else if (path.startsWith("/api/v1/getMovie") && method.equals("GET")) {
				handleGetMovie(exchange);
			} else if (path.startsWith("/api/v1/hasRelationship") && method.equals("GET")) {
				handleHasRelationship(exchange);
			} else if (path.startsWith("/api/v1/computeBaconNumber") && method.equals("GET")) {
				handleComputeBaconNumber(exchange);
			} else if (path.startsWith("/api/v1/computeBaconPath") && method.equals("GET")) {
				handleComputerBaconPath(exchange);
			} else if (path.startsWith("api/v1/AddMovieRating") && method.equals("PUT")) {
				handleAddMovieRating(exchange);
			} else if (path.startsWith("/api/v1/getAverageRating") && method.equals("GET")) {
				handleGetAverageRating(exchange);
			} else if (path.startsWith("/api/v1/addMovieBoxRevenue") && method.equals("PUT")) {
				handleAddMovieBoxRevenue(exchange);
			} else if (path.startsWith("/api/v1/getActorMoviesByBoxRevenue") && method.equals("GET")) {
				handleGetActorMoviesByBoxRevenue(exchange);
			} else {
				exchange.sendResponseHeaders(404, -1);
			}
		} catch (Exception e){
			e.printStackTrace();
			exchange.sendResponseHeaders(500,-1);
		} finally {
			os.close();
		}
	}

	private void handleAddActor(HttpExchange exchange) throws IOException, JSONException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
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
			exchange.sendResponseHeaders(200, -1);
		} else {
			exchange.sendResponseHeaders(400, -1);
		}
	}

	private void handleAddMovie(HttpExchange exchange) throws IOException, JSONException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
		StringBuilder requestBody = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			requestBody.append(line);
		}
		reader.close();

		// Parse the JSON request body
		JSONObject json = new JSONObject(requestBody.toString());
		String name = json.getString("name");
		String id = json.getString("id");

		// Create a Movie object
		Movie movie = new Movie(id, name);

		// Add the movie using the MovieController
		boolean response = movieController.addMovie(movie);

		// Send response based on the result
		if (success) {
			exchange.sendResponseHeaders(200, -1);  // OK
		} else {
			exchange.sendResponseHeaders(400, -1);  // Bad request
		}
	}

	private void handleAddRelationship(HttpExchange exchange) throws IOException{
	}

	private void handleGetActor(HttpExchange exchange) throws IOException, JSONException {
		String query = exchange.getRequestURI().getQuery();
		String actorId = Utils.getQueryParameter(query, "actorId");

		if (actorId.isEmpty()) {
			exchange.sendResponseHeaders(400, -1);
			return;
		}

		// Fetch actor details from the controller
		Actor actor = actorController.getActor(actorId);

		if (actor != null) {
			// Create JSON response with actor details
			JSONObject json = new JSONObject();
			json.put("id", actor.getActorId());
			json.put("name", actor.getName());
			json.put("movies", new JSONArray(actor.getMovies()));

			// Send a 200 OK response with the actor details
			byte[] response = json.toString().getBytes(StandardCharsets.UTF_8);
			exchange.sendResponseHeaders(200, response.length);
			OutputStream os = exchange.getResponseBody();
			os.write(response);
			os.close();
		} else {
			// If actor is not found, return a 404 Not Found
			exchange.sendResponseHeaders(404, -1);
		}
	}

	private void handleGetMovie(HttpExchange exchange) throws IOException{
	}

	private void handleHasRelationship(HttpExchange exchange) throws IOException{
	}

	private void handleComputeBaconNumber(HttpExchange exchange) throws IOException {
		String query = exchange.getRequestURI().getQuery();
		String actorId = Utils.getQueryParameter(query, "actorId");

		if(actorId.isEmpty()){
			exchange.sendResponseHeaders(400,-1);
			return;
		}

		String response = actorController.computeBaconNumber(actorId);

		int statusCode;

		if(response == null || response.contains("not found")){
			statusCode= 404;
		}else{
			statusCode = 200;
		}
		exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes(StandardCharsets.UTF_8));
		os.close();

	}

	private void handleComputerBaconPath(HttpExchange exchange) throws IOException {

	}

	private void handleAddMovieRating(HttpExchange exchange) throws IOException{
	}

	private void handleGetAverageRating(HttpExchange exchange) throws IOException{
	}

	private void handleAddMovieBoxRevenue(HttpExchange exchange) throws IOException{
	}

	private void handleGetActorMoviesByBoxRevenue(HttpExchange exchange) throws IOException{
	}


}
