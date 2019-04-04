package no.imr.sea2data.stox.editor;

/**
 * What does this class do????
 *
 * @author aasmunds
 */
import java.awt.Component;
import java.beans.*;
import no.imr.stox.dlg.SelectFileDlg;
import no.imr.stox.model.IProject;

public class ProjectFileNameEditor extends PropertyEditorSupport {
    IProject project;
    String defPath;
    Boolean dirOnly;

    public ProjectFileNameEditor(IProject project, String defPath, Boolean dirOnly) {
        this.project = project;
        this.defPath = defPath;
        this.dirOnly = dirOnly;
    }
    
    @Override
    public String getJavaInitializationString() {
        Object value = getValue();
        if (value == null) {
            return "null";
        }

        String str = value.toString();
        int length = str.length();
        StringBuilder sb = new StringBuilder(length + 2);
        sb.append('"');
        for (int i = 0; i < length; i++) {
            char ch = str.charAt(i);
            switch (ch) {
                case '\b':
                    sb.append("\\b");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                default:
                    if ((ch < ' ') || (ch > '~')) {
                        sb.append("\\u");
                        String hex = Integer.toHexString((int) ch);
                        for (int len = hex.length(); len < 4; len++) {
                            sb.append('0');
                        }
                        sb.append(hex);
                    } else {
                        sb.append(ch);
                    }
                    break;
            }
        }
        sb.append('"');
        return sb.toString();
    }

    @Override
    public void setAsText(String text) {
        setValue(text);
    }

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    @Override
    public Component getCustomEditor() {
        return new SelectFileDlg(project, defPath, this, dirOnly);
    }

}
