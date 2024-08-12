package ca.yorku.eecs.dao;

import ca.yorku.eecs.Neo4jConfig;
import ca.yorku.eecs.model.Actor;
import ca.yorku.eecs.model.Movie;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MovieDAOImp implements MovieDAO{

    private final Driver driver;

    public MovieDAOImp(Driver driver){
        this.driver = driver;
    }
    @Override
    public boolean addMovie(Movie movie) {

        String checkMovieQuery = "MATCH (m:Movie {id: $id}) RETURN m";

        String addMovieQuery = "MERGE (m:Movie {id: $id}) " +
                "SET m.name = $name, " +
                "    m.actors = COALESCE(m.actors, []) " +
                "RETURN m";


        try (Session session = driver.session()) {
            Transaction tx = session.beginTransaction();

            StatementResult checkResult = tx.run(checkMovieQuery, Values.parameters("id", movie.getMovieId(), "name", movie.getName()));


            if(checkResult.hasNext()){
                tx.success();
                return false;
            }

            StatementResult addResult = tx.run(addMovieQuery, Values.parameters("id", movie.getMovieId(), "name", movie.getName()));
            tx.success();
            return addResult.hasNext();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Movie getMovie(String movieId) {
        Movie movie_OBJ = null;

        try (Session session = driver.session()) {
            String neo_LINE = "MATCH (m:Movie {id: $movieId}) RETURN m";
            StatementResult neo_RESULT = session.run(neo_LINE, Values.parameters("movieId", movieId));

            if (neo_RESULT.hasNext()) {
                Node ndd = neo_RESULT.single().get("m").asNode();

                List<String> actors = new ArrayList<>();
                if (ndd.containsKey("actors")) {
                    actors = ndd.get("actors").asList(Value::asString);
                }

                String id = ndd.get("id").asString();
                String name = ndd.get("name").asString();
                movie_OBJ = new Movie(id, name);

                for (String actorId : actors) {
                    movie_OBJ.addActor(actorId);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return movie_OBJ;
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
    public boolean addMovieBoxOffice(String movieId, double revenue) {
        try (Session session = driver.session()) {

            String query = "MATCH (m:Movie {id: $id}) " + "SET m.revenue = $revenue";
            session.run(query, Values.parameters("id", movieId, "revenue", revenue));
            return true;

        } catch (Exception e) {
            
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateMovie(Movie movie) {
        try (Session session = driver.session()) {
            String query = "MATCH (m:Movie {id: $movieId}) SET m.actors = $actors";
            List<String> actors = movie.getActors();
            session.run(query, Values.parameters("movieId", movie.getMovieId(), "actors", actors));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Movie> getMoviesForActor(String actorId) {
        List<Movie> movies = new ArrayList<>();
        try(Session session = driver.session()){
            String query = "MATCH (a:Actor {id: $actorId})-[:ACTED_IN]->(m:Movie) RETURN m";
            StatementResult result = session.run(query, Values.parameters("actorId", actorId));

            while(result.hasNext()){
                Record record = result.next();
                Node movieNode = record.get("m").asNode();
                movies.add(new Movie(
                        movieNode.get("id").asString(),
                        movieNode.get("name").asString(),
                        movieNode.get("rating").asDouble()
                        ));
            }
        }
        return movies;
    }
}
