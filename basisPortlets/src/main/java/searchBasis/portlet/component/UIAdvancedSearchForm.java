package searchBasis.portlet.component;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.impl.core.query.QueryImpl;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormDateTimeInput;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.input.UICheckBoxInput;
import javax.jcr.NodeIterator;

import javax.jcr.Session;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: rousselle-k
 * Date: 16/11/12
 * Time: 10:42
 * To change this template use File | Settings | File Templates.
 */
@ComponentConfig(
        lifecycle = UIFormLifecycle.class,
        template =  "app:/groovy/SearchBasis/portlet/UIAdvancedSearchForm.gtmpl",
        events = {
                @EventConfig(listeners = UIAdvancedSearchForm.SearchActionListener.class),
                @EventConfig(listeners = UIAdvancedSearchForm.CancelActionListener.class),
                @EventConfig(listeners = UIAdvancedSearchForm.ChangeActionListener.class, phase= Event.Phase.DECODE )
        }
)
public class UIAdvancedSearchForm extends UIForm  {
    public static final String FIELD_DOC = "Document" ;
    public static final String FIELD_FOLDER = "Folder" ;
    public static final String FIELD_FOLLOW_DOC = "Follow document" ;
    public static final String FIELD_FOLLOW_FOLDER = "Follow folder" ;
    public static final String FIELD_FROM = "From" ;
    public static final String FIELD_LIMITED_SEARCH = "LimitedSearch";
    private  static final String NUMBERRESULT = "NumberResult";
    private int i;

    public UIAdvancedSearchForm() throws Exception {
        UIBasisFolderForm uiBasisFolderForm = addChild(UIBasisFolderForm.class,null,FIELD_FOLDER);
        uiBasisFolderForm.load(FIELD_FOLDER);

        UIBasisDocForm uiBasisDocForm = addChild(UIBasisDocForm.class,null,FIELD_DOC);
        uiBasisDocForm.load(FIELD_DOC);

        UIBasisFollowDocForm uiBasisFollowDocForm =addChild(UIBasisFollowDocForm.class,null,FIELD_FOLLOW_DOC);
        uiBasisFollowDocForm.load(FIELD_FOLLOW_DOC);

        UIBasisFollowFolderForm uiBasisFollowFolderForm = addChild(UIBasisFollowFolderForm.class,null,FIELD_FOLLOW_FOLDER);
        uiBasisFollowFolderForm.load(FIELD_FOLLOW_FOLDER);

        List<SelectItemOption<String>> lsFrom = new ArrayList<SelectItemOption<String>>() ;
        UIFormSelectBox uiSelectBoxFrom = new UIFormSelectBox(FIELD_FROM, FIELD_FROM, lsFrom) ;
        addChild(uiSelectBoxFrom);
        setActions(new String[]{"Search", "Cancel"}) ;

        List<SelectItemOption<String>> lsNumber = new ArrayList<SelectItemOption<String>>() ;
        lsNumber.add(new SelectItemOption<String>("10", "10")) ;
        lsNumber.add(new SelectItemOption<String>("20", "20")) ;
        lsNumber.add(new SelectItemOption<String>("50", "50")) ;
        UIFormSelectBox uiFormSelectBoxNumber = new UIFormSelectBox(NUMBERRESULT,NUMBERRESULT,lsNumber);
        uiFormSelectBoxNumber.setValue("20");
        addUIFormInput(uiFormSelectBoxNumber);

        addUIFormInput(new UICheckBoxInput(FIELD_LIMITED_SEARCH,null,true));
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    static public class SearchActionListener extends EventListener<UIAdvancedSearchForm> {
        public void execute(Event<UIAdvancedSearchForm> event) throws Exception {
            String language =  Util.getPortalRequestContext().getLocale().getDisplayName();
            UIAdvancedSearchForm uiAdvancedSearchForm = event.getSource();
            UISearchBasisPortlet uiSearchBasisPortlet = uiAdvancedSearchForm.getAncestorOfType(UISearchBasisPortlet.class);
            Map<String,String[]> mapFolder = new HashMap<String,String[]>();
            Map<String,String[]> mapDoc = new HashMap<String,String[]>();
            Map<String,String[]> mapFollowDoc = new HashMap<String,String[]>();
            Map<String,String[]> mapFollowFolder = new HashMap<String,String[]>();

            String from = uiAdvancedSearchForm.getUIFormSelectBox(FIELD_FROM).getValue();
            Boolean limited = uiAdvancedSearchForm.getUICheckBoxInput(FIELD_LIMITED_SEARCH).getValue();

            HttpServletRequest request = Util.getPortalRequestContext().getRequest();
            HttpServletResponse response = Util.getPortalRequestContext().getResponse();
            String url[] = Util.getPortalRequestContext().getRequestURI().split("BO:");
            String nameBO[] = url[1].split("/");
            Cookie[] cookies = request.getCookies();
            Cookie cookieDayCheck = new Cookie("dayCheck","");
            Cookie cookieBoName = new Cookie("boName","");
            for(int i=0; i < cookies.length; i++){
                if(cookies[i].getName().equals("dayCheck")){
                    cookieDayCheck = cookies[i];

                }
                else if(cookies[i].getName().equals("boName")){
                    cookieBoName = cookies[i];
                }
            }

            if(cookieBoName.getValue().isEmpty() || cookieBoName.getValue().equals(null)){
                cookieBoName.setValue(nameBO[0]);
                response.addCookie(cookieBoName);
            }
            else if(!cookieBoName.getValue().equals(nameBO[0])){
                cookieBoName.setValue(nameBO[0]);
                cookieDayCheck.setValue("No");
                response.addCookie(cookieBoName);
                response.addCookie(cookieDayCheck);
            }

            String xPathStatement = "";

            uiAdvancedSearchForm.setI(0);


            //Parcours des propriétés du folder
            UIBasisFolderForm uiBasisFolderForm = uiAdvancedSearchForm.getChildById(FIELD_FOLDER);
            UIPropertyInputForm uiPropertyInputForm = uiBasisFolderForm.getChildById(FIELD_FOLDER+"_basis:folderComments");
            UICheckBoxInput uiCheckBoxInput = uiPropertyInputForm.getChildById("basisFolder_basis.label.comments_checkBox");

            UIFormSelectBox uiFormSelectBox;
            UIFormStringInput uiFormStringInput;
            UIFormDateTimeInput uiFormDateTimeInput;
            if(uiCheckBoxInput.getValue()){
                String[] parameter= new String[2];

                uiFormSelectBox  = uiPropertyInputForm.getChildById("basisFolder_basis.label.comments_searchType");
                parameter[0] = uiFormSelectBox.getValue();
                uiFormStringInput = uiPropertyInputForm.getChildById("basisFolder_basis_comments");
                parameter[1] = uiFormStringInput.getValue();

                mapFolder.put("basis:folderComments", parameter);
            }
            uiPropertyInputForm = uiBasisFolderForm.getChildById("exo:title");
            uiCheckBoxInput = uiPropertyInputForm.getChildById("basisFolder.label.folderNumber_checkBox");
            if(uiCheckBoxInput.getValue()){
                String[] parameter= new String[2];

                uiFormSelectBox  = uiPropertyInputForm.getChildById("basisFolder.label.folderNumber_searchType");
                parameter[0] = uiFormSelectBox.getValue();
                uiFormStringInput = uiPropertyInputForm.getChildById("basisFolder.label.folderNumber");
                parameter[1] = uiFormStringInput.getValue();

                mapFolder.put("exo:title", parameter);
            }


            PropertyDefinition[] basisNodetypeProperties = uiBasisFolderForm.getBasisFolderNodetypeProperties();
            for (PropertyDefinition propertyFolder : basisNodetypeProperties) {
                if(propertyFolder.getName().contains("basis")) {
                    if(!propertyFolder.getName().contains("folderLanguage") && !propertyFolder.getName().contains("folderComments")) {
                        if(propertyFolder.getRequiredType() != 5){
                            uiPropertyInputForm = uiBasisFolderForm.getChildById(FIELD_FOLDER+"_"+propertyFolder.getName());
                            uiCheckBoxInput = uiPropertyInputForm.getChildById("basisFolder.label."+propertyFolder.getName().split("basis:")[1]+"_checkBox");
                            if(uiCheckBoxInput.getValue()){
                                String[] parameter= new String[2];

                                uiFormSelectBox  = uiPropertyInputForm.getChildById("basisFolder.label." + propertyFolder.getName().split("basis:")[1] + "_searchType");
                                parameter[0] = uiFormSelectBox.getValue();
                                uiFormStringInput = uiPropertyInputForm.getChildById("basisFolder_" + propertyFolder.getName().split("basis:")[1]);
                                parameter[1] = uiFormStringInput.getValue();

                                mapFolder.put(propertyFolder.getName(), parameter);
                            }
                        }
                        else{
                            uiPropertyInputForm = uiBasisFolderForm.getChildById(FIELD_FOLDER+"_"+propertyFolder.getName());
                            uiCheckBoxInput = uiPropertyInputForm.getChildById("basisFolder.label."+propertyFolder.getName().split("basis:")[1]+"_checkBox");
                            if(uiCheckBoxInput.getValue()){
                                uiFormDateTimeInput = uiPropertyInputForm.getChildById("basisFolder_" + propertyFolder.getName().split("basis:")[1]);
                                String date = uiFormDateTimeInput.getValue();
                                String day = date.substring(0,2);
                                String month = date.substring(3,5);
                                String year = date.substring(6,10);

                                String propertyDate;
                                if(language.equals("English")){
                                    propertyDate=year+"-"+day+"-"+month;
                                }
                                else{
                                    propertyDate=year+"-"+month+"-"+day;
                                }

                                String[] parameter= new String[3];

                                uiFormSelectBox  = uiPropertyInputForm.getChildById("basisFolder.label." + propertyFolder.getName().split("basis:")[1]+"_searchType");
                                parameter[0] = uiFormSelectBox.getValue();
                                parameter[1] = propertyDate;

                                if(uiPropertyInputForm.findComponentById("basisFolder_" + propertyFolder.getName().split("basis:")[1]+"_second") != null){
                                    uiFormDateTimeInput = uiPropertyInputForm.getChildById("basisFolder_" + propertyFolder.getName().split("basis:")[1]+"_second");
                                    String date2 = uiFormDateTimeInput.getValue();
                                    String day2 = date2.substring(0,2);
                                    String month2 = date2.substring(3,5);
                                    String year2 = date2.substring(6,10);

                                    //String [] dateSplitted = date.split("/");
                                    //String propertyDate=dateSplitted[2]+"-"+dateSplitted[1]+"-"+dateSplitted[0];
                                    String propertyDate2;
                                    if(language.equals("English")){
                                        propertyDate2=year2+"-"+day2+"-"+month2;
                                    }
                                    else{
                                        propertyDate2=year2+"-"+month2+"-"+day2;
                                    }
                                    parameter[2] = propertyDate2;
                                }

                                mapFolder.put(propertyFolder.getName(),parameter);
                            }
                        }
                    }
                }
            }

            uiSearchBasisPortlet.setMapFolder(mapFolder);


            //Parcours des propriétés du document
            UIBasisDocForm uiBasisDocForm = uiAdvancedSearchForm.getChildById(FIELD_DOC);
            basisNodetypeProperties = uiBasisDocForm.getBasisDocNodetypeProperties();
            for (PropertyDefinition propertyDoc : basisNodetypeProperties) {
                if(propertyDoc.getName().contains("basis")) {
                    if(!propertyDoc.getName().contains("docSenderType") && !propertyDoc.getName().contains("docComments")) {
                        if(propertyDoc.getRequiredType() != 5){
                            uiPropertyInputForm = uiBasisDocForm.getChildById(FIELD_DOC+"_"+propertyDoc.getName());
                            uiCheckBoxInput = uiPropertyInputForm.getChildById("basisDocument.label."+propertyDoc.getName().split("basis:")[1]+"_checkBox");
                            if(uiCheckBoxInput.getValue()){
                                String[] parameter= new String[2];

                                uiFormSelectBox  = uiPropertyInputForm.getChildById("basisDocument.label." + propertyDoc.getName().split("basis:")[1] + "_searchType");
                                parameter[0] = uiFormSelectBox.getValue();
                                uiFormStringInput = uiPropertyInputForm.getChildById("basisDocument_" + propertyDoc.getName().split("basis:")[1]);
                                parameter[1] = uiFormStringInput.getValue();

                                mapDoc.put(propertyDoc.getName(), parameter);
                            }
                        }
                        else{
                            uiPropertyInputForm = uiBasisDocForm.getChildById(FIELD_DOC+"_"+propertyDoc.getName());
                            uiCheckBoxInput = uiPropertyInputForm.getChildById("basisDocument.label."+propertyDoc.getName().split("basis:")[1]+"_checkBox");
                            if(uiCheckBoxInput.getValue()){
                                uiFormDateTimeInput = uiPropertyInputForm.getChildById("basisDocument_" + propertyDoc.getName().split("basis:")[1]);
                                String date = uiFormDateTimeInput.getValue();
                                String day = date.substring(0,2);
                                String month = date.substring(3,5);
                                String year = date.substring(6,10);

                                String propertyDate;
                                if(language.equals("English")){
                                    propertyDate=year+"-"+day+"-"+month;
                                }
                                else{
                                    propertyDate=year+"-"+month+"-"+day;
                                }

                                String[] parameter= new String[3];

                                uiFormSelectBox  = uiPropertyInputForm.getChildById("basisDocument.label." + propertyDoc.getName().split("basis:")[1]+"_searchType");
                                parameter[0] = uiFormSelectBox.getValue();
                                parameter[1] = propertyDate;

                                if(uiPropertyInputForm.findComponentById("basisDocument_" + propertyDoc.getName().split("basis:")[1]+"_second") != null){
                                    uiFormDateTimeInput = uiPropertyInputForm.getChildById("basisDocument_" + propertyDoc.getName().split("basis:")[1]+"_second");
                                    String date2 = uiFormDateTimeInput.getValue();
                                    String day2 = date2.substring(0,2);
                                    String month2 = date2.substring(3,5);
                                    String year2 = date2.substring(6,10);

                                    String propertyDate2;
                                    if(language.equals("English")){
                                        propertyDate2=year2+"-"+day2+"-"+month2;
                                    }
                                    else{
                                        propertyDate2=year2+"-"+month2+"-"+day2;
                                    }
                                    parameter[2] = propertyDate2;
                                }

                                mapDoc.put(propertyDoc.getName(),parameter);
                            }
                        }
                    }
                }
            }

            uiPropertyInputForm = uiBasisDocForm.getChildById(FIELD_DOC+"_basis:docComments");
            uiCheckBoxInput = uiPropertyInputForm.getChildById("basisDocument_basis.label.comments_checkBox");
            if(uiCheckBoxInput.getValue()){
                String[] parameter= new String[2];

                uiFormSelectBox  = uiPropertyInputForm.getChildById("basisDocument_basis.label.comments_searchType");
                parameter[0] = uiFormSelectBox.getValue();
                uiFormStringInput = uiPropertyInputForm.getChildById("basisDocument_basis_comments");
                parameter[1] = uiFormStringInput.getValue();

                mapDoc.put("basis:docComments", parameter);
            }
            uiPropertyInputForm = uiBasisDocForm.getChildById("exo:title");
            uiCheckBoxInput = uiPropertyInputForm.getChildById("basisDocument.label.docId_checkBox");
            if(uiCheckBoxInput.getValue()){
                String[] parameter= new String[2];

                uiFormSelectBox  = uiPropertyInputForm.getChildById("basisDocument.label.docId_searchType");
                parameter[0] = uiFormSelectBox.getValue();
                uiFormStringInput = uiPropertyInputForm.getChildById("basisDocument.label.docId");
                parameter[1] = uiFormStringInput.getValue();

                mapDoc.put("exo:title", parameter);
            }

            uiSearchBasisPortlet.setMapDoc(mapDoc);



            //Parcours des propriétés du follow folder
            UIBasisFollowFolderForm uiBasisFollowFolderForm = uiAdvancedSearchForm.getChildById(FIELD_FOLLOW_FOLDER);
            basisNodetypeProperties = uiBasisFollowFolderForm.getBasisFollowFolderNodetypeProperties();
            for (PropertyDefinition propertyFollowFolder : basisNodetypeProperties) {
                if(propertyFollowFolder.getName().contains("basis")) {
                    if(!propertyFollowFolder.getName().contains("followEditorType") && !propertyFollowFolder.getName().contains("followComments")) {
                        if(propertyFollowFolder.getRequiredType() != 5){
                            uiPropertyInputForm = uiBasisFollowFolderForm.getChildById(FIELD_FOLLOW_FOLDER+"_"+propertyFollowFolder.getName());
                            uiCheckBoxInput = uiPropertyInputForm.getChildById("folder_basisFollow.label."+propertyFollowFolder.getName().split("basis:")[1]+"_checkBox");
                            if(uiCheckBoxInput.getValue()){
                                String[] parameter= new String[2];

                                uiFormSelectBox  = uiPropertyInputForm.getChildById("folder_basisFollow.label." + propertyFollowFolder.getName().split("basis:")[1] + "_searchType");
                                parameter[0] = uiFormSelectBox.getValue();
                                uiFormStringInput = uiPropertyInputForm.getChildById("folder_basisFollow_" + propertyFollowFolder.getName().split("basis:")[1]);
                                parameter[1] = uiFormStringInput.getValue();

                                mapFollowFolder.put(propertyFollowFolder.getName(), parameter);
                            }
                        }
                        else{
                            uiPropertyInputForm = uiBasisFollowFolderForm.getChildById(FIELD_FOLLOW_FOLDER+"_"+propertyFollowFolder.getName());
                            uiCheckBoxInput = uiPropertyInputForm.getChildById("folder_basisFollow.label."+propertyFollowFolder.getName().split("basis:")[1]+"_checkBox");
                            if(uiCheckBoxInput.getValue()){
                                uiFormDateTimeInput = uiPropertyInputForm.getChildById("folder_basisFollow_" + propertyFollowFolder.getName().split("basis:")[1]);
                                String date = uiFormDateTimeInput.getValue();
                                String day = date.substring(0,2);
                                String month = date.substring(3,5);
                                String year = date.substring(6,10);

                                String propertyDate;
                                if(language.equals("English")){
                                    propertyDate=year+"-"+day+"-"+month;
                                }
                                else{
                                    propertyDate=year+"-"+month+"-"+day;
                                }

                                String[] parameter= new String[3];
                                uiFormSelectBox  = uiPropertyInputForm.getChildById("folder_basisFollow.label." + propertyFollowFolder.getName().split("basis:")[1]+"_searchType");
                                parameter[0] = uiFormSelectBox.getValue();
                                parameter[1] = propertyDate;

                                if(uiPropertyInputForm.findComponentById("folder_basisFollow_" + propertyFollowFolder.getName().split("basis:")[1]+"_second") != null){
                                    uiFormDateTimeInput = uiPropertyInputForm.getChildById("folder_basisFollow_" + propertyFollowFolder.getName().split("basis:")[1]+"_second");
                                    String date2 = uiFormDateTimeInput.getValue();
                                    String day2 = date2.substring(0,2);
                                    String month2 = date2.substring(3,5);
                                    String year2 = date2.substring(6,10);

                                    String propertyDate2;
                                    if(language.equals("English")){
                                        propertyDate2=year2+"-"+day2+"-"+month2;
                                    }
                                    else{
                                        propertyDate2=year2+"-"+month2+"-"+day2;
                                    }
                                    parameter[2] = propertyDate2;
                                }

                                mapFollowFolder.put(propertyFollowFolder.getName(),parameter);
                            }
                        }
                    }
                }
            }

            uiPropertyInputForm = uiBasisFollowFolderForm.getChildById(FIELD_FOLLOW_FOLDER+"_basis:followComments");
            uiCheckBoxInput = uiPropertyInputForm.getChildById("folder_basisFollow_basis.label.comments_checkBox");
            if(uiCheckBoxInput.getValue()){
                String[] parameter= new String[2];

                uiFormSelectBox  = uiPropertyInputForm.getChildById("folder_basisFollow_basis.label.comments_searchType");
                parameter[0] = uiFormSelectBox.getValue();
                uiFormStringInput = uiPropertyInputForm.getChildById("folder_basisFollow_basis_comments");
                parameter[1] = uiFormStringInput.getValue();

                mapFollowFolder.put("basis:followComments", parameter);
            }

            uiSearchBasisPortlet.setMapFollowFolder(mapFollowFolder);


            //Parcours des propriétés du follow document
            UIBasisFollowDocForm uiBasisFollowDocForm = uiAdvancedSearchForm.getChildById(FIELD_FOLLOW_DOC);
            basisNodetypeProperties = uiBasisFollowDocForm.getBasisFollowDocNodetypeProperties();
            for (PropertyDefinition propertyFollowDoc : basisNodetypeProperties) {
                if(propertyFollowDoc.getName().contains("basis")) {
                    if(!propertyFollowDoc.getName().contains("followEditorType") && !propertyFollowDoc.getName().contains("followComments")) {
                        if(propertyFollowDoc.getRequiredType() != 5){
                            uiPropertyInputForm = uiBasisFollowDocForm.getChildById(FIELD_FOLLOW_DOC+"_"+propertyFollowDoc.getName());
                            uiCheckBoxInput = uiPropertyInputForm.getChildById("document_basisFollow.label."+propertyFollowDoc.getName().split("basis:")[1]+"_checkBox");
                            if(uiCheckBoxInput.getValue()){
                                String[] parameter= new String[2];

                                uiFormSelectBox  = uiPropertyInputForm.getChildById("document_basisFollow.label." + propertyFollowDoc.getName().split("basis:")[1] + "_searchType");
                                parameter[0] = uiFormSelectBox.getValue();
                                uiFormStringInput = uiPropertyInputForm.getChildById("document_basisFollow_" + propertyFollowDoc.getName().split("basis:")[1]);
                                parameter[1] = uiFormStringInput.getValue();

                                mapFollowDoc.put(propertyFollowDoc.getName(), parameter);
                            }
                        }
                        else{
                            uiPropertyInputForm = uiBasisFollowDocForm.getChildById(FIELD_FOLLOW_DOC+"_"+propertyFollowDoc.getName());
                            uiCheckBoxInput = uiPropertyInputForm.getChildById("document_basisFollow.label."+propertyFollowDoc.getName().split("basis:")[1]+"_checkBox");
                            if(uiCheckBoxInput.getValue()){
                                uiFormDateTimeInput = uiPropertyInputForm.getChildById("document_basisFollow_" + propertyFollowDoc.getName().split("basis:")[1]);
                                String date = uiFormDateTimeInput.getValue();
                                String day = date.substring(0,2);
                                String month = date.substring(3,5);
                                String year = date.substring(6,10);

                                String propertyDate;
                                if(language.equals("English")){
                                    propertyDate=year+"-"+day+"-"+month;
                                }
                                else{
                                    propertyDate=year+"-"+month+"-"+day;
                                }

                                String[] parameter= new String[3];
                                uiFormSelectBox  = uiPropertyInputForm.getChildById("document_basisFollow.label." + propertyFollowDoc.getName().split("basis:")[1]+"_searchType");
                                parameter[0] = uiFormSelectBox.getValue();
                                parameter[1] = propertyDate;

                                if(uiPropertyInputForm.findComponentById("document_basisFollow_" + propertyFollowDoc.getName().split("basis:")[1]+"_second") != null){
                                    uiFormDateTimeInput = uiPropertyInputForm.getChildById("document_basisFollow_" + propertyFollowDoc.getName().split("basis:")[1]+"_second");
                                    String date2 = uiFormDateTimeInput.getValue();
                                    String day2 = date2.substring(0,2);
                                    String month2 = date2.substring(3,5);
                                    String year2 = date2.substring(6,10);

                                    String propertyDate2;
                                    if(language.equals("English")){
                                        propertyDate2=year2+"-"+day2+"-"+month2;
                                    }
                                    else{
                                        propertyDate2=year2+"-"+month2+"-"+day2;
                                    }
                                    parameter[2] = propertyDate2;
                                }

                                mapFollowDoc.put(propertyFollowDoc.getName(),parameter);
                            }
                        }
                    }
                }
            }

            uiPropertyInputForm = uiBasisFollowDocForm.getChildById(FIELD_FOLLOW_DOC+"_basis:followComments");
            uiCheckBoxInput = uiPropertyInputForm.getChildById("document_basisFollow_basis.label.comments_checkBox");
            if(uiCheckBoxInput.getValue()){
                String[] parameter= new String[2];

                uiFormSelectBox  = uiPropertyInputForm.getChildById("document_basisFollow_basis.label.comments_searchType");
                parameter[0] = uiFormSelectBox.getValue();
                uiFormStringInput = uiPropertyInputForm.getChildById("document_basisFollow_basis_comments");
                parameter[1] = uiFormStringInput.getValue();

                mapFollowDoc.put("basis:followComments", parameter);
            }
            uiSearchBasisPortlet.setMapFollowDoc(mapFollowDoc);
            uiSearchBasisPortlet.setFrom(from);

            //Etablissement de la requete
            xPathStatement = "/jcr:root/Files/BO/"+nameBO[0]+"//element (*,basis:basisFolder) [";
            xPathStatement = uiAdvancedSearchForm.firstChildRequest(mapFolder, xPathStatement);
            xPathStatement = uiAdvancedSearchForm.secondChildRequest(mapDoc, xPathStatement);
            xPathStatement = uiAdvancedSearchForm.secondChildRequest(mapFollowFolder, xPathStatement);
            xPathStatement = uiAdvancedSearchForm.thirdChildRequest(mapFollowDoc, xPathStatement);
            xPathStatement += "] order by @exo:name ascending";

            if(mapFolder.isEmpty() && mapDoc.isEmpty() && mapFollowDoc.isEmpty() && mapFollowFolder.isEmpty()){
                xPathStatement = "/jcr:root/Files/BO/"+nameBO[0]+"//element (*,basis:basisFolder) order by @exo:name ascending";
            }

            //Execution requete
            if(xPathStatement != ""){
                ExoContainer exoContainer = ExoContainerContext.getCurrentContainer();
                RepositoryService rs = (RepositoryService) exoContainer.getComponentInstanceOfType(RepositoryService.class);
                Session session = (Session) rs.getRepository("repository").getSystemSession("collaboration");
                QueryManager queryManager = null;
                queryManager = session.getWorkspace().getQueryManager();
                QueryImpl query = (QueryImpl)queryManager.createQuery(xPathStatement, Query.XPATH);
                //Query query = queryManager.createQuery(xPathStatement, Query.XPATH);
                if(limited == true){
                    query.setLimit(500);
                    query.setOffset(0);
                }
                QueryResult result = query.execute();

                uiSearchBasisPortlet.setQueryResult(result);
                uiSearchBasisPortlet.updateResultAdvanced();
                uiAdvancedSearchForm.setRendered(false);
            }
        }
    }

    static public class CancelActionListener extends EventListener<UIAdvancedSearchForm> {
        public void execute(Event<UIAdvancedSearchForm> event) throws Exception {
            UIAdvancedSearchForm uiAdvancedSearchForm = event.getSource() ;
            UISearchBasisPortlet uiManager = uiAdvancedSearchForm.getAncestorOfType(UISearchBasisPortlet.class) ;
            uiAdvancedSearchForm.reset() ;
            event.getRequestContext().addUIComponentToUpdateByAjax(uiManager) ;
        }
    }

    static public class ChangeActionListener extends EventListener<UIAdvancedSearchForm> {
        public void execute(Event<UIAdvancedSearchForm> event) throws Exception {

            UIAdvancedSearchForm uiAdvancedSearchForm = event.getSource() ;
            UIBasisFolderForm uiBasisFolderForm = uiAdvancedSearchForm.getChildById(FIELD_FOLDER);
            UIBasisDocForm uiBasisDocForm = uiAdvancedSearchForm.getChildById(FIELD_DOC);
            UIBasisFollowFolderForm uiBasisFollowFolderForm = uiAdvancedSearchForm.getChildById(FIELD_FOLLOW_FOLDER);
            UIBasisFollowDocForm uiBasisFollowDocForm = uiAdvancedSearchForm.getChildById(FIELD_FOLLOW_DOC);
            UIPropertyInputForm uiPropertyInputForm = null;

            PropertyDefinition[] basisNodetypeProperties = uiBasisFolderForm.getBasisFolderNodetypeProperties();
            for (PropertyDefinition propertyFolder : basisNodetypeProperties) {
                if(propertyFolder.getName().contains("basis")) {
                    if(propertyFolder.getRequiredType() == 5){
                        uiPropertyInputForm = uiBasisFolderForm.getChildById(FIELD_FOLDER+"_"+propertyFolder.getName());
                        UIFormSelectBox uiFormSelectBox  = uiPropertyInputForm.getChildById("basisFolder.label." + propertyFolder.getName().split("basis:")[1]+"_searchType");
                        if(uiFormSelectBox.getValue().equals("Between")){
                            if(uiPropertyInputForm.findComponentById("basisFolder_" + propertyFolder.getName().split("basis:")[1]+"_second") == null){
                                uiPropertyInputForm.addChild(new UIFormDateTimeInput("basisFolder_" + propertyFolder.getName().split("basis:")[1]+"_second", null, new Date(), false));
                            }
                        }
                        else{
                            uiPropertyInputForm.removeChildById("basisFolder_" + propertyFolder.getName().split("basis:")[1]+"_second");
                        }
                    }
                }
            }

            basisNodetypeProperties = uiBasisDocForm.getBasisDocNodetypeProperties();
            for (PropertyDefinition propertyDoc : basisNodetypeProperties) {
                if(propertyDoc.getName().contains("basis")) {
                    if(propertyDoc.getRequiredType() == 5){
                        uiPropertyInputForm = uiBasisDocForm.getChildById(FIELD_DOC+"_"+propertyDoc.getName());
                        UIFormSelectBox uiFormSelectBox  = uiPropertyInputForm.getChildById("basisDocument.label." + propertyDoc.getName().split("basis:")[1]+"_searchType");
                        if(uiFormSelectBox.getValue().equals("Between")){
                            if(uiPropertyInputForm.findComponentById("basisDocument_" + propertyDoc.getName().split("basis:")[1]+"_second") == null){
                                uiPropertyInputForm.addChild(new UIFormDateTimeInput("basisDocument_" + propertyDoc.getName().split("basis:")[1]+"_second", null, new Date(), false));
                            }
                        }
                        else{
                            uiPropertyInputForm.removeChildById("basisDocument_" + propertyDoc.getName().split("basis:")[1]+"_second");
                        }
                    }
                }
            }

            basisNodetypeProperties = uiBasisFollowFolderForm.getBasisFollowFolderNodetypeProperties();
            for (PropertyDefinition propertyFollowFolder : basisNodetypeProperties) {
                if(propertyFollowFolder.getName().contains("basis")) {
                    if(propertyFollowFolder.getRequiredType() == 5){
                        uiPropertyInputForm = uiBasisFollowFolderForm.getChildById(FIELD_FOLLOW_FOLDER+"_"+propertyFollowFolder.getName());
                        UIFormSelectBox uiFormSelectBox  = uiPropertyInputForm.getChildById("folder_basisFollow.label." + propertyFollowFolder.getName().split("basis:")[1] + "_searchType");
                        if(uiFormSelectBox.getValue().equals("Between")){
                            if(uiPropertyInputForm.findComponentById("folder_basisFollow_" + propertyFollowFolder.getName().split("basis:")[1]+"_second") == null){
                                uiPropertyInputForm.addChild(new UIFormDateTimeInput("folder_basisFollow_" + propertyFollowFolder.getName().split("basis:")[1]+"_second", null, new Date(), false));
                            }
                        }
                        else{
                            uiPropertyInputForm.removeChildById("folder_basisFollow_" + propertyFollowFolder.getName().split("basis:")[1]+"_second");
                        }
                    }
                }
            }

            basisNodetypeProperties = uiBasisFollowDocForm.getBasisFollowDocNodetypeProperties();
            for (PropertyDefinition propertyFollowDoc : basisNodetypeProperties) {
                if(propertyFollowDoc.getName().contains("basis")) {
                    if(propertyFollowDoc.getRequiredType() == 5){
                        uiPropertyInputForm = uiBasisFollowDocForm.getChildById(FIELD_FOLLOW_DOC+"_"+propertyFollowDoc.getName());
                        UIFormSelectBox uiFormSelectBox  = uiPropertyInputForm.getChildById("document_basisFollow.label." + propertyFollowDoc.getName().split("basis:")[1] + "_searchType");
                        if(uiFormSelectBox.getValue().equals("Between")){
                            if(uiPropertyInputForm.findComponentById("document_basisFollow_" + propertyFollowDoc.getName().split("basis:")[1]+"_second") == null){
                                uiPropertyInputForm.addChild(new UIFormDateTimeInput("document_basisFollow_" + propertyFollowDoc.getName().split("basis:")[1]+"_second", null, new Date(), false));
                            }
                        }
                        else{
                            uiPropertyInputForm.removeChildById("document_basisFollow_" + propertyFollowDoc.getName().split("basis:")[1]+"_second");
                        }
                    }
                }
            }
            event.getRequestContext().addUIComponentToUpdateByAjax(uiAdvancedSearchForm) ;
        }
    }

    public String firstChildRequest(Map<String,String[]> mapProperty, String xPathStatement){
        if(!mapProperty.isEmpty()){
            for (String mapKey : mapProperty.keySet()) {
                String[] value = mapProperty.get(mapKey);

                if(value[0].equals("Equals")){
                    if(!mapKey.contains("Date")){
                        if(value[1].contains("&")){
                            String valueSplitted[] = value[1].split("&");

                            for(int j = 0 ; j < valueSplitted.length ; j++){
                                if(i == 0){
                                    xPathStatement += "fn:lower-case(@"+mapKey+")='"+valueSplitted[j].toLowerCase()+"'";
                                }
                                else{
                                    xPathStatement += " and fn:lower-case(@"+mapKey+")='"+valueSplitted[j].toLowerCase()+"'";
                                }
                                i++;
                            }
                        }
                        else if(value[1].contains("#")){
                            String valueSplitted[] = value[1].split("#");

                            for(int j = 0 ; j < valueSplitted.length ; j++){
                                if(i == 0){
                                    xPathStatement += "fn:lower-case(@"+mapKey+")='"+valueSplitted[j].toLowerCase()+"'";
                                }
                                else{
                                    xPathStatement += " or fn:lower-case(@"+mapKey+")='"+valueSplitted[j].toLowerCase()+"'";
                                }
                                i++;
                            }
                        }
                        else{
                            if(i == 0){
                                xPathStatement += "fn:lower-case(@"+mapKey+")='"+value[1].toLowerCase()+"'";
                            }
                            else{
                                xPathStatement += " and fn:lower-case(@"+mapKey+")='"+value[1].toLowerCase()+"'";
                            }
                        }
                    }
                    else{
                        if(i == 0){
                            xPathStatement += "@"+mapKey+"=xs:dateTime('"+value[1]+"')";
                        }
                        else{
                            xPathStatement += " and @"+mapKey+"=xs:dateTime('"+value[1]+"')";
                        }
                    }
                }
                else if(value[0].equals("Contains")){
                    if(value[1].contains("&")){
                        String valueSplitted[] = value[1].split("&");

                        for(int j = 0 ; j < valueSplitted.length ; j++){
                            if(i == 0){
                                xPathStatement += "jcr:like(fn:lower-case(@"+mapKey+"),'%"+valueSplitted[j].toLowerCase()+"%')";
                            }
                            else{
                                xPathStatement += " and jcr:like(fn:lower-case(@"+mapKey+"),'%"+valueSplitted[j].toLowerCase()+"%')";
                            }
                            i++;
                        }
                    }
                    else if(value[1].contains("#")){
                        String valueSplitted[] = value[1].split("#");

                        for(int j = 0 ; j < valueSplitted.length ; j++){
                            if(i == 0){
                                xPathStatement += "jcr:like(fn:lower-case(@"+mapKey+"),'%"+valueSplitted[j].toLowerCase()+"%')";
                            }
                            else{
                                xPathStatement += " or jcr:like(fn:lower-case(@"+mapKey+"),'%"+valueSplitted[j].toLowerCase()+"%')";
                            }
                            i++;
                        }
                    }
                    else{
                        if(i == 0){
                            xPathStatement += "jcr:like(fn:lower-case(@"+mapKey+"),'%"+value[1].toLowerCase()+"%')";
                        }
                        else{
                            xPathStatement += " and jcr:like(fn:lower-case(@"+mapKey+"),'%"+value[1].toLowerCase()+"%')";
                        }
                    }
                }
                else if(value[0].equals("Not_Equals")){
                    if(!mapKey.contains("Date")){
                        if(value[1].contains("&")){
                            String valueSplitted[] = value[1].split("&");

                            for(int j = 0 ; j < valueSplitted.length ; j++){
                                if(i == 0){
                                    xPathStatement += "not(fn:lower-case(@"+mapKey+")='"+valueSplitted[j].toLowerCase()+"')";
                                }
                                else{
                                    xPathStatement += " and not(fn:lower-case(@"+mapKey+")='"+valueSplitted[j].toLowerCase()+"')";
                                }
                                i++;
                            }
                        }
                        else if(value[1].contains("#")){
                            String valueSplitted[] = value[1].split("#");

                            for(int j = 0 ; j < valueSplitted.length ; j++){
                                if(i == 0){
                                    xPathStatement += "not(fn:lower-case(@"+mapKey+")='"+valueSplitted[j].toLowerCase()+"')";
                                }
                                else{
                                    xPathStatement += " or not(fn:lower-case(@"+mapKey+")='"+valueSplitted[j].toLowerCase()+"')";
                                }
                                i++;
                            }
                        }
                        else{
                            if(i == 0){
                                xPathStatement += "not(fn:lower-case(@"+mapKey+")='"+value[1].toLowerCase()+"')";
                            }
                            else{
                                xPathStatement += " and not(fn:lower-case(@"+mapKey+")='"+value[1].toLowerCase()+"')";
                            }
                        }
                    }
                    else{
                        if(i == 0){
                            xPathStatement += "not(@"+mapKey+"=xs:dateTime('"+value[1]+"'))";
                        }
                        else{
                            xPathStatement += " and not(@"+mapKey+"=xs:dateTime('"+value[1]+"'))";
                        }
                    }
                }
                else if(value[0].equals("Not_Contains")){
                    if(value[1].contains("&")){
                        String valueSplitted[] = value[1].split("&");

                        for(int j = 0 ; j < valueSplitted.length ; j++){
                            if(i == 0){
                                xPathStatement += "not(jcr:like(fn:lower-case(@"+mapKey+"),'%"+valueSplitted[j].toLowerCase()+"%'))";
                            }
                            else{
                                xPathStatement += " and not(jcr:like(fn:lower-case(@"+mapKey+"),'%"+valueSplitted[j].toLowerCase()+"%'))";
                            }
                            i++;
                        }
                    }
                    else if(value[1].contains("#")){
                        String valueSplitted[] = value[1].split("#");

                        for(int j = 0 ; j < valueSplitted.length ; j++){
                            if(i == 0){
                                xPathStatement += "not(jcr:like(fn:lower-case(@"+mapKey+"),'%"+valueSplitted[j].toLowerCase()+"%'))";
                            }
                            else{
                                xPathStatement += " or not(jcr:like(fn:lower-case(@"+mapKey+"),'%"+valueSplitted[j].toLowerCase()+"%'))";
                            }
                            i++;
                        }
                    }
                    else{
                        if(i == 0){
                            xPathStatement += "not(jcr:like(fn:lower-case(@"+mapKey+"),'%"+value[1].toLowerCase()+"%'))";
                        }
                        else{
                            xPathStatement += " and not(jcr:like(fn:lower-case(@"+mapKey+"),'%"+value[1].toLowerCase()+"%'))";
                        }
                    }
                }
                else if(value[0].equals("Between")){
                    if(i == 0){
                        xPathStatement += "@"+mapKey+">=xs:dateTime('"+value[1]+"') and @"+mapKey+"<=xs:dateTime('"+value[2]+"')";
                    }
                    else{
                        xPathStatement += " and @"+mapKey+">=xs:dateTime('"+value[1]+"') and @"+mapKey+"<=xs:dateTime('"+value[2]+"')";
                    }
                }

                i++;

            }
        }
        return xPathStatement;
    }

    public String secondChildRequest(Map<String,String[]> mapProperty, String xPathStatement){
        if(!mapProperty.isEmpty()){
            for (String mapKey : mapProperty.keySet()) {
                String[] value = mapProperty.get(mapKey);

                if(value[0].equals("Equals")){
                    if(!mapKey.contains("Date")){
                        if(value[1].contains("&")){
                            String valueSplitted[] = value[1].split("&");

                            for(int j = 0 ; j < valueSplitted.length ; j++){
                                if(i == 0){
                                    xPathStatement += "fn:lower-case(*/@"+mapKey+")='"+valueSplitted[j].toLowerCase()+"'";
                                }
                                else{
                                    xPathStatement += " and fn:lower-case(*/@"+mapKey+")='"+valueSplitted[j].toLowerCase()+"'";
                                }
                                i++;
                            }
                        }
                        else if(value[1].contains("#")){
                            String valueSplitted[] = value[1].split("#");

                            for(int j = 0 ; j < valueSplitted.length ; j++){
                                if(i == 0){
                                    xPathStatement += "fn:lower-case(*/@"+mapKey+")='"+valueSplitted[j].toLowerCase()+"'";
                                }
                                else{
                                    xPathStatement += " or fn:lower-case(*/@"+mapKey+")='"+valueSplitted[j].toLowerCase()+"'";
                                }
                                i++;
                            }
                        }
                        else{
                            if(i == 0){
                                xPathStatement += "fn:lower-case(*/@"+mapKey+")='"+value[1].toLowerCase()+"'";
                            }
                            else{
                                xPathStatement += " and fn:lower-case(*/@"+mapKey+")='"+value[1].toLowerCase()+"'";
                            }
                        }
                    }
                    else{
                        if(i == 0){
                            xPathStatement += "*/@"+mapKey+"=xs:dateTime('"+value[1]+"')";
                        }
                        else{
                            xPathStatement += " and */@"+mapKey+"=xs:dateTime('"+value[1]+"')";
                        }
                    }
                }
                else if(value[0].equals("Contains")){
                    if(value[1].contains("&")){
                        String valueSplitted[] = value[1].split("&");

                        for(int j = 0 ; j < valueSplitted.length ; j++){
                            if(i == 0){
                                xPathStatement += "jcr:like(fn:lower-case(*/@"+mapKey+"),'%"+valueSplitted[j].toLowerCase()+"%')";
                            }
                            else{
                                xPathStatement += " and jcr:like(fn:lower-case(*/@"+mapKey+"),'%"+valueSplitted[j].toLowerCase()+"%')";
                            }
                            i++;
                        }
                    }
                    else if(value[1].contains("#")){
                        String valueSplitted[] = value[1].split("#");

                        for(int j = 0 ; j < valueSplitted.length ; j++){
                            if(i == 0){
                                xPathStatement += "jcr:like(fn:lower-case(*/@"+mapKey+"),'%"+valueSplitted[j].toLowerCase()+"%')";
                            }
                            else{
                                xPathStatement += " or jcr:like(fn:lower-case(*/@"+mapKey+"),'%"+valueSplitted[j].toLowerCase()+"%')";
                            }
                            i++;
                        }
                    }
                    else{
                        if(i == 0){
                            xPathStatement += "jcr:like(fn:lower-case(*/@"+mapKey+"),'%"+value[1].toLowerCase()+"%')";
                        }
                        else{
                            xPathStatement += " and jcr:like(fn:lower-case(*/@"+mapKey+"),'%"+value[1].toLowerCase()+"%')";
                        }
                    }
                }
                else if(value[0].equals("Not_Equals")){
                    if(!mapKey.contains("Date")){
                        if(value[1].contains("&")){
                            String valueSplitted[] = value[1].split("&");

                            for(int j = 0 ; j < valueSplitted.length ; j++){
                                if(i == 0){
                                    xPathStatement += "not(fn:lower-case(*/@"+mapKey+")='"+valueSplitted[j].toLowerCase()+"')";
                                }
                                else{
                                    xPathStatement += " and not(fn:lower-case(*/@"+mapKey+")='"+valueSplitted[j].toLowerCase()+"')";
                                }
                                i++;
                            }
                        }
                        else if(value[1].contains("#")){
                            String valueSplitted[] = value[1].split("#");

                            for(int j = 0 ; j < valueSplitted.length ; j++){
                                if(i == 0){
                                    xPathStatement += "not(fn:lower-case(*/@"+mapKey+")='"+valueSplitted[j].toLowerCase()+"')";
                                }
                                else{
                                    xPathStatement += " or not(fn:lower-case(*/@"+mapKey+")='"+valueSplitted[j].toLowerCase()+"')";
                                }
                                i++;
                            }
                        }
                        else{
                            if(i == 0){
                                xPathStatement += "not(fn:lower-case(*/@"+mapKey+")='"+value[1].toLowerCase()+"')";
                            }
                            else{
                                xPathStatement += " and not(fn:lower-case(*/@"+mapKey+")='"+value[1].toLowerCase()+"')";
                            }
                        }
                    }
                    else{
                        if(i == 0){
                            xPathStatement += "not(*/@"+mapKey+"=xs:dateTime('"+value[1]+"'))";
                        }
                        else{
                            xPathStatement += " and not(*/@"+mapKey+"=xs:dateTime('"+value[1]+"'))";
                        }
                    }
                }
                else if(value[0].equals("Not_Contains")){
                    if(value[1].contains("&")){
                        String valueSplitted[] = value[1].split("&");

                        for(int j = 0 ; j < valueSplitted.length ; j++){
                            if(i == 0){
                                xPathStatement += "not(jcr:like(fn:lower-case(*/@"+mapKey+"),'%"+valueSplitted[j].toLowerCase()+"%'))";
                            }
                            else{
                                xPathStatement += " and not(jcr:like(fn:lower-case(*/@"+mapKey+"),'%"+valueSplitted[j].toLowerCase()+"%'))";
                            }
                            i++;
                        }
                    }
                    else if(value[1].contains("#")){
                        String valueSplitted[] = value[1].split("#");

                        for(int j = 0 ; j < valueSplitted.length ; j++){
                            if(i == 0){
                                xPathStatement += "not(jcr:like(fn:lower-case(*/@"+mapKey+"),'%"+valueSplitted[j].toLowerCase()+"%'))";
                            }
                            else{
                                xPathStatement += " or not(jcr:like(fn:lower-case(*/@"+mapKey+"),'%"+valueSplitted[j].toLowerCase()+"%'))";
                            }
                            i++;
                        }
                    }
                    else{
                        if(i == 0){
                            xPathStatement += "not(jcr:like(fn:lower-case(*/@"+mapKey+"),'%"+value[1].toLowerCase()+"%'))";
                        }
                        else{
                            xPathStatement += " and not(jcr:like(fn:lower-case(*/@"+mapKey+"),'%"+value[1].toLowerCase()+"%'))";
                        }
                    }
                }
                else if(value[0].equals("Between")){
                    if(i == 0){
                        xPathStatement += "*/@"+mapKey+">=xs:dateTime('"+value[1]+"') and */@"+mapKey+"<=xs:dateTime('"+value[2]+"')";
                    }
                    else{
                        xPathStatement += " and */@"+mapKey+">=xs:dateTime('"+value[1]+"') and */@"+mapKey+"<=xs:dateTime('"+value[2]+"')";
                    }
                }

                i++;

            }
        }
        return xPathStatement;
    }

    public String thirdChildRequest(Map<String,String[]> mapProperty, String xPathStatement){
        if(!mapProperty.isEmpty()){
            for (String mapKey : mapProperty.keySet()) {
                String[] value = mapProperty.get(mapKey);

                if(value[0].equals("Equals")){
                    if(!mapKey.contains("Date")){
                        if(value[1].contains("&")){
                            String valueSplitted[] = value[1].split("&");

                            for(int j = 0 ; j < valueSplitted.length ; j++){
                                if(i == 0){
                                    xPathStatement += "fn:lower-case(*/*/@"+mapKey+")='"+valueSplitted[j].toLowerCase()+"'";
                                }
                                else{
                                    xPathStatement += " and fn:lower-case(*/*/@"+mapKey+")='"+valueSplitted[j].toLowerCase()+"'";
                                }
                                i++;
                            }
                        }
                        else if(value[1].contains("#")){
                            String valueSplitted[] = value[1].split("#");

                            for(int j = 0 ; j < valueSplitted.length ; j++){
                                if(i == 0){
                                    xPathStatement += "fn:lower-case(*/*/@"+mapKey+")='"+valueSplitted[j].toLowerCase()+"'";
                                }
                                else{
                                    xPathStatement += " or fn:lower-case(*/*/@"+mapKey+")='"+valueSplitted[j].toLowerCase()+"'";
                                }
                                i++;
                            }
                        }
                        else{
                            if(i == 0){
                                xPathStatement += "fn:lower-case(*/*/@"+mapKey+")='"+value[1].toLowerCase()+"'";
                            }
                            else{
                                xPathStatement += " and fn:lower-case(*/*/@"+mapKey+")='"+value[1].toLowerCase()+"'";
                            }
                        }
                    }
                    else{
                        if(i == 0){
                            xPathStatement += "*/*/@"+mapKey+"=xs:dateTime('"+value[1]+"')";
                        }
                        else{
                            xPathStatement += " and */*/@"+mapKey+"=xs:dateTime('"+value[1]+"')";
                        }
                    }
                }
                else if(value[0].equals("Contains")){
                    if(value[1].contains("&")){
                        String valueSplitted[] = value[1].split("&");

                        for(int j = 0 ; j < valueSplitted.length ; j++){
                            if(i == 0){
                                xPathStatement += "jcr:like(fn:lower-case(*/*/@"+mapKey+"),'%"+valueSplitted[j].toLowerCase()+"%')";
                            }
                            else{
                                xPathStatement += " and jcr:like(fn:lower-case(*/*/@"+mapKey+"),'%"+valueSplitted[j].toLowerCase()+"%')";
                            }
                            i++;
                        }
                    }
                    else if(value[1].contains("#")){
                        String valueSplitted[] = value[1].split("#");

                        for(int j = 0 ; j < valueSplitted.length ; j++){
                            if(i == 0){
                                xPathStatement += "jcr:like(fn:lower-case(*/*/@"+mapKey+"),'%"+valueSplitted[j].toLowerCase()+"%')";
                            }
                            else{
                                xPathStatement += " or jcr:like(fn:lower-case(*/*/@"+mapKey+"),'%"+valueSplitted[j].toLowerCase()+"%')";
                            }
                            i++;
                        }
                    }
                    else{
                        if(i == 0){
                            xPathStatement += "jcr:like(fn:lower-case(*/*/@"+mapKey+"),'%"+value[1].toLowerCase()+"%')";
                        }
                        else{
                            xPathStatement += " and jcr:like(fn:lower-case(*/*/@"+mapKey+"),'%"+value[1].toLowerCase()+"%')";
                        }
                    }
                }
                else if(value[0].equals("Not_Equals")){
                    if(!mapKey.contains("Date")){
                        if(value[1].contains("&")){
                            String valueSplitted[] = value[1].split("&");

                            for(int j = 0 ; j < valueSplitted.length ; j++){
                                if(i == 0){
                                    xPathStatement += "not(fn:lower-case(*/*/@"+mapKey+")='"+valueSplitted[j].toLowerCase()+"')";
                                }
                                else{
                                    xPathStatement += " and not(fn:lower-case(*/*/@"+mapKey+")='"+valueSplitted[j].toLowerCase()+"')";
                                }
                                i++;
                            }
                        }
                        else if(value[1].contains("#")){
                            String valueSplitted[] = value[1].split("#");

                            for(int j = 0 ; j < valueSplitted.length ; j++){
                                if(i == 0){
                                    xPathStatement += "not(fn:lower-case(*/*/@"+mapKey+")='"+valueSplitted[j].toLowerCase()+"')";
                                }
                                else{
                                    xPathStatement += " or not(fn:lower-case(*/*/@"+mapKey+")='"+valueSplitted[j].toLowerCase()+"')";
                                }
                                i++;
                            }
                        }
                        else{
                            if(i == 0){
                                xPathStatement += "not(fn:lower-case(*/*/@"+mapKey+")='"+value[1].toLowerCase()+"')";
                            }
                            else{
                                xPathStatement += " and not(fn:lower-case(*/*/@"+mapKey+")='"+value[1].toLowerCase()+"')";
                            }
                        }
                    }
                    else{
                        if(i == 0){
                            xPathStatement += "not(*/*/@"+mapKey+"=xs:dateTime('"+value[1]+"'))";
                        }
                        else{
                            xPathStatement += " and not(*/*/@"+mapKey+"=xs:dateTime('"+value[1]+"'))";
                        }
                    }
                }
                else if(value[0].equals("Not_Contains")){
                    if(value[1].contains("&")){
                        String valueSplitted[] = value[1].split("&");

                        for(int j = 0 ; j < valueSplitted.length ; j++){
                            if(i == 0){
                                xPathStatement += "not(jcr:like(fn:lower-case(*/*/@"+mapKey+"),'%"+valueSplitted[j].toLowerCase()+"%'))";
                            }
                            else{
                                xPathStatement += " and not(jcr:like(fn:lower-case(*/*/@"+mapKey+"),'%"+valueSplitted[j].toLowerCase()+"%'))";
                            }
                            i++;
                        }
                    }
                    else if(value[1].contains("#")){
                        String valueSplitted[] = value[1].split("#");

                        for(int j = 0 ; j < valueSplitted.length ; j++){
                            if(i == 0){
                                xPathStatement += "not(jcr:like(fn:lower-case(*/*/@"+mapKey+"),'%"+valueSplitted[j].toLowerCase()+"%'))";
                            }
                            else{
                                xPathStatement += " or not(jcr:like(fn:lower-case(*/*/@"+mapKey+"),'%"+valueSplitted[j].toLowerCase()+"%'))";
                            }
                            i++;
                        }
                    }
                    else{
                        if(i == 0){
                            xPathStatement += "not(jcr:like(fn:lower-case(*/*/@"+mapKey+"),'%"+value[1].toLowerCase()+"%'))";
                        }
                        else{
                            xPathStatement += " and not(jcr:like(fn:lower-case(*/*/@"+mapKey+"),'%"+value[1].toLowerCase()+"%'))";
                        }
                    }
                }
                else if(value[0].equals("Between")){
                    if(i == 0){
                        xPathStatement += "*/*/@"+mapKey+">=xs:dateTime('"+value[1]+"') and */*/@"+mapKey+"<=xs:dateTime('"+value[2]+"')";
                    }
                    else{
                        xPathStatement += " and */*/@"+mapKey+">=xs:dateTime('"+value[1]+"') and */*/@"+mapKey+"<=xs:dateTime('"+value[2]+"')";
                    }
                }

                i++;

            }
        }
        return xPathStatement;
    }
}



