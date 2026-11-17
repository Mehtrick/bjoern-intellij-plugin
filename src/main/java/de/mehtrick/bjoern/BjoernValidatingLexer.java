package de.mehtrick.bjoern;

import com.intellij.lexer.LayeredLexer;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.yaml.YAMLParserDefinition;
import org.jetbrains.yaml.YAMLTokenTypes;

public class BjoernValidatingLexer extends LayeredLexer {
    public BjoernValidatingLexer() {
        // Use YAML's compatibility adapter instead of a lexer implementation removed in 2026.3.
        super(new YAMLParserDefinition().createLexer(ProjectManager.getInstance().getDefaultProject()));
        
        // Register lexer for keyword validation
        registerSelfStoppingLayer(new BjoernKeywordValidationLexer(), 
                                  new IElementType[]{YAMLTokenTypes.SCALAR_KEY}, 
                                  IElementType.EMPTY_ARRAY);
        
        // Register lexer for double-quoted strings in text content
        registerSelfStoppingLayer(new BjoernDoubleQuotedStringLexer(), 
                                  new IElementType[]{YAMLTokenTypes.TEXT}, 
                                  IElementType.EMPTY_ARRAY);
    }
}
