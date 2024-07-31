package ca.yorku.eecs;
import java.util.ArrayList; 
import java.util.List;
/**
 * Class displays an actor with id, name, and a list of movies they havce acted in. 
 */
public class Actor {

	private String actorId;
	private String name;
	private List<String> movies; 






	/**
	 * 
	 */
	public Actor(String actorId, String name){

		this.actorId = actorId;
		this.name = name;
		this.movies = new ArrayList<>();
	}


	public String getActorId(){return actorId;}


	public void setActorId(String actorId){

		this.actorId = actorId;
	}


	public String getName(){

		return name; 
		}
	
	
	public String setname(String name){

		return this.name = name; 
		}
	
	
	public List<String> getMovies(){
		return movies; 
	}
	
	public void addMovie() {
		
	}
	
	public void removeMovie() {movies.remove(movieId);}
	
	public String toString() {
		return actorId + " "+ " "+ name+ " "+ movies;
	}
	
}



