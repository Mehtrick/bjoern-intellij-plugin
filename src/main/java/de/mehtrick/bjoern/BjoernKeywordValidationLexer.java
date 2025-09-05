package de.mehtrick.bjoern;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLTokenTypes;

import java.util.Set;

public class BjoernKeywordValidationLexer extends LexerBase {
    private static final Set<String> VALID_BDD_KEYWORDS = Set.of(
            "Feature", "Background", "Given", "When", "Then", "Scenario", "Scenarios"
    );
    
    private CharSequence buffer;
    private int startOffset;
    private int endOffset;
    private int currentPosition;
    private IElementType currentTokenType;
    private int currentTokenStart;
    private int currentTokenEnd;

    @Override
    public void start(CharSequence buffer, int startOffset, int endOffset, int initialState) {
        this.buffer = buffer;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.currentPosition = startOffset;
        advance();
    }

    @Override
    public int getState() {
        return 0;
    }

    @Override
    public @Nullable IElementType getTokenType() {
        return currentTokenType;
    }

    @Override
    public int getTokenStart() {
        return currentTokenStart;
    }

    @Override
    public int getTokenEnd() {
        return currentTokenEnd;
    }

    @Override
    public void advance() {
        if (currentPosition >= endOffset) {
            currentTokenType = null;
            return;
        }

        currentTokenStart = currentPosition;
        currentTokenEnd = endOffset;
        
        // Extract the keyword text
        String keywordText = buffer.subSequence(currentTokenStart, currentTokenEnd).toString().trim();
        
        // Remove colon if present
        if (keywordText.endsWith(":")) {
            keywordText = keywordText.substring(0, keywordText.length() - 1);
        }
        
        // Check if it's a valid BDD keyword
        if (VALID_BDD_KEYWORDS.contains(keywordText)) {
            currentTokenType = BjoernTokenTypes.VALID_KEYWORD;
        } else {
            currentTokenType = BjoernTokenTypes.INVALID_KEYWORD;
        }
        
        currentPosition = endOffset;
    }

    @Override
    public CharSequence getBufferSequence() {
        return buffer;
    }

    @Override
    public int getBufferEnd() {
        return endOffset;
    }
}