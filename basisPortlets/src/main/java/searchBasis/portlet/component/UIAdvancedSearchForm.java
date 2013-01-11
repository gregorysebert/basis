package searchBasis.portlet.component;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormSelectBox;

import javax.jcr.Session;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                @EventConfig(listeners = UIAdvancedSearchForm.CancelActionListener.class)
        }
)
public class UIAdvancedSearchForm extends UIForm  {
    public static final String FIELD_DOC = "Document" ;
    public static final String FIELD_FOLDER = "Folder" ;
    public static final String FIELD_FOLLOW_DOC = "Follow document" ;
    public static final String FIELD_FOLLOW_FOLDER = "Follow folder" ;
    public static final String FIELD_FROM = "From" ;
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
        lsNumber.add(new SelectItemOption<String>("5", "5")) ;
        lsNumber.add(new SelectItemOption<String>("10", "10")) ;
        lsNumber.add(new SelectItemOption<String>("20", "20")) ;
        lsNumber.add(new SelectItemOption<String>("50", "50")) ;
        UIFormSelectBox uiFormSelectBoxNumber = new UIFormSelectBox(NUMBERRESULT,NUMBERRESULT,lsNumber);
        addUIFormInput(uiFormSelectBoxNumber);
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    static public class SearchActionListener extends EventListener<UIAdvancedSearchForm> {
        public void execute(Event<UIAdvancedSearchForm> event) throws Exception {
            UIAdvancedSearchForm uiAdvancedSearchForm = event.getSource();
            UISearchBasisPortlet uiSearchBasisPortlet = uiAdvancedSearchForm.getAncestorOfType(UISearchBasisPortlet.class);
            Map<String,String[]> mapFolder = new HashMap<String,String[]>();
            Map<String,String[]> mapDoc = new HashMap<String,String[]>();
            Map<String,String[]> mapFollowDoc = new HashMap<String,String[]>();
            Map<String,String[]> mapFollowFolder = new HashMap<String,String[]>();

            String from = uiAdvancedSearchForm.getUIFormSelectBox(FIELD_FROM).getValue();
            String url = Util.getPortalRequestContext().getRequestURI();
            String urlSplitted[] = url.split("BO:");
            String nameBO[] = urlSplitted[1].split("/");
            String xPathStatement = "";

            uiAdvancedSearchForm.setI(0);


            //Parcours des propriétés du folder
            UIBasisFolderForm uiBasisFolderForm = uiAdvancedSearchForm.getChildById(FIELD_FOLDER);
            UIPropertyInputForm uiPropertyInputForm = uiBasisFolderForm.getChildById(FIELD_FOLDER+"_basis:folderComments");
            if(uiPropertyInputForm.getUICheckBoxInput("basisFolder_basis.label.comments_checkBox").getValue()){
                String[] parameter= new String[2];
                parameter[0] = uiPropertyInputForm.getUIFormSelectBox("basisFolder_basis.label.comments_searchType").getValue();
                parameter[1] = uiPropertyInputForm.getUIStringInput("basisFolder_basis.label.comments").getValue();
                mapFolder.put("basis:folderComments", parameter);
            }
            uiPropertyInputForm = uiBasisFolderForm.getChildById("exo:title");
            if(uiPropertyInputForm.getUICheckBoxInput("basisFolder.label.folderNumber_checkBox").getValue()){
                String[] parameter= new String[2];
                parameter[0] = uiPropertyInputForm.getUIFormSelectBox("basisFolder.label.folderNumber_searchType").getValue();
                parameter[1] = uiPropertyInputForm.getUIStringInput("basisFolder.label.folderNumber").getValue();
                mapFolder.put("exo:title", parameter);
            }


            PropertyDefinition[] basisNodetypeProperties = uiBasisFolderForm.getBasisFolderNodetypeProperties();
            for (PropertyDefinition propertyFolder : basisNodetypeProperties) {
                if(propertyFolder.getName().contains("basis")) {
                    if(!propertyFolder.getName().contains("folderLanguage") && !propertyFolder.getName().contains("folderComments")) {
                        if(propertyFolder.getRequiredType() != 5){
                            uiPropertyInputForm = uiBasisFolderForm.getChildById(FIELD_FOLDER+"_"+propertyFolder.getName());
                            if(uiPropertyInputForm.getUICheckBoxInput("basisFolder.label."+propertyFolder.getName().split("basis:")[1]+"_checkBox").getValue()){
                                String[] parameter= new String[2];
                                parameter[0] = uiPropertyInputForm.getUIFormSelectBox("basisFolder.label." + propertyFolder.getName().split("basis:")[1] + "_searchType").getValue();
                                parameter[1] = uiPropertyInputForm.getUIStringInput("basisFolder.label." + propertyFolder.getName().split("basis:")[1]).getValue();
                                mapFolder.put(propertyFolder.getName(), parameter);
                            }
                        }
                        else{
                            uiPropertyInputForm = uiBasisFolderForm.getChildById(FIELD_FOLDER+"_"+propertyFolder.getName());
                            if(uiPropertyInputForm.getUICheckBoxInput("basisFolder.label."+propertyFolder.getName().split("basis:")[1]+"_checkBox").getValue()){
                                String date = uiPropertyInputForm.getUIFormDateTimeInput("basisFolder.label." + propertyFolder.getName().split("basis:")[1]).getValue();
                                String [] dateSplitted = date.split("/");
                                String propertyDate=dateSplitted[2]+"-"+dateSplitted[1]+"-"+dateSplitted[0];

                                String[] parameter= new String[2];
                                parameter[0] = uiPropertyInputForm.getUIFormSelectBox("basisFolder.label." + propertyFolder.getName().split("basis:")[1]+"_searchType").getValue();
                                parameter[1] = propertyDate;

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
                            if(uiPropertyInputForm.getUICheckBoxInput("basisDocument.label."+propertyDoc.getName().split("basis:")[1]+"_checkBox").getValue()){
                                String[] parameter= new String[2];
                                parameter[0] = uiPropertyInputForm.getUIFormSelectBox("basisDocument.label." + propertyDoc.getName().split("basis:")[1] + "_searchType").getValue();
                                parameter[1] = uiPropertyInputForm.getUIStringInput("basisDocument.label." + propertyDoc.getName().split("basis:")[1]).getValue();
                                mapDoc.put(propertyDoc.getName(), parameter);
                            }
                        }
                        else{
                            uiPropertyInputForm = uiBasisDocForm.getChildById(FIELD_DOC+"_"+propertyDoc.getName());
                            if(uiPropertyInputForm.getUICheckBoxInput("basisDocument.label."+propertyDoc.getName().split("basis:")[1]+"_checkBox").getValue()){
                                String date = uiPropertyInputForm.getUIFormDateTimeInput("basisDocument.label." + propertyDoc.getName().split("basis:")[1]).getValue();
                                String [] dateSplitted = date.split("/");
                                String propertyDate=dateSplitted[2]+"-"+dateSplitted[1]+"-"+dateSplitted[0];

                                String[] parameter= new String[2];
                                parameter[0] = uiPropertyInputForm.getUIFormSelectBox("basisDocument.label." + propertyDoc.getName().split("basis:")[1]+"_searchType").getValue();
                                parameter[1] = propertyDate;

                                mapDoc.put(propertyDoc.getName(),parameter);
                            }
                        }
                    }
                }
            }

            uiPropertyInputForm = uiBasisDocForm.getChildById(FIELD_DOC+"_basis:docComments");
            if(uiPropertyInputForm.getUICheckBoxInput("basisDocument_basis.label.comments_checkBox").getValue()){
                String[] parameter= new String[2];
                parameter[0] = uiPropertyInputForm.getUIFormSelectBox("basisDocument_basis.label.comments_searchType").getValue();
                parameter[1] = uiPropertyInputForm.getUIStringInput("basisDocument_basis.label.comments").getValue();
                mapDoc.put("basis:docComments", parameter);
            }
            uiPropertyInputForm = uiBasisDocForm.getChildById("exo:title");
            if(uiPropertyInputForm.getUICheckBoxInput("basisDocument.label.docId_checkBox").getValue()){
                String[] parameter= new String[2];
                parameter[0] = uiPropertyInputForm.getUIFormSelectBox("basisDocument.label.docId_searchType").getValue();
                parameter[1] = uiPropertyInputForm.getUIStringInput("basisDocument.label.docId").getValue();
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
                            if(uiPropertyInputForm.getUICheckBoxInput("folder_basisFollow.label."+propertyFollowFolder.getName().split("basis:")[1]+"_checkBox").getValue()){
                                String[] parameter= new String[2];
                                parameter[0] = uiPropertyInputForm.getUIFormSelectBox("folder_basisFollow.label." + propertyFollowFolder.getName().split("basis:")[1] + "_searchType").getValue();
                                parameter[1] = uiPropertyInputForm.getUIStringInput("folder_basisFollow.label." + propertyFollowFolder.getName().split("basis:")[1]).getValue();
                                mapFollowFolder.put(propertyFollowFolder.getName(), parameter);
                            }
                        }
                        else{
                            uiPropertyInputForm = uiBasisFollowFolderForm.getChildById(FIELD_FOLLOW_FOLDER+"_"+propertyFollowFolder.getName());
                            if(uiPropertyInputForm.getUICheckBoxInput("folder_basisFollow.label."+propertyFollowFolder.getName().split("basis:")[1]+"_checkBox").getValue()){
                                String date = uiPropertyInputForm.getUIFormDateTimeInput("folder_basisFollow.label." + propertyFollowFolder.getName().split("basis:")[1]).getValue();
                                String [] dateSplitted = date.split("/");
                                String propertyDate=dateSplitted[2]+"-"+dateSplitted[1]+"-"+dateSplitted[0];

                                String[] parameter= new String[2];
                                parameter[0] = uiPropertyInputForm.getUIFormSelectBox("folder_basisFollow.label." + propertyFollowFolder.getName().split("basis:")[1]+"_searchType").getValue();
                                parameter[1] = propertyDate;

                                mapFollowFolder.put(propertyFollowFolder.getName(),parameter);
                            }
                        }
                    }
                }
            }

            uiPropertyInputForm = uiBasisFollowFolderForm.getChildById(FIELD_FOLLOW_FOLDER+"_basis:followComments");
            if(uiPropertyInputForm.getUICheckBoxInput("folder_basisFollow_basis.label.comments_checkBox").getValue()){
                String[] parameter= new String[2];
                parameter[0] = uiPropertyInputForm.getUIFormSelectBox("folder_basisFollow_basis.label.comments_searchType").getValue();
                parameter[1] = uiPropertyInputForm.getUIStringInput("folder_basisFollow_basis.label.comments").getValue();
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
                            if(uiPropertyInputForm.getUICheckBoxInput("document_basisFollow.label."+propertyFollowDoc.getName().split("basis:")[1]+"_checkBox").getValue()){
                                String[] parameter= new String[2];
                                parameter[0] = uiPropertyInputForm.getUIFormSelectBox("document_basisFollow.label." + propertyFollowDoc.getName().split("basis:")[1] + "_searchType").getValue();
                                parameter[1] = uiPropertyInputForm.getUIStringInput("document_basisFollow.label." + propertyFollowDoc.getName().split("basis:")[1]).getValue();
                                mapFollowDoc.put(propertyFollowDoc.getName(), parameter);
                            }
                        }
                        else{
                            uiPropertyInputForm = uiBasisFollowDocForm.getChildById(FIELD_FOLLOW_DOC+"_"+propertyFollowDoc.getName());
                            if(uiPropertyInputForm.getUICheckBoxInput("document_basisFollow.label."+propertyFollowDoc.getName().split("basis:")[1]+"_checkBox").getValue()){
                                String date = uiPropertyInputForm.getUIFormDateTimeInput("document_basisFollow.label." + propertyFollowDoc.getName().split("basis:")[1]).getValue();
                                String [] dateSplitted = date.split("/");
                                String propertyDate=dateSplitted[2]+"-"+dateSplitted[1]+"-"+dateSplitted[0];

                                String[] parameter= new String[2];
                                parameter[0] = uiPropertyInputForm.getUIFormSelectBox("document_basisFollow.label." + propertyFollowDoc.getName().split("basis:")[1]+"_searchType").getValue();
                                parameter[1] = propertyDate;

                                mapFollowDoc.put(propertyFollowDoc.getName(),parameter);
                            }
                        }
                    }
                }
            }

            uiPropertyInputForm = uiBasisFollowDocForm.getChildById(FIELD_FOLLOW_DOC+"_basis:followComments");
            if(uiPropertyInputForm.getUICheckBoxInput("document_basisFollow_basis.label.comments_checkBox").getValue()){
                String[] parameter= new String[2];
                parameter[0] = uiPropertyInputForm.getUIFormSelectBox("document_basisFollow_basis.label.comments_searchType").getValue();
                parameter[1] = uiPropertyInputForm.getUIStringInput("document_basisFollow_basis.label.comments").getValue();
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
                Query query = queryManager.createQuery(xPathStatement, Query.XPATH);
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

    public String firstChildRequest(Map<String,String[]> mapProperty, String xPathStatement){
        if(!mapProperty.isEmpty()){
            for (String mapKey : mapProperty.keySet()) {
                String[] value = mapProperty.get(mapKey);

                if(value[0].equals("Equals")){
                    if(!mapKey.contains("Date")){
                        if(i == 0){
                            xPathStatement += "@"+mapKey+"='"+value[1]+"'";
                        }
                        else{
                            xPathStatement += " and @"+mapKey+"='"+value[1]+"'";
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
                    if(!mapKey.contains("Date")){
                        if(i == 0){
                            xPathStatement += "jcr:like(@"+mapKey+",'%"+value[1]+"%')";
                        }
                        else{
                            xPathStatement += " and jcr:like(@"+mapKey+",'%"+value[1]+"%')";
                        }
                    }
                    else{
                        xPathStatement += "";
                    }
                }
                else if(value[0].equals("Not_Equals")){
                    if(!mapKey.contains("Date")){
                        if(i == 0){
                            xPathStatement += "not(@"+mapKey+"='"+value[1]+"')";
                        }
                        else{
                            xPathStatement += " and not(@"+mapKey+"='"+value[1]+"')";
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
                    if(!mapKey.contains("Date")){
                        if(i == 0){
                            xPathStatement += "not(jcr:like(@"+mapKey+",'%"+value[1]+"%'))";
                        }
                        else{
                            xPathStatement += " and not(jcr:like(@"+mapKey+",'%"+value[1]+"%'))";
                        }
                    }
                    else{
                        xPathStatement += "";
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
                        if(i == 0){
                            xPathStatement += "*/@"+mapKey+"='"+value[1]+"'";
                        }
                        else{
                            xPathStatement += " and */@"+mapKey+"='"+value[1]+"'";
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
                    if(!mapKey.contains("Date")){
                        if(i == 0){
                            xPathStatement += "jcr:like(*/@"+mapKey+",'%"+value[1]+"%')";
                        }
                        else{
                            xPathStatement += " and jcr:like(*/@"+mapKey+",'%"+value[1]+"%')";
                        }
                    }
                    else{
                        xPathStatement += "";
                    }
                }
                else if(value[0].equals("Not_Equals")){
                    if(!mapKey.contains("Date")){
                        if(i == 0){
                            xPathStatement += "not(*/@"+mapKey+"='"+value[1]+"')";
                        }
                        else{
                            xPathStatement += " and not(*/@"+mapKey+"='"+value[1]+"')";
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
                    if(!mapKey.contains("Date")){
                        if(i == 0){
                            xPathStatement += "not(jcr:like(*/@"+mapKey+",'%"+value[1]+"%'))";
                        }
                        else{
                            xPathStatement += " and not(jcr:like(*/@"+mapKey+",'%"+value[1]+"%'))";
                        }
                    }
                    else{
                        xPathStatement += "";
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
                        if(i == 0){
                            xPathStatement += "*/*/@"+mapKey+"='"+value[1]+"'";
                        }
                        else{
                            xPathStatement += " and */*/@"+mapKey+"='"+value[1]+"'";
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
                    if(!mapKey.contains("Date")){
                        if(i == 0){
                            xPathStatement += "jcr:like(*/*/@"+mapKey+",'%"+value[1]+"%')";
                        }
                        else{
                            xPathStatement += " and jcr:like(*/*/@"+mapKey+",'%"+value[1]+"%')";
                        }
                    }
                    else{
                        xPathStatement += "";
                    }
                }
                else if(value[0].equals("Not_Equals")){
                    if(!mapKey.contains("Date")){
                        if(i == 0){
                            xPathStatement += "not(*/*/@"+mapKey+"='"+value[1]+"')";
                        }
                        else{
                            xPathStatement += " and not(*/*/@"+mapKey+"='"+value[1]+"')";
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
                    if(!mapKey.contains("Date")){
                        if(i == 0){
                            xPathStatement += "not(jcr:like(*/*/@"+mapKey+",'%"+value[1]+"%'))";
                        }
                        else{
                            xPathStatement += " and not(jcr:like(*/*/@"+mapKey+",'%"+value[1]+"%'))";
                        }
                    }
                    else{
                        xPathStatement += "";
                    }
                }

                i++;

            }
        }
        return xPathStatement;
    }
}



