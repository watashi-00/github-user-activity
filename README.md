# GitHub User Activity (roadmap.sh Challenge)

This is a solution to the [GitHub User Activity](https://roadmap.sh/projects/github-user-activity) challenge from [roadmap.sh](https://roadmap.sh).

I have chosen **Java** to implement this project, adhering to the constraint of not using any external libraries or frameworks (no third-party JSON parsers or HTTP libraries other than Java's built-in APIs).

## Features

- **Custom JSON Parser**: Includes a hand-written `JsonLexer` and recursive-descent `JsonParser` to tokenize, validate, and parse JSON responses from the GitHub API.
- **Robust Number & String Validation**: Lexer strictly validates JSON numbers (including fractions and exponents) and decodes/validates string escape sequences (e.g. `\n`, `\t`, `\uXXXX`).
- **HTTP Client Integration**: Fetches data dynamically from the official GitHub events API.
- **Detailed Activity Formatting**: Displays user events in a clean and human-readable format.
- **Graceful Error Handling**: Returns helpful error messages for nonexistent users, API rate limits, or network failures.

## Requirements

- **Java Development Kit (JDK)**: Version 17 or higher.
- **Maven**: To compile and package the project.

## Getting Started

### Compilation & Packaging

Use Maven to clean, compile, and build the executable JAR file:

```bash
mvn clean package
```

This compiles the application and generates the runnable JAR file at `target/activity-1.0-SNAPSHOT.jar`.

### Running the Application

To run the CLI application, execute the JAR file and pass the command and your target GitHub username:

```bash
java -jar target/activity-1.0-SNAPSHOT.jar github-activity <username>
```

#### Examples:

```bash
# Fetch recent activity for user 'watashi-00'
java -jar target/activity-1.0-SNAPSHOT.jar github-activity watashi-00
```

Output:
```text
- Pushed commits to 'master' in watashi-00/github-user-activity
- Created branch 'master' in watashi-00/github-user-activity
- Starred kamranahmedse/developer-roadmap
```

Show help menu:
```bash
java -jar target/activity-1.0-SNAPSHOT.jar help
```

## GitHub API Note

> [!NOTE]
> During development and exploration of the GitHub API, we discovered that GitHub removed slow-performing fields from the public events API stream in late 2025 (such as `size` / commit count and detailed `commits` lists inside `PushEvent` and `PullRequestEvent` payloads) to improve delivery latency.
> As a result, the application dynamically displays the target branch instead (e.g. `Pushed commits to 'master' in ...`) rather than showing hardcoded commit counts or falling back to `0`.