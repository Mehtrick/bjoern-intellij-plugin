package de.mehtrick.bjoern;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.tree.IElementType;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.yaml.YAMLTokenTypes;

public class BjoernSyntaxHighlighterTest extends BasePlatformTestCase {

    public void testKeywordHighlighting() {
        BjoernSyntaxHighlighter highlighter = new BjoernSyntaxHighlighter();
        
        // Test that SCALAR_KEY tokens are highlighted as regular YAML keys
        TextAttributesKey[] keys = highlighter.getTokenHighlights(YAMLTokenTypes.SCALAR_KEY);
        assertNotNull("Should return highlighting for SCALAR_KEY", keys);
        assertEquals("Should have one highlighting key", 1, keys.length);
        assertEquals("Should highlight as YAML scalar key", BjoernSyntaxHighlighter.YAML_SCALAR_KEY, keys[0]);
    }
    
    public void testValidKeywordHighlighting() {
        BjoernSyntaxHighlighter highlighter = new BjoernSyntaxHighlighter();
        
        // Test that valid BDD keywords are highlighted correctly
        TextAttributesKey[] keys = highlighter.getTokenHighlights(BjoernTokenTypes.VALID_KEYWORD);
        assertNotNull("Should return highlighting for VALID_KEYWORD", keys);
        assertEquals("Should have one highlighting key", 1, keys.length);
        assertEquals("Should highlight as Bjoern keyword", BjoernSyntaxHighlighter.BJOERN_KEYWORD, keys[0]);
    }
    
    public void testInvalidKeywordHighlighting() {
        BjoernSyntaxHighlighter highlighter = new BjoernSyntaxHighlighter();
        
        // Test that invalid keywords are highlighted as errors
        TextAttributesKey[] keys = highlighter.getTokenHighlights(BjoernTokenTypes.INVALID_KEYWORD);
        assertNotNull("Should return highlighting for INVALID_KEYWORD", keys);
        assertEquals("Should have one highlighting key", 1, keys.length);
        assertEquals("Should highlight as invalid keyword", BjoernSyntaxHighlighter.BJOERN_INVALID_KEYWORD, keys[0]);
    }

    public void testVariableHighlighting() {
        BjoernSyntaxHighlighter highlighter = new BjoernSyntaxHighlighter();
        
        // Test that SCALAR_DSTRING tokens are highlighted as variables
        TextAttributesKey[] keys = highlighter.getTokenHighlights(YAMLTokenTypes.SCALAR_DSTRING);
        assertNotNull("Should return highlighting for SCALAR_DSTRING", keys);
        assertEquals("Should have one highlighting key", 1, keys.length);
        assertEquals("Should highlight as Bjoern variable", BjoernSyntaxHighlighter.BJOERN_VARIABLE, keys[0]);
    }

    public void testCustomDoubleQuotedStringHighlighting() {
        BjoernSyntaxHighlighter highlighter = new BjoernSyntaxHighlighter();
        
        // Test that our custom DOUBLE_QUOTED_STRING tokens are highlighted as variables
        TextAttributesKey[] keys = highlighter.getTokenHighlights(BjoernTokenTypes.DOUBLE_QUOTED_STRING);
        assertNotNull("Should return highlighting for DOUBLE_QUOTED_STRING", keys);
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