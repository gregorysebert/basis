package searchBasis.portlet.component;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormSelectBox;

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
                @EventConfig(listeners = UIAdvancedSearchForm.CancelActionListener.class)
        }
)
public class UIAdvancedSearchForm extends UIForm  {
    public static final String FIELD_DOC = "Document" ;
    public static final String FIELD_FOLDER = "Folder" ;
    public static final String FIELD_FOLLOW_DOC = "Follow document" ;
    public static final String FIELD_FOLLOW_FOLDER = "Follow folder" ;
    public static final String FIELD_FROM = "From" ;

    public UIAdvancedSearchForm() throws Exception {
        UIBasisFolderForm uiBasisFolderForm = addChild(UIBasisFolderForm.class,null,FIELD_FOLDER);
        uiBasisFolderForm.load(FIELD_FOLDER);

        UIBasisDocForm uiBasisDocForm = addChild(UIBasisDocForm.class,null,FIELD_DOC);
        uiBasisDocForm.load(FIELD_DOC);

        UIBasisFollowDocForm uiBasisFollowDocForm =addChild(UIBasisFollowDocForm.class,null,FIELD_FOLLOW_DOC);
        uiBasisFollowDocForm.load(FIELD_FOLLOW_DOC);

        UIBasisFollowFolderForm uiBasisFollowFolderForm = addChild(UIBasisFollowFolderForm.class,null,FIELD_FOLLOW_FOLDER);
        uiBasisFollowFolderForm.load(FIELD_FOLLOW_FOLDER);

        List<SelectItemOption<String>> lsFrom = new ArrayList<SelectItemOption<String>>() ;
        lsFrom.add(new SelectItemOption<String>("Folder", "Folder")) ;
        lsFrom.add(new SelectItemOption<String>("Document", "Document")) ;
        lsFrom.add(new SelectItemOption<String>("Follow", "Follow")) ;
        UIFormSelectBox uiSelectBoxFrom = new UIFormSelectBox(FIELD_FROM, FIELD_FROM, lsFrom) ;
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
}



