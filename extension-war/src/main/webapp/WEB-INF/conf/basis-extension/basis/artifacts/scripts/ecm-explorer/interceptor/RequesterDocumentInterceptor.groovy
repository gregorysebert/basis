package be.bull.exo.scripts;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.jcr.*;
import javax.jcr.Session;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import org.exoplatform.services.cms.scripts.CmsScript;
import org.exoplatform.services.cms.records.RecordsService;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.apache.commons.logging.Log;
import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.contact.service.AddressBook;
import org.exoplatform.contact.service.Contact;
import org.exoplatform.contact.service.ContactService;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserProfile;

public class RequesterDocumentInterceptor implements CmsScript {

    //private String REST_ADDRESS = "/rest/jcr/repository/collaboration/Files/Ruling/Regularisation/conf/img";

    private String WORKSPACE = "dev-monit";
    private String REST_ADDRESS = "/rest/jcr/repository/"+WORKSPACE;

    /** NT_RESOURCE */
    private static final String NT_RESOURCE = "nt:resource";

    /** JCR_CONTENT */
    private static final String JCR_CONTENT = "jcr:content";

    /** NT_FILE */
    private static final String NT_FILE = "nt:file";

    /** JCR_DATA */
    private static final String JCR_DATA = "jcr:data";

    /** JCR_LAST_MODIFIED */
    private static final String JCR_LAST_MODIFIED = "jcr:lastModified";

    /** JCR_ENCODING */
    private static final String JCR_ENCODING = "jcr:encoding";

    /** JCR_MIME_TYPE */
    private static final String JCR_MIME_TYPE = "jcr:mimeType";

    private Map mapPara = new HashMap();
    private RepositoryService repositoryService_;
    private OrganizationService organizationService_;
    Node folderNode;
    Node currentNode;

    public RequesterDocumentInterceptor(RepositoryService repositoryService,
                                        OrganizationService organizationService) {
        repositoryService_ = repositoryService;
        organizationService_ = organizationService;
    }

    public void execute(Object context) {
        String path = (String) context;
        Session session = null;
        try {
            String[] splittedPath = path.split("&workspaceName=");
            String[] splittedContent = splittedPath[1].split("&repository=");
            session = repositoryService_.getRepository(splittedContent[1])
                    .getSystemSession(splittedContent[0]);
            currentNode = (Node) session.getItem(splittedPath[0]);
            folderNode = currentNode.getParent();
            Node temp;
            if (folderNode.hasNode("Generated documents")) {
                temp = folderNode.getNode("Generated documents");
                session.save();
                InputStream myjasperstream;
                String language = folderNode.getProperty("spff:language").getString();
                if (language.equals("FR")) {
                    myjasperstream = getReportTemplate(WORKSPACE, splittedContent[1], "/conf/JasperTemplate/DOC-R-03-fr.jasper");
                } else if (language.equals("NL")) {
                    myjasperstream = getReportTemplate(WORKSPACE, splittedContent[1], "/conf/JasperTemplate/DOC-R-03-nl.jasper");
                }
                makeReport(temp, myjasperstream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }


    private void makeReport(Node temp, InputStream myjasperstream)
    throws RepositoryException, ValueFormatException,
            PathNotFoundException {
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        dateFormat.applyPattern(pattern);
        String letterContent = "";
        String introducerName = "";
        String introducerAddress = "";
        String requestDocumentReference = "";
        String inputDocDate = "";
        String folderNumber = "";
        String teamFolderLeader = "";
        String teamFolderLeaderemail = "";
        String teamFolderLeaderphone = "";
        String teamFolderLeaderfaxnumber = "";
        //letter content
        if (currentNode.hasProperty("spff:letterContent")) {
            letterContent = currentNode.getProperty("spff:letterContent").getString();
        } else {
            letterContent = " ";
        }
        // requesterName
        if (folderNode.hasProperty("spff:requesterName")) {
            introducerName = folderNode.getProperty("spff:requesterTitle").getString() + " " + folderNode.getProperty("spff:requesterName").getString();
        } else {
            introducerName = " ";
        }
        // requestDocumentReference
        if (folderNode.hasProperty("spff:requestDocumentReference")) {
            requestDocumentReference = folderNode.getProperty("spff:requestDocumentReference").getString();
        } else {
            requestDocumentReference = " ";
        }
        // introducerAddress
        if (folderNode.hasProperty("spff:requesterAddress")) {
            introducerAddress = (folderNode.getProperty("spff:requesterAddress").getString() + "<br>" + folderNode.getProperty("spff:requesterPostCode").getString() + " " + folderNode.getProperty("spff:requesterCity").getString() + " " + folderNode.getProperty("spff:requesterCountry").getString());
        } else {
            introducerAddress = " ";
        }
        // inputDocDate
        if (folderNode.hasProperty("spff:inputDocDate")) {
            inputDocDate = dateFormat.format(folderNode.getProperty("spff:inputDocDate").getDate().getTime());

        } else {
            inputDocDate = " ";
        }
        // folderNumber
        folderNumber = folderNode.getName();

        String leaderUserName = "";
        // teamFolderLeader
        if (folderNode.hasProperty("spff:teamFolderLeader")) {
            leaderUserName = folderNode.getProperty("spff:teamFolderLeader").getString();
            User leader = organizationService_.getUserHandler().findUserByName(leaderUserName);
            teamFolderLeader = leader.getFullName();
            //teamFolderLeaderemail=leader.getEmail();

        } else {
            teamFolderLeader = " ";
        }

        try {
            UserProfile profile = organizationService_.getUserProfileHandler().findUserProfileByName(leaderUserName);

            if (profile.getUserInfoMap() != null) {
                teamFolderLeaderphone = profile.getAttribute("user.business-info.telecom.telephone.number");
                teamFolderLeaderfaxnumber = profile.getAttribute("user.home-info.telecom.mobile.number");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ContactService service = (ContactService) PortalContainer.getComponent(ContactService.class);
        Contact contact;

        List VerifList1 = service.getPersonalContactsByAddressBook(leaderUserName, "default" + leaderUserName).currentPage(leaderUserName);
        for (Iterator iterator = VerifList1.iterator(); iterator.hasNext();) {
            Contact object = (Contact) iterator.next();

            /*if(object.getWorkPhone1() != null)
               {
                   teamFolderLeaderphone= object.getWorkPhone1();
               }
               else{
                   System.out.println("object.getWorkPhone1() == null");
                   teamFolderLeaderphone= "";
               }
               if(object.getWorkFax() != null){
                   teamFolderLeaderfaxnumber=object.getWorkFax();
               }else{
                   teamFolderLeaderfaxnumber= "";
               }*/
            if (object.getEmailAddress() != null) {
                teamFolderLeaderemail = object.getEmailAddress();
            } else {
                teamFolderLeaderemail = "";
            }
        }
        mapPara.put("letterContent", letterContent);
        mapPara.put("requesterName", introducerName);
        mapPara.put("requestDocumentReference", requestDocumentReference);
        mapPara.put("requesterAddress", introducerAddress);
        mapPara.put("inputDocDate", inputDocDate);
        mapPara.put("folderNumber", folderNumber);
        mapPara.put("teamFolderLeader", teamFolderLeader);
        mapPara.put("teamFolderLeaderemail", teamFolderLeaderemail);
        mapPara.put("teamFolderLeaderphone", teamFolderLeaderphone);
        mapPara.put("teamFolderLeaderfaxnumber", teamFolderLeaderfaxnumber);
        mapPara.put("logo", getServerBaseUrl()+ REST_ADDRESS + "/conf/img");
        mapPara.put("be", getServerBaseUrl()+ REST_ADDRESS + "/conf/img");
        mapPara.put("signature", getServerBaseUrl()+ REST_ADDRESS + "/conf/img");
        mapPara.put(JRPdfExporterParameter.FORCE_LINEBREAK_POLICY, true);

        createReport(temp, currentNode.getName() + "-DOC-R-03.pdf", myjasperstream, mapPara);
    }

    public String getServerBaseUrl() {
        PortletRequestContext portletRequestContext = PortletRequestContext.getCurrentInstance();
        String url = portletRequestContext.getRequest().getScheme() + "://" +
                portletRequestContext.getRequest().getServerName() + ":";
        url += (portletRequestContext.getRequest().getServerPort());
        System.out.println(url);
        return url;
    }

    private InputStream getReportTemplate(String workSpace, String repositoryName, String pathName)
    throws Exception {
        Session session = null;
        session = repositoryService_.getRepository(repositoryName).getSystemSession(workSpace);
        System.out.println("ok session");
        Node documentNode = (Node) session.getItem(pathName);
        System.out.println("ok node");
        if (documentNode != null && NT_FILE.equals(documentNode.getPrimaryNodeType().getName())) {
            Node node4 = documentNode.getNode(JCR_CONTENT);
            System.out.println("ok node" + JCR_CONTENT);
            if (node4 != null && NT_RESOURCE.equals(node4.getPrimaryNodeType().getName())) {
                System.out.println("ok node" + JCR_DATA + "length" + node4.getProperty(JCR_DATA).getLength());
                return node4.getProperty(JCR_DATA).getStream();
            }
        }
        return null;
    }

    private void createReport(Node node, String reportName, InputStream sourceTemplate, Map map) {
        try {
            //JasperCompileManager.compileReportToFile("../groovy/reports/GroovyReport.jrxml","GroovyReport.jasper");
            JasperPrint jasperPrint = JasperFillManager.fillReport(sourceTemplate, map, new JREmptyDataSource());
            //JasperExportManager.exportReportToPdfFile("GroovyReport.jrprint");
            //byte[] temp = JasperExportManager.exportReportToPdf(jasperPrint);


            Node fileNode;
            if (node.hasNode(reportName)) {
                //fileNode = node.getNode(reportName);
                System.out.println("NodeName: " + node.getName() + "has Node: " + reportName);
            } else {
                System.out.println("NodeName: " + node.getName() + " reportName: " + reportName);
                fileNode = node.addNode(reportName, NT_FILE);
                Node resNode = fileNode.addNode(JCR_CONTENT, NT_RESOURCE);
                resNode.setProperty(JCR_MIME_TYPE, "application/pdf");
                resNode.setProperty(JCR_ENCODING, "");
                resNode.setProperty(JCR_DATA, new ByteArrayInputStream(JasperExportManager.exportReportToPdf(jasperPrint)));
                Calendar lastModified = Calendar.getInstance();
                resNode.setProperty(JCR_LAST_MODIFIED, lastModified);
                fileNode.getSession().save();
                resNode.getSession().save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setParams(String[] params) {
    }
}