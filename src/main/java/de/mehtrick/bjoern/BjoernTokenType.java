package de.mehtrick.bjoern;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class BjoernTokenType extends IElementType {
    public BjoernTokenType(@NotNull @NonNls String debugName) {
        super(debugName, BjoernLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "BjoernTokenType." + super.toString();
    }
}