package de.mehtrick.bjoern;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLTokenTypes;
import org.jetbrains.yaml.lexer.YAMLFlexLexer;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class BjoernSyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("BJOERN_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey STRING =
            createTextAttributesKey("BJOERN_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("BJOERN_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);

    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public com.intellij.lexer.Lexer getHighlightingLexer() {
        return new BjoernHighlightingLexer();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        if (tokenType == YAMLTokenTypes.SCALAR_KEY) {
            return KEYWORD_KEYS;
        } else if (tokenType == YAMLTokenTypes.SCALAR_STRING || 
                   tokenType == YAMLTokenTypes.SCALAR_DSTRING ||
                   tokenType == YAMLTokenTypes.TEXT) {
            return STRING_KEYS;
        } else if (tokenType == YAMLTokenTypes.COMMENT) {
            return COMMENT_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}