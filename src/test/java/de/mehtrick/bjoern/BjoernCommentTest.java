package de.mehtrick.bjoern;

import com.intellij.psi.tree.IElementType;

public class BjoernCommentTest {

    public void testCommentLexerFullLineComment() {
        // Test that the lexer can identify full-line comments
        BjoernCommentLexer lexer = new BjoernCommentLexer();
        String testText = "#This is a comment";
        
        lexer.start(testText, 0, testText.length(), 0);
        
        boolean foundComment = false;
        int tokenCount = 0;
        
        while (lexer.getTokenType() != null && tokenCount < 10) {
            IElementType tokenType = lexer.getTokenType();
            
            if (tokenType == BjoernTokenTypes.COMMENT) {
                foundComment = true;
                String tokenText = testText.substring(lexer.getTokenStart(), lexer.getTokenEnd());
                if (!testText.equals(tokenText)) {
                    throw new AssertionError("Expected '#This is a comment' but got '" + tokenText + "'");
                }
            }
            
            // Validate token boundaries
            int tokenStart = lexer.getTokenStart();
            int tokenEnd = lexer.getTokenEnd();
            if (tokenStart < 0 || tokenEnd > testText.length() || tokenStart >= tokenEnd) {
                throw new AssertionError("Invalid token boundaries: start=" + tokenStart + ", end=" + tokenEnd);
            }
            
            lexer.advance();
            tokenCount++;
        }
        
        if (!foundComment) {
            throw new AssertionError("Should have found a comment token");
        }
    }
    
    public void testCommentLexerInlineComment() {
        // Test that the lexer can identify inline comments after content
        BjoernCommentLexer lexer = new BjoernCommentLexer();
        String testText = "- A car #does not matter what car";
        
        lexer.start(testText, 0, testText.length(), 0);
        
        boolean foundComment = false;
        int tokenCount = 0;
        
        while (lexer.getTokenType() != null && tokenCount < 10) {
            IElementType tokenType = lexer.getTokenType();
            
            if (tokenType == BjoernTokenTypes.COMMENT) {
                foundComment = true;
                String tokenText = testText.substring(lexer.getTokenStart(), lexer.getTokenEnd());
                if (!"#does not matter what car".equals(tokenText)) {
                    throw new AssertionError("Expected '#does not matter what car' but got '" + tokenText + "'");
                }
            }
            
            // Validate token boundaries
            int tokenStart = lexer.getTokenStart();
            int tokenEnd = lexer.getTokenEnd();
            if (tokenStart < 0 || tokenEnd > testText.length() || tokenStart >= tokenEnd) {
                throw new AssertionError("Invalid token boundaries: start=" + tokenStart + ", end=" + tokenEnd);
            }
            
            lexer.advance();
            tokenCount++;
        }
        
        if (!foundComment) {
            throw new AssertionError("Should have found a comment token in inline text");
        }
    }
    
    public void testCommentLexerWithoutComment() {
        // Test that the lexer handles text without comments correctly
        BjoernCommentLexer lexer = new BjoernCommentLexer();
        String testText = "- A car without comments";
        
        lexer.start(testText, 0, testText.length(), 0);
        
        boolean foundComment = false;
        int tokenCount = 0;
        
        while (lexer.getTokenType() != null && tokenCount < 10) {
            IElementType tokenType = lexer.getTokenType();
            
            if (tokenType == BjoernTokenTypes.COMMENT) {
                foundComment = true;
            }
            
            lexer.advance();
            tokenCount++;
        }
        
        if (foundComment) {
            throw new AssertionError("Should not have found a comment token in text without comments");
        }
        
        // If we get here without exceptions, the test passes
        if (tokenCount == 0) {
            throw new AssertionError("Lexer should produce at least one token");
        }
    }
    
    public void testCommentLexerWithQuotesAndComments() {
        // Test mixed content with both quotes and comments
        BjoernCommentLexer lexer = new BjoernCommentLexer();
        String testText = "- User \"john.doe\" and password \"secret\" #comment with quotes";
        
        lexer.start(testText, 0, testText.length(), 0);
        
        boolean foundComment = false;
        int tokenCount = 0;
        
        while (lexer.getTokenType() != null && tokenCount < 10) {
            IElementType tokenType = lexer.getTokenType();
            
            if (tokenType == BjoernTokenTypes.COMMENT) {
                foundComment = true;
                String tokenText = testText.substring(lexer.getTokenStart(), lexer.getTokenEnd());
                if (!"#comment with quotes".equals(tokenText)) {
                    throw new AssertionError("Expected '#comment with quotes' but got '" + tokenText + "'");
                }
            }
            
            lexer.advance();
            tokenCount++;
        }
        
        if (!foundComment) {
            throw new AssertionError("Should have found a comment token in mixed content");
        }
    }
    
    public void testCommentLexerMultipleHashes() {
        // Test that comments with multiple hash symbols work correctly
        BjoernCommentLexer lexer = new BjoernCommentLexer();
        String testText = "text #comment with # multiple ## hashes";
        
        lexer.start(testText, 0, testText.length(), 0);
        
        boolean foundComment = false;
        int tokenCount = 0;
        
        while (lexer.getTokenType() != null && tokenCount < 10) {
            IElementType tokenType = lexer.getTokenType();
            
            if (tokenType == BjoernTokenTypes.COMMENT) {
                foundComment = true;
                String tokenText = testText.substring(lexer.getTokenStart(), lexer.getTokenEnd());
                if (!"#comment with # multiple ## hashes".equals(tokenText)) {
                    throw new AssertionError("Expected full comment with multiple hashes but got '" + tokenText + "'");
                }
            }
            
            lexer.advance();
            tokenCount++;
        }
        
        if (!foundComment) {
            throw new AssertionError("Should have found comment with multiple hashes");
        }
    }
}