package de.mehtrick.bjoern;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLTokenTypes;

public class BjoernDoubleQuotedStringLexer extends LexerBase {
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
        
        // Look for double-quoted strings in the text
        if (currentPosition < endOffset && buffer.charAt(currentPosition) == '"') {
            // Find the closing quote
            int closeQuote = findClosingQuote(currentPosition + 1);
            if (closeQuote != -1) {
                currentTokenEnd = closeQuote + 1;
                currentTokenType = BjoernTokenTypes.DOUBLE_QUOTED_STRING;
                currentPosition = currentTokenEnd;
                return;
            }
        }
        
        // Skip to next potential quote or end of text
        int nextQuote = findNextQuote(currentPosition);
        if (nextQuote == -1) {
            // No more quotes, consume rest as text
            currentTokenEnd = endOffset;
            currentTokenType = YAMLTokenTypes.TEXT;
            currentPosition = endOffset;
        } else {
            // Consume text up to the quote
            currentTokenEnd = nextQuote;
            currentTokenType = YAMLTokenTypes.TEXT;
            currentPosition = nextQuote;
        }
    }

    private int findClosingQuote(int startPos) {
        for (int i = startPos; i < endOffset; i++) {
            char c = buffer.charAt(i);
            if (c == '"') {
                // Check if it's escaped
                int backslashes = 0;
                for (int j = i - 1; j >= startPos && buffer.charAt(j) == '\\'; j--) {
                    backslashes++;
                }
                if (backslashes % 2 == 0) {
                    return i; // Even number of backslashes means not escaped
                }
            }
        }
        return -1; // No closing quote found
    }

    private int findNextQuote(int startPos) {
        for (int i = startPos; i < endOffset; i++) {
            if (buffer.charAt(i) == '"') {
                return i;
            }
        }
        return -1;
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