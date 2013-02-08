package searchBasis.portlet.component;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.webui.container.UIContainer;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.form.UIFormDateTimeInput;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.input.UICheckBoxInput;

import javax.jcr.Session;
import javax.jcr.nodetype.PropertyDefinition;
import java.text.SimpleDateFormat;
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
        template =  "app:/groovy/SearchBasis/portlet/UIPropertyInputForm.gtmpl",
        events = @EventConfig(listeners = UIAdvancedSearchForm.ChangeActionListener.class, phase= Event.Phase.DECODE)
)
public class UIPropertyInputForm extends UIContainer {

    public UIPropertyInputForm() throws Exception {
    }

    public void load(PropertyDefinition property, String typeNode) throws Exception{

        List<SelectItemOption<String>> lsListBox = new ArrayList<SelectItemOption<String>>() ;
        String language =  Util.getPortalRequestContext().getLocale().getDisplayName();
        //System.out.println("langue : " + language);

        String labelProperty;
        if(property != null){
            if(!property.getName().contains("Comments")) {
                labelProperty = typeNode+".label." + property.getName().split("basis:")[1];
            }
            else {
                labelProperty = typeNode+"_basis.label.comments";
            }
        }
        else {
            labelProperty = typeNode;
        }

        String idProperty;
        if(property != null){
            if(!property.getName().contains("Comments")) {
                idProperty = typeNode+"_" + property.getName().split("basis:")[1];
            }
            else {
                idProperty = typeNode+"_basis_comments";
            }
        }
        else {
            idProperty = typeNode;
        }
        UICheckBoxInput uiCheckBoxInput = new UICheckBoxInput(labelProperty+"_checkBox",null,null);
        addChild(uiCheckBoxInput);

        if(property != null){
            if(property.getRequiredType() != 5){
                if(property.getName().contains("Comments")){
                    if(language.equals("French")){
                        lsListBox.add(new SelectItemOption<String>("Contient", "Contains")) ;
                        lsListBox.add(new SelectItemOption<String>("Ne contient pas", "Not_Contains")) ;
                    }
                    else if(language.equals("Nederlands")){
                        lsListBox.add(new SelectItemOption<String>("Bevat", "Contains")) ;
                        lsListBox.add(new SelectItemOption<String>("Niet bevat", "Not_Contains")) ;
                    }
                    else {
                        lsListBox.add(new SelectItemOption<String>("Contains", "Contains")) ;
                        lsListBox.add(new SelectItemOption<String>("Not Contains", "Not_Contains")) ;
                    }
                }
                else{
                    if(language.equals("French")){
                        lsListBox.add(new SelectItemOption<String>("Contient", "Contains")) ;
                        lsListBox.add(new SelectItemOption<String>("Ne contient pas", "Not_Contains")) ;
                        lsListBox.add(new SelectItemOption<String>("Egal", "Equals")) ;
                        lsListBox.add(new SelectItemOption<String>("Pas égal", "Not_Equals")) ;
                    }
                    else if(language.equals("Nederlands")){
                        lsListBox.add(new SelectItemOption<String>("Bevat", "Contains")) ;
                        lsListBox.add(new SelectItemOption<String>("Niet bevat", "Not_Contains")) ;
                        lsListBox.add(new SelectItemOption<String>("Gelijk", "Equals")) ;
                        lsListBox.add(new SelectItemOption<String>("Niet gelijk", "Not_Equals")) ;
                    }
                    else {
                        lsListBox.add(new SelectItemOption<String>("Contains", "Contains")) ;
                        lsListBox.add(new SelectItemOption<String>("Equals", "Equals")) ;
                        lsListBox.add(new SelectItemOption<String>("Not Contains", "Not_Contains")) ;
                        lsListBox.add(new SelectItemOption<String>("Not Equals", "Not_Equals")) ;
                    }
                }
                UIFormSelectBox uiFormSelectBoxSearchType = new UIFormSelectBox(labelProperty+"_searchType", null, lsListBox);
                uiFormSelectBoxSearchType.setOnChange("Change");
                addChild(uiFormSelectBoxSearchType);

                addChild(new UIFormStringInput(idProperty, null, null));
            }
            else {
                if(language.equals("French")){
                    lsListBox.add(new SelectItemOption<String>("Egal", "Equals")) ;
                    lsListBox.add(new SelectItemOption<String>("Pas égal", "Not_Equals")) ;
                    lsListBox.add(new SelectItemOption<String>("Entre", "Between")) ;
                }
                else if(language.equals("Nederlands")){
                    lsListBox.add(new SelectItemOption<String>("Gelijk", "Equals")) ;
                    lsListBox.add(new SelectItemOption<String>("Niet gelijk", "Not_Equals")) ;
                    lsListBox.add(new SelectItemOption<String>("Tussen", "Between")) ;
                }
                else {
                    lsListBox.add(new SelectItemOption<String>("Equals", "Equals")) ;
                    lsListBox.add(new SelectItemOption<String>("Not Equals", "Not_Equals")) ;
                    lsListBox.add(new SelectItemOption<String>("Between", "Between")) ;
                }
                UIFormSelectBox uiFormSelectBoxSearchType = new UIFormSelectBox(labelProperty+"_searchType", null, lsListBox);
                uiFormSelectBoxSearchType.setOnChange("Change");
                addChild(uiFormSelectBoxSearchType);


                addChild(new UIFormDateTimeInput(idProperty, null, new Date(), false));
            }
        }
        else{
            if(language.equals("French")){
                lsListBox.add(new SelectItemOption<String>("Contient", "Contains")) ;
                lsListBox.add(new SelectItemOption<String>("Ne contient pas", "Not_Contains")) ;
                lsListBox.add(new SelectItemOption<String>("Egal", "Equals")) ;
                lsListBox.add(new SelectItemOption<String>("Pas égal", "Not_Equals")) ;
            }
            else if(language.equals("Nederlands")){
                lsListBox.add(new SelectItemOption<String>("Bevat", "Contains")) ;
                lsListBox.add(new SelectItemOption<String>("Niet bevat", "Not_Contains")) ;
                lsListBox.add(new SelectItemOption<String>("Gelijk", "Equals")) ;
                lsListBox.add(new SelectItemOption<String>("Niet gelijk", "Not_Equals")) ;
            }
            else {
                lsListBox.add(new SelectItemOption<String>("Contains", "Contains")) ;
                lsListBox.add(new SelectItemOption<String>("Equals", "Equals")) ;
                lsListBox.add(new SelectItemOption<String>("Not Contains", "Not_Contains")) ;
                lsListBox.add(new SelectItemOption<String>("Not Equals", "Not_Equals")) ;
            }
            UIFormSelectBox uiFormSelectBoxSearchType = new UIFormSelectBox(labelProperty+"_searchType", null, lsListBox);
            uiFormSelectBoxSearchType.setOnChange("Change");
            addChild(uiFormSelectBoxSearchType);

            addChild(new UIFormStringInput(idProperty, null, null));
        }
    }
}
