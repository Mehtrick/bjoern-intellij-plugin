package de.mehtrick.bjoern;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class BjoernFileType extends LanguageFileType {
    public static final BjoernFileType INSTANCE = new BjoernFileType();

    private BjoernFileType() {
        super(BjoernLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Bjoern File";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Bjoern BDD specification file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "zgr";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return IconLoader.getIcon("/icons/icon.svg", BjoernFileType.class);
    }
}