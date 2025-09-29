package de.mehtrick.bjoern;

import com.intellij.lexer.LayeredLexer;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.yaml.YAMLTokenTypes;
import org.jetbrains.yaml.lexer.YAMLFlexLexer;

public class BjoernValidatingLexer extends LayeredLexer {
    public BjoernValidatingLexer() {
        super(new YAMLFlexLexer());
        
        // Register lexer for keyword validation
        registerSelfStoppingLayer(new BjoernKeywordValidationLexer(), 
                                  new IElementType[]{YAMLTokenTypes.SCALAR_KEY}, 
                                  IElementType.EMPTY_ARRAY);
        
        // Register combined lexer for double-quoted strings and hash comments in text content
        registerSelfStoppingLayer(new BjoernTextContentLexer(), 
                                  new IElementType[]{YAMLTokenTypes.TEXT}, 
                                  IElementType.EMPTY_ARRAY);
    }
}