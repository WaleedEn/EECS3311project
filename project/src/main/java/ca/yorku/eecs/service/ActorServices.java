package ca.yorku.eecs.service;

import java.util.List;

import org.neo4j.driver.v1.Driver;

import ca.yorku.eecs.dao.ActorDAO;
import ca.yorku.eecs.dao.ActorDAOImp;
import ca.yorku.eecs.model.Actor;
import ca.yorku.eecs.model.Movie;

public class ActorServices{
	
	private ActorDAO actorDAO;

	public ActorServices(Driver driver) {
		// TODO Auto-generated constructor stub
		 this.actorDAO = new ActorDAOImp(driver);
	}

	public boolean addActor(String actorId , String name) {
		// TODO Auto-generated method stub
		return actorDAO.addActor(actorId,name);
	}

	public Actor getActor(String actorId) {
		// TODO Auto-generated method stub
		return actorDAO.getActor(actorId);
		}
	
	public List<Movie> getActorMoviesByBoxRevenue(String actorId) {
		// TODO Auto-generated method stub
		return actorDAO.getActorMoviesByBoxRevenue(actorId);
		}
	
	
	
	
	
}