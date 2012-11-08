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


public class RegularisationYearInterceptor implements CmsScript {


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

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (session != null) {
                session.logout();
            }
        }
    }

    public void setParams(String[] params) {
    }
}