package ca.yorku.eecs.service;

import ca.yorku.eecs.model.Movie;

public interface MovieService {
    boolean addMovie(Movie movie);
    Movie getMovie(String movieId);


}