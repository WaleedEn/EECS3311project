package ca.yorku.eecs.dao;

import ca.yorku.eecs.Neo4jConfig;
import ca.yorku.eecs.model.Actor;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;

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
    public boolean addActor(Actor actor) {
        try (Session session = driver.session()) {
            String query = "CREATE (a:Actor {id: $id, name: $name})";
            session.run(query, Values.parameters("id", actor.getActorId(), "name", actor.getName()));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Actor getActor(String actorId) {
        try (Session session = driver.session()) {
            String query = "MATCH (a:Actor {id: $id})-[:ACTED_IN]->(m:Movie) " +
                    "RETURN a.id AS id, a.name AS name, collect(m.id) AS movies";
            StatementResult result = session.run(query, Collections.singletonMap("id", actorId));

            // Check if the result contains any records
            if (result.hasNext()) {
                String id = result.next().get("id").asString();
                String name = result.next().get("name").asString();
                List<String> movieIds = result.next().get("movies").asList(Value::asString);

                if(movieIds==null) movieIds=new ArrayList<>();
                return new Actor(id, name, movieIds);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean addRelationship(String actorId, String movieId) {
        return false;
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
    public String computeBaconPath(String actorId) {
        try (Session session = driver.session()) {
            String query = "MATCH (start:Actor {actorId: $actorId}), (bacon:Actor {actorId: 'nm0000102'}) " +
                    "MATCH path = shortestPath((start)-[:ACTED_IN*]-(bacon)) " +
                    "RETURN [node IN nodes(path) | node.actorId] AS baconPath";

            StatementResult result = session.run(query, parameters("actorId", actorId));
            if (result.hasNext()) {
                return "Bacon Path: " + result.single().get("baconPath").asList().toString();
            }
            return "No path to Kevin Bacon";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred";
        }
    }
}
