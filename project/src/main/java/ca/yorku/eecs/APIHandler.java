package ca.yorku.eecs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

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

		System.out.println("Request Method: " + method);
		System.out.println("Request Path: " + path);

		try {
			if (path.startsWith("/api/v1/addActor") && method.equals("PUT")) {
				handleAddActor(request);
			} else if (path.equals("/api/v1/addMovie") && method.equals("PUT")) {
				handleAddMovie(request);
			} else if (path.startsWith("/api/v1/addRelationship") && method.equals("PUT")) {
				handleAddRelationship(request);
			} else if (path.equals("/api/v1/getActor") && method.equals("GET")) {
				handleGetActor(request);
			} else if (path.startsWith("/api/v1/getMovie") && method.equals("GET")) {
				handleGetMovie(request);
			} else if (path.startsWith("/api/v1/hasRelationship") && method.equals("GET")) {
				handleHasRelationship(request);
			} else if (path.equals("/api/v1/computeBaconNumber") && method.equals("GET")) {
				handleComputeBaconNumber(request);
			} else if (path.equals("/api/v1/computeBaconPath") && method.equals("GET")) {
				handleComputeBaconPath(request);
			} else if (path.equals("/api/v1/addMovieRating") && method.equals("PUT")) {
				handleAddMovieRating(request);
			} else if (path.startsWith("/api/v1/getAverageRating") && method.equals("GET")) {
				handleGetAverageRating(request);
			} else if (path.equals("/api/v1/addMovieBoxRevenue") && method.equals("PUT")) {
				handleAddMovieBoxRevenue(request);
			} else if (path.equals("/api/v1/getActorMoviesByBoxRevenue") && method.equals("GET")) {
				handleGetActorMoviesByBoxRevenue(request);
			} else if (path.equals("/api/v1/deleteAll") && method.equals("DELETE")){
				handleDeleteAll(request);
			} else {
				this.sendResponse(request, 400, "Invalid Request");
			}
		} catch (Exception e){
			e.printStackTrace();
			this.sendResponse(request, 500, "Internal Server Error");
		} finally {
			os.close();
		}
	}

	private void handleAddActor(HttpExchange request) throws IOException {
		if("PUT".equals(request.getRequestMethod())) {
			// initializing the stream
			InputStreamReader i_SR = new InputStreamReader(request.getRequestBody(), StandardCharsets.UTF_8);
			BufferedReader bfr = new BufferedReader(i_SR);

			String line ; 
			StringBuilder sb  = new StringBuilder(); 

			// read the request body
			while((line = bfr.readLine())!=null) {
				sb.append(line);
			}

			String requested_body = sb.toString();

			try {
				// parse the request body into a JSON Object
				JSONObject json_obj = new JSONObject(requested_body);
				String actorId = json_obj.getString("actorId");
				String name = json_obj.getString("name");

				//check if both actor and name are missing
				if (actorId == null || name == null || actorId.isEmpty() || name.isEmpty()) {
					sendResponse(request, 400,"actorId and name are required");
					return;
				}
				// add the actor to the database and get the status
				boolean checker = actorController.addActor(actorId, name);

				// set the response message using the checker and jsonObj
				String response_generator;

				if (checker) {
					response_generator = "Succesfully added Actor " +actorId+ " to the database";
				} else {
					response_generator = "Failed to add the actor to the database";
				}

				// send the response with correct HTTP status code and response message
				sendResponse(request,checker ? 200 : 400, response_generator);

			} catch(Exception e) {
				e.printStackTrace();
				sendResponse(request, 500, "Internal Server Error");
			}

		}

	}

	private void handleAddMovie(HttpExchange request) throws IOException, JSONException {
		// read the request body
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getRequestBody(), StandardCharsets.UTF_8));
		StringBuilder requestBody = new StringBuilder();
		reader.lines().forEach(requestBody::append);
		reader.close();

		try {
			// parse the request body into a JSONObject
			JSONObject json = new JSONObject(requestBody.toString());
			String name = json.getString("name");
			String id = json.getString("movieId");

			if (name==null || name.isEmpty() || id==null || id.isEmpty()) {
				sendResponse(request, 400, "movieId and name are required");
				return;
			}

			// create a new movie object
			Movie movie = new Movie(id, name);

			// add the movie to the database and get the status
			boolean response = movieController.addMovie(movie);

			// send the response with correct HTTP status code and response message
			sendResponse(request, response ? 200 : 400, response ? "Movie added Successfully" : "Failed to add Movie to the database");
		} catch (Exception e){
			e.printStackTrace();
			sendResponse(request, 500, "Internal Server Error");
		}
	}

	private void handleAddRelationship(HttpExchange request) throws IOException, JSONException {
		// read the request body
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getRequestBody(), StandardCharsets.UTF_8));
		StringBuilder requestBody = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			requestBody.append(line);
		}
		reader.close();

		try {
			// parse the request body into a JSONObject
			JSONObject json = new JSONObject(requestBody.toString());
			String actorId = json.getString("actorId");
			String movieId = json.getString("movieId");

			if (actorId==null || actorId.isEmpty() || movieId==null || movieId.isEmpty()) {
				sendResponse(request, 400, "actorId and movieId are required");
				return;
			}

			// add the relationship and get the status
			boolean response = actorController.addRelationship(actorId, movieId);

			if (response) {
				// update the actor's movie list
				Actor actor = actorController.getActor(actorId);
				if (actor != null) {
					actor.addMovie(movieId);
					actorController.updateActor(actor);
				} else {
					// if the actor does not exist, send a 404 response
					sendResponse(request, 404, "Actor not found");
				}

				// update the movie's actor list
				Movie movie = movieController.getMovie(movieId);
				if (movie != null) {
					movie.addActor(actorId);
					movieController.updateMovie(movie);
				} else {
					// if the movie does not exist, send a 404 response
					sendResponse(request, 404, "Movie not found");
				}

				// send success response
				sendResponse(request, 200, "Relationship [ACTED IN] added Successfully");
			} else {
				// send failure response if relationship fails to be added
				sendResponse(request, 400, "Failed to add Relationship");
			}
		} catch (Exception e){
			e.printStackTrace();
			sendResponse(request, 500, "Internal Server Error");
		}
	}

	private void handleGetActor(HttpExchange request) throws IOException, JSONException {

		try {
			// extract the "actorId" parameter from the query
			String query = request.getRequestURI().getQuery();
			String actorId = Utils.getQueryParameter(query, "actorId");

			// check if actorId is missing
			if (actorId == null || actorId.isEmpty()) {
				sendResponse(request, 400, "Missing Actor Id");
				return;
			}

			// get the actor object
			Actor actorobj = actorController.getActor(actorId);

			// check if the actor was found
			if (actorobj != null) {
				// build the JSON response
				String response = "{"
						+ "\"actorId\": \"" + actorobj.getActorId() + "\", "
						+ "\"name\": \"" + actorobj.getName() + "\", "
						+ "\"movies\": " + new JSONArray(actorobj.getMovies()).toString()
						+ "}";
				sendResponse(request, 200, response);
			} else {
				// send a 404 response if actor was not found
				sendResponse(request, 404, "Actor not found");
			}
		} catch (Exception e){
			e.printStackTrace();
			sendResponse(request, 500, "Internal Server Error");
		}

	}

	private void handleGetMovie(HttpExchange request) throws IOException, JSONException {

		try {
			// extract the "movieId" parameter from the query
			String query = request.getRequestURI().getQuery();
			String movieId = Utils.getQueryParameter(query, "movieId");

			// check if movieId is missing
			if (movieId == null) {
				sendResponse(request, 400, "Missing Movie Id");
				return;
			}
			// get the movie object
			Movie movieOBJ = movieController.getMovie(movieId);

			// check if the movie was found
			if (movieOBJ != null) {
				// build the JSON response
				String response = "{"
						+ "\"movieId\": \"" + movieOBJ.getMovieId() + "\", "
						+ "\"name\": \"" + movieOBJ.getName() + "\", "
						+ "\"actors\": " + new JSONArray(movieOBJ.getActors()).toString()
						+ "}";
				sendResponse(request, 200, response);
			} else {
				// send a 404 response if movie was not found
				sendResponse(request, 404, "Movie not found");
			}
		} catch (Exception e){
			e.printStackTrace();
			sendResponse(request, 500, "Internal Server Error");
		}

	}

	private void handleHasRelationship(HttpExchange request) throws IOException, JSONException {

		try {
			// extract the "actorId" and "movieId" parameters from the query
			String query = request.getRequestURI().getQuery();
			String actorId = Utils.getQueryParameter(query, "actorId");
			String movieId = Utils.getQueryParameter(query, "movieId");

			// check if either parameter is missing or empty
			if (actorId==null || actorId.isEmpty() || movieId==null || movieId.isEmpty()) {
				sendResponse(request, 400, "Missing actorId or movieId");
				return;
			}

			// check if the relationship exists
			boolean hasRelationship = actorController.hasRelationship(actorId, movieId);

			// create a JSON Object to hold the response
			JSONObject jr = new JSONObject();
			jr.put("actorId", actorId);
			jr.put("movieId", movieId);
			jr.put("hasRelationship", hasRelationship);

			if (hasRelationship) {
				// send a 200 response if the relationship exists
				sendResponse(request, 200, jr.toString());
			} else {
				// send a 404 response if the relationship does not exist
				sendResponse(request, 404, "No relationship exists between actor and movie");
			}
		} catch (Exception e){
			e.printStackTrace();
			sendResponse(request, 500, "Internal Server Error");
		}
	}

	private void handleComputeBaconNumber(HttpExchange request) throws IOException {
		try {
		// extract the "actorId" parameter from the query
		String query = request.getRequestURI().getQuery();
		String actorId = Utils.getQueryParameter(query, "actorId");

		// check if actorId is missing or empty
		if(actorId==null || actorId.isEmpty()){
			sendResponse(request, 400, "Missing actorId");
			return;
		}
			// get the Bacon Number for the actor
			int baconNumber = actorController.computeBaconNumber(actorId);

			// if the Bacon Number is negative, send a 404 response
			if(baconNumber<0){
				sendResponse(request, 404, "No path to Kevin Bacon found");
			}
			else {
				// create a JSON Object to hold the response and send a 200 response
				JSONObject jr = new JSONObject();
				jr.put("baconNumber", baconNumber);
				sendResponse(request, 200, jr.toString());
			}

		} catch (Exception e){
			e.printStackTrace();
			sendResponse(request, 500, "Internal Server error");
		}

	}

	private void handleComputeBaconPath(HttpExchange request) throws IOException, JSONException {

		try {
			// extract the "actorId" parameter from the query
			String query = request.getRequestURI().getQuery();
			String actorId = Utils.getQueryParameter(query, "actorId");

			// check if actorId is missing or empty
			if (actorId == null || actorId.trim().isEmpty()) {
				sendResponse(request, 400, "Missing actorId");
				return;
			}

			// compute the bacon path
			List<String> baconPath;
			try {
				baconPath = actorController.computeBaconPath(actorId);
			} catch (Exception e) {
				e.printStackTrace();
				sendResponse(request, 500, "Internal Server Error");
				return;
			}

			// prepare the response
			if (baconPath == null || baconPath.isEmpty()) {
				this.sendResponse(request, 404, "No path Found");
			} else {
				JSONObject responseJson = new JSONObject();
				responseJson.put("baconPath", new JSONArray(baconPath));
				sendResponse(request, 200, responseJson.toString());
			}
		}  catch (Exception e) {
			e.printStackTrace();
			sendResponse(request, 500, "Internal Server Error");
		}
	}

	private void handleAddMovieRating(HttpExchange request) throws IOException, JSONException{
		// read the request body
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getRequestBody(), StandardCharsets.UTF_8));
		StringBuilder requestBody = new StringBuilder();
		String line;


		while ((line = reader.readLine()) != null) {
			requestBody.append(line);
		}

		reader.close();

		try {
			// request body turned into JSON object:
			JSONObject json = new JSONObject(requestBody.toString());
			String movieId = json.getString("movieId");
			double rating = json.getDouble("rating");

			if(movieId == null || movieId.isEmpty()){
				sendResponse(request, 400, "movieId is required");
				return;
			}
			if (rating < 1 || rating > 10) {
				sendResponse(request, 400, "rating must be between 1 and 10");
				return;
			}

			Movie movie = movieController.getMovie(movieId);
			if (movie == null) {
				sendResponse(request, 404, "Movie not found");
				return;
			}

			// movieController being called
			boolean success = movieController.addMovieRating(movieId, rating);

			// send response to client
			this.sendResponse(request, success ? 200 : 400, success ? "Successfully added movie rating of " + rating + "/10" : "Failed to add Movie rating");
		}   catch (Exception e) {
			e.printStackTrace();
			sendResponse(request, 500, "Internal Server Error");
		}
	}

	private void handleGetAverageRating(HttpExchange request) throws IOException{

		try {
			// extract the "actorId" parameter from the query
			String query = request.getRequestURI().getQuery();
			String actorId = Utils.getQueryParameter(query, "actorId");


			// Check if actorId is missing or empty
			if(actorId == null || actorId.isEmpty()){
				sendResponse(request, 400, "Missing actorId");
				return;
			}

			// get the actor object using actorId
			Actor actor = actorController.getActor(actorId);
			if(actor==null){
				sendResponse(request, 404, "Actor not Found");
				return;
			}

			// get the list of movies from the actor
			List<Movie> movies = actorController.getMoviesForActor(actorId);
			if(movies.isEmpty()){
				sendResponse(request, 404, "No movies found for actor");
				return;
			}

			// iterate through the actor's movies and sum up the ratings
			double totalRating = 0;
			int count = 0;
			for(Movie movie : movies){
				Double rating = movie.getRating();
				if(rating!=null){
					totalRating+=rating;
					count++;
				}
			}

			// check if the movies have ratings to calculate
			if(count==0){
				sendResponse(request, 404, "No ratings found for the actor's movies");
			}
			// calculate the average rating of the actor's movies and send the response
			double averageRating = totalRating/count;
			sendResponse(request, 200, "Average rating for Actor " +actorId+ " is " +averageRating);

			} catch (Exception e){
				e.printStackTrace();
				sendResponse(request, 500, "Internal Sever Error");
			}
		}

	private void handleAddMovieBoxRevenue(HttpExchange request) throws IOException, JSONException{
		try {
			// read the request body
			BufferedReader reader = new BufferedReader(new InputStreamReader(request.getRequestBody(), StandardCharsets.UTF_8));
			StringBuilder requestBody = new StringBuilder();
			String line;


			while ((line = reader.readLine()) != null) {
				requestBody.append(line);
			}
			reader.close();

			// request body turned into JSON object
			JSONObject json = new JSONObject(requestBody.toString());
			String movieId = json.getString("movieId");
			double revenue = json.getDouble("revenue");

			// check if movie exists
			Movie movie = movieController.getMovie(movieId);
			if (movie == null) {
				sendResponse(request, 404, "Movie not found");
				return;
			}

			// MovieController being called to add box revenue property to movie
			boolean success = movieController.addMovieBoxRevenue(movieId, revenue);

			// Send response to client
			this.sendResponse(request, success ? 200 : 400, success ? "Successfully added movie box revenue " : "Failed to add movie box revenue");
		} catch (Exception e){
			e.printStackTrace();
			sendResponse(request, 500, "Internal Sever Error");
		}
	}
	private void handleGetActorMoviesByBoxRevenue(HttpExchange request) throws IOException {

		try {
			// extract the "actorId" parameter from the query
			String query = request.getRequestURI().getQuery();
			String actorId = Utils.getQueryParameter(query, "actorId");

			// check if actorId is missing or empty
			if (actorId==null || actorId.isEmpty()) {
				sendResponse(request, 400, "actorId Missing or Invalid");
				return;
			}

			// get the list of movies for the actor using actorId
			List<Movie> movies = actorController.getActorMoviesByBoxRevenue(actorId);

			// check if any movies exist
			if (movies.isEmpty()) {
				sendResponse(request, 404, "No movies found for actor");
				return;
			}

			// create a JSON array to hold the movies as a list
			JSONArray jsonResponse = new JSONArray();
			for (Movie movie : movies) {
				JSONObject jsonMovie = new JSONObject();
				jsonMovie.put("movieId", movie.getMovieId());
				jsonMovie.put("name", movie.getName());
				jsonMovie.put("revenue", movie.getRevenue());
				jsonResponse.put(jsonMovie);
			}

			String response = jsonResponse.toString();
			sendResponse(request, 200, response);

		} catch (Exception e){
			e.printStackTrace();
			sendResponse(request, 500, "Internal Sever Error");
		}
	}

	private void handleDeleteAll(HttpExchange request) throws IOException {
		try {
			boolean success = actorController.deleteAll();
			if (success) sendResponse(request, 200, "All data deleted successfully");
		} catch (Exception e) {
			e.printStackTrace();
			sendResponse(request, 500, "Internal Server Error");
		}
	}
	public void sendResponse(HttpExchange request, int statusCode, String response) throws IOException {
		request.sendResponseHeaders(statusCode, response.length());
		OutputStream os = request.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

}
