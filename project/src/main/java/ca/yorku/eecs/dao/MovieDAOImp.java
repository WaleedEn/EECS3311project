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
    
    try (Session session = driver.session()) {

        String query = "MATCH (m:Movie {id: $id}) " + "SET m.rating = $rating";
        session.run(query, Values.parameters("id", movieId, "rating", rating));
        return true;

    } catch (Exception e) {
        
        e.printStackTrace();
        return false;
    }
}


    @Override
    public boolean addMovieBoxOffice(String movieId, double boxRevenue) {
        return false;
    }
}
