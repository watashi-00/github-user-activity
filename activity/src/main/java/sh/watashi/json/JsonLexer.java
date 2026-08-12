package sh.watashi.json;

import java.util.ArrayList;
import java.util.List;

public class JsonLexer {
    
    private final String input;
    private int position;

    public JsonLexer(String input) {
        this.input = input;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (position < input.length()) {
            char current = input.charAt(position);

            switch (current) {
                case '{' -> {
                    tokens.add(new Token(TokenType.LBRACE, "{"));
                    position++;
                }

                case '}' -> {
                    tokens.add(new Token(TokenType.RBRACE, "}"));
                    position++;
                }

                case '[' -> {
                    tokens.add(new Token(TokenType.LBRACKET, "["));
                    position++;
                }

                case ']' -> {
                    tokens.add(new Token(TokenType.RBRACKET, "]"));
                    position++;
                }

                case ':' -> {
                    tokens.add(new Token(TokenType.COLON, ":"));
                    position++;
                }

                case ',' -> {
                    tokens.add(new Token(TokenType.COMMA, ","));
                    position++;
                }

                case '"' -> tokens.add(readString());

                default ->  System.out.println("...");
            }
            
        }

        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;

    }

    private Token readString() {
        position++; // ignore first "

        int start = position;

        while (position < input.length()) {
            if (input.charAt(position) == '"' &&
                input.charAt(position - 1) != '\\') {

                String value = input.substring(start, position);

                position++; // ignore "

                return new Token(TokenType.STRING, value);
            }

            position++;
        }

        throw new RuntimeException("Unterminated string");
    }

}
