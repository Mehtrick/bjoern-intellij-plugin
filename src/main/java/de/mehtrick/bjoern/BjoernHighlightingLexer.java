package de.mehtrick.bjoern;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLTokenTypes;
import org.jetbrains.yaml.lexer.YAMLFlexLexer;

import java.util.regex.Pattern;

public class BjoernHighlightingLexer extends LexerBase {
    private final YAMLFlexLexer yamlLexer;
    private CharSequence buffer;
    private int startOffset;
    private int endOffset;
    private int currentOffset;
    private IElementType currentTokenType;
    
    private static final Pattern BJOERN_KEYWORDS = Pattern.compile(
        "^\\s*(Feature|Background|Given|When|Then|Scenarios?|And)\\s*:"
    );

    public BjoernHighlightingLexer() {
        this.yamlLexer = new YAMLFlexLexer();
    }

    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        this.buffer = buffer;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.currentOffset = startOffset;
        yamlLexer.reset(buffer, startOffset, endOffset, initialState);
        advance();
    }

    @Override
    public int getState() {
        return yamlLexer.getState();
    }

    @Nullable
    @Override
    public IElementType getTokenType() {
        return currentTokenType;
    }

    @Override
    public int getTokenStart() {
        return yamlLexer.getTokenStart();
    }

    @Override
    public int getTokenEnd() {
        return yamlLexer.getTokenEnd();
    }

    @Override
    public void advance() {
        if (currentOffset >= endOffset) {
            currentTokenType = null;
            return;
        }

        yamlLexer.advance();
        currentTokenType = yamlLexer.getTokenType();
        
        // Enhanced highlighting for Bjoern keywords
        if (currentTokenType == YAMLTokenTypes.SCALAR_KEY) {
            String tokenText = yamlLexer.getTokenText();
            if (tokenText != null && isBjoernKeyword(tokenText)) {
                // Keep it as a key but it will be highlighted as keyword
                currentTokenType = YAMLTokenTypes.SCALAR_KEY;
            }
        }
        
        currentOffset = yamlLexer.getTokenEnd();
    }

    private boolean isBjoernKeyword(String text) {
        return text.matches("(?i)^\\s*(Feature|Background|Given|When|Then|Scenarios?|And)\\s*$");
    }

    @NotNull
    @Override
    public CharSequence getBufferSequence() {
        return buffer;
    }

    @Override
    public int getBufferEnd() {
        return endOffset;
    }
}