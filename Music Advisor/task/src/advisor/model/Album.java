package advisor.model;

import java.util.Arrays;

public class Album {
    private String name;
    private String[] artists;
    private String link;

    public Album(String name, String[] artists, String link) {
        this.name = name;
        this.artists = artists;
        this.link = link;
    }

    public Album() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getArtists() {
        return artists;
    }

    public void setArtists(String[] artists) {
        this.artists = artists;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return name+"\n"+Arrays.toString(artists)+"\n"+link;
    }
}

