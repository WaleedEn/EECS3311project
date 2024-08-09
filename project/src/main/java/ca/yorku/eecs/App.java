package ca.yorku.eecs;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

public class App 
{
    static int PORT = 8080;
    public static void main(String[] args) throws IOException
    
    
    {
    	
    	
    	
    	//Initialize neo4jconfig 
    	Neo4jConfig neo4jconfig = new Neo4jConfig(); 
    	
    	
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
        
        
        
        
        //creating a single context for all API requests as mentioned in assignment      
        APIHandler apihandler = new APIHandler(neo4jconfig);
        server.createContext("/", apihandler);
        server.setExecutor(null);
        
        server.start();
        System.out.printf("Server started on port %d...\n", PORT);
    }
}
