import java.util.logging.Logger;
import org.exoplatform.services.cms.scripts.CmsScript;
import javax.jcr.Node;
import javax.jcr.Session;

import org.exoplatform.services.jcr.RepositoryService;

public class GenerateVersionInterceptor implements CmsScript {
	private static Logger logger = Logger.getLogger("RenameFollowInterceptor");
	private RepositoryService repositoryService_;

	public GenerateVersionInterceptor(RepositoryService repositoryService) {
		repositoryService_ = repositoryService;
	}

	public void execute(Object context) {

		// get parameters from dialog ... when creating the action
		String path = (String) context;
		String[] splittedContent = path.split("&workspaceName=");
		String nodePath = splittedContent[0];
		String[] splittedContentAgain = splittedContent[1].split("&repository=");
		String workspace = splittedContentAgain[0];
		Session session = null;
		try {
			session = repositoryService_.getCurrentRepository().getSystemSession(workspace);
			Node srcNode = (Node) session.getItem(nodePath);
			srcNode.checkin();
			srcNode.checkout();
			session.save();
		} catch (Exception e) {
			logger.warning("Error in GenerateVersionInterceptor script : " + e.getMessage());
		} finally {
			if (session != null) {
				session.logout();
			}
		}

	}

	public void setParams(String[] params) {
	}
}
