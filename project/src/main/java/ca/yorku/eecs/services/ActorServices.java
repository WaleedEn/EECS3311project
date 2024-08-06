package ca.yorku.eecs.services;

import ca.yorku.eecs.model.Actor;
import ca.yorku.eecs.Neo4jConfig;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import static org.neo4j.driver.v1.Values.parameters;

public class ActorServices {
    private final Neo4jConfig neo4jConfig;

    public ActorServices() {
        this.neo4jConfig = new Neo4jConfig();
    }

    public void addActor(Actor actor) {
        Driver driver = neo4jConfig.getDriver();
        try (Session session = driver.session()) {
            session.run("CREATE (a:Actor {name: $name, actorId: $actorId})",
                    parameters("name", actor.getName(), "actorId", actor.getActorId()));
        }
    }

    // Other service methods
}
