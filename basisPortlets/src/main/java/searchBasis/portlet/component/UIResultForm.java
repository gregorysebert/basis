package searchBasis.portlet.component;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormStringInput;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: rousselle-k
 * Date: 23/11/12
 * Time: 12:47
 * To change this template use File | Settings | File Templates.
 */
@ComponentConfig(
        lifecycle = UIFormLifecycle.class,
        template =  "app:/groovy/SearchBasis/portlet/UIResultForm.gtmpl"
)
public class UIResultForm extends UIForm {

    private QueryResult queryResult;
    private NodeIterator nodeIterator1;
    Session session = null;

    public UIResultForm () throws Exception{
        ExoContainer exoContainer = ExoContainerContext.getCurrentContainer();
        RepositoryService rs = (RepositoryService) exoContainer.getComponentInstanceOfType(RepositoryService.class);
        session = (Session) rs.getRepository("repository").getSystemSession("collaboration");
    }

    public  void update()throws Exception{
        UISearchBasisPortlet uiSearchBasisPortlet = this.getAncestorOfType(UISearchBasisPortlet.class);
        try{
            if(uiSearchBasisPortlet.getQueryResult() != null){
                queryResult = uiSearchBasisPortlet.getQueryResult();
                nodeIterator1 = queryResult.getNodes();
                String typeQuery = uiSearchBasisPortlet.getTypeQuery();

                while(nodeIterator1.hasNext()){
                    Node basisFolderNode = nodeIterator1.nextNode();

                    String url = Util.getPortalRequestContext().getRequestURI();
                    String urlSplitted[] = url.split("BO:");
                    String nameBO[] = urlSplitted[1].split("/");

                    String selectFollowByFolder = "/jcr:root/Files/BO/"+nameBO[0]+"/*/*/*/"+basisFolderNode.getName()+"/element (*,basis:basisFollow) order by @exo:dateCreated descending";

                    QueryManager queryManager = session.getWorkspace().getQueryManager();
                    Query selectFollowByFolderQuery = queryManager.createQuery(selectFollowByFolder, Query.XPATH);
                    QueryResult resultQuery2 = selectFollowByFolderQuery.execute();
                    NodeIterator nodeIterator2 = resultQuery2.getNodes();
                    Node basisFollowNode = nodeIterator2.nextNode();

                    if(typeQuery.equals("currentUser")){
                        String remoteUser =  Util.getPortalRequestContext().getRemoteUser();
                        if(basisFollowNode.getProperty("basis:followUserInternEditor").getString().equals(remoteUser)){
                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                            String path = basisFolderNode.getPath() ;
                            String pathSlippted [] = path.split("/Files/BO");
                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                        }
                    }
                    else if(typeQuery.equals("byGroup")){
                        String group = uiSearchBasisPortlet.getAttribute();
                        if(basisFollowNode.getProperty("basis:followGroupInternEditor").getString().equals(group)){
                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                            String path = basisFolderNode.getPath() ;
                            String pathSlippted [] = path.split("/Files/BO");
                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                        }
                    }
                    else if(typeQuery.equals("byUser")){
                        String user = uiSearchBasisPortlet.getAttribute();
                        if(basisFollowNode.getProperty("basis:followUserInternEditor").getString().equals(user)){
                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                            String path = basisFolderNode.getPath() ;
                            String pathSlippted [] = path.split("/Files/BO");
                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                        }
                    }
                    else if(typeQuery.equals("byAction")){
                        String action = uiSearchBasisPortlet.getAttribute();
                        if(basisFollowNode.getProperty("basis:followRequiredAction").getString().equals(action)){
                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                            String path = basisFolderNode.getPath() ;
                            String pathSlippted [] = path.split("/Files/BO");
                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                        }
                    }
                }
            }
        }
        catch (NullPointerException ex){
            System.out.print("Null Pointer Exception" + ex);
        }

    }
    public  void updateAdvanced()throws Exception{
        UISearchBasisPortlet uiSearchBasisPortlet = this.getAncestorOfType(UISearchBasisPortlet.class);
        try{
            if(uiSearchBasisPortlet.getQueryResult() != null){
                queryResult = uiSearchBasisPortlet.getQueryResult();
                nodeIterator1 = queryResult.getNodes();
                while(nodeIterator1.hasNext()){
                    Node basisFolderNode = nodeIterator1.nextNode();
                    UIAdvancedSearchForm uiAdvancedSearchForm = uiSearchBasisPortlet.getChild(UIAdvancedSearchForm.class);
                    UIBasisFollowFolderForm uiBasisFollowFolderForm = uiAdvancedSearchForm.getChildById("Follow folder");
                    UIBasisFollowDocForm uiBasisFollowDocForm = uiAdvancedSearchForm.getChildById("Follow document");

                    if(uiBasisFollowFolderForm.getUIInput("RadioBox_folder").getValue().toString().equals("Last")){
                        String url = Util.getPortalRequestContext().getRequestURI();
                        String urlSplitted[] = url.split("BO:");
                        String nameBO[] = urlSplitted[1].split("/");

                        String selectFollowFolderByFolder = "/jcr:root/Files/BO/"+nameBO[0]+"/*/*/*/"+basisFolderNode.getName()+"/element (*,basis:basisFollow) order by @exo:dateCreated descending";

                        QueryManager queryManager = session.getWorkspace().getQueryManager();
                        Query selectFollowByFolderQuery = queryManager.createQuery(selectFollowFolderByFolder, Query.XPATH);
                        QueryResult resultQuery2 = selectFollowByFolderQuery.execute();
                        NodeIterator nodeIterator2 = resultQuery2.getNodes();
                        Node basisFollowFolderNode = nodeIterator2.nextNode();
                        Map<String,String[]> mapFollowFolder = uiSearchBasisPortlet.getMapFollowFolder();
                        if(!mapFollowFolder.isEmpty()){
                            for (String mapKey : mapFollowFolder.keySet()) {
                                String[] value = mapFollowFolder.get(mapKey);
                                if(value[0].equals("Equals")){
                                    if(basisFollowFolderNode.getProperty(mapKey).getString().equals(value[1])){
                                        if(uiBasisFollowDocForm.getUIInput("RadioBox_doc").getValue().toString().equals("Last")){
                                            String url2 = Util.getPortalRequestContext().getRequestURI();
                                            String urlSplitted2[] = url2.split("BO:");
                                            String nameBO2[] = urlSplitted2[1].split("/");

                                            String selectFollowDocByFolder = "/jcr:root/Files/BO/"+nameBO2[0]+"/*/*/*/"+basisFolderNode.getName()+"/*/element (*,basis:basisFollow) order by @exo:dateCreated descending";

                                            QueryManager queryManager2 = session.getWorkspace().getQueryManager();
                                            Query selectFollowDocByFolderQuery = queryManager2.createQuery(selectFollowDocByFolder, Query.XPATH);
                                            QueryResult resultQuery3 = selectFollowDocByFolderQuery.execute();
                                            NodeIterator nodeIterator3 = resultQuery3.getNodes();
                                            Node basisFollowDocNode = nodeIterator3.nextNode();
                                            Map<String,String[]> mapFollowDoc = uiSearchBasisPortlet.getMapFollowDoc();
                                            if(!mapFollowDoc.isEmpty()){
                                                for (String mapKey2 : mapFollowDoc.keySet()) {
                                                    String[] value2 = mapFollowDoc.get(mapKey2);
                                                    if(value2[0].equals("Equals")){
                                                        if(basisFollowDocNode.getProperty(mapKey2).getString().equals(value2[1])){
                                                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                                            String path = basisFolderNode.getPath() ;
                                                            String pathSlippted [] = path.split("/Files/BO");
                                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                                        }
                                                    }
                                                    else if(value2[0].equals("Contains")){
                                                        if(basisFollowDocNode.getProperty(mapKey2).getString().contains(value2[1])){
                                                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                                            String path = basisFolderNode.getPath() ;
                                                            String pathSlippted [] = path.split("/Files/BO");
                                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                                        }
                                                    }
                                                    else if(value2[0].equals("Not_Equals")){
                                                        if(!basisFollowDocNode.getProperty(mapKey2).getString().equals(value2[1])){
                                                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                                            String path = basisFolderNode.getPath() ;
                                                            String pathSlippted [] = path.split("/Files/BO");
                                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                                        }
                                                    }
                                                    else if(value2[0].equals("Not_Contains")){
                                                        if(!basisFollowDocNode.getProperty(mapKey2).getString().contains(value2[1])){
                                                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                                            String path = basisFolderNode.getPath() ;
                                                            String pathSlippted [] = path.split("/Files/BO");
                                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        else{

                                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                            String path = basisFolderNode.getPath() ;
                                            String pathSlippted [] = path.split("/Files/BO");
                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        }
                                    }
                                }
                                else if(value[0].equals("Contains")){
                                    if(basisFollowFolderNode.getProperty(mapKey).getString().contains(value[1])){
                                        if(uiBasisFollowDocForm.getUIInput("RadioBox_doc").getValue().toString().equals("Last")){
                                            String url2 = Util.getPortalRequestContext().getRequestURI();
                                            String urlSplitted2[] = url2.split("BO:");
                                            String nameBO2[] = urlSplitted2[1].split("/");

                                            String selectFollowDocByFolder = "/jcr:root/Files/BO/"+nameBO2[0]+"/*/*/*/"+basisFolderNode.getName()+"/*/element (*,basis:basisFollow) order by @exo:dateCreated descending";

                                            QueryManager queryManager2 = session.getWorkspace().getQueryManager();
                                            Query selectFollowDocByFolderQuery = queryManager2.createQuery(selectFollowDocByFolder, Query.XPATH);
                                            QueryResult resultQuery3 = selectFollowDocByFolderQuery.execute();
                                            NodeIterator nodeIterator3 = resultQuery3.getNodes();
                                            Node basisFollowDocNode = nodeIterator3.nextNode();
                                            Map<String,String[]> mapFollowDoc = uiSearchBasisPortlet.getMapFollowDoc();
                                            if(!mapFollowDoc.isEmpty()){
                                                for (String mapKey2 : mapFollowDoc.keySet()) {
                                                    String[] value2 = mapFollowDoc.get(mapKey2);
                                                    if(value2[0].equals("Equals")){
                                                        if(basisFollowDocNode.getProperty(mapKey2).getString().equals(value2[1])){
                                                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                                            String path = basisFolderNode.getPath() ;
                                                            String pathSlippted [] = path.split("/Files/BO");
                                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                                        }
                                                    }
                                                    else if(value2[0].equals("Contains")){
                                                        if(basisFollowDocNode.getProperty(mapKey2).getString().contains(value2[1])){
                                                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                                            String path = basisFolderNode.getPath() ;
                                                            String pathSlippted [] = path.split("/Files/BO");
                                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                                        }
                                                    }
                                                    else if(value2[0].equals("Not_Equals")){
                                                        if(!basisFollowDocNode.getProperty(mapKey2).getString().equals(value2[1])){
                                                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                                            String path = basisFolderNode.getPath() ;
                                                            String pathSlippted [] = path.split("/Files/BO");
                                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                                        }
                                                    }
                                                    else if(value2[0].equals("Not_Contains")){
                                                        if(!basisFollowDocNode.getProperty(mapKey2).getString().contains(value2[1])){
                                                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                                            String path = basisFolderNode.getPath() ;
                                                            String pathSlippted [] = path.split("/Files/BO");
                                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        else{

                                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                            String path = basisFolderNode.getPath() ;
                                            String pathSlippted [] = path.split("/Files/BO");
                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        }
                                    }
                                }
                                else if(value[0].equals("Not_Equals")){
                                    if(!basisFollowFolderNode.getProperty(mapKey).getString().equals(value[1])){
                                        if(uiBasisFollowDocForm.getUIInput("RadioBox_doc").getValue().toString().equals("Last")){
                                            String url2 = Util.getPortalRequestContext().getRequestURI();
                                            String urlSplitted2[] = url2.split("BO:");
                                            String nameBO2[] = urlSplitted2[1].split("/");

                                            String selectFollowDocByFolder = "/jcr:root/Files/BO/"+nameBO2[0]+"/*/*/*/"+basisFolderNode.getName()+"/*/element (*,basis:basisFollow) order by @exo:dateCreated descending";

                                            QueryManager queryManager2 = session.getWorkspace().getQueryManager();
                                            Query selectFollowDocByFolderQuery = queryManager2.createQuery(selectFollowDocByFolder, Query.XPATH);
                                            QueryResult resultQuery3 = selectFollowDocByFolderQuery.execute();
                                            NodeIterator nodeIterator3 = resultQuery3.getNodes();
                                            Node basisFollowDocNode = nodeIterator3.nextNode();
                                            Map<String,String[]> mapFollowDoc = uiSearchBasisPortlet.getMapFollowDoc();
                                            if(!mapFollowDoc.isEmpty()){
                                                for (String mapKey2 : mapFollowDoc.keySet()) {
                                                    String[] value2 = mapFollowDoc.get(mapKey2);
                                                    if(value2[0].equals("Equals")){
                                                        if(basisFollowDocNode.getProperty(mapKey2).getString().equals(value2[1])){
                                                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                                            String path = basisFolderNode.getPath() ;
                                                            String pathSlippted [] = path.split("/Files/BO");
                                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                                        }
                                                    }
                                                    else if(value2[0].equals("Contains")){
                                                        if(basisFollowDocNode.getProperty(mapKey2).getString().contains(value2[1])){
                                                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                                            String path = basisFolderNode.getPath() ;
                                                            String pathSlippted [] = path.split("/Files/BO");
                                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                                        }
                                                    }
                                                    else if(value2[0].equals("Not_Equals")){
                                                        if(!basisFollowDocNode.getProperty(mapKey2).getString().equals(value2[1])){
                                                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                                            String path = basisFolderNode.getPath() ;
                                                            String pathSlippted [] = path.split("/Files/BO");
                                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                                        }
                                                    }
                                                    else if(value2[0].equals("Not_Contains")){
                                                        if(!basisFollowDocNode.getProperty(mapKey2).getString().contains(value2[1])){
                                                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                                            String path = basisFolderNode.getPath() ;
                                                            String pathSlippted [] = path.split("/Files/BO");
                                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        else{

                                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                            String path = basisFolderNode.getPath() ;
                                            String pathSlippted [] = path.split("/Files/BO");
                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        }
                                    }
                                }
                                else if(value[0].equals("Not_Contains")){
                                    if(!basisFollowFolderNode.getProperty(mapKey).getString().contains(value[1])){
                                        if(uiBasisFollowDocForm.getUIInput("RadioBox_doc").getValue().toString().equals("Last")){
                                            String url2 = Util.getPortalRequestContext().getRequestURI();
                                            String urlSplitted2[] = url2.split("BO:");
                                            String nameBO2[] = urlSplitted2[1].split("/");

                                            String selectFollowDocByFolder = "/jcr:root/Files/BO/"+nameBO2[0]+"/*/*/*/"+basisFolderNode.getName()+"/*/element (*,basis:basisFollow) order by @exo:dateCreated descending";

                                            QueryManager queryManager2 = session.getWorkspace().getQueryManager();
                                            Query selectFollowDocByFolderQuery = queryManager2.createQuery(selectFollowDocByFolder, Query.XPATH);
                                            QueryResult resultQuery3 = selectFollowDocByFolderQuery.execute();
                                            NodeIterator nodeIterator3 = resultQuery3.getNodes();
                                            Node basisFollowDocNode = nodeIterator3.nextNode();
                                            Map<String,String[]> mapFollowDoc = uiSearchBasisPortlet.getMapFollowDoc();
                                            if(!mapFollowDoc.isEmpty()){
                                                for (String mapKey2 : mapFollowDoc.keySet()) {
                                                    String[] value2 = mapFollowDoc.get(mapKey2);
                                                    if(value2[0].equals("Equals")){
                                                        if(basisFollowDocNode.getProperty(mapKey2).getString().equals(value2[1])){
                                                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                                            String path = basisFolderNode.getPath() ;
                                                            String pathSlippted [] = path.split("/Files/BO");
                                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                                        }
                                                    }
                                                    else if(value2[0].equals("Contains")){
                                                        if(basisFollowDocNode.getProperty(mapKey2).getString().contains(value2[1])){
                                                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                                            String path = basisFolderNode.getPath() ;
                                                            String pathSlippted [] = path.split("/Files/BO");
                                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                                        }
                                                    }
                                                    else if(value2[0].equals("Not_Equals")){
                                                        if(!basisFollowDocNode.getProperty(mapKey2).getString().equals(value2[1])){
                                                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                                            String path = basisFolderNode.getPath() ;
                                                            String pathSlippted [] = path.split("/Files/BO");
                                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                                        }
                                                    }
                                                    else if(value2[0].equals("Not_Contains")){
                                                        if(!basisFollowDocNode.getProperty(mapKey2).getString().contains(value2[1])){
                                                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                                            String path = basisFolderNode.getPath() ;
                                                            String pathSlippted [] = path.split("/Files/BO");
                                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        else{

                                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                            String path = basisFolderNode.getPath() ;
                                            String pathSlippted [] = path.split("/Files/BO");
                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        }
                                    }
                                }
                            }
                        }

                    }
                    else{
                        if(uiBasisFollowDocForm.getUIInput("RadioBox_doc").getValue().toString().equals("Last")){
                            String url = Util.getPortalRequestContext().getRequestURI();
                            String urlSplitted[] = url.split("BO:");
                            String nameBO[] = urlSplitted[1].split("/");

                            String selectFollowDocByFolder = "/jcr:root/Files/BO/"+nameBO[0]+"/*/*/*/"+basisFolderNode.getName()+"/*/element (*,basis:basisFollow) order by @exo:dateCreated descending";

                            QueryManager queryManager = session.getWorkspace().getQueryManager();
                            Query selectFollowDocByFolderQuery = queryManager.createQuery(selectFollowDocByFolder, Query.XPATH);
                            QueryResult resultQuery2 = selectFollowDocByFolderQuery.execute();
                            NodeIterator nodeIterator2 = resultQuery2.getNodes();
                            Node basisFollowDocNode = nodeIterator2.nextNode();
                            Map<String,String[]> mapFollowDoc = uiSearchBasisPortlet.getMapFollowDoc();
                            if(!mapFollowDoc.isEmpty()){
                                for (String mapKey : mapFollowDoc.keySet()) {
                                    String[] value = mapFollowDoc.get(mapKey);
                                    if(value[0].equals("Equals")){
                                        if(basisFollowDocNode.getProperty(mapKey).getString().equals(value[1])){
                                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                            String path = basisFolderNode.getPath() ;
                                            String pathSlippted [] = path.split("/Files/BO");
                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        }
                                    }
                                    else if(value[0].equals("Contains")){
                                        if(basisFollowDocNode.getProperty(mapKey).getString().contains(value[1])){
                                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                            String path = basisFolderNode.getPath() ;
                                            String pathSlippted [] = path.split("/Files/BO");
                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        }
                                    }
                                    else if(value[0].equals("Not_Equals")){
                                        if(!basisFollowDocNode.getProperty(mapKey).getString().equals(value[1])){
                                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                            String path = basisFolderNode.getPath() ;
                                            String pathSlippted [] = path.split("/Files/BO");
                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        }
                                    }
                                    else if(value[0].equals("Not_Contains")){
                                        if(!basisFollowDocNode.getProperty(mapKey).getString().contains(value[1])){
                                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                            String path = basisFolderNode.getPath() ;
                                            String pathSlippted [] = path.split("/Files/BO");
                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        }
                                    }
                                }
                            }
                        }
                        else{

                            addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                            String path = basisFolderNode.getPath() ;
                            String pathSlippted [] = path.split("/Files/BO");
                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                        }
                    }
                }
            }
        }
        catch (NullPointerException ex){
            System.out.print("Null Pointer Exception" + ex);
        }

    }

    public QueryResult getQueryResult() {
        return queryResult;
    }

    public void setQueryResult(QueryResult queryResult) {
        this.queryResult = queryResult;
    }
}
