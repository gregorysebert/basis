package searchBasis.portlet.component;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.webui.container.UIContainer;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.webui.config.annotation.ComponentConfig;

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
        template =  "app:/groovy/SearchBasis/portlet/UIBasisDocForm.gtmpl"
)
public class UIBasisDocForm extends UIContainer  {

    public static final String FIELD_DOC_ID = "exo:title" ;
    private   PropertyDefinition[] basisDocNodetypeProperties;

    public UIBasisDocForm() throws Exception {
    }

    public void  load(String parent) throws Exception {
        ExoContainer exoContainer = ExoContainerContext.getCurrentContainer();
        RepositoryService rs = (RepositoryService) exoContainer.getComponentInstanceOfType(RepositoryService.class);
        NodeType basisFolderNodetype = rs.getRepository("repository").getNodeTypeManager().getNodeType("basis:basisDocument");
        basisDocNodetypeProperties = basisFolderNodetype.getPropertyDefinitions();
        UIPropertyInputForm uiPropertyInputForm = addChild(UIPropertyInputForm.class,null, FIELD_DOC_ID);
        uiPropertyInputForm.load(null,"basisDocument.label.docId");
        for (PropertyDefinition property : basisDocNodetypeProperties) {
            if(property.getName().contains("basis")) {
                if(!property.getName().contains("docSenderType")) {
                    uiPropertyInputForm = addChild(UIPropertyInputForm.class,null, parent+"_"+property.getName());
                    uiPropertyInputForm.load(property,"basisDocument");
                }
            }

        }
    }

    public PropertyDefinition[] getBasisDocNodetypeProperties() {
        return basisDocNodetypeProperties;
    }

    public void setBasisDocNodetypeProperties(PropertyDefinition[] basisDocNodetypeProperties) {
        this.basisDocNodetypeProperties = basisDocNodetypeProperties;
    }
}
