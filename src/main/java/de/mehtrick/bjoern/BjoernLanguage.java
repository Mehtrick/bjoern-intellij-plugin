package de.mehtrick.bjoern;

import com.intellij.lang.Language;

public class BjoernLanguage extends Language {
    public static final BjoernLanguage INSTANCE = new BjoernLanguage();

    private BjoernLanguage() {
        super("Bjoern");
    }
}