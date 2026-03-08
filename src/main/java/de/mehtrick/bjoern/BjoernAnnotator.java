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
 * Annotator that applies blue-underline (hyperlink) formatting to Reference field values
 * in Bjoern spec files that are valid markdown links: {@code [text](http(s)://url)}.
 * <p>
 * Plain text references (e.g. {@code "TICKET-123"}) and bare URLs are intentionally
 * not highlighted — only the markdown-link format receives the visual treatment.
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

        if (isValidMarkdownLink(element.getText())) {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(element)
                    .textAttributes(BjoernSyntaxHighlighter.BJOERN_VALID_LINK)
                    .create();
        }
    }

    /**
     * Returns {@code true} only when the value is a well-formed markdown link
     * whose URL uses the {@code http://} or {@code https://} scheme.
     * Bare URLs and plain text are intentionally excluded.
     */
    static boolean isValidMarkdownLink(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return false;
        }
        String s = stripOuterQuotes(rawValue.trim());

        // Must contain "](" to even be considered a markdown link attempt
        if (!s.contains("](")) {
            return false;
        }

        Matcher m = MARKDOWN_LINK_PATTERN.matcher(s);
        if (!m.matches()) {
            return false;
        }
        return isAllowedUrl(m.group(2).trim());
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
