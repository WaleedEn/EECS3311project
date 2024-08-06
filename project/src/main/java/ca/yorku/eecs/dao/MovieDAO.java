package ca.yorku.eecs.dao;

public interface MovieDAO {

    boolean addMovie(String name);
    boolean addMovieRating(String movieId, int rating);
    boolean addMovieBoxOffice(String movieId, String boxRevenue);
    String getMoviesSortedByBoxRevenue(String actorId);

}
