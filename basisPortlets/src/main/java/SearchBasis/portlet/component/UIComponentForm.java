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
        template =  "app:/groovy/SearchBasis/portlet/UIComponentForm.gtmpl",
        events = {
                @EventConfig(listeners = UIComponentForm.SearchActionListener.class),
                @EventConfig(listeners = UIComponentForm.CancelActionListener.class),
                @EventConfig(listeners = UIComponentForm.AddFieldActionListener.class)
        }
)
public class UIComponentForm extends UIForm  {
    public static final String FIELD_OPERATOR = "operator" ;
    public static final String FIELD_DOC_TYPE = "basis:docType" ;
    public static final String FIELD_DOC_REGISTRATION = "basis:docRegistrationDate" ;
    public static final String FIELD_DOC_DATE = "basis:docDate" ;
    public static final String FIELD_DOC_REFERENCE = "basis:docReference" ;
    public static final String FIELD_DOC_KEYWORD = "basis:docKeywords" ;
    public static final String FIELD_DOC_SENDER_TYPE = "basis:docSenderType" ;
    public static final String FIELD_DOC_INTERN = "basis:docInternSender" ;
    public static final String FIELD_DOC_SENDER_NAME = "basis:docExternSenderName" ;
    public static final String FIELD_DOC_SENDER_ADRESS = "basis:docExternSenderAdress" ;
    public static final String FIELD_DOC_SENDER_CP = "basis:docExternSenderZipCode" ;
    public static final String FIELD_DOC_SENDER_CITY = "basis:docExternSenderCity" ;
    public static final String FIELD_DOC_SENDER_COUNTRY = "basis:docExternSenderCountry" ;
    public static final String FIELD_FOLDER_LANGUAGE = "basis:folderLanguage" ;
    public static final String FIELD_FOLDER_REGISTRATION = "basis:folderRegistrationDate" ;
    public static final String FIELD_FOLDER_CLOSE = "basis:folderCloseBeforeDate" ;
    public static final String FIELD_FOLDER_RNN = "basis:folderRNN" ;
    public static final String FIELD_FOLDER_REFERENCE = "basis:folderExternalReference" ;
    public static final String FIELD_FOLDER_STATUS = "basis:folderStatus" ;
    public static final String FIELD_FOLLOW_SEND_DATE = "basis:followSendDate" ;
    public static final String FIELD_FOLLOW_EDITOR = "basis:followEditor" ;
    public static final String FIELD_FOLLOW_ACTION = "basis:followRequiredAction" ;
    public static final String FIELD_FOLLOW_COMMENT = "basis:followComments" ;
    public static final String FIELD_FOLLOW_ANSWER = "basis:followAnswerByDate" ;

    public UIComponentForm() throws Exception {
        /*List<SelectItemOption<String>> lsSearch = new ArrayList<SelectItemOption<String>>() ;
        lsSearch.add(new SelectItemOption<String>(" ", " ")) ;
        lsSearch.add(new SelectItemOption<String>("Equals", "=")) ;
        lsSearch.add(new SelectItemOption<String>("Contains", "Contains")) ;
        lsSearch.add(new SelectItemOption<String>("Not Equals", "!=")) ;
        lsSearch.add(new SelectItemOption<String>("Not Contains", "!Contains")) ;
        UIFormSelectBox uiSelectBoxSearch = new UIFormSelectBox(FIELD_SEARCH_TYPE, FIELD_SEARCH_TYPE, lsSearch) ;
        addUIFormInput(uiSelectBoxSearch);  */

        addChild(UIInputAdvancedSearchform.class,null,FIELD_DOC_TYPE);
        addChild(UIInputAdvancedSearchform.class,null,FIELD_DOC_REGISTRATION);
        addChild(UIInputAdvancedSearchform.class,null,FIELD_DOC_DATE);
        addChild(UIInputAdvancedSearchform.class,null,FIELD_DOC_REFERENCE);
        addChild(UIInputAdvancedSearchform.class,null,FIELD_DOC_KEYWORD);
        addChild(UIInputAdvancedSearchform.class,null,FIELD_DOC_SENDER_TYPE);
        addChild(UIInputAdvancedSearchform.class,null,FIELD_DOC_INTERN);
        addChild(UIInputAdvancedSearchform.class,null,FIELD_DOC_SENDER_NAME);
        addChild(UIInputAdvancedSearchform.class,null,FIELD_DOC_SENDER_ADRESS);
        addChild(UIInputAdvancedSearchform.class,null,FIELD_DOC_SENDER_CP);
        addChild(UIInputAdvancedSearchform.class,null,FIELD_DOC_SENDER_CITY);
        addChild(UIInputAdvancedSearchform.class,null,FIELD_DOC_SENDER_COUNTRY);
        addChild(UIInputAdvancedSearchform.class,null,FIELD_FOLDER_LANGUAGE);
        addChild(UIInputAdvancedSearchform.class,null,FIELD_FOLDER_REGISTRATION);
        addChild(UIInputAdvancedSearchform.class,null,FIELD_FOLDER_CLOSE);
        addChild(UIInputAdvancedSearchform.class,null,FIELD_FOLDER_RNN);
        addChild(UIInputAdvancedSearchform.class,null,FIELD_FOLDER_REFERENCE);
        addChild(UIInputAdvancedSearchform.class,null,FIELD_FOLDER_STATUS);
        addChild(UIInputAdvancedSearchform.class,null,FIELD_FOLLOW_SEND_DATE);
        addChild(UIInputAdvancedSearchform.class,null,FIELD_FOLLOW_EDITOR);
        addChild(UIInputAdvancedSearchform.class,null,FIELD_FOLLOW_ACTION);
        addChild(UIInputAdvancedSearchform.class,null,FIELD_FOLLOW_COMMENT);
        addChild(UIInputAdvancedSearchform.class,null,FIELD_FOLLOW_ANSWER);


        setActions(new String[]{"Search", "Cancel"}) ;
        setRendered(true);
    }

    /*public void update(Query query) throws Exception {
    }   */

    static public class SearchActionListener extends EventListener<UIComponentForm> {
        public void execute(Event<UIComponentForm> event) throws Exception {
            UIComponentForm uiComponentForm = event.getSource();
            String name = uiComponentForm.getUIStringInput(FIELD_DOC_TYPE).getValue();
        }
    }

    static public class CancelActionListener extends EventListener<UIComponentForm> {
        public void execute(Event<UIComponentForm> event) throws Exception {
            UIComponentForm uiComponentForm = event.getSource() ;
            UISearchBasisPortlet uiManager = uiComponentForm.getAncestorOfType(UISearchBasisPortlet.class) ;
            uiComponentForm.reset() ;
            event.getRequestContext().addUIComponentToUpdateByAjax(uiManager) ;
        }
    }

    static public class AddFieldActionListener extends EventListener<UIComponentForm> {
        public void execute(Event<UIComponentForm> event) throws Exception {
            UIComponentForm uiComponentForm = event.getSource();

            List<SelectItemOption<String>> lsOperator = new ArrayList<SelectItemOption<String>>() ;
            lsOperator.add(new SelectItemOption<String>(" ", " ")) ;
            lsOperator.add(new SelectItemOption<String>("Et", "&&")) ;
            lsOperator.add(new SelectItemOption<String>("Ou", "||")) ;
            UIFormSelectBox uiSelectBoxOperator = new UIFormSelectBox(FIELD_OPERATOR, FIELD_OPERATOR, lsOperator) ;
            uiComponentForm.addUIFormInput(uiSelectBoxOperator);

            event.getRequestContext().addUIComponentToUpdateByAjax(uiComponentForm);
        }
    }
}



