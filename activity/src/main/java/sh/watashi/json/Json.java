package sh.watashi.json;

public class Json {
    public static Object parse(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }
        JsonLexer lexer = new JsonLexer(input);
        JsonParser parser = new JsonParser(lexer.tokenize());
        return parser.parse();
    }
}
