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
import sun.security.krb5.internal.util.KerberosString;

import javax.jcr.Session;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.text.SimpleDateFormat;
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
                @EventConfig(listeners = UIAdvancedSearchForm.CancelActionListener.class)
        }
)
public class UIAdvancedSearchForm extends UIForm  {
    public static final String FIELD_DOC = "Document" ;
    public static final String FIELD_FOLDER = "Folder" ;
    public static final String FIELD_FOLLOW_DOC = "Follow document" ;
    public static final String FIELD_FOLLOW_FOLDER = "Follow folder" ;
    public static final String FIELD_FROM = "From" ;

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
        lsFrom.add(new SelectItemOption<String>("Folder", "Folder")) ;
        lsFrom.add(new SelectItemOption<String>("Document", "Document")) ;
        lsFrom.add(new SelectItemOption<String>("Follow", "Follow")) ;
        UIFormSelectBox uiSelectBoxFrom = new UIFormSelectBox(FIELD_FROM, FIELD_FROM, lsFrom) ;
        addChild(uiSelectBoxFrom);



        setActions(new String[]{"Search", "Cancel"}) ;
    }

    static public class SearchActionListener extends EventListener<UIAdvancedSearchForm> {
        public void execute(Event<UIAdvancedSearchForm> event) throws Exception {
            UIAdvancedSearchForm uiAdvancedSearchForm = event.getSource();
            UISearchBasisPortlet uiSearchBasisPortlet = uiAdvancedSearchForm.getAncestorOfType(UISearchBasisPortlet.class);
            Map<String,String[]> mapFolder = new HashMap<String,String[]>();

            String from = uiAdvancedSearchForm.getUIFormSelectBox(FIELD_FROM).getValue();
            String xPathStatement = "";


            UIBasisFolderForm uiBasisFolderForm = uiAdvancedSearchForm.getChildById(FIELD_FOLDER);
            PropertyDefinition[] basisFolderNodetypeProperties = uiBasisFolderForm.getBasisFolderNodetypeProperties();
            for (PropertyDefinition property : basisFolderNodetypeProperties) {
                if(property.getName().contains("basis")) {
                    if(!property.getName().contains("folderLanguage") && !property.getName().contains("folderComments")) {
                        if(property.getRequiredType() != 5){
                            UIPropertyInputForm uiPropertyInputForm = uiBasisFolderForm.getChildById(FIELD_FOLDER+"_"+property.getName());
                            if(uiPropertyInputForm.getUICheckBoxInput("basisFolder.label."+property.getName().split("basis:")[1]+"_checkBox").getValue()){
                                String[] parameter= new String[2];
                                parameter[0] = uiPropertyInputForm.getUIFormSelectBox("basisFolder.label." + property.getName().split("basis:")[1] + "_searchType").getValue();
                                parameter[1] = uiPropertyInputForm.getUIStringInput("basisFolder.label." + property.getName().split("basis:")[1]).getValue();
                                mapFolder.put(property.getName(), parameter);
                            }
                        }
                        else{
                            UIPropertyInputForm uiPropertyInputForm = uiBasisFolderForm.getChildById(FIELD_FOLDER+"_"+property.getName());
                            if(uiPropertyInputForm.getUICheckBoxInput("basisFolder.label."+property.getName().split("basis:")[1]+"_checkBox").getValue()){
                                String date = uiPropertyInputForm.getUIFormDateTimeInput("basisFolder.label." + property.getName().split("basis:")[1]).getValue();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String propertyDate=sdf.format(new Date(date));

                                String[] parameter= new String[2];
                                parameter[0] = uiPropertyInputForm.getUIFormSelectBox("basisFolder.label." + property.getName().split("basis:")[1]+"_searchType").getValue();
                                parameter[1] = propertyDate;

                                mapFolder.put(property.getName(),parameter);
                            }
                        }
                    }
                }
            }
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

            if(from.equals("Folder")){
                String url = Util.getPortalRequestContext().getRequestURI();
                String urlSplitted[] = url.split("BO:");
                String nameBO[] = urlSplitted[1].split("/");
                xPathStatement = "/jcr:root/Files/BO/"+nameBO[0]+"//element (*,basis:basisFolder) [";
            }

            if(!mapFolder.isEmpty()){
                int i = 0 ;
                for (String mapKey : mapFolder.keySet()) {
                    String[] value = mapFolder.get(mapKey);
                    //System.out.println("value 0 : " + value[0]+ "value 1 : "  +value[1] + " key : " + mapKey);

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
                        if(i == 0){
                            xPathStatement += "jcr:contains(@"+mapKey+"="+value[1]+")";
                        }
                        else{
                            xPathStatement += " and jcr:contains(@"+mapKey+"="+value[1]+")";
                        }

                    }
                    else if(value[0].equals("Not_Equals")){
                        if(!mapKey.contains("Date")){
                            if(i == 0){
                                xPathStatement += "not(@"+mapKey+"="+value[1]+")";
                            }
                            else{
                                xPathStatement += " and not(@"+mapKey+"="+value[1]+")";
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
                        if(i == 0){
                            xPathStatement += "not(jcr:contains(@"+mapKey+"="+value[1]+"))";
                        }
                        else{
                            xPathStatement += " and not(jcr:contains(@"+mapKey+"="+value[1]+"))";
                        }
                    }

                     i++;

                }
                xPathStatement += "]";
            }

            System.out.println("xpathstatement : " + xPathStatement);

            if(xPathStatement != null){
                ExoContainer exoContainer = ExoContainerContext.getCurrentContainer();
                RepositoryService rs = (RepositoryService) exoContainer.getComponentInstanceOfType(RepositoryService.class);
                Session session = (Session) rs.getRepository("repository").getSystemSession("collaboration");
                QueryManager queryManager = null;
                queryManager = session.getWorkspace().getQueryManager();
                Query query = queryManager.createQuery(xPathStatement, Query.XPATH);
                QueryResult result = query.execute();

                uiSearchBasisPortlet.setQueryResult(result);
                uiSearchBasisPortlet.updateResultAdvanced();
                //uiSimpleSearchForm.getUIFormSelectBox(QUERY).setDefaultValue("currentUser");
                uiAdvancedSearchForm.setRendered(false);

            }

            event.getRequestContext().addUIComponentToUpdateByAjax(uiAdvancedSearchForm) ;
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


}



