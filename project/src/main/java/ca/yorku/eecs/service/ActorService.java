package ca.yorku.eecs.service;

public interface ActorService {
    boolean addActor(String actorId, String name);
    String getActorsByBaconNumber(int limit);

}