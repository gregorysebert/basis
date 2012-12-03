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

    private  static final String ATTRIBUT = "Attribut";

    private static final String BASIS_FOLDER_BY_CURRENT_USER_XPATH_QUERY = "currentUser" ;
    private static final String BASIS_FOLDER_BY_GROUP_XPATH_QUERY = "byGroup" ;
    private static final String BASIS_FOLDER_BY_USER_XPATH_QUERY = "byUser" ;
    private static final String BASIS_FOLDER_BY_ACTION_XPATH_QUERY = "byAction" ;

    public UISimpleSearchForm() throws Exception {
        List<SelectItemOption<String>> lsQuery = new ArrayList<SelectItemOption<String>>() ;
        lsQuery.add(new SelectItemOption<String>("All folder where follow up editor is the current user", BASIS_FOLDER_BY_CURRENT_USER_XPATH_QUERY)) ;
        lsQuery.add(new SelectItemOption<String>("All folder where follow up editor is a specific group", BASIS_FOLDER_BY_GROUP_XPATH_QUERY)) ;
        lsQuery.add(new SelectItemOption<String>("All folder where follow up editor is a specific user", BASIS_FOLDER_BY_USER_XPATH_QUERY)) ;
        lsQuery.add(new SelectItemOption<String>("All folder where follow up editor is a specific action", BASIS_FOLDER_BY_ACTION_XPATH_QUERY)) ;
        UIFormSelectBox uiFormSelectBox = new UIFormSelectBox(QUERY, QUERY, lsQuery) ;
        uiFormSelectBox.setOnChange("Change");
        addUIFormInput(uiFormSelectBox);

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

            String language = Util.getPortalRequestContext().getLocale().getLanguage().toUpperCase();

            /*ExoContainer exoContainer = ExoContainerContext.getCurrentContainer();
            RepositoryService rs = (RepositoryService) exoContainer.getComponentInstanceOfType(RepositoryService.class);
            Session session = (Session) rs.getRepository("repository").getSystemSession("collaboration");
            QueryManager queryManager = null;
            queryManager = session.getWorkspace().getQueryManager();
            String xpathStatement = "/jcr:root/Files/BO//element (*, nt:file) [jcr:like(@exo:name,'followSelector.txt')]";
            Query query = queryManager.createQuery(xpathStatement, Query.XPATH);
            QueryResult result = query.execute();
            NodeIterator nodeIterator = result.getNodes();
            Node followSelectorNode = (Node) nodeIterator.nextNode();

            BasisSelectorService basisSelectorService = new BasisSelectorService(followSelectorNode, "GroupInternEditor", language);  */


            uiSimpleSearchForm.removeChildById(ATTRIBUT);
            if(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue().equals("byGroup")){
                List<SelectItemOption<String>> lsAttribut = new ArrayList<SelectItemOption<String>>() ;
                lsAttribut.add(new SelectItemOption<String>("Administration générale de la fiscalité", "Administration générale de la fiscalité")) ;
                lsAttribut.add(new SelectItemOption<String>("Administration générale des douanes et accises", "Administration générale des douanes et accises")) ;
                lsAttribut.add(new SelectItemOption<String>("Administration général de la perception et du recouvrement", "Administration général de la perception et du recouvrement")) ;
                lsAttribut.add(new SelectItemOption<String>("Administration général de la lutte contre la fraude fiscale", "Administration général de la lutte contre la fraude fiscale")) ;
                lsAttribut.add(new SelectItemOption<String>("Administration général de la documentation patrimoniale", "Administration général de la documentation patrimoniale")) ;
                lsAttribut.add(new SelectItemOption<String>("Administration général de la trésorerie", "Administration général de la trésorerie")) ;
                lsAttribut.add(new SelectItemOption<String>("Service d'encadrement expertise et support stratégique", "Service d'encadrement expertise et support stratégique")) ;
                lsAttribut.add(new SelectItemOption<String>("Service d'encadrement coordination stratégique et communication", "Service d'encadrement coordination stratégique et communication")) ;
                lsAttribut.add(new SelectItemOption<String>("Service d'encadrement Personnel et Organisation", "Service d'encadrement Personnel et Organisation")) ;
                lsAttribut.add(new SelectItemOption<String>("Service d'encadrement Budget et Contrôle de la Gestion", "Service d'encadrement Budget et Contrôle de la Gestion")) ;
                lsAttribut.add(new SelectItemOption<String>("Service d'encadrement Technologie de l'information et de la Communication", "Service d'encadrement Technologie de l'information et de la Communication")) ;
                lsAttribut.add(new SelectItemOption<String>("Service d'encadrement Logistique", "Service d'encadrement Logistique")) ;
                lsAttribut.add(new SelectItemOption<String>("Service Prestation de services multicanaux", "Service Prestation de services multicanaux")) ;
                lsAttribut.add(new SelectItemOption<String>("Cellule Fiscalité des investissements étrangers", "Cellule Fiscalité des investissements étrangers")) ;
                lsAttribut.add(new SelectItemOption<String>("Service des décisions anticipées", "Service des décisions anticipées")) ;
                lsAttribut.add(new SelectItemOption<String>("Service de concilation fiscale", "Service de concilation fiscale")) ;
                lsAttribut.add(new SelectItemOption<String>("Observatoire de la fiscalité régionale", "Observatoire de la fiscalité régionale")) ;
                lsAttribut.add(new SelectItemOption<String>("Service d'audit interne", "Service d'audit interne")) ;
                lsAttribut.add(new SelectItemOption<String>("Service juridique central", "Service juridique central")) ;
                lsAttribut.add(new SelectItemOption<String>("Services du Président", "Services du Président")) ;
                lsAttribut.add(new SelectItemOption<String>("Ministre des Finances", "Ministre des Finances")) ;
                lsAttribut.add(new SelectItemOption<String>("Secrétaire d'Etat à la modernisation du SPF Finances", "Secrétaire d'Etat à la modernisation du SPF Finances")) ;
                lsAttribut.add(new SelectItemOption<String>("Cour des comptes ", "Cour des comptes ")) ;
                lsAttribut.add(new SelectItemOption<String>("Médiateurs fédéraux", "Médiateurs fédéraux")) ;
                lsAttribut.add(new SelectItemOption<String>("Autres", "Autres")) ;
                lsAttribut.add(new SelectItemOption<String>("Privacy", "Privacy")) ;
                lsAttribut.add(new SelectItemOption<String>("Développement durable", "Développement durable")) ;
                lsAttribut.add(new SelectItemOption<String>("Inspection des finances", "Inspection des finances")) ;
                UIFormSelectBox uiFormSelectBox = new UIFormSelectBox(ATTRIBUT, ATTRIBUT, lsAttribut) ;
                uiSimpleSearchForm.addUIFormInput(uiFormSelectBox);
            }
            else if(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue().equals("byUser")){
                uiSimpleSearchForm.addUIFormInput(new UIFormStringInput(ATTRIBUT, ATTRIBUT, null));
            }
            else if(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue().equals("byAction")){
                List<SelectItemOption<String>> lsAttribut = new ArrayList<SelectItemOption<String>>() ;
                lsAttribut.add(new SelectItemOption<String>("Pour projet de réponse à signer par le Ministre", "Pour projet de réponse à signer par le Ministre")) ;
                lsAttribut.add(new SelectItemOption<String>("Pour rapport circonstancié / succinct", "Pour rapport circonstancié / succinct")) ;
                lsAttribut.add(new SelectItemOption<String>("Pour (enquête et) avis", "Pour (enquête et) avis")) ;
                lsAttribut.add(new SelectItemOption<String>("Pour exécution avec prière de me tenir au courant", "Pour exécution avec prière de me tenir au courant")) ;
                lsAttribut.add(new SelectItemOption<String>("Pour réponse à l'intéressé (copie Cabinet)", "Pour réponse à l'intéressé (copie Cabinet)")) ;
                lsAttribut.add(new SelectItemOption<String>("Prendre contact avec le contribuable/receveur", "Prendre contact avec le contribuable/receveur")) ;
                lsAttribut.add(new SelectItemOption<String>("Autre - à suivre", "Autre - à suivre")) ;
                lsAttribut.add(new SelectItemOption<String>("Pour information", "Pour information")) ;
                lsAttribut.add(new SelectItemOption<String>("Pour suite voulue", "Pour suite voulue")) ;
                lsAttribut.add(new SelectItemOption<String>("Autre", "Autre")) ;
                lsAttribut.add(new SelectItemOption<String>("Pour projet de réponse à signer par le Président", "Pour projet de réponse à signer par le Président")) ;
                lsAttribut.add(new SelectItemOption<String>("RAPPEL", "RAPPEL")) ;
                lsAttribut.add(new SelectItemOption<String>("Dossier ancien", "Dossier ancien")) ;
                lsAttribut.add(new SelectItemOption<String>("Mail à suivre", "Mail à suivre")) ;
                lsAttribut.add(new SelectItemOption<String>("Mail à ne pas suivre", "Mail à ne pas suivre")) ;
                UIFormSelectBox uiFormSelectBox = new UIFormSelectBox(ATTRIBUT, ATTRIBUT, lsAttribut) ;
                uiSimpleSearchForm.addUIFormInput(uiFormSelectBox);
            }
            event.getRequestContext().addUIComponentToUpdateByAjax(uiSimpleSearchForm) ;
        }
    }
}
