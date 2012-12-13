package searchBasis.portlet.component;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormDateTimeInput;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.input.UICheckBoxInput;

import java.util.ArrayList;
import java.util.Date;
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
        template =  "app:/groovy/SearchBasis/portlet/UIPropertyInputForm.gtmpl",
        events = {
                @EventConfig(listeners = UIPropertyInputForm.ChangeActionListener.class, phase= Event.Phase.DECODE)
        }

)
public class UIPropertyInputForm extends UIForm {

    private  static final String FIELD_OPERATOR = "Operator";
    public static final String FIELD_SEARCH_TYPE = "searchType" ;
    public static String FIELD_PROPERTY = "";

    public UIPropertyInputForm() throws Exception {


    }

    public void load(String propertyName, String label, int type) throws Exception{
        FIELD_PROPERTY = propertyName;

        UICheckBoxInput uiCheckBoxInput = new UICheckBoxInput("CheckBoxProperty",propertyName,null);
        uiCheckBoxInput.setOnChange("Change");
        addUIFormInput(uiCheckBoxInput);

        if(type != 5){
            addUIFormInput(new UIFormStringInput(label, propertyName, null));
        }
        else {
            addUIFormInput(new UIFormDateTimeInput(label, propertyName, new Date(), false));
        }

        List<SelectItemOption<String>> lsSearch = new ArrayList<SelectItemOption<String>>() ;
        lsSearch.add(new SelectItemOption<String>(" ", " ")) ;
        lsSearch.add(new SelectItemOption<String>("Equals", "=")) ;
        lsSearch.add(new SelectItemOption<String>("Contains", "Contains")) ;
        lsSearch.add(new SelectItemOption<String>("Not Equals", "!=")) ;
        lsSearch.add(new SelectItemOption<String>("Not Contains", "!Contains")) ;
        UIFormSelectBox uiSelectBoxSearch = new UIFormSelectBox(FIELD_SEARCH_TYPE, FIELD_SEARCH_TYPE, lsSearch) ;
        uiSelectBoxSearch.setDisabled(true);
        addUIFormInput(uiSelectBoxSearch);

        setActions(new String[]{"Add"}) ;

    }

    static public class ChangeActionListener extends EventListener<UIPropertyInputForm> {
        public void execute(Event<UIPropertyInputForm> event) throws Exception {
            System.out.print(FIELD_PROPERTY);
        }
      }
}
