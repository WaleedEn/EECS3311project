package ca.yorku.eecs.dao;

import ca.yorku.eecs.Neo4jConfig;
import ca.yorku.eecs.model.Movie;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Values;

import java.util.Collections;

public class MovieDAOImp implements MovieDAO{

    private final Driver driver;

    public MovieDAOImp(Driver driver){
        this.driver = driver;
    }
    @Override
    public boolean addMovie(Movie movie) {
        try (Session session = driver.session()) {
            String query = "CREATE (m:Movie {id: $id, name: $name})";
            session.run(query, Values.parameters("id", movie.getMovieId(), "name", movie.getName()));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Movie getMovie(String movieId) {
        return null;
    }

@Override

public boolean addMovieRating(String movieId, double rating) {

    Session session = null;

    try {
        session = driver.session();
        Transaction transaction = session.beginTransaction();

        // match the movie and set its rating

        String query = "MATCH (m:Movie {id: $id}) " + "SET m.rating = $rating";

        transaction.run(query, Values.parameters("id", movieId, "rating", rating));
        transaction.commit();
        return true;

    } catch (Exception e) {
        e.printStackTrace();
        if (session != null) {
            session.close();
        }

        return false;


    } finally {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }
}


    @Override
    public boolean addMovieBoxOffice(String movieId, double boxRevenue) {
        return false;
    }
}
