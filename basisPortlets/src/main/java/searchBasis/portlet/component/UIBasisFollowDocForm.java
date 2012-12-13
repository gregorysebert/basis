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
        template =  "app:/groovy/SearchBasis/portlet/UIBasisFollowDocForm.gtmpl"
)
public class UIBasisFollowDocForm extends UIForm {

    public static final String FIELD_RADIOBOX = "RadioBox" ;

    public UIBasisFollowDocForm() throws Exception {
        ExoContainer exoContainer = ExoContainerContext.getCurrentContainer();
        RepositoryService rs = (RepositoryService) exoContainer.getComponentInstanceOfType(RepositoryService.class);
        NodeType basisFolderNodetype = rs.getRepository("repository").getNodeTypeManager().getNodeType("basis:basisFollow");
        PropertyDefinition[] basisFolderNodetypeProperties = basisFolderNodetype.getPropertyDefinitions();
        for (PropertyDefinition property : basisFolderNodetypeProperties) {
            if(property.getName().contains("basis")) {
                if(!property.getName().contains("followEditorType")) {
                    String labelProperty;
                    if(!property.getName().contains("Comments")) {
                        labelProperty = "basisFollow.label." + property.getName().split("basis:")[1];
                    }
                    else {
                        labelProperty = "basis.label." + property.getName().split("basis:follow")[1].toLowerCase();
                    }
                    UIPropertyInputForm uiPropertyInputForm = addChild(UIPropertyInputForm.class,null, property.getName());
                    uiPropertyInputForm.load(property.getName(),labelProperty,property.getRequiredType() );
                }
            }

        }
        List<SelectItemOption<String>> lsRadioBox = new ArrayList<SelectItemOption<String>>() ;
        addUIFormInput(new UIFormRadioBoxInput(FIELD_RADIOBOX,FIELD_RADIOBOX,lsRadioBox));
    }
}
