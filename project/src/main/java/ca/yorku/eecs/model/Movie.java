package ca.yorku.eecs.model;

import java.util.ArrayList;
import java.util.List;

public class Movie {
    private String movieId;
    private String name;
    private List<String> actors;
    private double rating;
    private double revenue;


    public Movie() {
        actors = new ArrayList<>();
    }

    public Movie(String id, String name) {
        this.movieId = id;
        this.name = name;
    }

    public Movie(String id, String name, double rating, double revenue){
        this.movieId = id;
        this.name = name;
        this.rating = rating;
        this.revenue = revenue;
    }
    public String getMovieId() {
        return movieId;
    }

    public String getName() {
        return name;
    }

    public double getRating() {
        return rating;
    }
    public double getRevenue() {
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

