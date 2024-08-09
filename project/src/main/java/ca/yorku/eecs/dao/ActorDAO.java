package ca.yorku.eecs.dao;

import java.util.List;

import ca.yorku.eecs.model.Actor;
import ca.yorku.eecs.model.Movie;

public interface ActorDAO {
	
	
    Actor getActor(String actorId);
	boolean addActor(String actorId, String name); 
	List<Movie> getActorMoviesByBoxRevenue(String actorId); 
}
