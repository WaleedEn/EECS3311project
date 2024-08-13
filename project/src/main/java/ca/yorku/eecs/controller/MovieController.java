package ca.yorku.eecs.controller;

import ca.yorku.eecs.dao.MovieDAO;
import ca.yorku.eecs.dao.MovieDAOImp;
import ca.yorku.eecs.model.Movie;
import org.neo4j.driver.v1.Driver;

public class MovieController {

    private final MovieDAO movieDAO;
    public MovieController(Driver driver){
        this.movieDAO = new MovieDAOImp(driver);
    }
    public boolean addMovie(Movie movie) {
        return movieDAO.addMovie(movie);
    }

    public Movie getMovie(String movieId) {
        return movieDAO.getMovie(movieId);
    }
    public boolean addMovieRating(String id, double rating) {
        return movieDAO.addMovieRating(id, rating);
    }
    public boolean addMovieBoxRevenue(String id, double boxRevenue) {
        return movieDAO.addMovieBoxOffice(id, boxRevenue);
    }

    public boolean updateMovie(Movie movie){
        return movieDAO.updateMovie(movie);
    }
}
