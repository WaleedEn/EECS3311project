package ca.yorku.eecs.service;

import ca.yorku.eecs.model.Movie;

public interface MovieService {
    boolean addMovie(Movie movie);
    Movie getMovie(String movieId);
    boolean addMovieRating(String id, double rating);
    boolean addMovieBoxRevenue(String id, double revenue);

    boolean updateMovie(Movie movie);

}