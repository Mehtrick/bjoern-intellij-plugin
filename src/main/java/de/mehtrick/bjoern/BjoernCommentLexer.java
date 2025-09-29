package de.mehtrick.bjoern;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLTokenTypes;

public class BjoernCommentLexer extends LexerBase {
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
        
        // Look for hash comments in the text
        if (currentPosition < endOffset && buffer.charAt(currentPosition) == '#') {
            // Find the end of the line or end of buffer
            int commentEnd = findLineEnd(currentPosition);
            currentTokenEnd = commentEnd;
            currentTokenType = BjoernTokenTypes.COMMENT;
            currentPosition = currentTokenEnd;
            return;
        }
        
        // Skip to next potential hash or end of text
        int nextHash = findNextHash(currentPosition);
        if (nextHash == -1) {
            // No more hashes, consume rest as text
            currentTokenEnd = endOffset;
            currentTokenType = YAMLTokenTypes.TEXT;
            currentPosition = endOffset;
        } else {
            // Consume text up to the hash
            currentTokenEnd = nextHash;
            currentTokenType = YAMLTokenTypes.TEXT;
            currentPosition = nextHash;
        }
    }

    private int findLineEnd(int startPos) {
        for (int i = startPos; i < endOffset; i++) {
            char c = buffer.charAt(i);
            if (c == '\n' || c == '\r') {
                return i;
            }
        }
        return endOffset; // End of buffer
    }

    private int findNextHash(int startPos) {
        for (int i = startPos; i < endOffset; i++) {
            if (buffer.charAt(i) == '#') {
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