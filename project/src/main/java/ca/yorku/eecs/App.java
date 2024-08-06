package ca.yorku.eecs;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

public class App 
{
    static int PORT = 8080;
    public static void main(String[] args) throws IOException
    {
        Neo4jConfig neo4jConfig = new Neo4jConfig();

        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
        
        
        //creating a single context for all API requests as mentioned in assignment 
        
        server.createContext("/ap1/v1", new APIHandler(neo4jConfig));
        
        server.start();
        System.out.printf("Server started on port %d...\n", PORT);
    }
}
