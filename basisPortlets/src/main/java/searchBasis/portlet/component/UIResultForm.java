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
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
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
    private Session session = null;
    private String lastId = "";

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
        String from = uiSearchBasisPortlet.getFrom();
        Map<String,String[]> mapFollowDocProperty = uiSearchBasisPortlet.getMapFollowDoc();
        Map<String,String[]> mapDocument = uiSearchBasisPortlet.getMapDoc();
        try{
            if(uiSearchBasisPortlet.getQueryResult() != null){
                UIAdvancedSearchForm uiAdvancedSearchForm = uiSearchBasisPortlet.getChild(UIAdvancedSearchForm.class);
                UIBasisFollowFolderForm uiBasisFollowFolderForm = uiAdvancedSearchForm.getChildById("Follow folder");
                UIBasisFollowDocForm uiBasisFollowDocForm = uiAdvancedSearchForm.getChildById("Follow document");

                queryResult = uiSearchBasisPortlet.getQueryResult();
                nodeIterator1 = queryResult.getNodes();
                while(nodeIterator1.hasNext()){
                    Node basisFolderNode = nodeIterator1.nextNode();



                    if(from.equals("Folder")){

                        if(uiBasisFollowFolderForm.getUIInput("RadioBox_folder").getValue().toString().equals("Last")){
                            lastFollowFolder(basisFolderNode, basisFolderNode, uiSearchBasisPortlet, uiBasisFollowDocForm, from);

                        }
                        else{
                            if(uiBasisFollowDocForm.getUIInput("RadioBox_doc").getValue().toString().equals("Last")){
                                lastFollowDoc(basisFolderNode, basisFolderNode , uiSearchBasisPortlet, from);
                            }
                            else{

                                addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                String path = basisFolderNode.getPath() ;
                                String pathSlippted [] = path.split("/Files/BO");
                                addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                            }
                        }
                    }
                    else if(from.equals("Document")){

                        String url = Util.getPortalRequestContext().getRequestURI();
                        String urlSplitted[] = url.split("BO:");
                        String nameBO[] = urlSplitted[1].split("/");

                        String selectDocumentByFolder = "/jcr:root/Files/BO/"+nameBO[0]+"/*/*/*/"+basisFolderNode.getName()+"/element (*,basis:basisDocument)";
                        QueryManager queryManager = session.getWorkspace().getQueryManager();
                        Query selectDocumentByFolderQuery = queryManager.createQuery(selectDocumentByFolder, Query.XPATH);
                        QueryResult resultQuery2 = selectDocumentByFolderQuery.execute();
                        NodeIterator nodeIterator2 = resultQuery2.getNodes();

                        while(nodeIterator2.hasNext()){
                            Node basisDocumentNode = nodeIterator2.nextNode();

                            if(!mapDocument.isEmpty()){
                                filter(mapDocument, basisDocumentNode,basisFolderNode, uiBasisFollowFolderForm, uiBasisFollowDocForm, uiSearchBasisPortlet, from );
                            }
                            else{
                                if(uiBasisFollowFolderForm.getUIInput("RadioBox_folder").getValue().toString().equals("Last")){
                                    lastFollowFolder(basisDocumentNode, basisFolderNode, uiSearchBasisPortlet, uiBasisFollowDocForm, from);

                                }
                                else{
                                    if(uiBasisFollowDocForm.getUIInput("RadioBox_doc").getValue().toString().equals("Last")){
                                        lastFollowDoc(basisDocumentNode, basisFolderNode , uiSearchBasisPortlet, from);
                                    }
                                    else{

                                        if(!mapFollowDocProperty.isEmpty()){
                                            filterDocByAllFollowDoc(mapFollowDocProperty, basisDocumentNode,basisFolderNode);
                                        }
                                        else{
                                            addUIFormInput(new UIFormStringInput(basisDocumentNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                            String path = basisDocumentNode.getPath() ;
                                            String pathSlippted [] = path.split("/Files/BO");
                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        }
                                    }
                                }
                            }
                        }

                    }
                    else if(from.equals("Follow_folder")){
                        String url = Util.getPortalRequestContext().getRequestURI();
                        String urlSplitted[] = url.split("BO:");
                        String nameBO[] = urlSplitted[1].split("/");
                        int i = 0 ;

                        String selectFollowFolderByFolder = "/jcr:root/Files/BO/"+nameBO[0]+"/*/*/*/"+basisFolderNode.getName()+"/element (*,basis:basisFollow)";
                        Map<String,String[]> mapFollowFolder = uiSearchBasisPortlet.getMapFollowFolder();
                        if(!mapFollowFolder.isEmpty()){
                            selectFollowFolderByFolder += "[";
                            for (String mapKey : mapFollowFolder.keySet()) {
                                String[] value = mapFollowFolder.get(mapKey);

                                if(value[0].equals("Equals")){
                                    if(!mapKey.contains("Date")){
                                        if(i == 0){
                                            selectFollowFolderByFolder += "@"+mapKey+"='"+value[1]+"'";
                                        }
                                        else{
                                            selectFollowFolderByFolder += " and @"+mapKey+"='"+value[1]+"'";
                                        }
                                    }
                                    else{
                                        if(i == 0){
                                            selectFollowFolderByFolder += "@"+mapKey+"=xs:dateTime('"+value[1]+"')";
                                        }
                                        else{
                                            selectFollowFolderByFolder += " and @"+mapKey+"=xs:dateTime('"+value[1]+"')";
                                        }
                                    }
                                }
                                else if(value[0].equals("Contains")){
                                    if(!mapKey.contains("Date")){
                                        if(i == 0){
                                            selectFollowFolderByFolder += "jcr:like(@"+mapKey+",'%"+value[1]+"%')";
                                        }
                                        else{
                                            selectFollowFolderByFolder += " and jcr:like(@"+mapKey+",'%"+value[1]+"%')";
                                        }
                                    }
                                    else{
                                        selectFollowFolderByFolder += "";
                                    }
                                }
                                else if(value[0].equals("Not_Equals")){
                                    if(!mapKey.contains("Date")){
                                        if(i == 0){
                                            selectFollowFolderByFolder += "not(@"+mapKey+"='"+value[1]+"')";
                                        }
                                        else{
                                            selectFollowFolderByFolder += " and not(@"+mapKey+"='"+value[1]+"')";
                                        }
                                    }
                                    else{
                                        if(i == 0){
                                            selectFollowFolderByFolder += "not(@"+mapKey+"=xs:dateTime('"+value[1]+"'))";
                                        }
                                        else{
                                            selectFollowFolderByFolder += " and not(@"+mapKey+"=xs:dateTime('"+value[1]+"'))";
                                        }
                                    }
                                }
                                else if(value[0].equals("Not_Contains")){
                                    if(!mapKey.contains("Date")){
                                        if(i == 0){
                                            selectFollowFolderByFolder += "not(jcr:like(@"+mapKey+",'%"+value[1]+"%'))";
                                        }
                                        else{
                                            selectFollowFolderByFolder += " and not(jcr:like(@"+mapKey+",'%"+value[1]+"%'))";
                                        }
                                    }
                                    else{
                                        selectFollowFolderByFolder += "";
                                    }
                                }

                                i++;

                            }
                            selectFollowFolderByFolder += "]";
                        }


                        System.out.println("Query : " + selectFollowFolderByFolder);

                        QueryManager queryManager = session.getWorkspace().getQueryManager();
                        Query selectDocumentByFolderQuery = queryManager.createQuery(selectFollowFolderByFolder, Query.XPATH);
                        QueryResult resultQuery2 = selectDocumentByFolderQuery.execute();
                        NodeIterator nodeIterator2 = resultQuery2.getNodes();

                        while(nodeIterator2.hasNext()){
                            Node basisFollowFolderNode = nodeIterator2.nextNode();

                            if(uiBasisFollowFolderForm.getUIInput("RadioBox_folder").getValue().toString().equals("Last")){
                                lastFollowFolder(basisFollowFolderNode, basisFolderNode, uiSearchBasisPortlet, uiBasisFollowDocForm, from);

                            }
                            else{
                                if(uiBasisFollowDocForm.getUIInput("RadioBox_doc").getValue().toString().equals("Last")){
                                    lastFollowDoc(basisFollowFolderNode, basisFolderNode , uiSearchBasisPortlet, from);
                                }
                                else{
                                    addUIFormInput(new UIFormStringInput(basisFollowFolderNode.getProperty("exo:name").getString(), basisFolderNode.getProperty("exo:name").getString(), null));
                                    String path = basisFollowFolderNode.getPath() ;
                                    String pathSlippted [] = path.split("/Files/BO");
                                    addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                }
                            }
                        }

                    }
                    else if(from.equals("Follow_document")){

                    }
                }
            }
        }
        catch (NullPointerException ex){
            System.out.print("Null Pointer Exception" + ex);
        }

    }


    public void filter(Map<String,String[]> mapNodeProperty, Node basisNode, Node basisFolderNode, UIBasisFollowFolderForm uiBasisFollowFolderForm, UIBasisFollowDocForm uiBasisFollowDocForm,  UISearchBasisPortlet uiSearchBasisPortlet, String from)throws Exception{
        String valueProperty;
        for (String mapKey : mapNodeProperty.keySet()) {
            String[] value = mapNodeProperty.get(mapKey);
            if(mapKey.contains("Date")){
                valueProperty =  basisNode.getProperty(mapKey).getString().split("T")[0];
            }
            else{
                valueProperty =  basisNode.getProperty(mapKey).getString();
            }
            if(value[0].equals("Equals")){
                if(valueProperty.equals(value[1])){
                    if(uiBasisFollowFolderForm.getUIInput("RadioBox_folder").getValue().toString().equals("Last")){
                        lastFollowFolder(basisNode, basisFolderNode, uiSearchBasisPortlet, uiBasisFollowDocForm, from);

                    }
                    else{
                        if(uiBasisFollowDocForm.getUIInput("RadioBox_doc").getValue().toString().equals("Last")){
                            lastFollowDoc(basisNode, basisFolderNode , uiSearchBasisPortlet, from);
                        }
                        else{
                            if(from.equals("Document")) {
                                Map<String,String[]> mapFollowDocProperty = uiSearchBasisPortlet.getMapFollowDoc();
                                if(!mapFollowDocProperty.isEmpty()){
                                    filterDocByAllFollowDoc(mapFollowDocProperty, basisNode,basisFolderNode);
                                }
                                else{
                                    addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                    String path = basisNode.getPath() ;
                                    String pathSlippted [] = path.split("/Files/BO");
                                    addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                }
                            }
                            else{
                                if(!lastId.equals(basisNode.getProperty("exo:name").getString())){
                                    addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:name").getString(), basisFolderNode.getProperty("exo:name").getString(), null));
                                    String path = basisNode.getPath() ;
                                    String pathSlippted [] = path.split("/Files/BO");
                                    addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                }
                                lastId = basisNode.getProperty("exo:name").getString();
                            }
                        }
                    }
                }
            }
            else if(value[0].equals("Contains")){
                if(basisNode.getProperty(mapKey).getString().contains(value[1])){
                    if(uiBasisFollowFolderForm.getUIInput("RadioBox_folder").getValue().toString().equals("Last")){
                        lastFollowFolder(basisNode, basisFolderNode, uiSearchBasisPortlet, uiBasisFollowDocForm, from);

                    }
                    else{
                        if(uiBasisFollowDocForm.getUIInput("RadioBox_doc").getValue().toString().equals("Last")){
                            lastFollowDoc(basisNode, basisFolderNode , uiSearchBasisPortlet, from);
                        }
                        else{
                            if(from.equals("Document")) {
                                Map<String,String[]> mapFollowDocProperty = uiSearchBasisPortlet.getMapFollowDoc();
                                if(!mapFollowDocProperty.isEmpty()){
                                    filterDocByAllFollowDoc(mapFollowDocProperty, basisNode,basisFolderNode);
                                }
                                else{
                                    addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                    String path = basisNode.getPath() ;
                                    String pathSlippted [] = path.split("/Files/BO");
                                    addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                }
                            }
                            else{
                                if(!lastId.equals(basisNode.getProperty("exo:name").getString())){
                                    addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:name").getString(), basisFolderNode.getProperty("exo:name").getString(), null));
                                    String path = basisNode.getPath() ;
                                    String pathSlippted [] = path.split("/Files/BO");
                                    addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                }
                                lastId = basisNode.getProperty("exo:name").getString();
                            }
                        }
                    }
                }
            }
            else if(value[0].equals("Not_Equals")){
                if(!valueProperty.equals(value[1])){
                    if(uiBasisFollowFolderForm.getUIInput("RadioBox_folder").getValue().toString().equals("Last")){
                        lastFollowFolder(basisNode, basisFolderNode, uiSearchBasisPortlet, uiBasisFollowDocForm, from);

                    }
                    else{
                        if(uiBasisFollowDocForm.getUIInput("RadioBox_doc").getValue().toString().equals("Last")){
                            lastFollowDoc(basisNode, basisFolderNode , uiSearchBasisPortlet, from);
                        }
                        else{
                            if(from.equals("Document")) {
                                Map<String,String[]> mapFollowDocProperty = uiSearchBasisPortlet.getMapFollowDoc();
                                if(!mapFollowDocProperty.isEmpty()){
                                    filterDocByAllFollowDoc(mapFollowDocProperty, basisNode,basisFolderNode);
                                }
                                else{
                                    addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                    String path = basisNode.getPath() ;
                                    String pathSlippted [] = path.split("/Files/BO");
                                    addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                }
                            }
                            else{
                                if(!lastId.equals(basisNode.getProperty("exo:name").getString())){
                                    addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:name").getString(), basisFolderNode.getProperty("exo:name").getString(), null));
                                    String path = basisNode.getPath() ;
                                    String pathSlippted [] = path.split("/Files/BO");
                                    addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                }
                                lastId = basisNode.getProperty("exo:name").getString();
                            }
                        }
                    }
                }
            }
            else if(value[0].equals("Not_Contains")){
                if(!basisNode.getProperty(mapKey).getString().contains(value[1])){
                    if(uiBasisFollowFolderForm.getUIInput("RadioBox_folder").getValue().toString().equals("Last")){
                        lastFollowFolder(basisNode, basisFolderNode, uiSearchBasisPortlet, uiBasisFollowDocForm, from);

                    }
                    else{
                        if(uiBasisFollowDocForm.getUIInput("RadioBox_doc").getValue().toString().equals("Last")){
                            lastFollowDoc(basisNode, basisFolderNode , uiSearchBasisPortlet, from);
                        }
                        else{
                            if(from.equals("Document")) {
                                Map<String,String[]> mapFollowDocProperty = uiSearchBasisPortlet.getMapFollowDoc();
                                if(!mapFollowDocProperty.isEmpty()){
                                    filterDocByAllFollowDoc(mapFollowDocProperty, basisNode,basisFolderNode);
                                }
                                else{
                                    addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                    String path = basisNode.getPath() ;
                                    String pathSlippted [] = path.split("/Files/BO");
                                    addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                }
                            }
                            else{
                                if(!lastId.equals(basisNode.getProperty("exo:name").getString())){
                                    addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:name").getString(), basisFolderNode.getProperty("exo:name").getString(), null));
                                    String path = basisNode.getPath() ;
                                    String pathSlippted [] = path.split("/Files/BO");
                                    addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                }
                                lastId = basisNode.getProperty("exo:name").getString();
                            }
                        }
                    }
                }
            }
        }
    }



    public void lastFollowFolder(Node basisNode,Node basisFolderNode, UISearchBasisPortlet uiSearchBasisPortlet, UIBasisFollowDocForm uiBasisFollowDocForm, String from) throws Exception {

        String url = Util.getPortalRequestContext().getRequestURI();
        String urlSplitted[] = url.split("BO:");
        String nameBO[] = urlSplitted[1].split("/");

        String selectFollowFolderByFolder = null;
        try {
            selectFollowFolderByFolder = "/jcr:root/Files/BO/"+nameBO[0]+"/*/*/*/"+basisFolderNode.getName()+"/element (*,basis:basisFollow) order by @exo:dateCreated descending";


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
                                lastFollowDoc(basisNode, basisFolderNode, uiSearchBasisPortlet, from);
                            }
                            else{
                                if(from.equals("Folder")) {
                                    if(!lastId.equals(basisNode.getProperty("exo:title").getString())){
                                        addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:title").getString(), basisNode.getProperty("exo:title").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                    }
                                    lastId = basisNode.getProperty("exo:title").getString();
                                }
                                else if(from.equals("Document")){
                                    Map<String,String[]> mapFollowDocProperty = uiSearchBasisPortlet.getMapFollowDoc();
                                    if(!mapFollowDocProperty.isEmpty()){
                                        filterDocByAllFollowDoc(mapFollowDocProperty, basisNode,basisFolderNode);
                                    }
                                    else{
                                        if(!lastId.equals(basisNode.getProperty("exo:title").getString())){
                                            addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                            String path = basisNode.getPath() ;
                                            String pathSlippted [] = path.split("/Files/BO");
                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        }
                                        lastId = basisNode.getProperty("exo:title").getString();
                                    }
                                }
                                else{
                                    if(!lastId.equals(basisNode.getProperty("exo:name").getString())){
                                        addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:name").getString(), basisNode.getProperty("exo:name").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                    }
                                    lastId = basisNode.getProperty("exo:name").getString();
                                }
                            }
                        }
                    }
                    else if(value[0].equals("Contains")){
                        if(basisFollowFolderNode.getProperty(mapKey).getString().contains(value[1])){
                            if(uiBasisFollowDocForm.getUIInput("RadioBox_doc").getValue().toString().equals("Last")){
                                lastFollowDoc(basisNode, basisFolderNode, uiSearchBasisPortlet, from);
                            }
                            else{
                                if(from.equals("Folder")) {
                                    if(!lastId.equals(basisNode.getProperty("exo:title").getString())){
                                        addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:title").getString(), basisNode.getProperty("exo:title").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                    }
                                    lastId = basisNode.getProperty("exo:title").getString();
                                }
                                else if(from.equals("Document")){
                                    Map<String,String[]> mapFollowDocProperty = uiSearchBasisPortlet.getMapFollowDoc();
                                    if(!mapFollowDocProperty.isEmpty()){
                                        filterDocByAllFollowDoc(mapFollowDocProperty, basisNode,basisFolderNode);
                                    }
                                    else{
                                        if(!lastId.equals(basisNode.getProperty("exo:title").getString())){
                                            addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                            String path = basisNode.getPath() ;
                                            String pathSlippted [] = path.split("/Files/BO");
                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        }
                                        lastId = basisNode.getProperty("exo:title").getString();
                                    }
                                }
                                else{
                                    if(!lastId.equals(basisNode.getProperty("exo:name").getString())){
                                        addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:name").getString(), basisNode.getProperty("exo:name").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                    }
                                    lastId = basisNode.getProperty("exo:name").getString();
                                }
                            }
                        }
                    }
                    else if(value[0].equals("Not_Equals")){
                        if(!basisFollowFolderNode.getProperty(mapKey).getString().equals(value[1])){
                            if(uiBasisFollowDocForm.getUIInput("RadioBox_doc").getValue().toString().equals("Last")){
                                lastFollowDoc(basisNode, basisFolderNode, uiSearchBasisPortlet, from);
                            }
                            else{
                                if(from.equals("Folder")) {
                                    if(!lastId.equals(basisNode.getProperty("exo:title").getString())){
                                        addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:title").getString(), basisNode.getProperty("exo:title").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                    }
                                    lastId = basisNode.getProperty("exo:title").getString();
                                }
                                else if(from.equals("Document")){
                                    Map<String,String[]> mapFollowDocProperty = uiSearchBasisPortlet.getMapFollowDoc();
                                    if(!mapFollowDocProperty.isEmpty()){
                                        filterDocByAllFollowDoc(mapFollowDocProperty, basisNode,basisFolderNode);
                                    }
                                    else{
                                        if(!lastId.equals(basisNode.getProperty("exo:title").getString())){
                                            addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                            String path = basisNode.getPath() ;
                                            String pathSlippted [] = path.split("/Files/BO");
                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        }
                                        lastId = basisNode.getProperty("exo:title").getString();
                                    }
                                }
                                else{
                                    if(!lastId.equals(basisNode.getProperty("exo:name").getString())){
                                        addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:name").getString(), basisNode.getProperty("exo:name").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                    }
                                    lastId = basisNode.getProperty("exo:name").getString();
                                }
                            }
                        }
                    }
                    else if(value[0].equals("Not_Contains")){
                        if(!basisFollowFolderNode.getProperty(mapKey).getString().contains(value[1])){
                            if(uiBasisFollowDocForm.getUIInput("RadioBox_doc").getValue().toString().equals("Last")){
                                lastFollowDoc(basisNode, basisFolderNode, uiSearchBasisPortlet, from);
                            }
                            else{
                                if(from.equals("Folder")) {
                                    if(!lastId.equals(basisNode.getProperty("exo:title").getString())){
                                        addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:title").getString(), basisNode.getProperty("exo:title").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                    }
                                    lastId = basisNode.getProperty("exo:title").getString();
                                }
                                else if(from.equals("Document")){
                                    Map<String,String[]> mapFollowDocProperty = uiSearchBasisPortlet.getMapFollowDoc();
                                    if(!mapFollowDocProperty.isEmpty()){
                                        filterDocByAllFollowDoc(mapFollowDocProperty, basisNode,basisFolderNode);
                                    }
                                    else{
                                        if(!lastId.equals(basisNode.getProperty("exo:title").getString())){
                                            addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                            String path = basisNode.getPath() ;
                                            String pathSlippted [] = path.split("/Files/BO");
                                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        }
                                        lastId = basisNode.getProperty("exo:title").getString();
                                    }
                                }
                                else{
                                    if(!lastId.equals(basisNode.getProperty("exo:name").getString())){
                                        addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:name").getString(), basisNode.getProperty("exo:name").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                    }
                                    lastId = basisNode.getProperty("exo:name").getString();
                                }
                            }
                        }
                    }
                }
            }
        } catch (RepositoryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void lastFollowDoc(Node basisNode, Node basisFolderNode, UISearchBasisPortlet uiSearchBasisPortlet, String from){
        String url = Util.getPortalRequestContext().getRequestURI();
        String urlSplitted[] = url.split("BO:");
        String nameBO[] = urlSplitted[1].split("/");

        String selectFollowDocByFolder = null;
        try {
            if(from.equals("Folder")){
                selectFollowDocByFolder = "/jcr:root/Files/BO/"+nameBO[0]+"/*/*/*/"+basisFolderNode.getName()+"/*/element (*,basis:basisFollow) order by @exo:dateCreated descending";
            }
            else if(from.equals("Document")){
                selectFollowDocByFolder = "/jcr:root/Files/BO/"+nameBO[0]+"/*/*/*/"+basisFolderNode.getName()+"/"+basisNode.getName()+"/element (*,basis:basisFollow) order by @exo:dateCreated descending";
            }
            else if(from.equals("Follow_folder")){
                selectFollowDocByFolder = "/jcr:root/Files/BO/"+nameBO[0]+"/*/*/*/"+basisFolderNode.getName()+"/*/element (*,basis:basisFollow) order by @exo:dateCreated descending";
            }
            else if(from.equals("Follow_document")){

            }


            QueryManager queryManager = session.getWorkspace().getQueryManager();
            Query selectFollowDocByFolderQuery = queryManager.createQuery(selectFollowDocByFolder, Query.XPATH);
            QueryResult resultQuery2 = selectFollowDocByFolderQuery.execute();
            NodeIterator nodeIterator2 = resultQuery2.getNodes();
            if(nodeIterator2.hasNext()){
                Node basisFollowDocNode = nodeIterator2.nextNode();
                Map<String,String[]> mapFollowDoc = uiSearchBasisPortlet.getMapFollowDoc();
                if(!mapFollowDoc.isEmpty()){
                    for (String mapKey : mapFollowDoc.keySet()) {
                        String[] value = mapFollowDoc.get(mapKey);
                        if(value[0].equals("Equals")){
                            if(basisFollowDocNode.getProperty(mapKey).getString().equals(value[1])){
                                if(from.equals("Document") || from.equals("Folder")) {
                                    if(!lastId.equals(basisNode.getProperty("exo:title").getString())){
                                        addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:title").getString(), basisNode.getProperty("exo:title").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        lastId = basisNode.getProperty("exo:title").getString();
                                    }
                                }
                                else{
                                    if(!lastId.equals(basisNode.getProperty("exo:name").getString())){
                                        addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:name").getString(), basisNode.getProperty("exo:name").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        lastId = basisNode.getProperty("exo:name").getString();
                                    }
                                }
                            }
                        }
                        else if(value[0].equals("Contains")){
                            if(basisFollowDocNode.getProperty(mapKey).getString().contains(value[1])){
                                if(from.equals("Document") || from.equals("Folder")) {
                                    if(!lastId.equals(basisNode.getProperty("exo:title").getString())){
                                        addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:title").getString(), basisNode.getProperty("exo:title").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        lastId = basisNode.getProperty("exo:title").getString();
                                    }
                                }
                                else{
                                    if(!lastId.equals(basisNode.getProperty("exo:name").getString())){
                                        addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:name").getString(), basisNode.getProperty("exo:name").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        lastId = basisNode.getProperty("exo:name").getString();
                                    }
                                }
                            }
                        }
                        else if(value[0].equals("Not_Equals")){
                            if(!basisFollowDocNode.getProperty(mapKey).getString().equals(value[1])){
                                if(from.equals("Document") || from.equals("Folder")) {
                                    if(!lastId.equals(basisNode.getProperty("exo:title").getString())){
                                        addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:title").getString(), basisNode.getProperty("exo:title").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        lastId = basisNode.getProperty("exo:title").getString();
                                    }
                                }
                                else{
                                    if(!lastId.equals(basisNode.getProperty("exo:name").getString())){
                                        addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:name").getString(), basisNode.getProperty("exo:name").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        lastId = basisNode.getProperty("exo:name").getString();
                                    }
                                }
                            }
                        }
                        else if(value[0].equals("Not_Contains")){
                            if(!basisFollowDocNode.getProperty(mapKey).getString().contains(value[1])){

                                if(from.equals("Document") || from.equals("Folder")) {
                                    if(!lastId.equals(basisNode.getProperty("exo:title").getString())){
                                        addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:title").getString(), basisNode.getProperty("exo:title").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        lastId = basisNode.getProperty("exo:title").getString();
                                    }
                                }
                                else{
                                    if(!lastId.equals(basisNode.getProperty("exo:name").getString())){
                                        addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:name").getString(), basisNode.getProperty("exo:name").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        lastId = basisNode.getProperty("exo:name").getString();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (RepositoryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void filterDocByAllFollowDoc(Map<String,String[]> mapFollowDocProperty, Node basisDocumentNode, Node basisFolderNode) throws Exception{
        String url = Util.getPortalRequestContext().getRequestURI();
        String urlSplitted[] = url.split("BO:");
        String nameBO[] = urlSplitted[1].split("/");
        String valueProperty;

        String selectFollowDocByFolder = "/jcr:root/Files/BO/"+nameBO[0]+"/*/*/*/"+basisFolderNode.getName()+"/"+basisDocumentNode.getName()+"/element (*,basis:basisFollow)";
        QueryManager queryManager = session.getWorkspace().getQueryManager();
        Query selectFollowDocByFolderQuery = queryManager.createQuery(selectFollowDocByFolder, Query.XPATH);
        QueryResult resultQuery3 = selectFollowDocByFolderQuery.execute();
        NodeIterator nodeIterator3 = resultQuery3.getNodes();
        while(nodeIterator3.hasNext()){
            Node basisFollowDocNode = nodeIterator3.nextNode();
            for (String mapKey : mapFollowDocProperty.keySet()) {
                String[] value = mapFollowDocProperty.get(mapKey);
                if(mapKey.contains("Date")){
                    valueProperty =  basisFollowDocNode.getProperty(mapKey).getString().split("T")[0];
                }
                else{
                    valueProperty =  basisFollowDocNode.getProperty(mapKey).getString();
                }
                if(value[0].equals("Equals")){
                    if(valueProperty.equals(value[1])){
                        if(!lastId.equals(basisDocumentNode.getProperty("exo:title").getString())){
                            addUIFormInput(new UIFormStringInput(basisDocumentNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                            String path = basisDocumentNode.getPath() ;
                            String pathSlippted [] = path.split("/Files/BO");
                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                        }
                        lastId = basisDocumentNode.getProperty("exo:title").getString();
                    }
                }
                else if(value[0].equals("Contains")){
                    if(basisFollowDocNode.getProperty(mapKey).getString().contains(value[1])){
                        if(!lastId.equals(basisDocumentNode.getProperty("exo:title").getString())){
                            addUIFormInput(new UIFormStringInput(basisDocumentNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                            String path = basisDocumentNode.getPath() ;
                            String pathSlippted [] = path.split("/Files/BO");
                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                        }
                        lastId = basisDocumentNode.getProperty("exo:title").getString();
                    }
                }
                else if(value[0].equals("Not_Equals")){
                    if(!valueProperty.equals(value[1])){
                        if(!lastId.equals(basisDocumentNode.getProperty("exo:title").getString())){
                            addUIFormInput(new UIFormStringInput(basisDocumentNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                            String path = basisDocumentNode.getPath() ;
                            String pathSlippted [] = path.split("/Files/BO");
                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                        }
                        lastId = basisDocumentNode.getProperty("exo:title").getString();
                    }
                }
                else if(value[0].equals("Not_Contains")){
                    if(!basisFollowDocNode.getProperty(mapKey).getString().contains(value[1])){
                        if(!lastId.equals(basisDocumentNode.getProperty("exo:title").getString())){
                            addUIFormInput(new UIFormStringInput(basisDocumentNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                            String path = basisDocumentNode.getPath() ;
                            String pathSlippted [] = path.split("/Files/BO");
                            addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                        }
                        lastId = basisDocumentNode.getProperty("exo:title").getString();
                    }
                }
            }
        }


    }
}
