package advisor.view;

import advisor.controller.SpotifyConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class UserInterface {
    private final Scanner scanner;
    private final SpotifyConnection spotifyConnection;
    private final int entriesPerPage;

    public UserInterface(Scanner scanner, String access, String resources, int entriesPerPage) {
        this.scanner = scanner;
        spotifyConnection = new SpotifyConnection(access, resources);
        this.entriesPerPage = entriesPerPage;
    }

    public void start() {
        label:
        while (true) {
            String input = scanner.nextLine();
            switch (input) {
                case "auth":
                    spotifyConnection.startServer();
                    options();
                    break label;
                case "exit":
                    System.out.println("---GOODBYE!---");
                    break label;
                default:
                    System.out.println("Please, provide access for application.");
            }
        }
    }

    private void options() {
        int page = 0;
        ArrayList<?> list = new ArrayList<>();
        label:
        while (true) {
            String input = scanner.nextLine();
            String category = "";
            if (input.startsWith("playlists")) {
                category = input.replaceFirst("playlists", "").trim();
                input = "playlists";
            }
            switch (input) {
                case "new":
                    page = 0;
                    /*ArrayList<Album> albums = spotifyConnection.newReleases();
                    //printPages(albums);*/
                    list = spotifyConnection.newReleases();
                    printPage(list, page);
                    break;
                case "featured":
                    page = 0;
                    list = spotifyConnection.featured();
                    printPage(list, page);
                    break;
                case "categories":
                    page = 0;
                    LinkedHashMap<String, String> categories = spotifyConnection.categories();
                    list = new ArrayList<>(categories.keySet());
                    printPage(list, page);
                    break;
                case "playlists":
                    page = 0;
                    HashMap<String, String> cat = spotifyConnection.categories();
                    String id = cat.get(category);
                    if (id != null) {
                        list = spotifyConnection.getCategoryPlaylists(id);
                        printPage(list , page);
                    } else {
                        System.out.println("Specified id doesn't exist");
                    }
                    break;
                case "prev":
                    if (page > 0) {
                        page--;
                        printPage(list, page);
                    } else {
                        System.out.println("No more pages.");
                    }
                    break;
                case "next":
                    if (++page < (list.size() / entriesPerPage)) {
                        printPage(list, page);
                    } else {
                        page--;
                        System.out.println("No more pages.");
                    }
                    break;
                case "exit":
                    System.out.println("---GOODBYE!---");
                    break label;
            }
            System.out.println();
        }
    }

    private void printPage(ArrayList<?> list, int page) {
        list.stream()
                .skip((long) page * entriesPerPage)
                .limit(entriesPerPage).forEach(entry -> System.out.println(entry));
        System.out.println("\n---PAGE " + (page + 1) + " OF " + list.size() / entriesPerPage + "---");
    }
}
