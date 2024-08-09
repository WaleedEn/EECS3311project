package ca.yorku.eecs.service;

import ca.yorku.eecs.model.Actor;
import ca.yorku.eecs.model.Movie;

import java.util.List;

public interface ActorService {
    boolean addActor(String actorId, String name );
    Actor getActor(String actorId);
    boolean addRelationship(String actorId, String movieId);
    String hasRelationship(String actorId, String movieId);
    String computeBaconNumber(String actorId);
    List<String> computeBaconPath(String actorId, String kevinBaconId);
    double getAverageRating(String actorId);
	List<Movie> getActorMoviesByBoxRevenue(String actorId);

}