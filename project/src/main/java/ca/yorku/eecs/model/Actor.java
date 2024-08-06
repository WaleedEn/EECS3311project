package ca.yorku.eecs.model;
import java.util.ArrayList; 
import java.util.List;

/**
 * Class displays an actor with id, name, and a list of movies they have acted in. 
 */
public class Actor {

	private String actorId;
	private String name;
	private List<Movie> movies; 
	private List<String> movieNames; 

	/**
	 * 
	 */
	public Actor(String actorId, String name){

		this.actorId = actorId;
		this.name = name;
		this.movies = new ArrayList<>();
		this.movieNames = new ArrayList<>();	}


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


	public List<Movie> getMovies(){
		return movies; 
	}

	public List<String> getMoviesNames(){
		return movieNames; 
	}



	public void addMovie(Movie movie) {

		if(movie!=null && !movies.contains(movie))

		{

			movies.add(movie);
			movieNames.add(movie.getName());

		}
	}

	public void remove(Movie movie) {

		if(movie!=null )

		{

			movies.remove(movie);
			movieNames.remove(movie.getName());

		}
	}



	@Override

	public String toString() {
		return "Actor [actorId=" + actorId + ", name=" + name + ", movies=" + movies + ", movieNames=" + movieNames
				+ "]";
	}

}



