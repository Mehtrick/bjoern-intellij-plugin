package de.mehtrick.bjoern;

import com.intellij.lexer.FlexAdapter;

public class BjoernLexerAdapter extends FlexAdapter {
    public BjoernLexerAdapter() {
        super(new BjoernLexer(null));
    }
}