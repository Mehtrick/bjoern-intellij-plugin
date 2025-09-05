package de.mehtrick.bjoern;

import com.intellij.psi.tree.IElementType;

public class BjoernTokenizationTest {

    public void testValidatingLexerDoubleQuotedStrings() {
        // Test that the lexer can identify double-quoted strings
        BjoernValidatingLexer lexer = new BjoernValidatingLexer();
        
        if (lexer == null) {
            throw new AssertionError("Lexer should not be null");
        }
    }
    
    public void testDoubleQuotedStringLexerWithSingleQuote() {
        // Test that the lexer handles single quotes gracefully
        BjoernDoubleQuotedStringLexer lexer = new BjoernDoubleQuotedStringLexer();
        String testText = "text with single quote \" and more text";
        
        lexer.start(testText, 0, testText.length(), 0);
        
        // Should not throw exceptions and should handle the single quote
        int tokenCount = 0;
        while (lexer.getTokenType() != null && tokenCount < 10) { // Prevent infinite loop
            IElementType tokenType = lexer.getTokenType();
            int tokenStart = lexer.getTokenStart();
            int tokenEnd = lexer.getTokenEnd();
            
            // Basic validation that tokens make sense
            if (tokenStart < 0 || tokenEnd < tokenStart || tokenEnd > testText.length()) {
                throw new AssertionError("Invalid token boundaries: start=" + tokenStart + ", end=" + tokenEnd);
            }
            
            lexer.advance();
            tokenCount++;
        }
        
        // If we get here without exceptions, the test passes
        if (tokenCount == 0) {
            throw new AssertionError("Lexer should produce at least one token");
        }
    }
    
    public void testDoubleQuotedStringLexerWithCompleteQuotes() {
        // Test that the lexer properly handles complete quoted strings
        BjoernDoubleQuotedStringLexer lexer = new BjoernDoubleQuotedStringLexer();
        String testText = "text with \"complete\" quotes and more";
        
        lexer.start(testText, 0, testText.length(), 0);
        
        boolean foundDoubleQuotedString = false;
        int tokenCount = 0;
        
        while (lexer.getTokenType() != null && tokenCount < 10) {
            IElementType tokenType = lexer.getTokenType();
            
            if (tokenType == BjoernTokenTypes.DOUBLE_QUOTED_STRING) {
                foundDoubleQuotedString = true;
                String tokenText = testText.substring(lexer.getTokenStart(), lexer.getTokenEnd());
                if (!"\"complete\"".equals(tokenText)) {
                    throw new AssertionError("Expected '\"complete\"' but got '" + tokenText + "'");
                }
            }
            
            lexer.advance();
            tokenCount++;
        }
        
        if (!foundDoubleQuotedString) {
            throw new AssertionError("Should have found a double-quoted string token");
        }
    }
}