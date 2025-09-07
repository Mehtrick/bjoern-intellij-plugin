package de.mehtrick.bjoern;

import com.intellij.lexer.LayeredLexer;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.yaml.YAMLTokenTypes;
import org.jetbrains.yaml.lexer.YAMLFlexLexer;

public class BjoernLayeredLexer extends LayeredLexer {
    public BjoernLayeredLexer() {
        super(new YAMLFlexLexer());
        
        // Register a custom lexer for TEXT tokens to find double-quoted strings
        registerSelfStoppingLayer(new BjoernDoubleQuotedStringLexer(), 
                                  new IElementType[]{YAMLTokenTypes.TEXT}, 
                                  IElementType.EMPTY_ARRAY);
    }
}