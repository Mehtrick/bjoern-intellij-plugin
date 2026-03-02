package de.mehtrick.bjoern;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles Tab key navigation between parameter placeholders ("") in Bjoern .zgr files.
 * <p>
 * When the cursor is inside a quoted parameter, pressing Tab jumps to the next parameter.
 * If the target field already contains a value, the value is selected for easy replacement.
 * Navigation wraps around from the last parameter back to the first.
 */
public class BjoernTabHandler extends EditorActionHandler {

    private static final Pattern PARAMETER_PATTERN = Pattern.compile("\"[^\"]*\"");

    private final EditorActionHandler myOriginalHandler;

    public BjoernTabHandler(EditorActionHandler originalHandler) {
        this.myOriginalHandler = originalHandler;
    }

    @Override
    protected boolean isEnabledForCaret(@NotNull Editor editor, @NotNull Caret caret, DataContext dataContext) {
        if (isInBjoernFile(editor)) {
            String text = editor.getDocument().getText();
            int offset = caret.getOffset();
            if (isInsideParameter(text, offset)) {
                return true;
            }
        }
        return myOriginalHandler != null && myOriginalHandler.isEnabled(editor, caret, dataContext);
    }

    @Override
    protected void doExecute(@NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {
        if (isInBjoernFile(editor)) {
            int offset = caret != null ? caret.getOffset() : editor.getCaretModel().getOffset();
            if (navigateToNextParameter(editor, offset)) {
                return;
            }
        }
        if (myOriginalHandler != null) {
            myOriginalHandler.execute(editor, caret, dataContext);
        }
    }

    private boolean isInBjoernFile(Editor editor) {
        VirtualFile file = FileDocumentManager.getInstance().getFile(editor.getDocument());
        return file != null && "zgr".equals(file.getExtension());
    }

    static boolean isInsideParameter(String text, int offset) {
        Matcher matcher = PARAMETER_PATTERN.matcher(text);
        while (matcher.find()) {
            // "inside" means after the opening quote and before the closing quote
            if (offset > matcher.start() && offset < matcher.end()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds all quoted parameter positions, locates the current one, then moves the caret
     * to the next parameter (wrapping around). Selects non-empty content so it can be
     * immediately overwritten by typing.
     *
     * @return true if navigation was performed, false if no parameters found
     */
    static boolean navigateToNextParameter(Editor editor, int currentOffset) {
        String text = editor.getDocument().getText();
        int[] target = findNextParameter(text, currentOffset);
        if (target == null) {
            return false;
        }

        int contentStart = target[0] + 1; // position after the opening quote
        int contentEnd = target[1] - 1;   // position before the closing quote

        editor.getCaretModel().moveToOffset(contentStart);
        if (contentEnd > contentStart) {
            // Field has content – select it so it can be easily replaced by typing
            editor.getSelectionModel().setSelection(contentStart, contentEnd);
        } else {
            editor.getSelectionModel().removeSelection();
        }

        return true;
    }

    /**
     * Pure computation: given the document text and the current caret offset, returns the
     * [startQuote, endQuote) range of the next parameter to navigate to, or {@code null}
     * if the cursor is not inside any parameter.  Navigation wraps around at the last
     * parameter.
     */
    static int[] findNextParameter(String text, int currentOffset) {
        List<int[]> params = new ArrayList<>();
        Matcher matcher = PARAMETER_PATTERN.matcher(text);
        while (matcher.find()) {
            params.add(new int[]{matcher.start(), matcher.end()});
        }

        if (params.isEmpty()) {
            return null;
        }

        // Find the index of the parameter the cursor is currently inside
        int currentParamIndex = -1;
        for (int i = 0; i < params.size(); i++) {
            int[] param = params.get(i);
            if (currentOffset > param[0] && currentOffset < param[1]) {
                currentParamIndex = i;
                break;
            }
        }

        if (currentParamIndex == -1) {
            return null;
        }

        // Navigate to next parameter, wrapping around at the end
        int nextParamIndex = (currentParamIndex + 1) % params.size();
        return params.get(nextParamIndex);
    }
}
