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

public class ActorServicesImp implements ActorService {

    private final ActorDAO actorDAO = new ActorDAOImp();

    @Override
    public boolean addActor(String actorId, String name) {
        return false;
    }

    @Override
    public String getActorsByBaconNumber(int limit) {
        return null;
    }
}