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
        template =  "app:/groovy/SearchBasis/portlet/UIBasisDocForm.gtmpl"
)
public class UIBasisDocForm extends UIForm {

    public static final String FIELD_DOC_ID = "exo:title" ;

    public UIBasisDocForm() throws Exception {
        ExoContainer exoContainer = ExoContainerContext.getCurrentContainer();
        RepositoryService rs = (RepositoryService) exoContainer.getComponentInstanceOfType(RepositoryService.class);
        NodeType basisFolderNodetype = rs.getRepository("repository").getNodeTypeManager().getNodeType("basis:basisDocument");
        PropertyDefinition[] basisFolderNodetypeProperties = basisFolderNodetype.getPropertyDefinitions();
        UIPropertyInputForm uiPropertyInputForm = addChild(UIPropertyInputForm.class,null, FIELD_DOC_ID);
        uiPropertyInputForm.load(FIELD_DOC_ID,"basisDocument.label.docId",1 );
        for (PropertyDefinition property : basisFolderNodetypeProperties) {
            if(property.getName().contains("basis")) {
                if(!property.getName().contains("docSenderType")) {
                    String labelProperty;
                    if(!property.getName().contains("Comments")) {
                        labelProperty = "basisDocument.label." + property.getName().split("basis:")[1];
                    }
                    else{
                        labelProperty = "basis.label." + property.getName().split("basis:doc")[1].toLowerCase();
                    }
                    uiPropertyInputForm = addChild(UIPropertyInputForm.class,null, property.getName());
                    uiPropertyInputForm.load(property.getName(),labelProperty,property.getRequiredType() );
                }
            }

        }
    }
}
