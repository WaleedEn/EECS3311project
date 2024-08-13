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
import ca.yorku.eecs.dao.ActorDAO;
import ca.yorku.eecs.dao.ActorDAOImp;
import ca.yorku.eecs.dao.MovieDAO;
import ca.yorku.eecs.dao.MovieDAOImp;
import ca.yorku.eecs.model.Actor;
import ca.yorku.eecs.model.Movie;
import org.neo4j.driver.v1.Driver;

import java.util.List;

public class ActorController {

    private final ActorDAO actorDAO;
    private final MovieDAO movieDAO;

    public ActorController(Driver driver){
        this.actorDAO = new ActorDAOImp(driver);
        this.movieDAO = new MovieDAOImp(driver);
    }

	public boolean addActor(String actorId, String name) {
        return actorDAO.addActor(actorId,name);
	}

    public Actor getActor(String actorId){
        return actorDAO.getActor(actorId);
    }

    public boolean addRelationship(String actorId, String movieId){
        return actorDAO.addRelationship(actorId, movieId);
    }

    public boolean hasRelationship(String actorId, String movieId){
        return actorDAO.hasRelationship(actorId, movieId);
    }

    public int computeBaconNumber(String actorId){
        return actorDAO.computeBaconNumber(actorId);
    }

    public List<String> computeBaconPath(String actorId){
        return actorDAO.computeBaconPath(actorId);
    }

    public double getAverageRating(String actorId){
        return actorDAO.getAverageRating(actorId);
    }

    public List<Movie> getActorMoviesByBoxRevenue(String actorId){
    	return actorDAO.getActorMoviesByBoxRevenue(actorId);
    }
    public boolean updateActor(Actor actor) {
        return actorDAO.updateActor(actor);
    }

    public List<Movie> getMoviesForActor(String actorId){
        return movieDAO.getMoviesForActor(actorId);
    }
}
