package de.mehtrick.bjoern;

import com.intellij.lexer.Lexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.yaml.YAMLTokenTypes;

public class BjoernTokenizationTest extends BasePlatformTestCase {

    public void testValidatingLexerDoubleQuotedStrings() {
        // Test if the new validating lexer correctly identifies double-quoted strings
        String testContent = "Given:\n  - A user with username \"john.doe\"\n  - Password is \"securePassword123\"";
        
        Lexer lexer = new BjoernValidatingLexer();
        lexer.start(testContent);
        
        System.out.println("Testing validating lexer with: " + testContent);
        
        int doubleQuotedCount = 0;
        
        while (lexer.getTokenType() != null) {
            IElementType tokenType = lexer.getTokenType();
            String tokenText = lexer.getTokenText();
            
            System.out.println("Token: " + tokenType + " = '" + tokenText + "'");
            
            // Check if we find our custom DOUBLE_QUOTED_STRING tokens
            if (tokenType == BjoernTokenTypes.DOUBLE_QUOTED_STRING) {
                System.out.println("  *** FOUND CUSTOM DOUBLE-QUOTED STRING: " + tokenText);
                doubleQuotedCount++;
            }
            
            lexer.advance();
        }
        
        // We should find 2 double-quoted strings: "john.doe" and "securePassword123"
        assertTrue("Should find at least 2 double-quoted strings", doubleQuotedCount >= 2);
    }

    public void testDoubleQuotedStringTokenization() {
        // Test if YAML lexer correctly tokenizes double-quoted strings
        String testContent = "Given:\n  - A user with username \"john.doe\"\n  - Password is \"securePassword123\"";
        
        Lexer lexer = new BjoernValidatingLexer();
        lexer.start(testContent);
        
        System.out.println("Tokenizing: " + testContent);
        
        while (lexer.getTokenType() != null) {
            IElementType tokenType = lexer.getTokenType();
            String tokenText = lexer.getTokenText();
            
            System.out.println("Token: " + tokenType + " = '" + tokenText + "'");
            
            // Check if we find SCALAR_DSTRING tokens
            if (tokenType == YAMLTokenTypes.SCALAR_DSTRING) {
                System.out.println("  *** FOUND DOUBLE-QUOTED STRING: " + tokenText);
            }
            
            lexer.advance();
        }
    }
    
    public void testKeywordValidation() {
        // Test that valid and invalid keywords are correctly identified
        String testContent = "Feature:\nInvalidKeyword:\nGiven:";
        
        Lexer lexer = new BjoernValidatingLexer();
        lexer.start(testContent);
        
        System.out.println("Testing keyword validation with: " + testContent);
        
        int validKeywords = 0;
        int invalidKeywords = 0;
        
        while (lexer.getTokenType() != null) {
            IElementType tokenType = lexer.getTokenType();
            String tokenText = lexer.getTokenText();
            
            System.out.println("Token: " + tokenType + " = '" + tokenText + "'");
            
            if (tokenType == BjoernTokenTypes.VALID_KEYWORD) {
                System.out.println("  *** VALID BDD KEYWORD: " + tokenText);
                validKeywords++;
            } else if (tokenType == BjoernTokenTypes.INVALID_KEYWORD) {
                System.out.println("  *** INVALID KEYWORD: " + tokenText);
                invalidKeywords++;
            }
            
            lexer.advance();
        }
        
        // We should find valid keywords (Feature, Given) and invalid ones (InvalidKeyword)
        assertTrue("Should find valid keywords", validKeywords > 0);
        assertTrue("Should find invalid keywords", invalidKeywords > 0);
    }
    
    public void testVariousStringTypes() {
        // Test different string formats
        String[] testCases = {
            "\"double quoted\"",
            "'single quoted'", 
            "unquoted text",
            "key: \"value\"",
            "- item: \"double quoted value\""
        };
        
        for (String testCase : testCases) {
            System.out.println("\n--- Testing: " + testCase + " ---");
            
            Lexer lexer = new BjoernValidatingLexer();
            lexer.start(testCase);
            
            while (lexer.getTokenType() != null) {
                IElementType tokenType = lexer.getTokenType();
                String tokenText = lexer.getTokenText();
                
                System.out.println("  " + tokenType + " = '" + tokenText + "'");
                lexer.advance();
            }
        }
    }
}