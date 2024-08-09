package ca.yorku.eecs.dao;

import ca.yorku.eecs.Neo4jConfig;
import ca.yorku.eecs.model.Actor;
import ca.yorku.eecs.model.Movie;

import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;

import static org.neo4j.driver.v1.Values.parameters;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
THE BOOK DATABASE ASSISTANT

Implements the 'ActorDAO' interface to interact with the Neo4J database
How it works:
    - Uses Neo4j queries to perform operations like adding an actor to the database.

Example Role: Actually goes into the database and adds or finds the actor. Uses specific tools to interact with the
library's database.
 */
public class ActorDAOImp implements ActorDAO {

    private final Driver driver;

    public ActorDAOImp(Driver driver){
        this.driver = driver;
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
    public boolean addRelationship(String actorId, String movieId) {
        try (Session session = driver.session()) {
            String query = "MATCH (a:Actor {id: $actorId}), (m:Movie {id: $movieId}) " +
                    "MERGE (a)-[:ACTED_IN]->(m)";
            StatementResult result = session.run(query,
                    Values.parameters("actorId", actorId, "movieId", movieId));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean HasRelationship(String actorId, String movieId) {
        return false;
    }

    @Override
    public int computeBaconNumber(String actorId) {
        try(Session session = driver.session()) {
            String query = "MATCH (a:Actor {name: $actorName}), (b:Actor {name: 'Kevin Bacon'}) " +
                    "CALL algo.shortestPath.stream(a, b, 'ACTED_IN') " +
                    "YIELD nodeId, cost " +
                    "RETURN count(nodeId) AS baconNumber";
            StatementResult result = session.run(query, Collections.singletonMap("actorId", actorId));

            if (result.hasNext()) {
                return result.single().get("baconNumber").asInt();
            }
            return -1;
        } catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public List<String> computeBaconPath(String actorId, String kevinBaconId) {
        List<String> listofPath = new ArrayList<>();
        try (Session session = driver.session()) {
            String query = "MATCH p=shortestPath((a:Actor {id: $actorId})-[:ACTED_IN*..$int]-(b:Actor {id: $kevinBaconId})) " +
                    "RETURN p";
            StatementResult result = session.run(query, Values.parameters("actorId", actorId, "int", Integer.MAX_VALUE, "kevinBaconId", kevinBaconId));

            if (result.hasNext()) {
                Path path = result.next().get("p").asPath();
                for (Node node : path.nodes()) {
                    listofPath.add(node.get("id").asString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listofPath;
    }

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
