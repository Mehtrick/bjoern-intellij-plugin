package de.mehtrick.bjoern;

import com.intellij.psi.tree.IElementType;

public class BjoernTokenTypes {
    public static final IElementType DOUBLE_QUOTED_STRING = new IElementType("BJOERN_DOUBLE_QUOTED_STRING", BjoernLanguage.INSTANCE);
    public static final IElementType INVALID_KEYWORD = new IElementType("BJOERN_INVALID_KEYWORD", BjoernLanguage.INSTANCE);
    public static final IElementType VALID_KEYWORD = new IElementType("BJOERN_VALID_KEYWORD", BjoernLanguage.INSTANCE);
    public static final IElementType COMMENT = new IElementType("BJOERN_COMMENT", BjoernLanguage.INSTANCE);
}