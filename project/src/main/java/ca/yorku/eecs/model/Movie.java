package ca.yorku.eecs;

import java.util.ArrayList;
import java.util.List;

public class Movie {
    private String movieId;
    private String name;
    private List<String> actors;
    private int rating;
    private int revenue;


    public Movie() {
        actors = new ArrayList<>();
    }

    public Movie(String id, String name, int revenue, int rating) {
        this.movieId = id;
        this.name = name;
        this.revenue = revenue;
        this.rating = rating;
    }

    public String getMovieId() {
        return movieId;
    }


    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }

    public int getRevenue() {
        return revenue;
    }

    public List<String> getActors() {
        return actors;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMovieId(String id) {
        this.movieId = id;
    }

    public void setActors(List<String> actors) {
        this.actors = actors;
    }


    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }
}

