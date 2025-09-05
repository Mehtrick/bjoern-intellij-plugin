package de.mehtrick.bjoern;

import com.intellij.lang.Language;
import org.jetbrains.yaml.YAMLLanguage;

public class BjoernLanguage extends Language {
    public static final BjoernLanguage INSTANCE = new BjoernLanguage();

    private BjoernLanguage() {
        super(YAMLLanguage.INSTANCE, "Bjoern");
    }
}