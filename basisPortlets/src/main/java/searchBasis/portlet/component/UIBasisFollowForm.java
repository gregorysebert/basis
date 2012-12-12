package searchBasis.portlet.component;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.form.UIForm;

import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinition;

/**
 * Created with IntelliJ IDEA.
 * User: Kevin
 * Date: 6/12/12
 * Time: 15:35
 * To change this template use File | Settings | File Templates.
 */
@ComponentConfig(
        lifecycle = UIFormLifecycle.class,
        template =  "app:/groovy/SearchBasis/portlet/UIBasisFollowForm.gtmpl"
)
public class UIBasisFollowForm extends UIForm {

    public UIBasisFollowForm() throws Exception {
        ExoContainer exoContainer = ExoContainerContext.getCurrentContainer();
        RepositoryService rs = (RepositoryService) exoContainer.getComponentInstanceOfType(RepositoryService.class);
        NodeType basisFolderNodetype = rs.getRepository("repository").getNodeTypeManager().getNodeType("basis:basisFollow");
        PropertyDefinition[] basisFolderNodetypeProperties = basisFolderNodetype.getPropertyDefinitions();
        for (PropertyDefinition property : basisFolderNodetypeProperties) {
            if(property.getName().contains("basis")) {
                if(!property.getName().contains("folderInternSender")) {
                    UIPropertyInputForm uiPropertyInputForm = addChild(UIPropertyInputForm.class,null, property.getName());
                    uiPropertyInputForm.load(property.getName(),property.getName().split("basis:")[1],"" );
                }
            }

        }
    }

    public void load(String parentName) throws Exception{

        ExoContainer exoContainer = ExoContainerContext.getCurrentContainer();
        RepositoryService rs = (RepositoryService) exoContainer.getComponentInstanceOfType(RepositoryService.class);
        NodeType basisFolderNodetype = rs.getRepository("repository").getNodeTypeManager().getNodeType("basis:basisFollow");
        PropertyDefinition[] basisFolderNodetypeProperties = basisFolderNodetype.getPropertyDefinitions();
        for (PropertyDefinition property : basisFolderNodetypeProperties) {
            if(property.getName().contains("basis")) {
                if(!property.getName().contains("folderInternSender")) {
                    UIPropertyInputForm uiPropertyInputForm = addChild(UIPropertyInputForm.class,null, property.getName());
                    uiPropertyInputForm.load(property.getName(),property.getName().split("basis:")[1],parentName );
                }
            }

        }
    }
}
