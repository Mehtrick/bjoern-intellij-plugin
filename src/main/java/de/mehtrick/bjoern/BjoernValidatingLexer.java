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
        
        // Register lexer for double-quoted strings in text content
        registerSelfStoppingLayer(new BjoernDoubleQuotedStringLexer(), 
                                  new IElementType[]{YAMLTokenTypes.TEXT}, 
                                  IElementType.EMPTY_ARRAY);
        
        // Note: Removed custom comment lexer to let YAML handle comments natively
        // YAML should process hash comments as comment tokens automatically
    }
}