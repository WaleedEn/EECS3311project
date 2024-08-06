package ca.yorku.eecs.dao;

import ca.yorku.eecs.Neo4jConfig;

public class MovieDAOImp implements MovieDAO{

    private final Neo4jConfig neo4jConfig = new Neo4jConfig();

    @Override
    public boolean addMovie(String name) {
        return false;
    }

    @Override
    public boolean addMovieRating(String movieId, int rating) {
        return false;
    }

    @Override
    public boolean addMovieBoxOffice(String movieId, String boxRevenue) {
        return false;
    }

    @Override
    public String getMoviesSortedByBoxRevenue(String actorId) {
        return null;
    }
}
