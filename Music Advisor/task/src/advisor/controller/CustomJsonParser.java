package advisor.controller;

import advisor.model.Album;
import advisor.model.Playlist;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class CustomJsonParser {

    public static ArrayList<Album> parseAlbums(String response) {
        ArrayList<Album> albums = new ArrayList<>();

        JsonArray jsonArray = JsonParser.parseString(response).getAsJsonObject()
                .getAsJsonObject("albums")
                .getAsJsonArray("items");
        for (JsonElement element : jsonArray) {
            if (element.isJsonObject()) {
                JsonObject jsonObject = element.getAsJsonObject();
                String name = jsonObject.get("name").getAsString();

                String[] artist = getArtistNames(jsonObject.getAsJsonArray("artists"));

                String link = jsonObject.getAsJsonObject("external_urls").get("spotify").getAsString();
                albums.add(new Album(name, artist, link));
            }
        }
        return albums;
    }

    private static String[] getArtistNames(JsonArray jsonArray) {
        String[] artist = new String[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            artist[i] = jsonArray.get(i).getAsJsonObject().get("name").getAsString();
        }
        return artist;
    }

    public static ArrayList<Playlist> parseFeatured(String body) {
        ArrayList<Playlist> playlists = new ArrayList<>();
        System.out.println(body);

        JsonArray jsonArray = JsonParser.parseString(body).getAsJsonObject()
                .getAsJsonObject("playlists").getAsJsonArray("items");

        for (JsonElement element : jsonArray) {
            if (element.isJsonObject()) {
                JsonObject jsonObject = element.getAsJsonObject();
                String name = jsonObject.get("name").getAsString();
                String link = jsonObject.getAsJsonObject("external_urls").get("spotify").getAsString();
                playlists.add(new Playlist(name, link));
            }
        }
        return playlists;
    }

    public static LinkedHashMap<String, String> parseCategories(String body) {
        LinkedHashMap<String, String> categories = new LinkedHashMap<>();

        JsonArray jsonArray = JsonParser.parseString(body).getAsJsonObject()
                .getAsJsonObject("categories").getAsJsonArray("items");

        for(JsonElement element : jsonArray){
            if(element.isJsonObject()){
                JsonObject jsonObject = element.getAsJsonObject();
                String name = jsonObject.get("name").getAsString();
                String id = jsonObject.get("id").getAsString();
                categories.put(name, id);
            }
        }
        return categories;
    }
}
