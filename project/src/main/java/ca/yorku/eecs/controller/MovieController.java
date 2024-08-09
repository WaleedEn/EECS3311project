package ca.yorku.eecs.controller;

import ca.yorku.eecs.model.Movie;
import ca.yorku.eecs.service.MovieService;
import ca.yorku.eecs.service.MovieServiceImp;
import org.neo4j.driver.v1.Driver;

public class MovieController {

    private final MovieService movieService;
    public MovieController(Driver driver){
        this.movieService = new MovieServiceImp(driver);
    }
    public boolean addMovie(Movie movie) {
        return movieService.addMovie(movie);
    }

    public Movie getMovie(String movieId) {
        return movieService.getMovie(movieId);
    }
    public boolean addMovieRating(String id, double rating) {
        return movieService.addMovieRating(id, rating);
    }
    public boolean addMovieBoxRevenue(String id, double boxRevenue) {
        return movieService.addMovieBoxRevenue(id, boxRevenue);
    }
}
