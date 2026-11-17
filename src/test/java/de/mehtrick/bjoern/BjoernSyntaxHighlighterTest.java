package de.mehtrick.bjoern;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import java.util.Arrays;

public class BjoernSyntaxHighlighterTest extends BasePlatformTestCase {
    public void testHighlightsKeywordsParametersAndComments() {
        String text = "Feature: Checkout\n# shared precondition\nBackground:\n  Given:\n    - A customer \"Ada\"\n";
        BjoernSyntaxHighlighter highlighter = new BjoernSyntaxHighlighter();
        Lexer lexer = highlighter.getHighlightingLexer();
        lexer.start(text);
        boolean keyword = false;
        boolean parameter = false;
        boolean comment = false;
        int end = 0;
        while (lexer.getTokenType() != null) {
            assertEquals("Tokens must cover the document without gaps", end, lexer.getTokenStart());
            assertTrue("Lexer must advance", lexer.getTokenEnd() > lexer.getTokenStart());
            var attributes = Arrays.asList(highlighter.getTokenHighlights(lexer.getTokenType()));
            keyword |= attributes.contains(BjoernSyntaxHighlighter.BJOERN_KEYWORD);
            parameter |= attributes.contains(BjoernSyntaxHighlighter.BJOERN_VARIABLE);
            comment |= attributes.contains(BjoernSyntaxHighlighter.BJOERN_COMMENT);
            end = lexer.getTokenEnd();
            lexer.advance();
        }
        assertEquals(text.length(), end);
        assertTrue("BDD keywords should be highlighted", keyword);
        assertTrue("Quoted parameters should be highlighted", parameter);
        assertTrue("YAML comments should be highlighted", comment);
    }

    public void testUnknownKeywordUsesInvalidKeywordAttributes() {
        BjoernSyntaxHighlighter highlighter = new BjoernSyntaxHighlighter();
        Lexer lexer = highlighter.getHighlightingLexer();
        lexer.start("Unknown: value\n");

        assertSame(BjoernTokenTypes.INVALID_KEYWORD, lexer.getTokenType());
        TextAttributesKey[] attributes = highlighter.getTokenHighlights(lexer.getTokenType());
        assertEquals(1, attributes.length);
        assertSame(BjoernSyntaxHighlighter.BJOERN_INVALID_KEYWORD, attributes[0]);
    }
}
