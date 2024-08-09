package ca.yorku.eecs.dao;

import ca.yorku.eecs.model.Movie;

public interface MovieDAO {

    boolean addMovie(Movie movie);
    Movie getMovie(String movieId);
    boolean addMovieRating(String movieId, double rating);
    boolean addMovieBoxOffice(String movieId, double boxRevenue);

}
