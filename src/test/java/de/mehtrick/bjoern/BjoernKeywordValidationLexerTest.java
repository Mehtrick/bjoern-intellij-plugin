package de.mehtrick.bjoern;

import com.intellij.psi.tree.IElementType;

public class BjoernKeywordValidationLexerTest {

    public void testDeprecatedIsValidKeyword() {
        BjoernKeywordValidationLexer lexer = new BjoernKeywordValidationLexer();
        lexer.start("Deprecated:", 0, 11, 0);

        IElementType tokenType = lexer.getTokenType();
        if (tokenType != BjoernTokenTypes.VALID_KEYWORD) {
            throw new AssertionError("Expected Deprecated: to be a valid BDD keyword, but got " + tokenType);
        }
    }

    public void testDeprecatedWithoutColonIsValidKeyword() {
        BjoernKeywordValidationLexer lexer = new BjoernKeywordValidationLexer();
        lexer.start("Deprecated", 0, 10, 0);

        IElementType tokenType = lexer.getTokenType();
        if (tokenType != BjoernTokenTypes.VALID_KEYWORD) {
            throw new AssertionError("Expected Deprecated to be a valid BDD keyword, but got " + tokenType);
        }
    }

    public void testUnknownKeywordIsInvalid() {
        BjoernKeywordValidationLexer lexer = new BjoernKeywordValidationLexer();
        lexer.start("Unknown:", 0, 8, 0);

        IElementType tokenType = lexer.getTokenType();
        if (tokenType != BjoernTokenTypes.INVALID_KEYWORD) {
            throw new AssertionError("Expected Unknown: to be an invalid BDD keyword, but got " + tokenType);
        }
    }
}
