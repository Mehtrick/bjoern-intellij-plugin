package de.mehtrick.bjoern;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.tree.IElementType;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.yaml.YAMLTokenTypes;

public class BjoernSyntaxHighlighterTest extends BasePlatformTestCase {

    public void testKeywordHighlighting() {
        BjoernSyntaxHighlighter highlighter = new BjoernSyntaxHighlighter();
        
        // Test that SCALAR_KEY tokens are highlighted as keywords
        TextAttributesKey[] keys = highlighter.getTokenHighlights(YAMLTokenTypes.SCALAR_KEY);
        assertNotNull("Should return highlighting for SCALAR_KEY", keys);
        assertEquals("Should have one highlighting key", 1, keys.length);
        assertEquals("Should highlight as Bjoern keyword", BjoernSyntaxHighlighter.BJOERN_KEYWORD, keys[0]);
    }

    public void testVariableHighlighting() {
        BjoernSyntaxHighlighter highlighter = new BjoernSyntaxHighlighter();
        
        // Test that SCALAR_DSTRING tokens are highlighted as variables
        TextAttributesKey[] keys = highlighter.getTokenHighlights(YAMLTokenTypes.SCALAR_DSTRING);
        assertNotNull("Should return highlighting for SCALAR_DSTRING", keys);
        assertEquals("Should have one highlighting key", 1, keys.length);
        assertEquals("Should highlight as Bjoern variable", BjoernSyntaxHighlighter.BJOERN_VARIABLE, keys[0]);
    }

    public void testRegularStringHighlighting() {
        BjoernSyntaxHighlighter highlighter = new BjoernSyntaxHighlighter();
        
        // Test that SCALAR_STRING tokens are highlighted as regular values
        TextAttributesKey[] keys = highlighter.getTokenHighlights(YAMLTokenTypes.SCALAR_STRING);
        assertNotNull("Should return highlighting for SCALAR_STRING", keys);
        assertEquals("Should have one highlighting key", 1, keys.length);
        assertEquals("Should highlight as YAML scalar value", BjoernSyntaxHighlighter.YAML_SCALAR_VALUE, keys[0]);
    }
}