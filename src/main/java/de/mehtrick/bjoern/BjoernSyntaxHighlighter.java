package de.mehtrick.bjoern;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLTokenTypes;

import java.util.Set;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class BjoernSyntaxHighlighter extends SyntaxHighlighterBase {
    // Valid BDD keywords
    private static final Set<String> VALID_BDD_KEYWORDS = Set.of(
            "Feature", "Background", "Given", "When", "Then", "Scenario", "Scenarios"
    );
    
    public static final TextAttributesKey BJOERN_KEYWORD =
            createTextAttributesKey("BJOERN_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    
    // Invalid keyword highlighting
    public static final TextAttributesKey BJOERN_INVALID_KEYWORD =
            createTextAttributesKey("BJOERN_INVALID_KEYWORD", DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE);
    
    public static final TextAttributesKey BJOERN_SCALAR_KEY =
            createTextAttributesKey("BJOERN_SCALAR_KEY", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
    
    // Normal text with subtle coloring (default identifier color which adapts to theme)
    public static final TextAttributesKey BJOERN_SCALAR_VALUE =
            createTextAttributesKey("BJOERN_SCALAR_VALUE", DefaultLanguageHighlighterColors.IDENTIFIER);
    
    // Enhanced attribute for variables (double-quoted strings) - more vibrant color
    public static final TextAttributesKey BJOERN_VARIABLE =
            createTextAttributesKey("BJOERN_VARIABLE", DefaultLanguageHighlighterColors.CONSTANT);

    // Comment highlighting
    public static final TextAttributesKey BJOERN_COMMENT =
            createTextAttributesKey("BJOERN_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);

    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{BJOERN_KEYWORD};
    private static final TextAttributesKey[] INVALID_KEYWORD_KEYS = new TextAttributesKey[]{BJOERN_INVALID_KEYWORD};
    private static final TextAttributesKey[] SCALAR_KEY_KEYS = new TextAttributesKey[]{BJOERN_SCALAR_KEY};
    private static final TextAttributesKey[] SCALAR_VALUE_KEYS = new TextAttributesKey[]{BJOERN_SCALAR_VALUE};
    private static final TextAttributesKey[] VARIABLE_KEYS = new TextAttributesKey[]{BJOERN_VARIABLE};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{BJOERN_COMMENT};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @Override
    public @NotNull Lexer getHighlightingLexer() {
        // Use custom lexer that can handle keyword validation and double-quoted strings
        return new BjoernValidatingLexer();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        // Handle valid BDD keywords
        if (tokenType == BjoernTokenTypes.VALID_KEYWORD) {
            return KEYWORD_KEYS;
        }
        // Handle invalid keywords
        else if (tokenType == BjoernTokenTypes.INVALID_KEYWORD) {
            return INVALID_KEYWORD_KEYS;
        }
        // Handle BDD keywords as special keys
        else if (tokenType == YAMLTokenTypes.SCALAR_KEY) {
            return SCALAR_KEY_KEYS;
        } 
        // Handle double-quoted strings as variables
        else if (tokenType == YAMLTokenTypes.SCALAR_DSTRING) {
            return VARIABLE_KEYS;
        }
        // Handle our custom double-quoted token as variables
        else if (tokenType == BjoernTokenTypes.DOUBLE_QUOTED_STRING) {
            return VARIABLE_KEYS;
        }
        // Handle hash comments
        else if (tokenType == BjoernTokenTypes.COMMENT) {
            return COMMENT_KEYS;
        }
        // Handle other string types as regular values
        else if (tokenType == YAMLTokenTypes.SCALAR_STRING) {
            return SCALAR_VALUE_KEYS;
        } 
        else if (tokenType == YAMLTokenTypes.TEXT) {
            return SCALAR_VALUE_KEYS;
        }
        
        return EMPTY_KEYS;
    }
}