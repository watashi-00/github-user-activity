package sh.watashi.json;

import java.util.ArrayList;
import java.util.List;

public class JsonLexer {
    
    private final String input;
    private int pos;

    public JsonLexer(String input) {
        this.input = input;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (pos < input.length()) {
            char current = input.charAt(pos);

            switch (current) {
                case '{' -> {
                    tokens.add(new Token(TokenType.LBRACE, "{"));
                    pos++;
                }

                case '}' -> {
                    tokens.add(new Token(TokenType.RBRACE, "}"));
                    pos++;
                }

                case '[' -> {
                    tokens.add(new Token(TokenType.LBRACKET, "["));
                    pos++;
                }

                case ']' -> {
                    tokens.add(new Token(TokenType.RBRACKET, "]"));
                    pos++;
                }

                case ':' -> {
                    tokens.add(new Token(TokenType.COLON, ":"));
                    pos++;
                }

                case ',' -> {
                    tokens.add(new Token(TokenType.COMMA, ","));
                    pos++;
                }

                case '"' -> tokens.add(readString());


                default -> {
                    if (Character.isWhitespace(current)) {
                        pos++;
                    } else if (current == '-' || Character.isDigit(current)) {
                        tokens.add(readNumber());
                    } else if (input.startsWith("true", pos)) {
                        tokens.add(new Token(TokenType.TRUE, "true"));
                        pos += 4;
                    } else if (input.startsWith("false", pos)) {
                        tokens.add(new Token(TokenType.FALSE, "false"));
                        pos += 5;
                    } else if (input.startsWith("null", pos)) {
                        tokens.add(new Token(TokenType.NULL, "null"));
                        pos += 4;
                    } else {
                        throw new RuntimeException(
                            "Unexpected character '" + current +
                            "' at pos " + pos
                        );
                    }
                }
            }
            
        }

        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;

    }

    private Token readString() {
        pos++; // ignore first "

        int start = pos;

        while (pos < input.length()) {
            if (input.charAt(pos) == '"' &&
                input.charAt(pos - 1) != '\\') {

                String value = input.substring(start, pos);

                pos++; // ignore "

                return new Token(TokenType.STRING, value);
            }

            pos++;
        }

        throw new RuntimeException("Unterminated string");
    }

    private Token readNumber() {
        int start = pos;

        if (input.charAt(pos) == '-') {
            pos++;
        }

        while (pos < input.length() &&
            Character.isDigit(input.charAt(pos))) {
            pos++;
        }

        if (pos < input.length() &&
            input.charAt(pos) == '.') {

            pos++;

            while (pos < input.length() &&
                Character.isDigit(input.charAt(pos))) {
                pos++;
            }
        }

        return new Token(
            TokenType.NUMBER,
            input.substring(start, pos)
        );
    }

}
