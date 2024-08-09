package ca.yorku.eecs.service;

import ca.yorku.eecs.dao.MovieDAO;
import ca.yorku.eecs.dao.MovieDAOImp;
import ca.yorku.eecs.model.Movie;
import ca.yorku.eecs.service.MovieService;
import org.neo4j.driver.v1.Driver;

public class MovieServiceImp implements MovieService{

    private final MovieDAO movieDAO;

    public MovieServiceImp(Driver driver) {
        this.movieDAO = new MovieDAOImp(driver);
    }
    @Override
    public boolean addMovie(Movie movie) {
       return movieDAO.addMovie(movie);
    }

    @Override
    public Movie getMovie(String movieId) {
        return null;
    }

    @Override
    public boolean addMovieRating(String id, double rating) {
        return movieDAO.addMovieRating(id, rating);
    }
    @Override
    public boolean addMovieBoxRevenue(String id, double revenue) {
        return movieDAO.addMovieBoxOffice(id, revenue);
    }
}