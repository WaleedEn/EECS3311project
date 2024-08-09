package ca.yorku.eecs.service;/*
THE BOOKKEEPER

This class contains business logic for actors.

How it Works:
    - Receives requests from the controller
    - Uses 'ActorDAO' to interact with the database.

Example Role: Knows how to handle requests related to actors. It gets instructions from the librarian (ActorController)
and figures out what needs to be done with the actors.
 */


import ca.yorku.eecs.dao.ActorDAO;
import ca.yorku.eecs.dao.ActorDAOImp;
import ca.yorku.eecs.model.Actor;
import org.neo4j.driver.v1.Driver;

public class ActorServiceImp implements ActorService {

    private final ActorDAO actorDAO;

    public ActorServiceImp(Driver driver){
        this.actorDAO = new ActorDAOImp(driver);
    }
    @Override
    public boolean addActor(Actor actor) {
        return actorDAO.addActor(actor);
    }

    @Override
    public Actor getActor(String actorId) {
        return actorDAO.getActor(actorId);
    }

    @Override
    public String addRelationship(String actorId1, String actorId2, String relationshipType) {
        return null;
    }

    @Override
    public String hasRelationship(String actorId, String movieId) {
        return null;
    }

    @Override
    public String computeBaconNumber(String actorId) {
        try{
            int baconNumber = actorDAO.computeBaconNumber(actorId);
            if(baconNumber==-1) return "Actor not found";
            return "Bacon number for Actor " + actorId + " is " + baconNumber;
        } catch (Exception e){
            e.printStackTrace();
            return "Internal server error occured";
        }
    }

    @Override
    public String computeBaconPath(String actorId) {
        return null;
    }

    @Override
    public double getAverageRating(String actorId) {
        return 0;
    }

}