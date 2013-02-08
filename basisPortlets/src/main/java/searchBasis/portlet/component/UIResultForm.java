package searchBasis.portlet.component;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIPageIterator;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormRadioBoxInput;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.util.ArrayList;
import java.util.List;
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
    private UIGrid grid_;

    private static final String NAME= "Name";

    private static final String PATH = "Path";

    private static final String[] NODE_BEAN_FIELD = {NAME, PATH};

    private static final String[] NODE_ACTION = {"ViewNode"};

    private List<String> result = new ArrayList<String>();


    public UIResultForm () throws Exception{
        ExoContainer exoContainer = ExoContainerContext.getCurrentContainer();
        RepositoryService rs = (RepositoryService) exoContainer.getComponentInstanceOfType(RepositoryService.class);
        session = (Session) rs.getRepository("repository").getSystemSession("collaboration");

        grid_ = addChild(UIGrid.class, null, "UIListResultSearchGrid");
        grid_.configure(NAME, NODE_BEAN_FIELD, NODE_ACTION);
        grid_.getUIPageIterator().setId("UIListResultSearchIterator");
        grid_.getUIPageIterator().setParent(this);
    }

    private void search(List<String> result, int numberResult) throws Exception {
        grid_.getUIPageIterator().setPageList(new NodeResultList(result, numberResult));
        UIPageIterator pageIterator = grid_.getUIPageIterator();
        if (pageIterator.getAvailable() == 0) {
            UIApplication uiApp = Util.getPortalRequestContext().getUIApplication();
            uiApp.addMessage(new ApplicationMessage("UISearchForm.msg.empty", null));
        }
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
                            //addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                            String path = basisFolderNode.getPath() ;
                            String pathSlippted [] = path.split("/Files/BO");
                            //addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                            result.add(basisFolderNode.getProperty("exo:title").getString());
                            result.add(pathSlippted[1]);
                        }
                    }
                    else if(typeQuery.equals("byGroup")){
                        String group = uiSearchBasisPortlet.getAttribute();
                        if(basisFollowNode.getProperty("basis:followGroupInternEditor").getString().equals(group)){
                            //addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                            String path = basisFolderNode.getPath() ;
                            String pathSlippted [] = path.split("/Files/BO");
                            //addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                            result.add(basisFolderNode.getProperty("exo:title").getString());
                            result.add(pathSlippted[1]);
                        }
                    }
                    else if(typeQuery.equals("byUser")){
                        String user = uiSearchBasisPortlet.getAttribute();
                        if(basisFollowNode.getProperty("basis:followUserInternEditor").getString().equals(user)){
                            //addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                            String path = basisFolderNode.getPath() ;
                            String pathSlippted [] = path.split("/Files/BO");
                            //addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                            result.add(basisFolderNode.getProperty("exo:title").getString());
                            result.add(pathSlippted[1]);
                        }
                    }
                    else if(typeQuery.equals("byAction")){
                        String action = uiSearchBasisPortlet.getAttribute();
                        if(basisFollowNode.getProperty("basis:followRequiredAction").getString().equals(action)){
                            //addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                            String path = basisFolderNode.getPath() ;
                            String pathSlippted [] = path.split("/Files/BO");
                            //addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                            result.add(basisFolderNode.getProperty("exo:title").getString());
                            result.add(pathSlippted[1]);
                        }
                    }
                }
            }
            UISimpleSearchForm uiSimpleSearchForm = uiSearchBasisPortlet.getChild(UISimpleSearchForm.class);
            String number = uiSimpleSearchForm.getUIFormSelectBox("NumberResult").getValue();
            search(result, Integer.parseInt(number));
        }
        catch (NullPointerException ex){
            System.out.println("Null Pointer Exception : " + ex);
        }

    }
    public  void updateAdvanced()throws Exception{
        UISearchBasisPortlet uiSearchBasisPortlet = this.getAncestorOfType(UISearchBasisPortlet.class);
        String from = uiSearchBasisPortlet.getFrom();
        Map<String,String[]> mapFollowDoc = uiSearchBasisPortlet.getMapFollowDoc();
        Map<String,String[]> mapDocument = uiSearchBasisPortlet.getMapDoc();
        Map<String,String[]> mapFollowFolder = uiSearchBasisPortlet.getMapFollowFolder();
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
                        UIFormRadioBoxInput uiFormRadioBoxInputFolder =  uiBasisFollowFolderForm.getChildById("RadioBox_folder");
                        UIFormRadioBoxInput uiFormRadioBoxInputDocument =  uiBasisFollowDocForm.getChildById("RadioBox_doc");
                        if(uiFormRadioBoxInputFolder.getValue().toString().equals("Last")){
                            lastFollowFolder(basisFolderNode, basisFolderNode, uiSearchBasisPortlet, uiBasisFollowDocForm, from, null);
                        }
                        else{
                            if(uiFormRadioBoxInputDocument.getValue().toString().equals("Last")){
                                lastFollowDoc(basisFolderNode, basisFolderNode , uiSearchBasisPortlet, from, null);
                            }
                            else{
                                //addUIFormInput(new UIFormStringInput(basisFolderNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                String path = basisFolderNode.getPath() ;
                                String pathSlippted [] = path.split("/Files/BO");
                                //addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                result.add(basisFolderNode.getProperty("exo:title").getString());
                                result.add(pathSlippted[1]);
                            }
                        }
                    }
                    else if(from.equals("Document")){
                        UIFormRadioBoxInput uiFormRadioBoxInputFolder =  uiBasisFollowFolderForm.getChildById("RadioBox_folder");
                        UIFormRadioBoxInput uiFormRadioBoxInputDocument =  uiBasisFollowDocForm.getChildById("RadioBox_doc");

                        String url = Util.getPortalRequestContext().getRequestURI();
                        String urlSplitted[] = url.split("BO:");
                        String nameBO[] = urlSplitted[1].split("/");

                        uiAdvancedSearchForm.setI(0);
                        String selectDocumentByFolder = "" ;
                        if(!mapDocument.isEmpty() && !mapFollowDoc.isEmpty()){
                            selectDocumentByFolder = "/jcr:root/Files/BO/"+nameBO[0]+"/*/*/*/"+basisFolderNode.getName()+"/element (*,basis:basisDocument) [";
                            selectDocumentByFolder = uiAdvancedSearchForm.firstChildRequest(mapDocument, selectDocumentByFolder );
                            selectDocumentByFolder = uiAdvancedSearchForm.secondChildRequest(mapFollowDoc , selectDocumentByFolder);
                            selectDocumentByFolder += "] order by @exo:name ascending";
                        }
                        else{
                            selectDocumentByFolder = "/jcr:root/Files/BO/"+nameBO[0]+"/*/*/*/"+basisFolderNode.getName()+"/element (*,basis:basisDocument)";
                        }

                        QueryManager queryManager = session.getWorkspace().getQueryManager();
                        Query selectDocumentByFolderQuery = queryManager.createQuery(selectDocumentByFolder, Query.XPATH);
                        QueryResult resultQuery2 = selectDocumentByFolderQuery.execute();
                        NodeIterator nodeIterator2 = resultQuery2.getNodes();

                        while(nodeIterator2.hasNext()){
                            Node basisDocumentNode = nodeIterator2.nextNode();

                            if(uiFormRadioBoxInputFolder.getValue().toString().equals("Last")){
                                lastFollowFolder(basisDocumentNode, basisFolderNode, uiSearchBasisPortlet, uiBasisFollowDocForm, from, null);

                            }
                            else{
                                if(uiFormRadioBoxInputDocument.getValue().toString().equals("Last")){
                                    lastFollowDoc(basisDocumentNode, basisFolderNode , uiSearchBasisPortlet, from, null);
                                }
                                else{
                                    //addUIFormInput(new UIFormStringInput(basisDocumentNode.getProperty("exo:title").getString(), basisFolderNode.getProperty("exo:title").getString(), null));
                                    String path = basisDocumentNode.getPath() ;
                                    String pathSlippted [] = path.split("/Files/BO");
                                    //addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                    result.add(basisDocumentNode.getProperty("exo:title").getString());
                                    result.add(pathSlippted[1]);
                                }
                            }
                        }

                    }
                    else if(from.equals("Follow_folder")){
                        UIFormRadioBoxInput uiFormRadioBoxInputFolder =  uiBasisFollowFolderForm.getChildById("RadioBox_folder");
                        UIFormRadioBoxInput uiFormRadioBoxInputDocument =  uiBasisFollowDocForm.getChildById("RadioBox_doc");

                        String url = Util.getPortalRequestContext().getRequestURI();
                        String urlSplitted[] = url.split("BO:");
                        String nameBO[] = urlSplitted[1].split("/");

                        uiAdvancedSearchForm.setI(0);
                        String selectFollowFolderByFolder = "";
                        if(!mapFollowFolder.isEmpty()){
                            selectFollowFolderByFolder = "/jcr:root/Files/BO/"+nameBO[0]+"/*/*/*/"+basisFolderNode.getName()+"/element (*,basis:basisFollow) [";
                            selectFollowFolderByFolder = uiAdvancedSearchForm.firstChildRequest(mapFollowFolder, selectFollowFolderByFolder );
                            selectFollowFolderByFolder += "] order by @exo:name ascending";
                        }
                        else{
                            selectFollowFolderByFolder = "/jcr:root/Files/BO/"+nameBO[0]+"/*/*/*/"+basisFolderNode.getName()+"/element (*,basis:basisFollow)";
                        }

                        QueryManager queryManager = session.getWorkspace().getQueryManager();
                        Query selectDocumentByFolderQuery = queryManager.createQuery(selectFollowFolderByFolder, Query.XPATH);
                        QueryResult resultQuery2 = selectDocumentByFolderQuery.execute();
                        NodeIterator nodeIterator2 = resultQuery2.getNodes();

                        while(nodeIterator2.hasNext()){
                            Node basisFollowFolderNode = nodeIterator2.nextNode();

                            if(uiFormRadioBoxInputFolder.getValue().toString().equals("Last")){
                                lastFollowFolder(basisFollowFolderNode, basisFolderNode, uiSearchBasisPortlet, uiBasisFollowDocForm, from, null);

                            }
                            else{
                                if(uiFormRadioBoxInputDocument.getValue().toString().equals("Last")){
                                    lastFollowDoc(basisFollowFolderNode, basisFolderNode , uiSearchBasisPortlet, from, null);
                                }
                                else{
                                    //addUIFormInput(new UIFormStringInput(basisFollowFolderNode.getProperty("exo:name").getString(), basisFolderNode.getProperty("exo:name").getString(), null));
                                    String path = basisFollowFolderNode.getPath() ;
                                    String pathSlippted [] = path.split("/Files/BO");
                                    //addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                    result.add(basisFollowFolderNode.getProperty("exo:name").getString());
                                    result.add(pathSlippted[1]);
                                }
                            }
                        }

                    }
                    else if(from.equals("Follow_document")){
                        UIFormRadioBoxInput uiFormRadioBoxInputFolder =  uiBasisFollowFolderForm.getChildById("RadioBox_folder");
                        UIFormRadioBoxInput uiFormRadioBoxInputDocument =  uiBasisFollowDocForm.getChildById("RadioBox_doc");

                        String url = Util.getPortalRequestContext().getRequestURI();
                        String urlSplitted[] = url.split("BO:");
                        String nameBO[] = urlSplitted[1].split("/");

                        uiAdvancedSearchForm.setI(0);
                        String selectDocumentByFolder = "";

                        if(!mapDocument.isEmpty()){
                            selectDocumentByFolder = "/jcr:root/Files/BO/"+nameBO[0]+"/*/*/*/"+basisFolderNode.getName()+"/element (*,basis:basisDocument) [";
                            selectDocumentByFolder = uiAdvancedSearchForm.firstChildRequest(mapDocument, selectDocumentByFolder );
                            selectDocumentByFolder += "] order by @exo:name ascending";
                        }
                        else{
                            selectDocumentByFolder = "/jcr:root/Files/BO/"+nameBO[0]+"/*/*/*/"+basisFolderNode.getName()+"/element (*,basis:basisDocument)";
                        }

                        QueryManager queryManager = session.getWorkspace().getQueryManager();
                        Query selectDocumentByFolderQuery = queryManager.createQuery(selectDocumentByFolder, Query.XPATH);
                        QueryResult resultQuery2 = selectDocumentByFolderQuery.execute();
                        NodeIterator nodeIterator2 = resultQuery2.getNodes();

                        while(nodeIterator2.hasNext()){
                            Node basisDocumentNode = nodeIterator2.nextNode();

                            uiAdvancedSearchForm.setI(0);
                            String selectFollowDocumentByFolder = "" ;
                            if(!mapFollowDoc.isEmpty()){
                                selectFollowDocumentByFolder = "/jcr:root/Files/BO/"+nameBO[0]+"/*/*/*/"+basisFolderNode.getName()+"/"+basisDocumentNode.getName()+"/element (*,basis:basisFollow) [";
                                selectFollowDocumentByFolder = uiAdvancedSearchForm.firstChildRequest(mapFollowDoc, selectFollowDocumentByFolder );
                                selectFollowDocumentByFolder += "]";
                            }
                            else{
                                selectFollowDocumentByFolder = "/jcr:root/Files/BO/"+nameBO[0]+"/*/*/*/"+basisFolderNode.getName()+"/"+basisDocumentNode.getName()+"/element (*,basis:basisFollow)";
                            }

                            Query selectFollowDocumentByFolderQuery = queryManager.createQuery(selectFollowDocumentByFolder, Query.XPATH);
                            QueryResult resultQuery3 = selectFollowDocumentByFolderQuery.execute();
                            NodeIterator nodeIterator3 = resultQuery3.getNodes();
                            while(nodeIterator3.hasNext()){
                                Node basisFollowDocumentNode = nodeIterator3.nextNode();

                                if(uiFormRadioBoxInputFolder.getValue().toString().equals("Last")){
                                    lastFollowFolder(basisFollowDocumentNode, basisFolderNode, uiSearchBasisPortlet, uiBasisFollowDocForm, from, basisDocumentNode);

                                }
                                else{
                                    if(uiFormRadioBoxInputDocument.getValue().toString().equals("Last")){
                                        lastFollowDoc(basisFollowDocumentNode, basisFolderNode , uiSearchBasisPortlet, from, basisDocumentNode);
                                    }
                                    else{
                                        //addUIFormInput(new UIFormStringInput(basisFollowDocumentNode.getProperty("exo:name").getString(), basisFollowDocumentNode.getProperty("exo:name").getString(), null));
                                        String path = basisFollowDocumentNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        //addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        result.add(basisFollowDocumentNode.getProperty("exo:name").getString());
                                        result.add(pathSlippted[1]);
                                    }
                                }
                            }
                        }
                    }
                }
                String number = uiAdvancedSearchForm.getUIFormSelectBox("NumberResult").getValue();
                search(result, Integer.parseInt(number));
            }
        }
        catch (NullPointerException ex){
            System.out.println("Null Pointer Exception : " + ex);
        }

    }

    public void lastFollowFolder(Node basisNode,Node basisFolderNode, UISearchBasisPortlet uiSearchBasisPortlet, UIBasisFollowDocForm uiBasisFollowDocForm, String from, Node basisDocumentNode) throws Exception {

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
                            UIFormRadioBoxInput uiFormRadioBoxInputDocument =  uiBasisFollowDocForm.getChildById("RadioBox_doc");
                            if(uiFormRadioBoxInputDocument.getValue().toString().equals("Last")){
                                lastFollowDoc(basisNode, basisFolderNode, uiSearchBasisPortlet, from, basisDocumentNode);
                            }
                            else{
                                if(from.equals("Document") || from.equals("Folder")) {
                                    if(!lastId.equals(basisNode.getProperty("exo:title").getString())){
                                        //addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:title").getString(), basisNode.getProperty("exo:title").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        //addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        result.add(basisNode.getProperty("exo:title").getString());
                                        result.add(pathSlippted[1]);
                                        lastId = basisNode.getProperty("exo:title").getString();
                                    }
                                }
                                else{
                                    if(!lastId.equals(basisNode.getProperty("exo:name").getString())){
                                        //addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:name").getString(), basisNode.getProperty("exo:name").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        //addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        result.add(basisNode.getProperty("exo:name").getString());
                                        result.add(pathSlippted[1]);
                                        lastId = basisNode.getProperty("exo:name").getString();
                                    }
                                }
                            }
                        }
                    }
                    else if(value[0].equals("Contains")){
                        if(basisFollowFolderNode.getProperty(mapKey).getString().contains(value[1])){
                            UIFormRadioBoxInput uiFormRadioBoxInputDocument =  uiBasisFollowDocForm.getChildById("RadioBox_doc");
                            if(uiFormRadioBoxInputDocument.getValue().toString().equals("Last")){
                                lastFollowDoc(basisNode, basisFolderNode, uiSearchBasisPortlet, from, basisDocumentNode);
                            }
                            else{
                                if(from.equals("Document") || from.equals("Folder")) {
                                    if(!lastId.equals(basisNode.getProperty("exo:title").getString())){
                                        //addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:title").getString(), basisNode.getProperty("exo:title").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        //addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        result.add(basisNode.getProperty("exo:title").getString());
                                        result.add(pathSlippted[1]);
                                        lastId = basisNode.getProperty("exo:title").getString();
                                    }
                                }
                                else{
                                    if(!lastId.equals(basisNode.getProperty("exo:name").getString())){
                                        //addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:name").getString(), basisNode.getProperty("exo:name").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        //addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        result.add(basisNode.getProperty("exo:name").getString());
                                        result.add(pathSlippted[1]);
                                        lastId = basisNode.getProperty("exo:name").getString();
                                    }
                                }
                            }
                        }
                    }
                    else if(value[0].equals("Not_Equals")){
                        if(!basisFollowFolderNode.getProperty(mapKey).getString().equals(value[1])){
                            UIFormRadioBoxInput uiFormRadioBoxInputDocument =  uiBasisFollowDocForm.getChildById("RadioBox_doc");
                            if(uiFormRadioBoxInputDocument.getValue().toString().equals("Last")){
                                lastFollowDoc(basisNode, basisFolderNode, uiSearchBasisPortlet, from, basisDocumentNode);
                            }
                            else{
                                if(from.equals("Document") || from.equals("Folder")) {
                                    if(!lastId.equals(basisNode.getProperty("exo:title").getString())){
                                        //addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:title").getString(), basisNode.getProperty("exo:title").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        //addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        result.add(basisNode.getProperty("exo:title").getString());
                                        result.add(pathSlippted[1]);
                                        lastId = basisNode.getProperty("exo:title").getString();
                                    }
                                }
                                else{
                                    if(!lastId.equals(basisNode.getProperty("exo:name").getString())){
                                        //addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:name").getString(), basisNode.getProperty("exo:name").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        //addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        result.add(basisNode.getProperty("exo:name").getString());
                                        result.add(pathSlippted[1]);
                                        lastId = basisNode.getProperty("exo:name").getString();
                                    }
                                }
                            }
                        }
                    }
                    else if(value[0].equals("Not_Contains")){
                        if(!basisFollowFolderNode.getProperty(mapKey).getString().contains(value[1])){
                            UIFormRadioBoxInput uiFormRadioBoxInputDocument =  uiBasisFollowDocForm.getChildById("RadioBox_doc");
                            if(uiFormRadioBoxInputDocument.getValue().toString().equals("Last")){
                                lastFollowDoc(basisNode, basisFolderNode, uiSearchBasisPortlet, from, basisDocumentNode);
                            }
                            else{
                                if(from.equals("Document") || from.equals("Folder")) {
                                    if(!lastId.equals(basisNode.getProperty("exo:title").getString())){
                                        //addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:title").getString(), basisNode.getProperty("exo:title").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        //addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        result.add(basisNode.getProperty("exo:title").getString());
                                        result.add(pathSlippted[1]);
                                        lastId = basisNode.getProperty("exo:title").getString();
                                    }
                                }
                                else{
                                    if(!lastId.equals(basisNode.getProperty("exo:name").getString())){
                                        //addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:name").getString(), basisNode.getProperty("exo:name").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        //addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        result.add(basisNode.getProperty("exo:name").getString());
                                        result.add(pathSlippted[1]);
                                        lastId = basisNode.getProperty("exo:name").getString();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            UIAdvancedSearchForm uiAdvancedSearchForm = uiSearchBasisPortlet.getChild(UIAdvancedSearchForm.class);
            String number = uiAdvancedSearchForm.getUIFormSelectBox("NumberResult").getValue();
            search(result, Integer.parseInt(number));
        } catch (RepositoryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void lastFollowDoc(Node basisNode, Node basisFolderNode, UISearchBasisPortlet uiSearchBasisPortlet, String from, Node basisDocumentNode) throws Exception {
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
                selectFollowDocByFolder = "/jcr:root/Files/BO/"+nameBO[0]+"/*/*/*/"+basisFolderNode.getName()+"/"+basisDocumentNode.getName()+"/element (*,basis:basisFollow) order by @exo:dateCreated descending";
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
                                        //addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:title").getString(), basisNode.getProperty("exo:title").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        //addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        result.add(basisNode.getProperty("exo:title").getString());
                                        result.add(pathSlippted[1]);
                                        lastId = basisNode.getProperty("exo:title").getString();
                                    }
                                }
                                else{
                                    if(!lastId.equals(basisNode.getProperty("exo:name").getString())){
                                        //addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:name").getString(), basisNode.getProperty("exo:name").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        //addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        result.add(basisNode.getProperty("exo:name").getString());
                                        result.add(pathSlippted[1]);
                                        lastId = basisNode.getProperty("exo:name").getString();
                                    }
                                }
                            }
                        }
                        else if(value[0].equals("Contains")){
                            if(basisFollowDocNode.getProperty(mapKey).getString().contains(value[1])){
                                if(from.equals("Document") || from.equals("Folder")) {
                                    if(!lastId.equals(basisNode.getProperty("exo:title").getString())){
                                        //addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:title").getString(), basisNode.getProperty("exo:title").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        //addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        result.add(basisNode.getProperty("exo:title").getString());
                                        result.add(pathSlippted[1]);
                                        lastId = basisNode.getProperty("exo:title").getString();
                                    }
                                }
                                else{
                                    if(!lastId.equals(basisNode.getProperty("exo:name").getString())){
                                        //addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:name").getString(), basisNode.getProperty("exo:name").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        //addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        result.add(basisNode.getProperty("exo:name").getString());
                                        result.add(pathSlippted[1]);
                                        lastId = basisNode.getProperty("exo:name").getString();
                                    }
                                }
                            }
                        }
                        else if(value[0].equals("Not_Equals")){
                            if(!basisFollowDocNode.getProperty(mapKey).getString().equals(value[1])){
                                if(from.equals("Document") || from.equals("Folder")) {
                                    if(!lastId.equals(basisNode.getProperty("exo:title").getString())){
                                        //addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:title").getString(), basisNode.getProperty("exo:title").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        //addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        result.add(basisNode.getProperty("exo:title").getString());
                                        result.add(pathSlippted[1]);
                                        lastId = basisNode.getProperty("exo:title").getString();
                                    }
                                }
                                else{
                                    if(!lastId.equals(basisNode.getProperty("exo:name").getString())){
                                        //addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:name").getString(), basisNode.getProperty("exo:name").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        //addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        result.add(basisNode.getProperty("exo:name").getString());
                                        result.add(pathSlippted[1]);
                                        lastId = basisNode.getProperty("exo:name").getString();
                                    }
                                }
                            }
                        }
                        else if(value[0].equals("Not_Contains")){
                            if(!basisFollowDocNode.getProperty(mapKey).getString().contains(value[1])){

                                if(from.equals("Document") || from.equals("Folder")) {
                                    if(!lastId.equals(basisNode.getProperty("exo:title").getString())){
                                        //addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:title").getString(), basisNode.getProperty("exo:title").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        //addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        result.add(basisNode.getProperty("exo:title").getString());
                                        result.add(pathSlippted[1]);
                                        lastId = basisNode.getProperty("exo:title").getString();
                                    }
                                }
                                else{
                                    if(!lastId.equals(basisNode.getProperty("exo:name").getString())){
                                        //addUIFormInput(new UIFormStringInput(basisNode.getProperty("exo:name").getString(), basisNode.getProperty("exo:name").getString(), null));
                                        String path = basisNode.getPath() ;
                                        String pathSlippted [] = path.split("/Files/BO");
                                        //addUIFormInput(new UIFormStringInput(pathSlippted[1], pathSlippted[1], null));
                                        result.add(basisNode.getProperty("exo:name").getString());
                                        result.add(pathSlippted[1]);
                                        lastId = basisNode.getProperty("exo:name").getString();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            UIAdvancedSearchForm uiAdvancedSearchForm = uiSearchBasisPortlet.getChild(UIAdvancedSearchForm.class);
            String number = uiAdvancedSearchForm.getUIFormSelectBox("NumberResult").getValue();
            search(result, Integer.parseInt(number));
        } catch (RepositoryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public String getSizeResultList(){
        String messageToReturn;
        if(Util.getPortalRequestContext().getLocale().getLanguage().equals("fr")){
            if((result.size()/2)>1 ) messageToReturn = "Il y a <i>" + result.size()/2 + "</i> resultats";
            else   messageToReturn = "Il y a <i>" + result.size()/2 + "</i> resultat";
        }
        else if(Util.getPortalRequestContext().getLocale().getLanguage().equals("nl")){
            if((result.size()/2)>1 ) messageToReturn = "Er zijn <i>" + result.size()/2 + "</i> resultaat";
            else   messageToReturn = "Er <i>" + result.size()/2 + "</i> resultaat";
        }
        else {
            if((result.size()/2)>1 ) messageToReturn = "There are <i>" + result.size()/2 + "</i> results";
            else   messageToReturn = "There is <i>" + result.size()/2 + "</i> result";

        }
        return messageToReturn;
    }
}
