package searchBasis.portlet.component;

import basis.selector.service.BasisSelectorService;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.organization.MembershipHandler;
import org.exoplatform.services.organization.MembershipTypeHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.UserProfile;
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

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
                @EventConfig(listeners = UISimpleSearchForm.ChangeActionListener.class, phase= Event.Phase.DECODE),
                @EventConfig(listeners = UISimpleSearchForm.ChangeDateActionListener.class, phase= Event.Phase.DECODE)
        }

)
public class UISimpleSearchForm extends UIForm {

    private  static final String QUERY = "Query";
    private  static final String LANGUAGE = "Language";
    private  static final String NUMBERRESULT = "NumberResult";
    private  static final String ATTRIBUT = "Attribut";
    private  static final String LSDATE = "ListDate";
    private  static final String DATE = "Date";
    private  static final String DATE_SECOND = "Date_second";
    private  static final String statusLanguageFR = "and not(jcr:like(@basis:folderStatus, 'clôturé')) and not(jcr:like(@basis:folderStatus, 'annulé'))";
    private  static final String statusLanguageEN = "and not(jcr:like(@basis:folderStatus, 'closed')) and not(jcr:like(@basis:folderStatus, 'cancelled'))";
    private  static final String statusLanguageNL = "and not(jcr:like(@basis:folderStatus, 'afgesloten')) and not(jcr:like(@basis:folderStatus, 'geannulleerd'))";

    public UISimpleSearchForm() throws Exception {

        List<SelectItemOption<String>> lsLanguage = new ArrayList<SelectItemOption<String>>() ;
        lsLanguage.add(new SelectItemOption<String>("NL", "NL")) ;
        lsLanguage.add(new SelectItemOption<String>("FR", "FR")) ;
        lsLanguage.add(new SelectItemOption<String>("EN", "EN")) ;
        UIFormSelectBox uiFormSelectBoxLanguage = new UIFormSelectBox(LANGUAGE,LANGUAGE,lsLanguage);
        uiFormSelectBoxLanguage.setOnChange("Change");
        addUIFormInput(uiFormSelectBoxLanguage);

        List<SelectItemOption<String>> lsNumber = new ArrayList<SelectItemOption<String>>() ;
        lsNumber.add(new SelectItemOption<String>("10", "10")) ;
        lsNumber.add(new SelectItemOption<String>("20", "20")) ;
        lsNumber.add(new SelectItemOption<String>("50", "50")) ;
        UIFormSelectBox uiFormSelectBoxNumber = new UIFormSelectBox(NUMBERRESULT,NUMBERRESULT,lsNumber);
        uiFormSelectBoxNumber.setValue("20");
        addUIFormInput(uiFormSelectBoxNumber);

        UIFormSelectBox uiFormSelectBoxQuery = new UIFormSelectBox(QUERY, QUERY, null) ;
        uiFormSelectBoxQuery.setOnChange("Change");
        addUIFormInput(uiFormSelectBoxQuery);

        List<SelectItemOption<String>> lsDate = new ArrayList<SelectItemOption<String>>() ;
        UIFormSelectBox uiFormSelectBoxDate = new UIFormSelectBox(LSDATE,LSDATE,lsDate);
        uiFormSelectBoxDate.setOnChange("ChangeDate");
        uiFormSelectBoxDate.setRendered(false);
        addUIFormInput(uiFormSelectBoxDate);

        /*UIFormDateTimeInput uiFormDateTimeInput = new UIFormDateTimeInput(DATE, null, new Date(), false);
        uiFormDateTimeInput.setRendered(false);
        addUIFormInput(uiFormDateTimeInput);

        UIFormDateTimeInput uiFormDateTimeInput2 = new UIFormDateTimeInput(DATE_SECOND, null, new Date(), false);
        uiFormDateTimeInput2.setRendered(false);
        addUIFormInput(uiFormDateTimeInput2); */

        setActions(new String[]{"Search", "Cancel"}) ;

    }

    public String queryCreated(String user, String date1, String date2, String nameBO, String lsDateOperator){
        String xpathStatement = "";
        String languagePortal =  Util.getPortalRequestContext().getLocale().getDisplayName();
        if(lsDateOperator.equals("At")){
            String day = date1.substring(0,2);
            String month = date1.substring(3,5);
            String year = date1.substring(6,10);
            String propertyDate;
            if(languagePortal.equals("French")){
                propertyDate=year+"-"+month+"-"+day;
                int day2 = Integer.parseInt(day)+1;
                String propertyDate2 = year+"-"+month+"-"+day2;
                xpathStatement = "/jcr:root/Files/BO/"+nameBO+"//element (*,basis:basisFolder) [jcr:like(fn:lower-case(@exo:lastModifier),'"+user+"') and @exo:dateCreated>=xs:dateTime('"+propertyDate+"') and @exo:dateCreated<xs:dateTime('"+propertyDate2+"') "+statusLanguageFR+"] order by @exo:name ascending";
            }
            else if(languagePortal.equals("English")){
                propertyDate=year+"-"+day+"-"+month;
                int day2 = Integer.parseInt(day)+1;
                String propertyDate2 = year+"-"+month+"-"+day2;
                xpathStatement = "/jcr:root/Files/BO/"+nameBO+"//element (*,basis:basisFolder) [jcr:like(fn:lower-case(@exo:lastModifier),'"+user+"') and @exo:dateCreated>=xs:dateTime('"+propertyDate+"') and @exo:dateCreated<xs:dateTime('"+propertyDate2+"') "+statusLanguageEN+"] order by @exo:name ascending";
            }
            else if(languagePortal.equals("Nederlands")) {
                propertyDate=year+"-"+month+"-"+day;
                int day2 = Integer.parseInt(day)+1;
                String propertyDate2 = year+"-"+month+"-"+day2;
                xpathStatement = "/jcr:root/Files/BO/"+nameBO+"//element (*,basis:basisFolder) [jcr:like(fn:lower-case(@exo:lastModifier),'"+user+"') and @exo:dateCreated>=xs:dateTime('"+propertyDate+"') and @exo:dateCreated<xs:dateTime('"+propertyDate2+"') "+statusLanguageNL+"] order by @exo:name ascending";
            }

        }
        else if(lsDateOperator.equals("Between")){
            String day1 = date1.substring(0,2);
            String month1 = date1.substring(3,5);
            String year1 = date1.substring(6,10);
            String propertyDate1;
            String dayTemp = date2.substring(0,2);
            String month2 = date2.substring(3,5);
            String year2 = date2.substring(6,10);
            int day2 = Integer.parseInt(dayTemp)+1;
            String propertyDate2;
            if(languagePortal.equals("French")){
                propertyDate1=year1+"-"+month1+"-"+day1;
                propertyDate2=year2+"-"+month2+"-"+day2;
                xpathStatement = "/jcr:root/Files/BO/"+nameBO+"//element (*,basis:basisFolder) [jcr:like(fn:lower-case(@exo:lastModifier),'"+user+"') and @exo:dateCreated>=xs:dateTime('"+propertyDate1+"') and @exo:dateCreated<xs:dateTime('"+propertyDate2+"') "+statusLanguageFR+"] order by @exo:name ascending";
            }
            else if(languagePortal.equals("English")){
                propertyDate1=year1+"-"+day1+"-"+month1;
                propertyDate2=year2+"-"+day2+"-"+day2;
                xpathStatement = "/jcr:root/Files/BO/"+nameBO+"//element (*,basis:basisFolder) [jcr:like(fn:lower-case(@exo:lastModifier),'"+user+"') and @exo:dateCreated>=xs:dateTime('"+propertyDate1+"') and @exo:dateCreated<xs:dateTime('"+propertyDate2+"') "+statusLanguageEN+"] order by @exo:name ascending";
            }
            else if(languagePortal.equals("Nederlands")) {
                propertyDate1=year1+"-"+month1+"-"+day1;
                propertyDate2=year2+"-"+month2+"-"+day2;
                xpathStatement = "/jcr:root/Files/BO/"+nameBO+"//element (*,basis:basisFolder) [jcr:like(fn:lower-case(@exo:lastModifier),'"+user+"') and @exo:dateCreated>=xs:dateTime('"+propertyDate1+"') and @exo:dateCreated<xs:dateTime('"+propertyDate2+"') "+statusLanguageNL+"] order by @exo:name ascending";
            }
        }
        return xpathStatement;
    }

    static public class SearchActionListener extends EventListener<UISimpleSearchForm> {
        public void execute(Event<UISimpleSearchForm> event) throws Exception {
            UISimpleSearchForm uiSimpleSearchForm = event.getSource();
            String xpathStatement = "";

            String url = Util.getPortalRequestContext().getRequestURI();
            String urlSplitted[] = url.split("BO:");
            String nameBO[] = urlSplitted[1].split("/");

            String language = uiSimpleSearchForm.getUIFormSelectBox(LANGUAGE).getValue();


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
                    }
                    else
                        return;
                }
                else if(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue().equals("contains")){
                    String contains = uiSimpleSearchForm.getUIStringInput(ATTRIBUT).getValue().toLowerCase();
                    System.out.println(contains);
                    uiSearchBasisPortlet.setTypeQuery(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue());

                    if(!contains.equals(null)){
                        if(language.equals("FR")){
                            xpathStatement = "/jcr:root/Files/BO/"+nameBO[0]+"//element (*,basis:basisFolder) [jcr:like(fn:lower-case(@exo:title),'%"+contains+"%') "+statusLanguageFR+"] order by @exo:name ascending";
                        }
                        else if(language.equals("EN")){
                            xpathStatement = "/jcr:root/Files/BO/"+nameBO[0]+"//element (*,basis:basisFolder) [jcr:like(fn:lower-case(@exo:title),'%"+contains+"%') "+statusLanguageEN+"] order by @exo:name ascending";
                        }
                        else if(language.equals("NL")) {
                            xpathStatement = "/jcr:root/Files/BO/"+nameBO[0]+"//element (*,basis:basisFolder) [jcr:like(fn:lower-case(@exo:title),'%"+contains+"%') "+statusLanguageNL+"] order by @exo:name ascending";
                        }
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
                    }
                    else
                        return;
                }

                else if(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue().equals("createdBy")){
                    uiSearchBasisPortlet.setTypeQuery(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue());

                    if(uiSimpleSearchForm.getUIFormSelectBox(LSDATE).getValue().equals("At")){
                        xpathStatement = uiSimpleSearchForm.queryCreated(Util.getPortalRequestContext().getRemoteUser().toLowerCase(), uiSimpleSearchForm.getUIFormDateTimeInput(DATE).getValue().toString(), "",nameBO[0], uiSimpleSearchForm.getUIFormSelectBox(LSDATE).getValue() );
                    }
                    else if(uiSimpleSearchForm.getUIFormSelectBox(LSDATE).getValue().equals("Between")){
                        xpathStatement = uiSimpleSearchForm.queryCreated(Util.getPortalRequestContext().getRemoteUser().toLowerCase(), uiSimpleSearchForm.getUIFormDateTimeInput(DATE).getValue().toString(), uiSimpleSearchForm.getUIFormDateTimeInput(DATE_SECOND).getValue().toString(),nameBO[0], uiSimpleSearchForm.getUIFormSelectBox(LSDATE).getValue());
                    }
                }

                else if(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue().equals("createdByOther")){
                    String user = uiSimpleSearchForm.getUIStringInput(ATTRIBUT).getValue().toLowerCase();
                    uiSearchBasisPortlet.setTypeQuery(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue());

                    if(!user.equals(null)){
                        if(uiSimpleSearchForm.getUIFormSelectBox(LSDATE).getValue().equals("At")){
                            xpathStatement = uiSimpleSearchForm.queryCreated(user, uiSimpleSearchForm.getUIFormDateTimeInput(DATE).getValue().toString(), "",nameBO[0], uiSimpleSearchForm.getUIFormSelectBox(LSDATE).getValue() );
                        }
                        else if(uiSimpleSearchForm.getUIFormSelectBox(LSDATE).getValue().equals("Between")){
                            xpathStatement = uiSimpleSearchForm.queryCreated(user, uiSimpleSearchForm.getUIFormDateTimeInput(DATE).getValue().toString(), uiSimpleSearchForm.getUIFormDateTimeInput(DATE_SECOND).getValue().toString(),nameBO[0], uiSimpleSearchForm.getUIFormSelectBox(LSDATE).getValue());
                        }
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
            uiSimpleSearchForm.removeChildById(DATE);
            uiSimpleSearchForm.removeChildById(DATE_SECOND);
            UIFormSelectBox uiFormSelectBoxDate = uiSimpleSearchForm.getChildById(LSDATE);
            uiFormSelectBoxDate.setRendered(false);

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
            else if(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue().equals("byUser") || uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue().equals("contains")){
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
            else if(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue().equals("createdBy")){
                uiFormSelectBoxDate.setRendered(true);
                uiSimpleSearchForm.addUIFormInput(new UIFormDateTimeInput(DATE, null, new Date(), false));

                /*if(uiSimpleSearchForm.getUIFormSelectBox(LSDATE).getValue().equals("Between")){
                    uiSimpleSearchForm.addUIFormInput(new UIFormDateTimeInput(DATE_SECOND, null, new Date(), false));
                }*/
            }
            else if(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue().equals("createdByOther")){
                uiSimpleSearchForm.addUIFormInput(new UIFormStringInput(ATTRIBUT, ATTRIBUT, null));
                uiFormSelectBoxDate.setRendered(true);
                uiSimpleSearchForm.addUIFormInput(new UIFormDateTimeInput(DATE, null, new Date(), false));
                uiFormSelectBoxDate.setDefaultValue("At");
                /*if(uiSimpleSearchForm.getUIFormSelectBox(LSDATE).getValue().equals("Between")){
                    uiSimpleSearchForm.addUIFormInput(new UIFormDateTimeInput(DATE_SECOND, null, new Date(), false));
                } */
            }
            uiSimpleSearchForm.getUIFormSelectBox(LSDATE).setDefaultValue(uiSimpleSearchForm.getUIFormSelectBox(LSDATE).getValue());
            uiSimpleSearchForm.getUIFormSelectBox(QUERY).setDefaultValue(uiSimpleSearchForm.getUIFormSelectBox(QUERY).getValue());

            event.getRequestContext().addUIComponentToUpdateByAjax(uiSimpleSearchForm) ;
        }
    }

    static public class ChangeDateActionListener extends EventListener<UISimpleSearchForm> {
        public void execute(Event<UISimpleSearchForm> event) throws Exception {
            UISimpleSearchForm uiSimpleSearchForm = event.getSource() ;

            uiSimpleSearchForm.removeChildById(DATE);
            uiSimpleSearchForm.removeChildById(DATE_SECOND);

            if(uiSimpleSearchForm.getUIFormSelectBox(LSDATE).getValue().equals("At")){
                uiSimpleSearchForm.addUIFormInput(new UIFormDateTimeInput(DATE, null, new Date(), false));
            }
            else if(uiSimpleSearchForm.getUIFormSelectBox(LSDATE).getValue().equals("Between")){
                uiSimpleSearchForm.addUIFormInput(new UIFormDateTimeInput(DATE, null, new Date(), false));
                uiSimpleSearchForm.addUIFormInput(new UIFormDateTimeInput(DATE_SECOND, null, new Date(), false));
            }
            uiSimpleSearchForm.getUIFormSelectBox(LSDATE).setDefaultValue(uiSimpleSearchForm.getUIFormSelectBox(LSDATE).getValue());
            event.getRequestContext().addUIComponentToUpdateByAjax(uiSimpleSearchForm) ;
        }
    }
}
