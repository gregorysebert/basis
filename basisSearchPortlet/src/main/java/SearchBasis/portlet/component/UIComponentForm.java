package SearchBasis.portlet.component;

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
                @EventConfig(listeners = UIComponentForm.CancelActionListener.class)
        }
)
public class UIComponentForm extends UIForm  {
    public static final String FIELD_OPERATOR = "operator" ;
    public static final String FIELD_SEARCH_TYPE = "searchType" ;
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
        List<SelectItemOption<String>> lsSearch = new ArrayList<SelectItemOption<String>>() ;
        lsSearch.add(new SelectItemOption<String>(" ", " ")) ;
        lsSearch.add(new SelectItemOption<String>("Equals", "=")) ;
        lsSearch.add(new SelectItemOption<String>("Contains", "Contains")) ;
        lsSearch.add(new SelectItemOption<String>("Not Equals", "!=")) ;
        lsSearch.add(new SelectItemOption<String>("Not Contains", "!Contains")) ;
        UIFormSelectBox uiSelectBoxSearch = new UIFormSelectBox(FIELD_SEARCH_TYPE, FIELD_SEARCH_TYPE, lsSearch) ;
        addUIFormInput(uiSelectBoxSearch);

        addUIFormInput(new UIFormStringInput(FIELD_DOC_TYPE, FIELD_DOC_TYPE, null));

        List<SelectItemOption<String>> lsOperator = new ArrayList<SelectItemOption<String>>() ;
        lsOperator.add(new SelectItemOption<String>(" ", " ")) ;
        lsOperator.add(new SelectItemOption<String>("Et", "&&")) ;
        lsOperator.add(new SelectItemOption<String>("Ou", "||")) ;
        UIFormSelectBox uiSelectBoxOperator = new UIFormSelectBox(FIELD_OPERATOR, FIELD_OPERATOR, lsOperator) ;
        addUIFormInput(uiSelectBoxOperator);

        addUIFormInput(new UIFormStringInput(FIELD_DOC_REGISTRATION, FIELD_DOC_REGISTRATION, null));
        addUIFormInput(new UIFormStringInput(FIELD_DOC_DATE, FIELD_DOC_DATE, null));
        addUIFormInput(new UIFormStringInput(FIELD_DOC_REFERENCE, FIELD_DOC_REFERENCE, null));
        addUIFormInput(new UIFormStringInput(FIELD_DOC_KEYWORD, FIELD_DOC_KEYWORD, null));
        addUIFormInput(new UIFormStringInput(FIELD_DOC_SENDER_TYPE, FIELD_DOC_SENDER_TYPE, null));
        addUIFormInput(new UIFormStringInput(FIELD_DOC_INTERN, FIELD_DOC_INTERN, null));
        addUIFormInput(new UIFormStringInput(FIELD_DOC_SENDER_NAME, FIELD_DOC_SENDER_NAME, null));
        addUIFormInput(new UIFormStringInput(FIELD_DOC_SENDER_ADRESS, FIELD_DOC_SENDER_ADRESS, null));
        addUIFormInput(new UIFormStringInput(FIELD_DOC_SENDER_CP, FIELD_DOC_SENDER_CP, null));
        addUIFormInput(new UIFormStringInput(FIELD_DOC_SENDER_CITY, FIELD_DOC_SENDER_CITY, null));
        addUIFormInput(new UIFormStringInput(FIELD_DOC_SENDER_COUNTRY, FIELD_DOC_SENDER_COUNTRY, null));
        addUIFormInput(new UIFormStringInput(FIELD_FOLDER_LANGUAGE, FIELD_FOLDER_LANGUAGE, null));
        addUIFormInput(new UIFormStringInput(FIELD_FOLDER_REGISTRATION, FIELD_FOLDER_REGISTRATION, null));
        addUIFormInput(new UIFormStringInput(FIELD_FOLDER_CLOSE, FIELD_FOLDER_CLOSE, null));
        addUIFormInput(new UIFormStringInput(FIELD_FOLDER_RNN, FIELD_FOLDER_RNN, null));
        addUIFormInput(new UIFormStringInput(FIELD_FOLDER_REFERENCE, FIELD_FOLDER_REFERENCE, null));
        addUIFormInput(new UIFormStringInput(FIELD_FOLDER_STATUS, FIELD_FOLDER_STATUS, null));
        addUIFormInput(new UIFormStringInput(FIELD_FOLLOW_SEND_DATE, FIELD_FOLLOW_SEND_DATE, null));
        addUIFormInput(new UIFormStringInput(FIELD_FOLLOW_EDITOR, FIELD_FOLLOW_EDITOR, null));
        addUIFormInput(new UIFormStringInput(FIELD_FOLLOW_ACTION, FIELD_FOLLOW_ACTION, null));
        addUIFormInput(new UIFormStringInput(FIELD_FOLLOW_COMMENT, FIELD_FOLLOW_COMMENT, null));
        addUIFormInput(new UIFormStringInput(FIELD_FOLLOW_ANSWER, FIELD_FOLLOW_ANSWER, null));

        setActions(new String[]{"Search", "Save", "Cancel"}) ;
        setRendered(true);
    }

    /*public void update(Query query) throws Exception {
    }   */

    static public class SearchActionListener extends EventListener<UIComponentForm> {
        public void execute(Event<UIComponentForm> event) throws Exception {
            UIComponentForm uiComponentForm = event.getSource();
            String name = uiComponentForm.getUIStringInput(FIELD_DOC_TYPE).getValue();
            System.out.println(name);
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
}



