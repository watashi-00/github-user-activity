package sh.watashi;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {

    private final String baseUrl = "https://api.github.com/";
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("No command provided. Use 'help' to see available commands.");
            return;
        }

        new Main().run(args);

    }

    void run(String[] args) {
        String op = args[0];

        switch (op) {
            case "github-activity":

                if(args.length <= 1) { 
                    System.out.println("please provide an username e.g github-activity watashi-00 or 'help' to see all commands and examples"); 
                    return;
                }
                
                HttpResponse<String> response = fetch("users/" + args[1]);

                System.out.println(response.statusCode());
                parseBodyResponse(response.body());


                break;
            case "help":
            default:
                help();
                break;
        }

    }

    void help() {
        //TODO: define java -cp ... name
        System.out.println("""
            Usage:
            <cli> <command> [arguments]

            Commands:
            github-activity <username>  Fetch a GitHub user's activity
            help                        Show this help message

            Examples:
            <cli> github-activity watashi
            <cli> help
            """);
    }

    HttpResponse<String> fetch(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + url))
                .GET()
                .build();

            return client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch GitHub API", e);
        }
    }
    //TODO: parse body response

    void parseBodyResponse(String body) {
        if(body == null || body.isBlank()) {
            System.out.println("Github returned an invalid response. Please try again later or another username");
        }


    }

}