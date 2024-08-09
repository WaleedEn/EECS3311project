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
import ca.yorku.eecs.service.ActorService;
import ca.yorku.eecs.service.ActorServiceImp;
import org.neo4j.driver.v1.Driver;

public class ActorController {

    private final ActorService actorService;

    public ActorController(Driver driver){
        this.actorService = new ActorServiceImp(driver);
    }

    public boolean addActor(Actor actor){
        return actorService.addActor(actor);
    }

    public Actor getActor(String actorId){
        return actorService.getActor(actorId);
    }

    public String computeBaconNumber(String actorId){
        try {
            return actorService.computeBaconNumber(actorId);
        } catch (Exception e){
            e.printStackTrace();
            return "Internal server error occured";
        }
    }


}
