package searchBasis.portlet.component;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Kevin
 * Date: 7/12/12
 * Time: 13:23
 * To change this template use File | Settings | File Templates.
 */
@ComponentConfig(
        lifecycle = UIFormLifecycle.class,
        template =  "app:/groovy/SearchBasis/portlet/UIPropertyInputForm.gtmpl"
)
public class UIPropertyInputForm extends UIForm {

    private  static final String FIELD_OPERATOR = "Operator";
    public static final String FIELD_SEARCH_TYPE = "searchType" ;

    public UIPropertyInputForm() throws Exception {


    }

    public void load(String propertyName, String label, String parentName) throws Exception{

        addUIFormInput(new UIFormStringInput(label,propertyName,null));

        List<SelectItemOption<String>> lsSearch = new ArrayList<SelectItemOption<String>>() ;
        lsSearch.add(new SelectItemOption<String>(" ", " ")) ;
        lsSearch.add(new SelectItemOption<String>("Equals", "=")) ;
        lsSearch.add(new SelectItemOption<String>("Contains", "Contains")) ;
        lsSearch.add(new SelectItemOption<String>("Not Equals", "!=")) ;
        lsSearch.add(new SelectItemOption<String>("Not Contains", "!Contains")) ;
        UIFormSelectBox uiSelectBoxSearch = new UIFormSelectBox(FIELD_SEARCH_TYPE, FIELD_SEARCH_TYPE, lsSearch) ;
        addUIFormInput(uiSelectBoxSearch);

        setActions(new String[]{"Add"}) ;

    }
}
