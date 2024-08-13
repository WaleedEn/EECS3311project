package ca.yorku.eecs.dao;

import ca.yorku.eecs.model.Movie;

import java.util.List;

public interface MovieDAO {

    boolean addMovie(Movie movie);
    Movie getMovie(String movieId);
    boolean addMovieRating(String movieId, double rating);
    boolean addMovieBoxOffice(String movieId, double boxRevenue);
    public boolean updateMovie(Movie movie);
    public List<Movie> getMoviesForActor(String actorId);
    public void deleteMovie(String movieId);

}
