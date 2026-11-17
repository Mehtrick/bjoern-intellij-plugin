package de.mehtrick.bjoern;

import com.intellij.lang.ASTNode;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLParserDefinition;

public class BjoernParserDefinition extends YAMLParserDefinition {
    private static final IFileElementType YAML_FILE = new YAMLParserDefinition().getFileNodeType();
    public static final IFileElementType FILE = new IFileElementType(BjoernLanguage.INSTANCE) {
        @Override
        public ASTNode parseContents(@NotNull ASTNode chameleon) {
            // Preserve Bjoern's file language while letting YAML choose its parsing pipeline.
            return YAML_FILE.parseContents(chameleon);
        }
    };

    @Override
    public @NotNull IFileElementType getFileNodeType() {
        return FILE;
    }

    @Override
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new BjoernFile(viewProvider);
    }
}
