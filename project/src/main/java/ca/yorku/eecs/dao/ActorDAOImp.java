package ca.yorku.eecs.dao;

import ca.yorku.eecs.Neo4jConfig;

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

    private final Neo4jConfig neo4jConfig = new Neo4jConfig();
    @Override
    public boolean addActor(String name, String actorId) {
        return false;
    }

    @Override
    public double getAverageMovieRating(String actorId) {
        return 0;
    }

    @Override
    public int computeBaconNumber(String actorId) {
        return 0;
    }

    @Override
    public List<String> computeBaconpath(String actorId) {
        return null;
    }

    @Override
    public boolean HasRelationship(String actorId, String movieId) {
        return false;
    }
}
