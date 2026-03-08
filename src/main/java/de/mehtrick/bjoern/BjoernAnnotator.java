package de.mehtrick.bjoern;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLValue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Annotator that applies blue-underline (hyperlink) formatting to valid Reference field values
 * in Bjoern spec files. A value is considered a valid link if it is:
 * <ul>
 *   <li>A bare {@code http://} or {@code https://} URL, or</li>
 *   <li>A markdown link {@code [text](http(s)://url)} with a valid URL.</li>
 * </ul>
 */
public class BjoernAnnotator implements Annotator {

    private static final Pattern MARKDOWN_LINK_PATTERN = Pattern.compile("\\[([^\\]]*)]\\(([^)]+)\\)");

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!element.getContainingFile().getName().endsWith(".zgr")) {
            return;
        }

        if (!(element instanceof YAMLValue)) {
            return;
        }

        PsiElement parent = element.getParent();
        if (!(parent instanceof YAMLKeyValue kv)) {
            return;
        }
        if (!"Reference".equals(kv.getKeyText())) {
            return;
        }

        if (isValidLink(element.getText())) {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(element)
                    .textAttributes(BjoernSyntaxHighlighter.BJOERN_VALID_LINK)
                    .create();
        }
    }

    static boolean isValidLink(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return false;
        }
        String s = stripOuterQuotes(rawValue.trim());

        if (s.contains("](")) {
            Matcher m = MARKDOWN_LINK_PATTERN.matcher(s);
            if (!m.matches()) {
                return false;
            }
            return isAllowedUrl(m.group(2).trim());
        }

        String lower = s.toLowerCase();
        return lower.startsWith("http://") || lower.startsWith("https://");
    }

    private static String stripOuterQuotes(String s) {
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    private static boolean isAllowedUrl(String url) {
        String lower = url.toLowerCase();
        return lower.startsWith("http://") || lower.startsWith("https://");
    }
}
