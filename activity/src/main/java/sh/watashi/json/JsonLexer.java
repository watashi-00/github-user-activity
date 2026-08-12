package sh.watashi.json;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class JsonLexer {
    
    private final String input;
    private int pos;

    private static final Pattern JSON_NUMBER_PATTERN = 
        Pattern.compile("^-?(?:0|[1-9][0-9]*)(?:\\.[0-9]+)?(?:[eE][+-]?[0-9]+)?$");

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
        pos++; // ignore starting "
        StringBuilder sb = new StringBuilder();

        while (pos < input.length()) {
            char c = input.charAt(pos);

            if (c == '"') {
                pos++; // ignore closing "
                return new Token(TokenType.STRING, sb.toString());
            }

            if (c == '\\') {
                pos++;
                if (pos >= input.length()) {
                    throw new RuntimeException("Unterminated string escape at pos " + pos);
                }
                char escapeChar = input.charAt(pos);
                switch (escapeChar) {
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    case '/' -> sb.append('/');
                    case 'b' -> sb.append('\b');
                    case 'f' -> sb.append('\f');
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case 'u' -> {
                        if (pos + 4 >= input.length()) {
                            throw new RuntimeException("Invalid unicode escape sequence at pos " + pos);
                        }
                        String hex = input.substring(pos + 1, pos + 5);
                        for (int i = 0; i < 4; i++) {
                            char hc = hex.charAt(i);
                            if (!((hc >= '0' && hc <= '9') || (hc >= 'a' && hc <= 'f') || (hc >= 'A' && hc <= 'F'))) {
                                throw new RuntimeException("Invalid hex digit in unicode escape: " + hex + " at pos " + pos);
                            }
                        }
                        int code = Integer.parseInt(hex, 16);
                        sb.append((char) code);
                        pos += 4;
                    }
                    default -> throw new RuntimeException("Invalid escape sequence: \\" + escapeChar + " at pos " + pos);
                }
                pos++;
            } else if (c < 32) {
                throw new RuntimeException("Raw control character not allowed in string: " + (int) c + " at pos " + pos);
            } else {
                sb.append(c);
                pos++;
            }
        }

        throw new RuntimeException("Unterminated string starting at position " + (pos - sb.length() - 1));
    }

    private Token readNumber() {
        int start = pos;

        while (pos < input.length()) {
            char c = input.charAt(pos);
            if ((c >= '0' && c <= '9') || c == '-' || c == '+' || c == '.' || c == 'e' || c == 'E') {
                pos++;
            } else {
                break;
            }
        }

        String lexeme = input.substring(start, pos);
        if (!JSON_NUMBER_PATTERN.matcher(lexeme).matches()) {
            throw new RuntimeException("Invalid JSON number: " + lexeme + " at pos " + start);
        }

        return new Token(TokenType.NUMBER, lexeme);
    }
}
