package de.mehtrick.bjoern;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

public class BjoernFile extends PsiFileBase {
    public BjoernFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, BjoernLanguage.INSTANCE);
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