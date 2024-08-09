package ca.yorku.eecs.dao;




import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.Values;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.Record;
import ca.yorku.eecs.model.Actor;
import ca.yorku.eecs.model.Movie;

public class ActorDAOImp implements ActorDAO{

	private Driver driver; 

	public ActorDAOImp(Driver driver) {
		// TODO Auto-generated constructor stub

		this.driver = driver; 
	}




	@Override
	public Actor getActor(String actorId) {

		Actor actor_OBJ = null;

		//----------------------------------------------------------

		try(Session session = driver.session()){
			String neo_LINE = "MATCH (a:Actor {actorId: $actorId}) RETURN a";
			StatementResult neo_RESULT = session.run(neo_LINE, Values.parameters("actorId",actorId)); 


			//----------------------------------------------------------

			if(neo_RESULT.hasNext()) {
				Node ndd = neo_RESULT.single().get("a").asNode(); 

				actor_OBJ = new Actor(ndd.get("actorId").asString(), ndd.get("name").asString());
				//----------------------------------------------------------

			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}










		//----------------------------------------------------------
		return actor_OBJ;


	}




	@Override
	public boolean addActor(String actorId, String name) {

		String neo_LINE = "MERGE (a:Actor {actorId: $actorId, name: $name}) RETURN a";


		//----------------------------------------------------------

		try(Session session = driver.session()){


			Transaction tx = session.beginTransaction();

			StatementResult neo_RESULT = tx.run(neo_LINE, Values.parameters("actorId",actorId, "name", name)); 


			//----------------------------------------------------------

			boolean actoradded_CHEck = neo_RESULT.hasNext();

			tx.success();

			return actoradded_CHEck;
		}
		catch(Exception e) {
			e.printStackTrace();
			return false; 
		}










		//----------------------------------------------------------

	}




	@Override
	public List<Movie> getActorMoviesByBoxRevenue(String actorId){
		List<Movie> movie_list = new ArrayList<>();

		String neo_line = "MATCH (a: Actor {actorId: $actorId})-[:ACTED_IN]->(m:Movie) " + "RETURN m.movieId AS movieId, m.name AS name, m.revenue AS revenue " + "ORDER BY m.revenue ASC" ;

		try (Session session = driver.session()){

			StatementResult neo_RESULT = session.run(neo_line, Values.parameters("actorId",actorId)); 

			
			while (neo_RESULT.hasNext()){
				Record recorder = neo_RESULT.next(); 
				String mvd = recorder.get("movieId").asString();
				String nme = recorder.get("name").asString();
				int rev = recorder.get("revenue").asInt();
				
				
				Movie mv = new Movie(mvd, nme, rev, 0);
				
				
				movie_list.add(mv);
				

			}

			


		}
		catch(Exception e) {
			e.printStackTrace();
			
		}


		return movie_list; 
	}



}
