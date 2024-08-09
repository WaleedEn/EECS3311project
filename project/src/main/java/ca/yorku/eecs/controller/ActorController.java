package ca.yorku.eecs.controller;

import java.util.List;

import org.neo4j.driver.v1.Driver;

import ca.yorku.eecs.dao.ActorDAOImp;
import ca.yorku.eecs.model.Actor;
import ca.yorku.eecs.model.Movie;
import ca.yorku.eecs.service.ActorServices;

public class ActorController {
	
	private ActorServices actor_Servicing;
	
	
	public ActorController(Driver driver) {
		this.actor_Servicing = new ActorServices(driver);
		
		
	}
	
	public boolean addActor(String actorId, String name) {
		return actor_Servicing.addActor(actorId, name);
		
	}

	public Actor getActor(String actorId) {
		// TODO Auto-generated method stub
		return actor_Servicing.getActor(actorId);
	}

	
	public List<Movie> getActorMoviesByBoxRevenue(String actorId) {
		// TODO Auto-generated method stub
		return actor_Servicing.getActorMoviesByBoxRevenue(actorId);
		}
	

	
	
	
    
}