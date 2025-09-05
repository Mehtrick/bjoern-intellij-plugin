package de.mehtrick.bjoern;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLSyntaxHighlighter;
import org.jetbrains.yaml.YAMLTokenTypes;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class BjoernSyntaxHighlighter extends YAMLSyntaxHighlighter {
    public static final TextAttributesKey BJOERN_KEYWORD =
            createTextAttributesKey("BJOERN_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);

    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{BJOERN_KEYWORD};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        // First get the default YAML highlighting
        TextAttributesKey[] yamlHighlights = super.getTokenHighlights(tokenType);
        
        // Override specific YAML keys with BDD keyword highlighting
        if (tokenType == YAMLTokenTypes.SCALAR_KEY) {
            return KEYWORD_KEYS;
        }
        
        return yamlHighlights;
    }
}