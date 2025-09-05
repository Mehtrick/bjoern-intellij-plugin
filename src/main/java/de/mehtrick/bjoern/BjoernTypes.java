package de.mehtrick.bjoern;

import com.intellij.psi.tree.IElementType;

public class BjoernTypes {
    public static final IElementType FEATURE = new BjoernTokenType("FEATURE");
    public static final IElementType BACKGROUND = new BjoernTokenType("BACKGROUND");
    public static final IElementType GIVEN = new BjoernTokenType("GIVEN");
    public static final IElementType WHEN = new BjoernTokenType("WHEN");
    public static final IElementType THEN = new BjoernTokenType("THEN");
    public static final IElementType SCENARIO = new BjoernTokenType("SCENARIO");
    public static final IElementType SCENARIOS = new BjoernTokenType("SCENARIOS");
    public static final IElementType DASH = new BjoernTokenType("DASH");
    public static final IElementType COLON = new BjoernTokenType("COLON");
    public static final IElementType STRING = new BjoernTokenType("STRING");
    public static final IElementType COMMENT = new BjoernTokenType("COMMENT");
    public static final IElementType IDENTIFIER = new BjoernTokenType("IDENTIFIER");
}