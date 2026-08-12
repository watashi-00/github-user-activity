package sh.watashi.json;

public enum TokenType {
    LBRACE,    // {
    RBRACE,    // }

    LBRACKET,  // [
    RBRACKET,  // ]

    COLON,     // :
    COMMA,     // ,

    STRING,    // "text"
    NUMBER,    // 123, -45.6, 1e10

    TRUE,      // true
    FALSE,     // false
    NULL,      // null

    EOF        // End of File / End of Input
}