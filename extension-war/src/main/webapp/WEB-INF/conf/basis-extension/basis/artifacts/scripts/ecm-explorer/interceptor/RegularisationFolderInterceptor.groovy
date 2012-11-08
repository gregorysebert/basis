package be.bull.exo.scripts;

import javax.jcr.Session;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.cms.scripts.CmsScript;

public class RegularisationFolderInterceptor implements CmsScript {

    private RepositoryService repositoryService_;
    private OrganizationService organizationService_;
    Node folderNode;

    public RegularisationFolderInterceptor(RepositoryService repositoryService,
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
            //folderNode = (Node) session.getItem(splittedPath[0]);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }

    public void setParams(String[] params) {
    }
}