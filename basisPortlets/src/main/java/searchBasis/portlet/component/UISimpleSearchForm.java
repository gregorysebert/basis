package searchBasis.portlet.component;

import basis.selector.service.BasisSelectorService;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.jcr.RepositoryService;
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
    private  static final String NUMBERRESULT = "NumberResult";
    private  static final String ATTRIBUT = "Attribut";

    private static final String BASIS_FOLDER_BY_CURRENT_USER_XPATH_QUERY = "currentUser" ;
    private static final String BASIS_FOLDER_BY_GROUP_XPATH_QUERY = "byGroup" ;
    private static final String BASIS_FOLDER_BY_USER_XPATH_QUERY = "byUser" ;
    private static final String BASIS_FOLDER_BY_ACTION_XPATH_QUERY = "byAction" ;

    public UISimpleSearchForm() throws Exception {
        PortletRequestContext portletRequestContext = WebuiRequestContext.getCurrentInstance();

        List<SelectItemOption<String>> lsLanguage = new ArrayList<SelectItemOption<String>>() ;
        lsLanguage.add(new SelectItemOption<String>("NL", "NL")) ;
        lsLanguage.add(new SelectItemOption<String>("FR", "FR")) ;
        lsLanguage.add(new SelectItemOption<String>("EN", "EN")) ;
        UIFormSelectBox uiFormSelectBoxLanguage = new UIFormSelectBox(LANGUAGE,LANGUAGE,lsLanguage);
        uiFormSelectBoxLanguage.setOnChange("Change");
        addUIFormInput(uiFormSelectBoxLanguage);

        List<SelectItemOption<String>> lsNumber = new ArrayList<SelectItemOption<String>>() ;
        lsNumber.add(new SelectItemOption<String>("5", "5")) ;
        lsNumber.add(new SelectItemOption<String>("10", "10")) ;
        lsNumber.add(new SelectItemOption<String>("20", "20")) ;
        lsNumber.add(new SelectItemOption<String>("50", "50")) ;
        UIFormSelectBox uiFormSelectBoxNumber = new UIFormSelectBox(NUMBERRESULT,NUMBERRESULT,lsNumber);
        addUIFormInput(uiFormSelectBoxNumber);

        List<SelectItemOption<String>> lsQuery = new ArrayList<SelectItemOption<String>>() ;
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

            String url = Util.getPortalRequestContext().getRequestURI();
            String urlSplitted[] = url.split("BO:");
            String nameBO[] = urlSplitted[1].split("/");

            String language = uiSimpleSearchForm.getUIFormSelectBox(LANGUAGE).getValue();
            String statusLanguageFR = "and not(jcr:like(@basis:folderStatus, 'clôturé')) and not(jcr:like(@basis:folderStatus, 'annulé'))";
            String statusLanguageEN = "and not(jcr:like(@basis:folderStatus, 'closed')) and not(jcr:like(@basis:folderStatus, 'cancelled'))";
            String statusLanguageNL = "and not(jcr:like(@basis:folderStatus, 'afgesloten')) and not(jcr:like(@basis:folderStatus, 'geannulleerd'))";

            try {
                ExoContainer exoContainer = ExoContainerContext.getCurrentContainer();
                RepositoryService rs = (RepositoryService) exoContainer.getComponentInstanceOfType(RepositoryService.class);
                Session session = (Session) rs.getRepository("repository").getSystemSession("collaboration");
                QueryManager queryManager = null;
                queryManager = session.getWorkspace().getQueryManager();



                UISearchBasisPortlet uiSearchBasisPortlet = uiSimpleSearchForm.getAncestorOfType(UISearchBasisPortlet.class);

                if(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue().equals("currentUser")){
                    String remoteUser =  Util.getPortalRequestContext().getRemoteUser();
                    if(language.equals("FR")){
                        xpathStatement = "/jcr:root/Files/BO/"+nameBO[0]+"//element (*,basis:basisFolder) [jcr:like(*/@basis:followUserInternEditor,'"+remoteUser+"') "+statusLanguageFR+"] order by @exo:name ascending";
                    }
                    else if(language.equals("EN")){
                        xpathStatement = "/jcr:root/Files/BO/"+nameBO[0]+"//element (*,basis:basisFolder) [jcr:like(*/@basis:followUserInternEditor,'"+remoteUser+"') "+statusLanguageEN+"] order by @exo:name ascending";
                    }
                    else if(language.equals("NL")) {
                        xpathStatement = "/jcr:root/Files/BO/"+nameBO[0]+"//element (*,basis:basisFolder) [jcr:like(*/@basis:followUserInternEditor,'"+remoteUser+"') "+statusLanguageNL+"] order by @exo:name ascending";
                    }
                    uiSearchBasisPortlet.setTypeQuery(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue());

                }
                else if(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue().equals("byGroup")){
                    String group = uiSimpleSearchForm.getUIStringInput(ATTRIBUT).getValue();
                    uiSearchBasisPortlet.setAttribute(group);
                    uiSearchBasisPortlet.setTypeQuery(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue());
                    if(!group.equals(null)) {
                        if(language.equals("FR")){
                            xpathStatement = "/jcr:root/Files/BO/"+nameBO[0]+"//element (*,basis:basisFolder) [jcr:like(*/@basis:followGroupInternEditor,'"+group+"') "+statusLanguageFR+"] order by @exo:name ascending";
                        }
                        else if(language.equals("EN")){
                            xpathStatement = "/jcr:root/Files/BO/"+nameBO[0]+"//element (*,basis:basisFolder) [jcr:like(*/@basis:followGroupInternEditor,'"+group+"') "+statusLanguageEN+"] order by @exo:name ascending";
                        }
                        else if(language.equals("NL")) {
                            xpathStatement = "/jcr:root/Files/BO/"+nameBO[0]+"//element (*,basis:basisFolder) [jcr:like(*/@basis:followGroupInternEditor,'"+group+"') "+statusLanguageNL+"] order by @exo:name ascending";
                        }
                        //uiSimpleSearchForm.removeChildById(ATTRIBUT);
                    }
                    else
                        return;
                }
                else if(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue().equals("byUser")){
                    String user = uiSimpleSearchForm.getUIStringInput(ATTRIBUT).getValue();
                    uiSearchBasisPortlet.setTypeQuery(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue());

                    if(!user.equals(null)){
                        if(language.equals("FR")){
                            uiSearchBasisPortlet.setAttribute(user);
                            xpathStatement = "/jcr:root/Files/BO/"+nameBO[0]+"//element (*,basis:basisFolder) [jcr:like(*/@basis:followUserInternEditor,'"+user+"') "+statusLanguageFR+"] order by @exo:name ascending";
                        }
                        else if(language.equals("EN")){
                            uiSearchBasisPortlet.setAttribute(user);
                            xpathStatement = "/jcr:root/Files/BO/"+nameBO[0]+"//element (*,basis:basisFolder) [jcr:like(*/@basis:followUserInternEditor,'"+user+"') "+statusLanguageEN+"] order by @exo:name ascending";
                        }
                        else if(language.equals("NL")) {
                            uiSearchBasisPortlet.setAttribute(user);
                            xpathStatement = "/jcr:root/Files/BO/"+nameBO[0]+"//element (*,basis:basisFolder) [jcr:like(*/@basis:followUserInternEditor,'"+user+"') "+statusLanguageNL+"] order by @exo:name ascending";
                        }
                        //uiSimpleSearchForm.removeChildById(ATTRIBUT);
                    }
                    else
                        return;
                }
                else if(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue().equals("byAction")){
                    String action = uiSimpleSearchForm.getUIStringInput(ATTRIBUT).getValue();
                    uiSearchBasisPortlet.setAttribute(action);
                    uiSearchBasisPortlet.setTypeQuery(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue());

                    uiSimpleSearchForm.removeChildById(ATTRIBUT);
                    if(!action.equals(null)) {
                        if(language.equals("FR")){
                            xpathStatement = "/jcr:root/Files/BO/"+nameBO[0]+"//element (*,basis:basisFolder) [jcr:like(*/@basis:followRequiredAction,'"+action+"') "+statusLanguageFR+"] order by @exo:name ascending";
                        }
                        else if(language.equals("EN")){
                            xpathStatement = "/jcr:root/Files/BO/"+nameBO[0]+"//element (*,basis:basisFolder) [jcr:like(*/@basis:followRequiredAction,'"+action+"') "+statusLanguageEN+"] order by @exo:name ascending";
                        }
                        else if(language.equals("NL")) {
                            xpathStatement = "/jcr:root/Files/BO/"+nameBO[0]+"//element (*,basis:basisFolder) [jcr:like(*/@basis:followRequiredAction,'"+action+"') "+statusLanguageNL+"] order by @exo:name ascending";
                        }
                        //uiSimpleSearchForm.removeChildById(ATTRIBUT);
                    }
                    else
                        return;
                }

                if(xpathStatement != null){
                    Query query = queryManager.createQuery(xpathStatement, Query.XPATH);
                    QueryResult result = query.execute();

                    uiSearchBasisPortlet.setQueryResult(result);
                    uiSearchBasisPortlet.updateResult();
                    //uiSimpleSearchForm.getUIFormSelectBox(QUERY).setDefaultValue("currentUser");
                    uiSimpleSearchForm.setRendered(false);

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

            String url = Util.getPortalRequestContext().getRequestURI();
            String urlSplitted[] = url.split("BO:");
            String nameBO[] = urlSplitted[1].split("/");

            if(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue().equals("byGroup")){
                List<SelectItemOption<String>> lsAttribut = new ArrayList<SelectItemOption<String>>() ;

                ExoContainer exoContainer = ExoContainerContext.getCurrentContainer();
                RepositoryService rs = (RepositoryService) exoContainer.getComponentInstanceOfType(RepositoryService.class);
                Session session = (Session) rs.getRepository("repository").getSystemSession("collaboration");
                QueryManager queryManager = null;
                queryManager = session.getWorkspace().getQueryManager();
                String xpathStatement = "/jcr:root/Files/BO/"+nameBO[0]+"//element (*, nt:file) [jcr:like(@exo:name,'followSelector.txt')]";
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
                String xpathStatement = "/jcr:root/Files/BO/"+nameBO[0]+"//element (*, nt:file) [jcr:like(@exo:name,'followSelector.txt')]";
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
            uiSimpleSearchForm.getUIFormSelectBox(QUERY).setDefaultValue(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue());
            event.getRequestContext().addUIComponentToUpdateByAjax(uiSimpleSearchForm) ;
        }
    }
}
