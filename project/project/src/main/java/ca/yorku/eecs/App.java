package ca.yorku.eecs;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
//import org.neo4j.driver.StatementResult;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.Transaction;

public class App 
{
    static int PORT = 8080;
    static Driver driver;

    public static void main(String[] args) throws IOException
    {
        // Initialize Neo4j Driver
        driver = GraphDatabase.driver(
            "bolt://localhost:7688", 
            AuthTokens.basic("neo4j", "password") // Change password if necessary
        );

        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);

        server.createContext("/", exchange -> {
            String response = "Welcome to the Neo4j HTTP server!";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });

        server.createContext("/query", exchange -> {
            String query = "MATCH (n) RETURN n LIMIT 25"; // Example query
            StringBuilder result = new StringBuilder();

            try (Session session = driver.session()) {
                Result res = session.run(query);
                while (res.hasNext()) {
                    result.append(res.next().toString()).append("\n");
                }
            }

            String response = "Query result:\n" + result.toString();
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });

        server.start();
        System.out.printf("Server started on port %d...\n", PORT);
    }
}
