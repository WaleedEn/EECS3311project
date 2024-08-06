package ca.yorku.eecs.controller;

import com.sun.net.httpserver.HttpExchange;
import ca.yorku.eecs.model.Actor;
import ca.yorku.eecs.services.ActorServices;
import ca.yorku.eecs.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.json.JSONException;

public class ActorController {
    private static final ActorServices actorService = new ActorServices();

    public static void addActor(HttpExchange exchange) throws IOException, JSONException {
        InputStream is = exchange.getRequestBody();
        String requestBody = new String(readAllBytes(is), StandardCharsets.UTF_8);
        Actor actor = Utils.parseActorFromJson(requestBody);
        String response;
        int statusCode;

        if (actor == null) {
            response = "Invalid actor data";
            statusCode = 400;
        } else {
            try {
                actorService.addActor(actor);  // Pass the Actor object directly
                response = "Actor added successfully";
                statusCode = 200;
            } catch (Exception e) {
                response = "Error adding actor: " + e.getMessage();
                statusCode = 500;
            }
        }

        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    // Other methods for updateActor, getActor, etc.
}
