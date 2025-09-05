package de.mehtrick.bjoern;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLTokenTypes;
import org.jetbrains.yaml.lexer.YAMLFlexLexer;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class BjoernSyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey BJOERN_KEYWORD =
            createTextAttributesKey("BJOERN_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    
    public static final TextAttributesKey YAML_SCALAR_KEY =
            createTextAttributesKey("YAML_SCALAR_KEY", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
    
    public static final TextAttributesKey YAML_SCALAR_VALUE =
            createTextAttributesKey("YAML_SCALAR_VALUE", DefaultLanguageHighlighterColors.STRING);

    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{BJOERN_KEYWORD};
    private static final TextAttributesKey[] SCALAR_KEY_KEYS = new TextAttributesKey[]{YAML_SCALAR_KEY};
    private static final TextAttributesKey[] SCALAR_VALUE_KEYS = new TextAttributesKey[]{YAML_SCALAR_VALUE};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return new YAMLFlexLexer();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        // Handle BDD keywords as special keys
        if (tokenType == YAMLTokenTypes.SCALAR_KEY) {
            return KEYWORD_KEYS;
        } else if (tokenType == YAMLTokenTypes.SCALAR_STRING || tokenType == YAMLTokenTypes.SCALAR_DSTRING) {
            return SCALAR_VALUE_KEYS;
        } else if (tokenType == YAMLTokenTypes.TEXT) {
            return SCALAR_VALUE_KEYS;
        }
        
        return EMPTY_KEYS;
    }
}