package no.imr.sea2data.stox.components.project;

import no.imr.guibase.node.BaseNode;
import no.imr.stox.api.IProjectProvider;
import org.openide.nodes.Children;

/**
 *
 * @author Åsmund
 */
public class ProjectNode  extends BaseNode {

    public ProjectNode(IProjectProvider provider) {
        super(provider, Children.LEAF);
        setDisplayName(provider.getProject().getProjectName());
        setIconBaseWithExtension("images/project_small2.png");
    }
}
