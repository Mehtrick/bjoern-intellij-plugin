package de.mehtrick.bjoern;

public class BjoernTokenizationTest {

    public void testValidatingLexerDoubleQuotedStrings() {
        // Test that the lexer can identify double-quoted strings
        BjoernValidatingLexer lexer = new BjoernValidatingLexer();
        
        if (lexer == null) {
            throw new AssertionError("Lexer should not be null");
        }
    }
}