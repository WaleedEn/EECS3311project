package ca.yorku.eecs.dao;

import ca.yorku.eecs.model.Actor;
import ca.yorku.eecs.model.Movie;

import java.util.List;

/*
THE BOOK DATABASE

This class defines methods for database operations on actors.
How it works:
    - Methods for adding, updating, or retrieving actors.
    - Implemented by 'ActorDAOImp' to perform actual database work.

Example Role: Keeps a record of all the actors in the library. It has special methods to find out if an actor is already
in the library or to add a new one.
 */
public interface ActorDAO {
	boolean addActor(String actorId, String name );
    Actor getActor(String actorId);
    boolean addRelationship(String actorId, String movieId);
    boolean HasRelationship(String actorId, String movieId);
    int computeBaconNumber(String actorId);
    List<String> computeBaconPath(String actorId, String kevinBaconId);
	
	List<Movie> getActorMoviesByBoxRevenue(String actorId);

    public boolean updateActor(Actor actor);

}
