package SearchBasis.portlet.component;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
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
import org.exoplatform.webui.form.UIFormSelectBox;

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

    private static final String ARTICLE_XPATH_QUERY = "//element(*,exo:article)" ;
    private static final String BASIS_DOCUMENT_XPATH_QUERY = "//element(*,basis:basisDocument)" ;

    public UISimpleSearchForm() throws Exception {
        List<SelectItemOption<String>> lsOperator = new ArrayList<SelectItemOption<String>>() ;
        lsOperator.add(new SelectItemOption<String>(" ", " ")) ;
        lsOperator.add(new SelectItemOption<String>("All article", ARTICLE_XPATH_QUERY)) ;
        lsOperator.add(new SelectItemOption<String>("All basis document", BASIS_DOCUMENT_XPATH_QUERY)) ;
        UIFormSelectBox uiSelectBoxOperator = new UIFormSelectBox(QUERY, QUERY, lsOperator) ;
        addUIFormInput(uiSelectBoxOperator);

        setActions(new String[]{"Search", "Cancel"}) ;

    }

    static public class SearchActionListener extends EventListener<UISimpleSearchForm> {
        public void execute(Event<UISimpleSearchForm> event) throws Exception {
            UISimpleSearchForm uiSimpleSearchForm = event.getSource();
            UIApplication uiApp = uiSimpleSearchForm.getAncestorOfType(UIApplication.class);

            try {
                ExoContainer exoContainer = ExoContainerContext.getCurrentContainer();
                RepositoryService rs = (RepositoryService) exoContainer.getComponentInstanceOfType(RepositoryService.class);
                Session session = (Session) rs.getRepository("repository").getSystemSession("collaboration");
                QueryManager queryManager = null;
                queryManager = session.getWorkspace().getQueryManager();
                String xpathStatement = uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue();
                if(!xpathStatement.equals(null)){
                    System.out.println("Query : " + xpathStatement);
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
                    System.out.print("ActionServiceContainer - Query Manager Factory of workspace not found. Check configuration." + e);
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
