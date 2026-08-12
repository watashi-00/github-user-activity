package sh.watashi;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {

    private final String baseUrl = "https://api.github.com/";
    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("args _> help to help");
            return;
        }

        String op = args[0];

        switch (op) {
            case "github-activity":
                
                if(args.length <= 1) { 
                    System.out.println("please provide an username"); 
                    return;
                }
                
                new Main().fetch("users/"+args[1]);

                break;
            case "help":
                
                break;
        
            default:
                break;
        }

    }

    void help() {
        
    }

    void fetch(String url) {
        try {
        HttpClient client   = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl+url))
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());
        System.out.println(response.body());

        } catch(Exception e) {
            e.printStackTrace();
        }

    }
}