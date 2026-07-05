package de.mehtrick.bjoern;

import com.intellij.codeInspection.*;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Inspection that validates the Reference field in Bjoern spec files.
 * <p>
 * Rules:
 * <ul>
 *   <li>URLs embedded in Reference values must use {@code http://} or {@code https://} schemes.</li>
 *   <li>Markdown-style links {@code [text](url)} must have the correct {@code [text](url)} format.</li>
 * </ul>
 */
public class BjoernReferenceInspection extends LocalInspectionTool {

    // Matches any URL-scheme portion like "ftp://", "file://", etc.
    private static final Pattern URL_SCHEME_PATTERN = Pattern.compile("[a-zA-Z][a-zA-Z0-9+\\-.]*://");

    // Matches a markdown link: [text](url)
    private static final Pattern MARKDOWN_LINK_PATTERN = Pattern.compile("\\[([^\\]]*)]\\(([^)]+)\\)");

    static boolean isValidReferenceValue(String value) {
        if (value == null || value.isBlank()) {
            return true;
        }

        String trimmed = stripOuterQuotes(value.trim());

        // Only validate as markdown link if the text contains the "](" sequence, which
        // distinguishes a markdown link attempt from plain text containing brackets.
        if (trimmed.contains("](")) {
            Matcher mdMatcher = MARKDOWN_LINK_PATTERN.matcher(trimmed);
            if (!mdMatcher.matches()) {
                // Looks like a markdown link attempt but has invalid format
                return false;
            }
            // Validate the URL in the markdown link
            String url = mdMatcher.group(2).trim();
            return isAllowedUrl(url);
        }

        // Check any embedded URL scheme in plain-text values
        Matcher schemeMatcher = URL_SCHEME_PATTERN.matcher(trimmed);
        while (schemeMatcher.find()) {
            String scheme = schemeMatcher.group().toLowerCase();
            if (!scheme.startsWith("http://") && !scheme.startsWith("https://")) {
                return false;
            }
        }

        return true;
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

    @Override
    public ProblemDescriptor @NotNull [] checkFile(@NotNull PsiFile file,
                                                    @NotNull InspectionManager manager,
                                                    boolean isOnTheFly) {
        if (!file.getName().endsWith(".zgr")) {
            return ProblemDescriptor.EMPTY_ARRAY;
        }

        List<ProblemDescriptor> problems = new ArrayList<>();

        if (!(file instanceof YAMLFile yamlFile)) {
            return ProblemDescriptor.EMPTY_ARRAY;
        }

        for (YAMLDocument document : yamlFile.getDocuments()) {
            YAMLValue topValue = document.getTopLevelValue();
            if (!(topValue instanceof YAMLMapping mapping)) {
                continue;
            }

            YAMLKeyValue referenceKV = mapping.getKeyValueByKey("Reference");
            if (referenceKV == null) {
                continue;
            }

            YAMLValue valueNode = referenceKV.getValue();
            if (valueNode == null) {
                continue;
            }

            String rawValue = valueNode.getText();
            if (!isValidReferenceValue(rawValue)) {
                String message = buildProblemMessage(rawValue);
                problems.add(manager.createProblemDescriptor(
                        valueNode,
                        TextRange.from(0, valueNode.getTextLength()),
                        message,
                        ProblemHighlightType.WARNING,
                        isOnTheFly));
            }
        }

        return problems.toArray(ProblemDescriptor.EMPTY_ARRAY);
    }

    private static String buildProblemMessage(String rawValue) {
        String trimmed = stripOuterQuotes(rawValue.trim());
        if (trimmed.contains("](") && !MARKDOWN_LINK_PATTERN.matcher(trimmed).matches()) {
            return "Reference: invalid markdown link format. Expected [text](url).";
        }
        return "Reference: only 'http://' and 'https://' URL schemes are allowed.";
    }
}
