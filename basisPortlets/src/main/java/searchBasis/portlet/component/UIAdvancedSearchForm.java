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
 * User: rousselle-k
 * Date: 16/11/12
 * Time: 10:42
 * To change this template use File | Settings | File Templates.
 */
@ComponentConfig(
        lifecycle = UIFormLifecycle.class,
        template =  "app:/groovy/SearchBasis/portlet/UIAdvancedSearchForm.gtmpl",
        events = {
                @EventConfig(listeners = UIAdvancedSearchForm.SearchActionListener.class),
                @EventConfig(listeners = UIAdvancedSearchForm.CancelActionListener.class),
                @EventConfig(listeners = UIAdvancedSearchForm.ChangeActionListener.class, phase= Event.Phase.DECODE)
        }
)
public class UIAdvancedSearchForm extends UIForm  {
    public static final String FIELD_DOC = "Document" ;
    public static final String FIELD_FOLDER = "Folder" ;
    public static final String FIELD_FOLLOW = "Follow" ;
    public static final String FIELD_FROM = "From" ;

    public UIAdvancedSearchForm() throws Exception {
        addChild(UIBasisFolderForm.class,null,FIELD_FOLDER);

        addChild(UIBasisDocForm.class,null,FIELD_DOC);

        addChild(UIBasisFollowForm.class,null,FIELD_FOLLOW);

        List<SelectItemOption<String>> lsFrom = new ArrayList<SelectItemOption<String>>() ;
        lsFrom.add(new SelectItemOption<String>("Folder", "Folder")) ;
        lsFrom.add(new SelectItemOption<String>("Document", "Document")) ;
        lsFrom.add(new SelectItemOption<String>("Follow", "Follow")) ;
        UIFormSelectBox uiSelectBoxFrom = new UIFormSelectBox(FIELD_FROM, FIELD_FROM, lsFrom) ;
        uiSelectBoxFrom.setOnChange("Change");
        addChild(uiSelectBoxFrom);



        setActions(new String[]{"Search", "Cancel"}) ;
    }

    static public class SearchActionListener extends EventListener<UIAdvancedSearchForm> {
        public void execute(Event<UIAdvancedSearchForm> event) throws Exception {
            UIAdvancedSearchForm uiAdvancedSearchForm = event.getSource();
        }
    }

    static public class CancelActionListener extends EventListener<UIAdvancedSearchForm> {
        public void execute(Event<UIAdvancedSearchForm> event) throws Exception {
            UIAdvancedSearchForm uiAdvancedSearchForm = event.getSource() ;
            UISearchBasisPortlet uiManager = uiAdvancedSearchForm.getAncestorOfType(UISearchBasisPortlet.class) ;
            uiAdvancedSearchForm.reset() ;
            event.getRequestContext().addUIComponentToUpdateByAjax(uiManager) ;
        }
    }

    static public class ChangeActionListener extends EventListener<UIAdvancedSearchForm> {
        public void execute(Event<UIAdvancedSearchForm> event) throws Exception {
            UIAdvancedSearchForm uiAdvancedSearchForm = event.getSource();
            UISearchBasisPortlet uiSearchBasisPortlet = uiAdvancedSearchForm.getParent();

            uiAdvancedSearchForm.removeChildById(FIELD_FOLDER);
            /*UIBasisFolderForm uiBasisFolderForm = uiAdvancedSearchForm.getChildById(FIELD_FOLDER);
            UIBasisDocForm uiBasisDocForm = uiAdvancedSearchForm.getChildById(FIELD_DOC);
            UIBasisFollowForm uiBasisFollowForm = uiAdvancedSearchForm.getChildById(FIELD_FOLLOW);  */


            String fromValue = uiAdvancedSearchForm.getUIFormSelectBox(FIELD_FROM).getValue();

            System.out.println("fromValue :" + fromValue);

            if(fromValue.equals("Folder")){
                uiAdvancedSearchForm.addChild(UIBasisFolderForm.class,null,FIELD_FOLDER);
                uiAdvancedSearchForm.addChild(UIBasisDocForm.class,null,FIELD_DOC);
                uiAdvancedSearchForm.addChild(UIBasisFollowForm.class,null,FIELD_FOLLOW);
            }
            else if(fromValue.equals("Document")){
                uiAdvancedSearchForm.addChild(UIBasisDocForm.class,null,FIELD_DOC);
                uiAdvancedSearchForm.addChild(UIBasisFollowForm.class,null,FIELD_FOLLOW);
            }
            else if(fromValue.equals("Follow")){
                uiAdvancedSearchForm.addChild(UIBasisFollowForm.class,null,FIELD_FOLLOW);
            }

            event.getRequestContext().addUIComponentToUpdateByAjax(uiSearchBasisPortlet) ;
        }
    }
}



