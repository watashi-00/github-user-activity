package sh.watashi;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import sh.watashi.json.Json;

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
                if (args.length <= 1) { 
                    System.out.println("please provide an username e.g github-activity watashi-00 or 'help' to see all commands and examples"); 
                    return;
                }
                
                String username = args[1];
                try {
                    HttpResponse<String> response = fetch("users/" + username + "/events");

                    if (response.statusCode() == 200) {
                        parseBodyResponse(response.body());
                    } else if (response.statusCode() == 404) {
                        System.out.println("Error: User '" + username + "' not found.");
                    } else if (response.statusCode() == 403) {
                        System.out.println("Error: API rate limit exceeded or access forbidden. Please try again later.");
                    } else {
                        System.out.println("Error: Failed to fetch activity (status code: " + response.statusCode() + ").");
                    }
                } catch (Exception e) {
                    System.out.println("Error: Network error or GitHub API is unreachable. Details: " + e.getMessage());
                }
                break;

            case "help":
            default:
                help();
                break;
        }
    }

    void help() {
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
                .header("User-Agent", "github-user-activity-cli")
                .GET()
                .build();

            return client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void parseBodyResponse(String body) {
        if (body == null || body.isBlank()) {
            System.out.println("GitHub returned an empty response.");
            return;
        }

        Object parsed = Json.parse(body);
        if (!(parsed instanceof List)) {
            System.out.println("Error: Unexpected JSON response structure.");
            return;
        }

        List<?> events = (List<?>) parsed;
        if (events.isEmpty()) {
            System.out.println("No recent activity found for this user.");
            return;
        }

        for (Object eventObj : events) {
            if (!(eventObj instanceof Map)) continue;
            Map<?, ?> event = (Map<?, ?>) eventObj;

            String type = (String) event.get("type");
            Map<?, ?> repo = (Map<?, ?>) event.get("repo");
            String repoName = repo != null ? (String) repo.get("name") : "unknown repository";
            Map<?, ?> payload = (Map<?, ?>) event.get("payload");

            String output = formatEvent(type, repoName, payload);
            if (output != null) {
                System.out.println("- " + output);
            }
        }
    }

    private String formatEvent(String type, String repoName, Map<?, ?> payload) {
        if (type == null) return null;
        switch (type) {
            case "PushEvent" -> {
                int commits = 0;
                if (payload != null && payload.get("size") instanceof Number num) {
                    commits = num.intValue();
                }
                return "Pushed " + commits + (commits == 1 ? " commit" : " commits") + " to " + repoName;
            }
            case "IssuesEvent" -> {
                String action = payload != null ? (String) payload.get("action") : "opened";
                return capitalize(action) + " an issue in " + repoName;
            }
            case "WatchEvent" -> {
                return "Starred " + repoName;
            }
            case "CreateEvent" -> {
                String refType = payload != null ? (String) payload.get("ref_type") : null;
                String ref = payload != null ? (String) payload.get("ref") : null;
                if ("repository".equals(refType)) {
                    return "Created repository " + repoName;
                } else if (refType != null) {
                    return "Created " + refType + " '" + ref + "' in " + repoName;
                }
                return "Created resource in " + repoName;
            }
            case "ForkEvent" -> {
                return "Forked " + repoName;
            }
            case "PullRequestEvent" -> {
                String action = payload != null ? (String) payload.get("action") : "opened";
                return capitalize(action) + " a pull request in " + repoName;
            }
            case "IssueCommentEvent" -> {
                return "Commented on an issue in " + repoName;
            }
            case "DeleteEvent" -> {
                String refType = payload != null ? (String) payload.get("ref_type") : "ref";
                String ref = payload != null ? (String) payload.get("ref") : "";
                return "Deleted " + refType + " '" + ref + "' in " + repoName;
            }
            case "GollumEvent" -> {
                return "Updated wiki pages in " + repoName;
            }
            case "MemberEvent" -> {
                String action = payload != null ? (String) payload.get("action") : "added";
                Map<?, ?> member = payload != null ? (Map<?, ?>) payload.get("member") : null;
                String memberName = member != null ? (String) member.get("login") : "a member";
                return capitalize(action) + " " + memberName + " to " + repoName;
            }
            case "PublicEvent" -> {
                return "Made " + repoName + " public";
            }
            case "ReleaseEvent" -> {
                String tag = "unknown";
                if (payload != null && payload.get("release") instanceof Map<?, ?> release) {
                    tag = (String) release.get("tag_name");
                }
                return "Released version " + tag + " of " + repoName;
            }
            default -> {
                String friendlyType = type.endsWith("Event") ? type.substring(0, type.length() - 5) : type;
                return "Performed " + friendlyType + " in " + repoName;
            }
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return "";
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}