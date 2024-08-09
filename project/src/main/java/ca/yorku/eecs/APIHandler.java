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
import ca.yorku.eecs.service.ActorServices;
import ca.yorku.eecs.service.MovieServices;
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
		if("PUT".equals(exchange.getRequestMethod())) {

			//starting the stream
			InputStreamReader i_SR = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);

			BufferedReader bfr = new BufferedReader(i_SR);

			String line ; 
			StringBuilder sb  = new StringBuilder(); 

			while((line = bfr.readLine())!=null) {
				sb.append(line);
			}

			//------------------------------------------------------------------------------

			String requested_body = sb.toString();

			//----------------------------------------------------------------------

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


				exchange.sendResponseHeaders(checker ? 200 : 400, response_generator.getBytes().length);

				//closing the stream
				OutputStream OUT_Stream = exchange.getResponseBody();
				OUT_Stream.write(response_generator.getBytes());
				OUT_Stream.close(); 



			}
			catch(Exception e) {
				e.printStackTrace();
				exchange.sendResponseHeaders(400, -1);// BAD REQUEST SECTION
			}



		}
		else {

			exchange.sendResponseHeaders(405, -1); //error for when the method constraint is not allowed 

		}





	}




	private void handleAddMovie(HttpExchange exchange) {
	}

	private void handleAddRelationship(HttpExchange exchange) {
	}

	private void handleGetActor(HttpExchange exchange) throws IOException, JSONException {

		String actorId = null; 
		String neo_query_line = exchange.getRequestURI().getQuery();



		if(neo_query_line!=null) {
			String [] splitted = neo_query_line.split("&");
			for(String smaller :splitted ) {
				String[] final_state = smaller.split("=");

				if(final_state.length==2 && final_state[0].equals("actorId")) {
					actorId = final_state[1];
					break;
				}
			}
		}


		if(actorId == null) {
			exchange.sendResponseHeaders(400, -1);
			return; 
		}

		Actor actorobj = actorController.getActor(actorId);  


		if(actorobj!=null) {
			JSONObject jr = new JSONObject();

			jr.put("actorId", actorobj.getActorId());
			jr.put("name", actorobj.getName());
			jr.put("movies", actorobj.getMovies());



			String rr = jr.toString();

			//exchange.getResponseHeaders().set("Content-Type", "application/json");


			exchange.sendResponseHeaders(200,rr.length());


			try(OutputStream OUT_stream = exchange.getResponseBody()){
				OUT_stream.write(rr.getBytes(StandardCharsets.UTF_8));
			}

		}
		else {
			exchange.sendResponseHeaders(404, -1);
		}

	}

	private void handleGetMovie(HttpExchange exchange)  {
	}

	private void handleHasRelationship(HttpExchange exchange) {
	}

	private void handleComputeBaconNumber(HttpExchange exchange) {
	}

	private void handleComputerBaconPath(HttpExchange exchange) {
	}

	private void handleAddMovieRating(HttpExchange exchange) {
	}

	private void handleGetAverageRating(HttpExchange exchange) {
	}

	private void handleAddMovieBoxRevenue(HttpExchange exchange) {
	}

	private void handleGetActorMoviesByBoxRevenue(HttpExchange exchange) throws IOException {
		
		String actorId = null;
		String neo_query_line = exchange.getRequestURI().getQuery();
		
		if(neo_query_line!=null) {
			String [] splitted = neo_query_line.split("&");
			for(String smaller :splitted ) {
				String[] final_state = smaller.split("=");

				if(final_state.length==2 && final_state[0].equals("actorId")) {
					actorId = final_state[1];
					break;
				}
			}
		}
		
		if(actorId == null) {
			exchange.sendResponseHeaders(400, -1);
			return; 
		}
		
		
		try {
			List<Movie> mvs = actorController.getActorMoviesByBoxRevenue(actorId);
			
			if(mvs.isEmpty()==false) {
				//coverting movie array to JSON array
				JSONArray jsonMVS = new JSONArray();
				
				for (Movie m: mvs) {
					
					JSONObject jsonMV = new JSONObject();

					jsonMV.put("actorId", m.getMovieId());
					jsonMV.put("name", m.getName());
					jsonMV.put("movies", m.getRevenue());
					
					jsonMVS.put(jsonMV);
				}
				
				String response_container = jsonMVS.toString();
				
				exchange.sendResponseHeaders(200, response_container.length());
				
				
				try(OutputStream OUT_stream = exchange.getResponseBody()){
					OUT_stream.write(response_container.getBytes(StandardCharsets.UTF_8));
				}
				
				
				
				
			}
			else {
				exchange.sendResponseHeaders(404, -1);//whne no movies found 
			}
			
			
		}
		catch(Exception e) {
			e.printStackTrace();
			exchange.sendResponseHeaders(500, -1);//Internal Server errror
		}
		
		
		
		
	}


}