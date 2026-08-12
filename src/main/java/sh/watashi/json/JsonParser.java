package sh.watashi.json;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

public class JsonParser {
    private final List<Token> tokens;
    private int current = 0;

    public JsonParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Object parse() {
        Object result = parseValue();
        if (peek().type() != TokenType.EOF) {
            throw new RuntimeException("Unexpected tokens after valid JSON input at index " + current);
        }
        return result;
    }

    private Object parseValue() {
        Token token = peek();
        switch (token.type()) {
            case LBRACE -> {
                return parseObject();
            }
            case LBRACKET -> {
                return parseArray();
            }
            case STRING -> {
                consume();
                return token.value();
            }
            case NUMBER -> {
                consume();
                return parseNumber(token.value());
            }
            case TRUE -> {
                consume();
                return Boolean.TRUE;
            }
            case FALSE -> {
                consume();
                return Boolean.FALSE;
            }
            case NULL -> {
                consume();
                return null;
            }
            default -> throw new RuntimeException("Unexpected token: " + token.type() + " ('" + token.value() + "') at index " + current);
        }
    }

    private Map<String, Object> parseObject() {
        consume(); // LBRACE
        Map<String, Object> map = new LinkedHashMap<>();

        if (peek().type() == TokenType.RBRACE) {
            consume(); // RBRACE
            return map;
        }

        while (true) {
            Token keyToken = peek();
            if (keyToken.type() != TokenType.STRING) {
                throw new RuntimeException("Expected string key in object at index " + current);
            }
            consume();
            String key = keyToken.value();

            if (peek().type() != TokenType.COLON) {
                throw new RuntimeException("Expected ':' after key at index " + current);
            }
            consume();

            Object value = parseValue();
            map.put(key, value);

            Token next = peek();
            if (next.type() == TokenType.COMMA) {
                consume();
                if (peek().type() == TokenType.RBRACE) {
                    throw new RuntimeException("Trailing comma not allowed in object at index " + current);
                }
            } else if (next.type() == TokenType.RBRACE) {
                consume();
                break;
            } else {
                throw new RuntimeException("Expected ',' or '}' in object at index " + current);
            }
        }

        return map;
    }

    private List<Object> parseArray() {
        consume(); // LBRACKET
        List<Object> list = new ArrayList<>();

        if (peek().type() == TokenType.RBRACKET) {
            consume(); // RBRACKET
            return list;
        }

        while (true) {
            Object value = parseValue();
            list.add(value);

            Token next = peek();
            if (next.type() == TokenType.COMMA) {
                consume();
                if (peek().type() == TokenType.RBRACKET) {
                    throw new RuntimeException("Trailing comma not allowed in array at index " + current);
                }
            } else if (next.type() == TokenType.RBRACKET) {
                consume();
                break;
            } else {
                throw new RuntimeException("Expected ',' or ']' in array at index " + current);
            }
        }

        return list;
    }

    private Token peek() {
        if (current >= tokens.size()) {
            throw new RuntimeException("Unexpected end of tokens");
        }
        return tokens.get(current);
    }

    private Token consume() {
        Token token = peek();
        current++;
        return token;
    }

    private Object parseNumber(String val) {
        if (val.contains(".") || val.contains("e") || val.contains("E")) {
            return Double.parseDouble(val);
        } else {
            try {
                return Long.parseLong(val);
            } catch (NumberFormatException e) {
                return Double.parseDouble(val);
            }
        }
    }
}
