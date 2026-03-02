package de.mehtrick.bjoern;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.NotNull;

public class BjoernCreateFileAction extends CreateFileFromTemplateAction {

    private static final String NEW_BJOERN_SPEC = "New Bjoern Spec File";

    public BjoernCreateFileAction() {
        super(NEW_BJOERN_SPEC, "Creates a new Bjoern BDD specification file", BjoernFileType.INSTANCE.getIcon());
    }

    @Override
    protected void buildDialog(@NotNull Project project, @NotNull PsiDirectory directory,
                               @NotNull CreateFileFromTemplateDialog.Builder builder) {
        builder.setTitle(NEW_BJOERN_SPEC)
                .addKind("Bjoern Spec (.zgr)", BjoernFileType.INSTANCE.getIcon(), "Bjoern Spec");
    }

    @Override
    protected String getActionName(PsiDirectory directory, @NotNull String newName, String templateName) {
        return NEW_BJOERN_SPEC + ": " + newName;
    }
}
