package ca.yorku.eecs;

import static org.neo4j.driver.v1.Values.parameters;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;

public class Neo4jConfig implements AutoCloseable{
    private Driver driver;
    private final String uriDb = "bolt://localhost:7687";
    private final String USERNAME = "neo4j";
    private final String PASSWORD = "12345678";

    public Neo4jConfig(){
        Config config = Config.builder().withoutEncryption().build();
        driver = GraphDatabase.driver(uriDb, AuthTokens.basic(USERNAME, PASSWORD), config);
    }

    public Driver getDriver() {
        return driver;
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }
}
