package ca.yorku.eecs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
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
			} else {
				this.sendResponse(request, 404, "Not Found");
			}
		} catch (Exception e){
			e.printStackTrace();
			this.sendResponse(request, 500, "Internal Server Error");
		} finally {
			os.close();
		}
	}

	private void handleAddActor(HttpExchange request) throws IOException, JSONException {
		if("PUT".equals(request.getRequestMethod())) {

			//starting the stream
			InputStreamReader i_SR = new InputStreamReader(request.getRequestBody(), StandardCharsets.UTF_8);

			BufferedReader bfr = new BufferedReader(i_SR);

			String line ; 
			StringBuilder sb  = new StringBuilder(); 

			while((line = bfr.readLine())!=null) {
				sb.append(line);
			}

			String requested_body = sb.toString();

			try {
				//parsing the json
				JSONObject json_obj = new JSONObject(requested_body);
				String actorId = json_obj.getString("actorId");
				String name = json_obj.getString("name");
				//adding the actor and stroing the boolean valuem for that
				boolean checker = actorController.addActor(actorId, name);


				//Setting the response using the checker and json
				String response_generator;

				if (checker==true) {
					response_generator = "Actor Added succesfully to the database";
				}
				else 
				{response_generator = "Failed to add the actor to the database";}


				sendResponse(request,checker ? 200 : 400, response_generator);

			}
			catch(Exception e) {
				e.printStackTrace();
				request.sendResponseHeaders(400, -1);
			}
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

		this.sendResponse(request, response ? 200 : 400, response ? "Movie added Successfully" : "Failed to add Movie");

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
			Actor actor = actorController.getActor(actorId);
			if(actor!=null){
				actor.addMovie(movieId);
				actorController.updateActor(actor);
			}
			Movie movie = movieController.getMovie(movieId);
			if (movie != null) {
				movie.addActor(actorId);
				movieController.updateMovie(movie);
			}
			else{
				sendResponse(request, 404, "Actor or movie not found");
			}

			this.sendResponse(request, 200, "Relationship [ACTED IN] added Successfully");
		} else {
			this.sendResponse(request, 400,"Failed to add Relationship");
		}
	}

	private void handleGetActor(HttpExchange request) throws IOException, JSONException {

		String query = request.getRequestURI().getQuery();
		String actorId = Utils.getQueryParameter(query, "actorId");


		if(actorId == null) {
			sendResponse(request, 400, "Missing Actor Id");
		}

		Actor actorobj = actorController.getActor(actorId);

		if(actorobj!=null) {
			String response = "{"
					+ "\"actorId\": \"" + actorobj.getActorId() + "\", "
					+ "\"name\": \"" + actorobj.getName() + "\", "
					+ "\"movies\": " + new JSONArray(actorobj.getMovies()).toString()
					+ "}";
			sendResponse(request, 200, response);
		}
		else {
			sendResponse(request, 404, "Actor not found");
		}

	}

	private void handleGetMovie(HttpExchange request) throws IOException, JSONException {
		String query = request.getRequestURI().getQuery();
		String movieId = Utils.getQueryParameter(query, "movieId");

		if(movieId == null){
			sendResponse(request, 400, "Missing Movie Id");
			return;
		}

		Movie movieOBJ = movieController.getMovie(movieId);
		if(movieOBJ!=null) {
			String response = "{"
					+ "\"movieId\": \"" + movieOBJ.getMovieId() + "\", "
					+ "\"name\": \"" + movieOBJ.getName() + "\", "
					+ "\"actors\": " + new JSONArray(movieOBJ.getActors()).toString()
					+ "}";
			sendResponse(request, 200, response);
		}
		else {
			sendResponse(request, 404, "Movie not found");
		}

	}

	private void handleHasRelationship(HttpExchange request) throws IOException{

		String query = request.getRequestURI().getQuery();
		String actorId = Utils.getQueryParameter(query, "actorId");
		String movieId = Utils.getQueryParameter(query, "movieId");

		if (actorId == null || actorId.isEmpty() || movieId == null || movieId.isEmpty()) {

			sendResponse(request, 400, "Missing actorId or movieId");
			return;
		}

		boolean hasRelationship = actorController.hasRelationship(actorId, movieId);

		if (hasRelationship) {

			sendResponse(request, 200, "Relationship exists between actor and movie");

		} else {

			sendResponse(request, 404, "No relationship exists between actor and movie");
		}
	}

	private void handleComputeBaconNumber(HttpExchange request) throws IOException {
		String query = request.getRequestURI().getQuery();
		String actorId = Utils.getQueryParameter(query, "actorId");

		if(actorId==null || actorId.isEmpty()){
			sendResponse(request, 400, "Missing actorId");
		}

		try{
			int baconNumber = actorController.computeBaconNumber(actorId);

			if(baconNumber<0){
				sendResponse(request, 404, "No path to Kevin Bacon found");
			}
			else {
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
			this.sendResponse(request, 400, "Invalid JSON format");
			return;
		}

		String actorId = json.optString("actorId", "").trim();

		// Validate actorId
		if (actorId.isEmpty()) {
			this.sendResponse(request, 400, "Missing actorId");
			return;
		}

		// Compute the Bacon Path
		List<String> baconPath;
		try {
			baconPath = actorController.computeBaconPath(actorId, "nm0000102");
		} catch (Exception e) {
			e.printStackTrace();
			this.sendResponse(request,500, "Internal Server Error");  // Internal Server Error
			return;
		}

		// Prepare the response
		if(baconPath==null || baconPath.isEmpty()) {
			this.sendResponse(request, 404, "No path Found");
		} else {
			JSONObject responseJson = new JSONObject();
			responseJson.put("baconPath", new JSONArray(baconPath));
			this.sendResponse(request, 200, responseJson.toString());
		}
	}

	private void handleAddMovieRating(HttpExchange request) throws IOException, JSONException{

		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getRequestBody(), StandardCharsets.UTF_8));
		StringBuilder requestBody = new StringBuilder();
		String line;


		while ((line = reader.readLine()) != null) {
			requestBody.append(line);
		}

		reader.close();

		// request body turned into JSON object: 
		JSONObject json = new JSONObject(requestBody.toString());
		String movieId = json.getString("movieId");
		double rating = json.getDouble("rating");

		if (rating<1 || rating>10) {
			sendResponse(request, 400, "Failed to add movie rating(must be between 1 and 10)");
			return;
		}

		Movie movie = movieController.getMovie(movieId);
		if (movie == null) {
			sendResponse(request, 404, "Movie not found");
			return;
		}

		// MovieController being called
		boolean success = movieController.addMovieRating(movieId, rating);

		// send response to client
		this.sendResponse(request, success ? 200 : 400, success ? "Successfully added movie rating of " + rating +"/10" : "Failed to add Movie rating");

	}

	private void handleGetAverageRating(HttpExchange request) throws IOException{
		String query = request.getRequestURI().getQuery();
		String actorId = Utils.getQueryParameter(query, "actorId");


		// Check if request is valid
		if(actorId == null || actorId.isEmpty()){
			sendResponse(request, 400, "Missing actorId");
			return;
		}

		try{
			Actor actor = actorController.getActor(actorId);
			if(actor==null){
				sendResponse(request, 404, "Actor not Found");
				return;
			}

			List<Movie> movies = actorController.getMoviesForActor(actorId);
			if(movies.isEmpty()){
				sendResponse(request, 404, "No movies found for actor");
				return;
			}

			//Calculate average rating
			double totalRating = 0;
			int count = 0;
			for(Movie movie : movies){
				Double rating = movie.getRating();
				if(rating!=null){
					totalRating+=rating;
					count++;
				}
			}

			if(count==0){
				sendResponse(request, 404, "No ratings found for the actor's movies");
			}

			double averageRating = totalRating/count;
			sendResponse(request, 200, "Average rating for Actor: " +actorId+ " is " +averageRating);
		} catch (Exception e){
			e.printStackTrace();
			sendResponse(request, 500, "Internal Sever Error");
		}
	}

	private void handleAddMovieBoxRevenue(HttpExchange request) throws IOException, JSONException{
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

		// checking if movie exists
		Movie movie = movieController.getMovie(movieId);
		if (movie == null) {
			sendResponse(request, 404, "Movie not found");
			return;
		}

		// MovieController being called to add box revenue property to movie
		boolean success = movieController.addMovieBoxRevenue(movieId, revenue);

		// Send response to client
		this.sendResponse(request, success ? 200 : 400, success ? "Successfully added movie box revenue "  : "Failed to add movie box revenue");
	}
	private void handleGetActorMoviesByBoxRevenue(HttpExchange request) throws IOException {

		String query = request.getRequestURI().getQuery();
		String actorId = Utils.getQueryParameter(query, "actorId");

		if (actorId == null || actorId.isEmpty()) {
			sendResponse(request, 400, "actorId Missing or Invalid");
			return;
		}

		try {
			List<Movie> movies = actorController.getActorMoviesByBoxRevenue(actorId);

			if (movies.isEmpty()) {
				sendResponse(request, 404, "No movies found for actor");
				return;
			}

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
