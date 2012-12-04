package searchBasis.portlet.component;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import basis.selector.service.BasisSelectorService;

import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;

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
                @EventConfig(listeners = UISimpleSearchForm.CancelActionListener.class),
                @EventConfig(listeners = UISimpleSearchForm.ChangeActionListener.class, phase= Event.Phase.DECODE)
        }

)
public class UISimpleSearchForm extends UIForm {

    private  static final String QUERY = "Query";
    private  static final String LANGUAGE = "Language";
    private  static final String ATTRIBUT = "Attribut";

    private static final String BASIS_FOLDER_BY_CURRENT_USER_XPATH_QUERY = "currentUser" ;
    private static final String BASIS_FOLDER_BY_GROUP_XPATH_QUERY = "byGroup" ;
    private static final String BASIS_FOLDER_BY_USER_XPATH_QUERY = "byUser" ;
    private static final String BASIS_FOLDER_BY_ACTION_XPATH_QUERY = "byAction" ;

    public UISimpleSearchForm() throws Exception {
        List<SelectItemOption<String>> lsLanguage = new ArrayList<SelectItemOption<String>>() ;
        lsLanguage.add(new SelectItemOption<String>("NL", "NL")) ;
        lsLanguage.add(new SelectItemOption<String>("FR", "FR")) ;
        lsLanguage.add(new SelectItemOption<String>("EN", "EN")) ;
        UIFormSelectBox uiFormSelectBoxLanguage = new UIFormSelectBox(LANGUAGE,LANGUAGE,lsLanguage);
        uiFormSelectBoxLanguage.setOnChange("Change");
        addUIFormInput(uiFormSelectBoxLanguage);

        List<SelectItemOption<String>> lsQuery = new ArrayList<SelectItemOption<String>>() ;
        lsQuery.add(new SelectItemOption<String>("All folder where follow up editor is the current user", BASIS_FOLDER_BY_CURRENT_USER_XPATH_QUERY)) ;
        lsQuery.add(new SelectItemOption<String>("All folder where follow up editor is a specific group", BASIS_FOLDER_BY_GROUP_XPATH_QUERY)) ;
        lsQuery.add(new SelectItemOption<String>("All folder where follow up editor is a specific user", BASIS_FOLDER_BY_USER_XPATH_QUERY)) ;
        lsQuery.add(new SelectItemOption<String>("All folder where follow up editor is a specific action", BASIS_FOLDER_BY_ACTION_XPATH_QUERY)) ;
        UIFormSelectBox uiFormSelectBoxQuery = new UIFormSelectBox(QUERY, QUERY, lsQuery) ;
        uiFormSelectBoxQuery.setOnChange("Change");
        addUIFormInput(uiFormSelectBoxQuery);

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
                if(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue().equals("currentUser")){
                    String remoteUser =  Util.getPortalRequestContext().getRemoteUser();
                    xpathStatement = "/jcr:root/Files/BO//element (*,basis:basisFolder) [jcr:like(*/@basis:followUserInternEditor,\'"+remoteUser+"\')]";
                }
                else if(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue().equals("byGroup")){
                    String group = uiSimpleSearchForm.getUIStringInput(ATTRIBUT).getValue();
                    if(!group.equals(null)) {
                        xpathStatement = "/jcr:root/Files/BO//element (*,basis:basisFolder) [jcr:like(*/@basis:followGroupInternEditor,'"+group+"\')]";
                        uiSimpleSearchForm.removeChildById(ATTRIBUT);
                    }
                    else
                        return;
                }
                else if(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue().equals("byUser")){
                    String user = uiSimpleSearchForm.getUIStringInput(ATTRIBUT).getValue();
                    if(!user.equals(null)){
                        xpathStatement = "/jcr:root/Files/BO//element (*,basis:basisFolder) [jcr:like(*/@basis:followUserInternEditor,\'"+user+"\')]";
                        uiSimpleSearchForm.removeChildById(ATTRIBUT);
                    }
                    else
                        return;
                }
                else if(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue().equals("byAction")){
                    String action = uiSimpleSearchForm.getUIStringInput(ATTRIBUT).getValue();
                    uiSimpleSearchForm.removeChildById(ATTRIBUT);
                    if(!action.equals(null)) {
                        xpathStatement = "/jcr:root/Files/BO//element (*,basis:basisFolder) [jcr:like(*/@basis:followRequiredAction,'"+action+"\')]";
                        uiSimpleSearchForm.removeChildById(ATTRIBUT);
                    }
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

    static public class ChangeActionListener extends EventListener<UISimpleSearchForm> {
        public void execute(Event<UISimpleSearchForm> event) throws Exception {
            UISimpleSearchForm uiSimpleSearchForm = event.getSource() ;
            uiSimpleSearchForm.removeChildById(ATTRIBUT);

            String language = uiSimpleSearchForm.getUIFormSelectBox(LANGUAGE).getValue();

            if(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue().equals("byGroup")){
                List<SelectItemOption<String>> lsAttribut = new ArrayList<SelectItemOption<String>>() ;

                ExoContainer exoContainer = ExoContainerContext.getCurrentContainer();
                RepositoryService rs = (RepositoryService) exoContainer.getComponentInstanceOfType(RepositoryService.class);
                Session session = (Session) rs.getRepository("repository").getSystemSession("collaboration");
                QueryManager queryManager = null;
                queryManager = session.getWorkspace().getQueryManager();
                String xpathStatement = "/jcr:root/Files/BO//element (*, nt:file) [jcr:like(@exo:name,'followSelector.txt')]";
                Query query = queryManager.createQuery(xpathStatement, Query.XPATH);
                QueryResult result = query.execute();
                NodeIterator nodeIterator = result.getNodes();
                Node followSelectorNode = nodeIterator.nextNode();

                BasisSelectorService basisSelectorService = new BasisSelectorService(followSelectorNode, "GroupInternEditor", language);
                String groupInternEditorOptions = basisSelectorService.getOptions();
                String[] groupInternEditorOptionsList = groupInternEditorOptions.split(",");
                for(String groupInternEditorOption:groupInternEditorOptionsList){
                    lsAttribut.add(new SelectItemOption<String>(groupInternEditorOption,groupInternEditorOption));
                }
                UIFormSelectBox uiFormSelectBox = new UIFormSelectBox(ATTRIBUT, ATTRIBUT, lsAttribut) ;
                uiSimpleSearchForm.addUIFormInput(uiFormSelectBox);

            }
            else if(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue().equals("byUser")){
                uiSimpleSearchForm.addUIFormInput(new UIFormStringInput(ATTRIBUT, ATTRIBUT, null));
                //uiSimpleSearchForm.addChild();
            }
            else if(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue().equals("byAction")){
                List<SelectItemOption<String>> lsAttribut = new ArrayList<SelectItemOption<String>>() ;

                ExoContainer exoContainer = ExoContainerContext.getCurrentContainer();
                RepositoryService rs = (RepositoryService) exoContainer.getComponentInstanceOfType(RepositoryService.class);
                Session session = (Session) rs.getRepository("repository").getSystemSession("collaboration");
                QueryManager queryManager = null;
                queryManager = session.getWorkspace().getQueryManager();
                String xpathStatement = "/jcr:root/Files/BO//element (*, nt:file) [jcr:like(@exo:name,'followSelector.txt')]";
                Query query = queryManager.createQuery(xpathStatement, Query.XPATH);
                QueryResult result = query.execute();
                NodeIterator nodeIterator = result.getNodes();
                Node followSelectorNode = nodeIterator.nextNode();

                BasisSelectorService basisSelectorService = new BasisSelectorService(followSelectorNode, "RequiredAction", language);
                String requiredActionOptions = basisSelectorService.getOptions();
                String[] requiredActionOptionsList = requiredActionOptions.split(",");
                for(String requiredActionOption:requiredActionOptionsList){
                    lsAttribut.add(new SelectItemOption<String>(requiredActionOption,requiredActionOption));
                }
                UIFormSelectBox uiFormSelectBox = new UIFormSelectBox(ATTRIBUT, ATTRIBUT, lsAttribut) ;
                uiSimpleSearchForm.addUIFormInput(uiFormSelectBox);
            }
            event.getRequestContext().addUIComponentToUpdateByAjax(uiSimpleSearchForm) ;
        }
    }
}
