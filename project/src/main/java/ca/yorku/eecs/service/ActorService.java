package ca.yorku.eecs.service;

import ca.yorku.eecs.model.Actor;
import ca.yorku.eecs.model.Movie;

import java.util.List;

public interface ActorService {
    boolean addActor(String actorId, String name );
    Actor getActor(String actorId);
    boolean addRelationship(String actorId, String movieId);
    boolean hasRelationship(String actorId, String movieId);
    int computeBaconNumber(String actorId);
    List<String> computeBaconPath(String actorId, String kevinBaconId);
    double getAverageRating(String actorId);
	List<Movie> getActorMoviesByBoxRevenue(String actorId);

    boolean updateActor(Actor actor);
    List<Movie> getMoviesForActor(String actorId);
}