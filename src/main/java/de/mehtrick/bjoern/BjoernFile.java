package de.mehtrick.bjoern;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.impl.YAMLFileImpl;

public class BjoernFile extends YAMLFileImpl {
    public BjoernFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return BjoernFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Bjoern File";
    }
}