package advisor;

import advisor.view.UserInterface;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String access;
        String resources;
        int entries;
        if(args.length>1){
            access = args[1];
            resources = args[3];
            entries = Integer.parseInt(args[5]);
        }else {
            access = "https://accounts.spotify.com";
            resources = "https://api.spotify.com";
            entries = 5;
        }
        Scanner scanner = new Scanner(System.in);
        new UserInterface(scanner, access, resources, entries).start();
    }
}
