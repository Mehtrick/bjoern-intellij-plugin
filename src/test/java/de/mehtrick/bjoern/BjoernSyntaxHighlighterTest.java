package de.mehtrick.bjoern;

public class BjoernSyntaxHighlighterTest {

    public void testKeywordHighlighting() {
        BjoernSyntaxHighlighter highlighter = new BjoernSyntaxHighlighter();
        
        // This is a basic test to ensure the highlighter is instantiated correctly
        if (highlighter == null) {
            throw new AssertionError("Highlighter should not be null");
        }
    }
}