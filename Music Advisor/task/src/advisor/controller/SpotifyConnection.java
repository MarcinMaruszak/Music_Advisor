package advisor.controller;

import advisor.model.Album;
import advisor.model.Playlist;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;

public class SpotifyConnection {
    private HttpServer server;
    private HttpClient client;
    private final String accessPath;
    private final String resourcesPath;
    private String accessToken;

    public SpotifyConnection(String access, String resourcesPath) {
        this.accessPath = access;
        this.resourcesPath = resourcesPath;
        try {
            server = HttpServer.create();
            client = HttpClient.newBuilder().build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer() {
        try {
            server.bind(new InetSocketAddress(8080), 0);
            server.createContext("/", exchange -> {
                String response;
                String query = exchange.getRequestURI().getQuery();
                if (query != null && query.startsWith("code")) {
                    response = "Got the code. Return back to your program.";
                    exchange.sendResponseHeaders(200, response.length());
                    if (validAccessToken(query.replace("code=", ""))) {
                        exchange.getResponseBody().write(response.getBytes());
                        exchange.getResponseBody().close();
                        System.out.println("---SUCCESS---");
                        server.stop(0);
                    }
                } else {
                    response = "Authorization code not found. Try again.";
                    exchange.sendResponseHeaders(400, response.length());
                    exchange.getResponseBody().write(response.getBytes());
                    exchange.getResponseBody().close();
                }
            });
            server.start();
            System.out.println("use this link to request the access code:\n" +
                    accessPath + "/authorize?" +
                    "client_id=f91bb1c591a64339b165e2ae1830c4d6&" +
                    "redirect_uri=http://localhost:8080&response_type=code\n" +
                    "waiting for code");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validAccessToken(String code) {
        String encodedID = Base64.getEncoder()
                .encodeToString("f91bb1c591a64339b165e2ae1830c4d6:0c50b4590b69477a9cf8d63a2ae2f144".getBytes());
        System.out.println("code received\n" +
                "making http request for access_token...");
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization",
                        "Basic " + encodedID)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(accessPath + "/api/token"))
                .POST(HttpRequest.BodyPublishers.ofString(
                        "grant_type=authorization_code&" +
                                "code=" + code + "&" +
                                "redirect_uri=http://localhost:8080")).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("response:\n" + response.body());
            if (response.body().contains("access_token")) {
                JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
                accessToken = jsonObject.get("access_token").getAsString();
                return true;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<Album> newReleases() {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .uri(URI.create(resourcesPath + "/v1/browse/new-releases"))
                .GET().build();
        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return CustomJsonParser.parseAlbums(response.body());

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public ArrayList<Playlist> featured() {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .uri(URI.create(resourcesPath + "/v1/browse/featured-playlists"))
                .GET().build();
        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return CustomJsonParser.parseFeatured(response.body());

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public LinkedHashMap<String, String> categories() {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .uri(URI.create(resourcesPath + "/v1/browse/categories"))
                .GET().build();

        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            //System.out.println(response.body());
            if(!response.body().contains("\"error\":{")){
                return CustomJsonParser.parseCategories(response.body());
            }else {
                LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
                JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
                hashMap.put(jsonObject.getAsJsonObject("error").get("message").getAsString(),"");
                return hashMap;
            }

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return new LinkedHashMap<>();
    }

    public ArrayList<Playlist> getCategoryPlaylists(String id) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .uri(URI.create(resourcesPath + "/v1/browse/categories/"+id+"/playlists"))
                .GET().build();
        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if(!response.body().contains("\"error\":{")){
                return CustomJsonParser.parseFeatured(response.body());
            }else {
                ArrayList<Playlist> list = new ArrayList<>();
                JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
                list.add(new Playlist(jsonObject.getAsJsonObject("error").get("message").getAsString(),""));
                return list;
            }

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
