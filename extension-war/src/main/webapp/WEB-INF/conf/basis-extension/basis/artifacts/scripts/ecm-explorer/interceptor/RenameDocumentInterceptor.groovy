import java.util.logging.Logger;
import org.exoplatform.services.cms.scripts.CmsScript;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import org.exoplatform.services.jcr.RepositoryService;

public class RenameDocumentInterceptor implements CmsScript {
	private static Logger logger = Logger.getLogger("RenameDocumentInterceptor");
	private RepositoryService repositoryService_;

	public RenameDocumentInterceptor(RepositoryService repositoryService) {
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
			Node basisFolerNode = srcNode.getParent();
			NodeIterator it = basisFolerNode.getNodes();
			String incre = "000";
            while (it.hasNext()) {
                Node basisDocumentNode = (Node) it.next();
                if ((!basisDocumentNode.getName().split("-")[0].equals("FU"))&&(basisDocumentNode.getName().split("-")[1].compareTo(incre) > 0)) {
                	incre = basisDocumentNode.getName().split("-")[1];
                }
            }
            if (incre.charAt(2) == '9' && incre.charAt(1) != '9') {
	            incre = (String)(incre.substring(0, 1) + (char)(incre.charAt(1)+1)) + "0";
			}
            else if (incre.charAt(2) == '9' && incre.charAt(1) == '9' ) {
	             incre = (String)((char)(incre.charAt(0)+1)) + "00";
			}
            else {
	            incre = (String)(incre.substring(0, 2) + (char)(incre.charAt(2)+1));
			}
            session.move(nodePath, basisFolerNode.getPath() + "/" + basisFolerNode.getName() + "-" + incre);
			srcNode.setProperty("exo:name", basisFolerNode.getName() + "-" + incre);
            srcNode.setProperty("exo:title", basisFolerNode.getProperty("exo:title").getString() + "-" + incre);
			session.save();
		} catch (Exception e) {
			logger.warning("Error in RenameDocumentInterceptor script : " + e.getMessage());
		} finally {
			if (session != null) {
				session.logout();
			}
		}

	}

	public void setParams(String[] params) {
	}
}
