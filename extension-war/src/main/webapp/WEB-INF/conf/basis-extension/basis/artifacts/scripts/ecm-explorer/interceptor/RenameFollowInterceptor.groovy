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
			Node basisFolerNode = srcNode.getParent();
			NodeIterator it = basisFolerNode.getNodes();
			String incre = "";
            while (it.hasNext()) {
                Node basisFollowNode = (Node) it.next();
                if ((basisFollowNode.getName().split("-")[0].equals("Suivi"))&&(basisFollowNode.getName().split("-")[1].compareTo(incre) > 0)) {
                	incre = basisFollowNode.getName().split("-")[1];
                }
            }
            if (!incre.equals("")) {
	            if (incre.charAt(2) == '9' && incre.charAt(1) != '9') {
		            incre = (String)(incre.substring(0, 1) + (char)(incre.charAt(1)+1)) + "0";
				}
	            else if (incre.charAt(2) == '9' && incre.charAt(1) == '9' ) {
		             incre = (String)((char)(incre.charAt(0)+1)) + "00";
				}
	            else {
		            incre = (String)(incre.substring(0, 2) + (char)(incre.charAt(2)+1));
				}
			}
			else {
                incre = "000";
            }		
            session.move(nodePath, basisFolerNode.getPath() + "/" + "Suivi-" + incre);
			srcNode.setProperty("exo:name", "Suivi-" + incre);
            srcNode.setProperty("exo:title", "Suivi-" + incre);
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
