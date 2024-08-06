package ca.yorku.eecs;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import ca.yorku.eecs.controller.ActorController;
import ca.yorku.eecs.controller.MovieController;
import ca.yorku.eecs.service.ActorService;
import ca.yorku.eecs.service.MovieServices;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.neo4j.driver.v1.Driver;

public class APIHandler implements HttpHandler {

	private Neo4jConfig neo4jConfig;
	private ActorController actorController;
	private MovieController movieController;

	public APIHandler(Neo4jConfig neo4jConfig){
		this.neo4jConfig = neo4jConfig;
		Driver driver = neo4jConfig.getDriver();
		this.actorController = new ActorController(neo4jConfig.getDriver());
		this.movieController = new MovieController(neo4jConfig.getDriver());
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
			} else if (path.startsWith("api/v1/updateMovieRating") && method.equals("PUT")) {
				handleUpdateMovieRating(exchange);
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


}
