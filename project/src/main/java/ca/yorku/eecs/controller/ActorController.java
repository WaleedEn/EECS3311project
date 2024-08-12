package ca.yorku.eecs.controller;
/*
THE LIBRARIAN

This class Handles incoming HTTP requests related to actors.

How it works:
    - Receives request from the server.
    - Calls the 'ActorService' to perform actions (like adding an actor)
    - Sends back an HTTP response based on the result.

Example role: When you come to the library with a request, like "I want to add a new actor," the librarian
(this is 'ActorController') listens to you. It takes your request and hands it over to the bookkeeper to get the job done.

 */

import ca.yorku.eecs.Neo4jConfig;
import ca.yorku.eecs.model.Actor;
import ca.yorku.eecs.model.Movie;
import ca.yorku.eecs.service.ActorService;
import ca.yorku.eecs.service.ActorServiceImp;
import org.neo4j.driver.v1.Driver;

import java.util.List;

public class ActorController {

    private final ActorService actorService;

    public ActorController(Driver driver){
        this.actorService = new ActorServiceImp(driver);
    }

	public boolean addActor(String actorId, String name) {
		return actorService.addActor(actorId, name);
		
	}

    public Actor getActor(String actorId){
        return actorService.getActor(actorId);
    }

    public boolean addRelationship(String actorId, String movieId){
        return actorService.addRelationship(actorId, movieId);
    }

    public boolean hasRelationship(String actorId, String movieId){
        return actorService.hasRelationship(actorId, movieId);
    }

    public String computeBaconNumber(String actorId){
        try {
            return actorService.computeBaconNumber(actorId);
        } catch (Exception e){
            e.printStackTrace();
            return "Internal server error occured";
        }
    }

    public List<String> computeBaconPath(String actorId, String kevinBaconId){
        return actorService.computeBaconPath(actorId, kevinBaconId);
    }

    public String getAverageRating(String actorId){
        return null;
    }

    public List<Movie> getActorMoviesByBoxRevenue(String actorId){
    	// TODO Auto-generated method stub
    	return actorService.getActorMoviesByBoxRevenue(actorId);
    }
    public boolean updateActor(Actor actor) {
        return actorService.updateActor(actor);
    }

    public List<Movie> getMoviesForActor(String actorId){
        return actorService.getMoviesForActor(actorId);
    }
}
