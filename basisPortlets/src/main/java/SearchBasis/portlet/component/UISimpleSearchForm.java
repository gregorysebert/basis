package SearchBasis.portlet.component;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.portal.webui.util.Util;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: rousselle-k
 * Date: 21/11/12
 * Time: 15:34
 * To change this template use File | Settings | File Templates.
 */
@ComponentConfig(
        lifecycle = UIFormLifecycle.class,
        template =  "app:/groovy/SearchBasis/portlet/UISimpleSearchForm.gtmpl",
        events = {
                @EventConfig(listeners = UISimpleSearchForm.SearchActionListener.class),
                @EventConfig(listeners = UISimpleSearchForm.CancelActionListener.class)
        }

)
public class UISimpleSearchForm extends UIForm {

    private  static final String QUERY = "Query";

    private  static final String ATTRIBUT = "attribut";

    private static final String BASIS_FOLDER_BY_CURRENT_USER_XPATH_QUERY = "currentUser" ;
    private static final String BASIS_FOLDER_BY_GROUP_XPATH_QUERY = "byGroup" ;
    private static final String BASIS_FOLDER_BY_USER_XPATH_QUERY = "byUser" ;
    private static final String BASIS_FOLDER_BY_ACTION_XPATH_QUERY = "byAction" ;

    public UISimpleSearchForm() throws Exception {
        List<SelectItemOption<String>> lsOperator = new ArrayList<SelectItemOption<String>>() ;
        lsOperator.add(new SelectItemOption<String>(" ", " ")) ;
        lsOperator.add(new SelectItemOption<String>("All folder where follow up editor is the current user", BASIS_FOLDER_BY_CURRENT_USER_XPATH_QUERY)) ;
        lsOperator.add(new SelectItemOption<String>("All folder where follow up editor is a specific group", BASIS_FOLDER_BY_GROUP_XPATH_QUERY)) ;
        lsOperator.add(new SelectItemOption<String>("All folder where follow up editor is a specific user", BASIS_FOLDER_BY_USER_XPATH_QUERY)) ;
        lsOperator.add(new SelectItemOption<String>("All folder where follow up editor is a specific action", BASIS_FOLDER_BY_ACTION_XPATH_QUERY)) ;
        UIFormSelectBox uiFormSelectBox = new UIFormSelectBox(QUERY, QUERY, lsOperator) ;
        addUIFormInput(uiFormSelectBox);
        addUIFormInput(new UIFormStringInput(ATTRIBUT, ATTRIBUT, null));

        setActions(new String[]{"Search", "Cancel"}) ;

    }

    static public class SearchActionListener extends EventListener<UISimpleSearchForm> {
        public void execute(Event<UISimpleSearchForm> event) throws Exception {
            UISimpleSearchForm uiSimpleSearchForm = event.getSource();
            UIApplication uiApp = uiSimpleSearchForm.getAncestorOfType(UIApplication.class);
            String xpathStatement = "";

            try {
                ExoContainer exoContainer = ExoContainerContext.getCurrentContainer();
                RepositoryService rs = (RepositoryService) exoContainer.getComponentInstanceOfType(RepositoryService.class);
                Session session = (Session) rs.getRepository("repository").getSystemSession("collaboration");
                QueryManager queryManager = null;
                queryManager = session.getWorkspace().getQueryManager();
                if(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue().contains("currentUser")){
                    String remoteUser =  Util.getPortalRequestContext().getRemoteUser();
                    xpathStatement = "//element(*, basis:basisFolder) [jcr:like(*/@basis:followUserInternEditor,\'"+remoteUser+"\')]";
                }
                else if(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue().contains("byGroup")){
                    String group = uiSimpleSearchForm.getUIStringInput(ATTRIBUT).getValue();
                    if(!group.equals(null))
                        xpathStatement = "//element(*, basis:basisFolder) [jcr:like(*/@basis:followGroupInternEditor,'"+group+"\')]";
                    else
                        return;
                }
                else if(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue().contains("byUser")){
                    String user = uiSimpleSearchForm.getUIStringInput(ATTRIBUT).getValue();
                    if(!user.equals(null))
                        xpathStatement = "//element(*, basis:basisFolder) [jcr:like(*/@basis:followUserInternEditor,\'"+user+"\')]";
                    else
                        return;
                }
                else if(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue().contains("byAction")){
                    String action = uiSimpleSearchForm.getUIStringInput(ATTRIBUT).getValue();
                    if(!action.equals(null))
                        xpathStatement = "//element(*, basis:basisFolder) [jcr:like(*/@basis:followRequiredAction,'"+action+"\')]";
                    else
                        return;
                }

                if(!xpathStatement.equals(null)){
                    Query query = queryManager.createQuery(xpathStatement, Query.XPATH);
                    QueryResult result = query.execute();

                    UISearchBasisPortlet uiSearchBasisPortlet = uiSimpleSearchForm.getAncestorOfType(UISearchBasisPortlet.class);
                    uiSearchBasisPortlet.setQueryResult(result);
                    uiSearchBasisPortlet.updateResult();
                    uiSimpleSearchForm.setRendered(false);
                    uiSimpleSearchForm.reset();
                }
                else{
                    uiApp.addMessage(new ApplicationMessage("UISimpleSearchForm.msg.value-null", null, ApplicationMessage.WARNING));
                    event.getRequestContext().addUIComponentToUpdateByAjax(uiSimpleSearchForm);
                    return;
                }
            } catch (RepositoryException e) {
                System.out.println("RepositoryException : " + e);
            }
        }
    }

    static public class CancelActionListener extends EventListener<UISimpleSearchForm> {
        public void execute(Event<UISimpleSearchForm> event) throws Exception {
            UISimpleSearchForm uiSimpleSearchForm = event.getSource() ;
            UISearchBasisPortlet uiManager = uiSimpleSearchForm.getAncestorOfType(UISearchBasisPortlet.class) ;
            uiSimpleSearchForm.reset() ;
            event.getRequestContext().addUIComponentToUpdateByAjax(uiManager) ;
        }
    }
}
