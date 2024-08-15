package ca.yorku.eecs.dao;

import ca.yorku.eecs.Neo4jConfig;
import ca.yorku.eecs.model.Actor;
import ca.yorku.eecs.model.Movie;

import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;

import static org.neo4j.driver.v1.Values.parameters;

import java.awt.image.AreaAveragingScaleFilter;
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

        String checkActorQuery = "MATCH (a:Actor {id: $id}) RETURN a";

        String addActorQuery = "MERGE (a:Actor {id: $id}) " +
                "SET a.name = $name, " +
                "a.movies = COALESCE(a.movies, []) " +
                "RETURN a";

		try(Session session = driver.session()){
			Transaction tx = session.beginTransaction();

			StatementResult neo_RESULT = tx.run(checkActorQuery, Values.parameters("id",actorId, "name", name));

            if(neo_RESULT.hasNext()){
                tx.success();
                return false;
            }

            StatementResult addResult = tx.run(addActorQuery, Values.parameters("id", actorId, "name", name));
			tx.success();


			return addResult.hasNext();
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

    @Override
    public Actor getActor(String actorId) {

		Actor actor_OBJ = null;

		//----------------------------------------------------------

		try(Session session = driver.session()){
			String neo_LINE = "MATCH (a:Actor {id: $actorId}) RETURN a";
			StatementResult neo_RESULT = session.run(neo_LINE, Values.parameters("actorId",actorId));

			if(neo_RESULT.hasNext()) {
				Node ndd = neo_RESULT.single().get("a").asNode();

                List<String> movies = new ArrayList<>();
                if(ndd.containsKey("movies")){
                    movies = ndd.get("movies").asList(Value::asString);
                }
                String id = ndd.get("id").asString();
                String name = ndd.get("name").asString();
				actor_OBJ = new Actor(id, name);

                for(String movieId : movies){
                    actor_OBJ.addMovie(movieId);
                }
                //----------------------------------------------------------
			}

		} catch(Exception e) {
			e.printStackTrace();
		}
		//----------------------------------------------------------
		return actor_OBJ;
	}

    @Override
    public boolean addRelationship(String actorId, String movieId) {
        try (Session session = driver.session()) {
            // Start a transaction
            try (Transaction tx = session.beginTransaction()) {
                // Check if the relationship already exists
                String checkQuery = "MATCH (a:Actor {id: $actorId})-[r:ACTED_IN]->(m:Movie {id: $movieId}) RETURN r";
                StatementResult checkResult = tx.run(checkQuery, Values.parameters("actorId", actorId, "movieId", movieId));

                if (checkResult.hasNext()) {
                    // Relationship already exists
                    System.out.println("Relationship already exists");
                    tx.failure(); // Indicate the transaction should be rolled back
                    return false; // Relationship already exists
                }

                // Create or update the relationship
                String relationshipQuery = "MATCH (a:Actor {id: $actorId}), (m:Movie {id: $movieId}) " +
                        "MERGE (a)-[:ACTED_IN]->(m)";
                tx.run(relationshipQuery, Values.parameters("actorId", actorId, "movieId", movieId));

                // Update the actor's movie list
                String updateActorQuery = "MATCH (a:Actor {id: $actorId}) " +
                        "SET a.movies = COALESCE(a.movies, []) " +
                        "WITH a, a.movies + [$movieId] AS updatedMovies " +
                        "SET a.movies = updatedMovies";
                tx.run(updateActorQuery, Values.parameters("actorId", actorId, "movieId", movieId));

                // Update the movie's actor list
                String updateMovieQuery = "MATCH (m:Movie {id: $movieId}) " +
                        "SET m.actors = COALESCE(m.actors, []) " +
                        "WITH m, m.actors + [$actorId] AS updatedActors " +
                        "SET m.actors = updatedActors";
                tx.run(updateMovieQuery, Values.parameters("movieId", movieId, "actorId", actorId));

                tx.success(); // Commit the transaction
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean hasRelationship(String actorId, String movieId) {

        try (Session session = driver.session()) {

            //check for relationship 
            String query = "MATCH (a:Actor {id: $actorId})-[:ACTED_IN]->(m:Movie {id: $movieId}) RETURN a, m";
            StatementResult result = session.run(query, Values.parameters("actorId", actorId, "movieId", movieId));
    
            return result.hasNext();

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int computeBaconNumber(String actorId) {

        final String KEVINBACON_ID = "nm0000102";

        if(actorId.equals(KEVINBACON_ID)) return 0;

        try(Session session = driver.session()) {
            String query = "MATCH path = shortestPath((a:Actor {id: $actorId})-[:ACTED_IN*]-(b:Actor {id: $kevinBaconId})) " +
                    "RETURN length(path) AS baconNumber";

            StatementResult result = session.run(query, Values.parameters("actorId", actorId, "kevinBaconId", KEVINBACON_ID));

            if (result.hasNext()) {
                Record record = result.next();
                return record.get("baconNumber").asInt()/2;
            } else{
                return -1;
            }
        } catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public List<String> computeBaconPath(String actorId) {
        List<String> baconPath = new ArrayList<>();

        final String KEVINBACON_ID = "nm0000102";

        if(actorId.equals(KEVINBACON_ID)){
            baconPath.add(KEVINBACON_ID);
            return baconPath;
        }

        try (Session session = driver.session()) {
            // cypher query to get the shortest path between the actor and Kevin Bacon
            String query = "MATCH p=shortestPath((a:Actor {id: $actorId})-[*]-(b:Actor {id: $kevinBaconId})) " +
                    "RETURN [node IN nodes(p) | " +
                    "  CASE " +
                    "    WHEN 'Actor' IN labels(node) THEN node.id " +
                    "    WHEN 'Movie' IN labels(node) THEN node.id " +
                    "    ELSE 'Unknown' " +
                    "  END] AS idList";

            StatementResult result = session.run(query, Values.parameters("actorId", actorId, "kevinBaconId", KEVINBACON_ID));

            if(result.hasNext()){
                Record record = result.next();
                List<Object> idList = record.get("idList").asList();
                for(Object id : idList){
                    if(id!=null) baconPath.add(id.toString());
                }
            } else{
                return new ArrayList<>();
            }

            if (!baconPath.contains(KEVINBACON_ID)) {
                baconPath.add(KEVINBACON_ID);
            }
            return baconPath;

        } catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Movie> getActorMoviesByBoxRevenue(String actorId){
        List<Movie> movie_list = new ArrayList<>();

        String neo_line = "MATCH (a:Actor {id: $actorId}) " +
                "WITH a.movies AS movieIds " +
                "UNWIND movieIds AS movieId " +
                "MATCH (m:Movie {id: movieId}) " +
                "RETURN m.id AS movieId, m.name AS name, m.revenue AS revenue " +
                "ORDER BY m.revenue ASC";

        try (Session session = driver.session()){
            StatementResult neo_RESULT = session.run(neo_line, Values.parameters("actorId",actorId));

            while (neo_RESULT.hasNext()){
                Record recorder = neo_RESULT.next();
                String mvd = recorder.get("movieId").asString();
                String nme = recorder.get("name").asString();
                int rev = recorder.get("revenue").asInt();

                Movie mv = new Movie(mvd, nme, rev, rev);
                movie_list.add(mv);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return movie_list;
    }
    @Override
    public boolean updateActor(Actor actor){
        try(Session session = driver.session()){
            String query = "MATCH (a:Actor {id: $actorId}) SET a.movies = $movies";
            List<String> movies = actor.getMovies();
            session.run(query, Values.parameters("actorId", actor.getActorId(), "movies", movies));
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public boolean deleteAll(){
        try(Session session = driver.session()){
            session.run("MATCH (n) DETACH DELETE n");
            return true;
        } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
    }
}
