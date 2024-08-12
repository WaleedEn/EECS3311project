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
import ca.yorku.eecs.dao.MovieDAO;
import ca.yorku.eecs.dao.MovieDAOImp;
import ca.yorku.eecs.model.Actor;
import ca.yorku.eecs.model.Movie;

import org.neo4j.driver.v1.Driver;

import java.util.List;

public class ActorServiceImp implements ActorService {

    private final ActorDAO actorDAO;
    private final MovieDAO movieDAO;

    public ActorServiceImp(Driver driver){
        this.actorDAO = new ActorDAOImp(driver);
        this.movieDAO = new MovieDAOImp(driver);
    }
    @Override
    public boolean addActor(String actorId , String name) {
		// TODO Auto-generated method stub
		return actorDAO.addActor(actorId,name);
	}

    @Override
    public Actor getActor(String actorId) {
        return actorDAO.getActor(actorId);
    }

    @Override
    public boolean addRelationship(String actorId, String movieId) {
        return actorDAO.addRelationship(actorId, movieId);
    }

    @Override
    public boolean hasRelationship(String actorId, String movieId) {
        return actorDAO.hasRelationship(actorId, movieId);
    }

    @Override
    public int computeBaconNumber(String actorId) {
        return actorDAO.computeBaconNumber(actorId);
    }

    @Override
    public List<String> computeBaconPath(String actorId, String kevinBaconId) {
        return actorDAO.computeBaconPath(actorId, kevinBaconId);
    }
    @Override
    public double getAverageRating(String actorId) {
        return 0;
    }
	@Override
	public List<Movie> getActorMoviesByBoxRevenue(String actorId) {
		return actorDAO.getActorMoviesByBoxRevenue(actorId);
	}
    @Override
    public boolean updateActor(Actor actor) {
        return actorDAO.updateActor(actor);
    }
    @Override
    public List<Movie> getMoviesForActor(String actorId) {
        return movieDAO.getMoviesForActor(actorId);
    }

}