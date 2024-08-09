package ca.yorku.eecs.model;

import java.util.ArrayList;
import java.util.List;

public class Actor {

    private String actorId;
    private String name;
    private List<String> movies;

    public Actor(String actorId, String name){

        this.actorId = actorId;
        this.name = name;
        this.movies = new ArrayList<>();
    }

    public Actor(String id, String name, List<String> movies) {
        this.actorId = id;
        this.name = name;
        this.movies = movies;
    }

    public String getActorId(){return actorId;}


    public void setActorId(String actorId){
        this.actorId = actorId;
    }


    public String getName(){
        return name;
    }

    public void setName(String name){

        this.name = name;
    }

    public List<String> getMovies(){
        return movies;
    }

    public void addMovie(String movieId) {
        if(movieId!=null && !movies.contains(movieId))
        {
            movies.add(movieId);
        }
    }

    @Override
    public String toString() {
        return "Actor{" +
                "actorId='" + actorId + '\'' +
                ", name='" + name + '\'' +
                ", movies=" + movies +
                '}';
    }
}