package ca.yorku.eecs.service;

import ca.yorku.eecs.model.Actor;

public interface ActorService {
    boolean addActor(Actor actor);
    Actor getActor(String actorId);
    String addRelationship(String actorId1, String actorId2, String relationshipType);
    String hasRelationship(String actorId, String movieId);
    String computeBaconNumber(String actorId);
    String computeBaconPath(String actorId);
    double getAverageRating(String actorId);

}