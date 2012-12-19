package searchBasis.portlet.component;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormRadioBoxInput;

import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinition;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Kevin
 * Date: 6/12/12
 * Time: 15:35
 * To change this template use File | Settings | File Templates.
 */
@ComponentConfig(
        lifecycle = UIFormLifecycle.class,
        template =  "app:/groovy/SearchBasis/portlet/UIBasisFollowFolderForm.gtmpl"
)
public class UIBasisFollowFolderForm extends UIForm {

    public static final String FIELD_RADIOBOX = "RadioBox_folder" ;
    private PropertyDefinition[] basisFollowFolderNodetypeProperties;

    public UIBasisFollowFolderForm() throws Exception {

    }

    public void  load(String parent) throws Exception {
        ExoContainer exoContainer = ExoContainerContext.getCurrentContainer();
        RepositoryService rs = (RepositoryService) exoContainer.getComponentInstanceOfType(RepositoryService.class);
        NodeType basisFolderNodetype = rs.getRepository("repository").getNodeTypeManager().getNodeType("basis:basisFollow");
        basisFollowFolderNodetypeProperties = basisFolderNodetype.getPropertyDefinitions();
        for (PropertyDefinition property : basisFollowFolderNodetypeProperties) {
            if(property.getName().contains("basis")) {
                if(!property.getName().contains("followEditorType")) {
                    UIPropertyInputForm uiPropertyInputForm = addChild(UIPropertyInputForm.class,null, parent+"_"+property.getName());
                    uiPropertyInputForm.load(property,"folder_basisFollow");
                }
            }

        }
        List<SelectItemOption<String>> lsRadioBox = new ArrayList<SelectItemOption<String>>() ;
        addUIFormInput(new UIFormRadioBoxInput(FIELD_RADIOBOX,FIELD_RADIOBOX,lsRadioBox));
    }

    public PropertyDefinition[] getBasisFollowFolderNodetypeProperties() {
        return basisFollowFolderNodetypeProperties;
    }

    public void setBasisFollowFolderNodetypeProperties(PropertyDefinition[] basisFollowFolderNodetypeProperties) {
        this.basisFollowFolderNodetypeProperties = basisFollowFolderNodetypeProperties;
    }
}
