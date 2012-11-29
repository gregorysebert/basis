import java.util.logging.Logger;
import javax.jcr.Session;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import org.exoplatform.services.cms.scripts.CmsScript;
import org.exoplatform.services.jcr.RepositoryService;
import javax.servlet.http.HttpSession;
import org.exoplatform.portal.webui.util.Util;

public class CreateFolderConfigTreeInterceptor implements CmsScript {

	private final String BASIS_DOCUMENT_NODETYPE = "basis:basisDocument";
	private final String BASIS_FOLLOW_NODETYPE = "basis:basisFollow";
	private static Logger logger = Logger.getLogger("CreateFolderTreeInterceptor");
	private RepositoryService repositoryService_;

	public CreateFolderConfigTreeInterceptor(RepositoryService repositoryService) {
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
			Node nodeRoot = session.getRootNode();
			Node srcNode = (Node) session.getItem(nodePath);
			Node basisFolderNode = srcNode.getParent();
			NodeIterator it = basisFolderNode.getNodes();
			String incre = "000";
            
			if (srcNode.getProperty("basis:folderConfigType").getString().equals("FollowFolder")) {//Add basis follow folder
			    while (it.hasNext()) {
	                Node basisFollowNode = (Node) it.next();
	                if ((basisFollowNode.getName().split("-")[0].equals("FU"))&&(basisFollowNode.getName().split("-")[1].compareTo(incre) > 0)) {
	                	incre = basisFollowNode.getName().split("-")[1];
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
				Node basisFolderFollowNode = basisFolderNode.addNode("FU-" + incre, BASIS_FOLLOW_NODETYPE);
				if (srcNode.hasProperty("basis:followSendDate")) {
					basisFolderFollowNode.setProperty("basis:followSendDate", srcNode.getProperty("basis:followSendDate").getDate());
				}
				if (srcNode.hasProperty("basis:followEditorType")) {
					basisFolderFollowNode.setProperty("basis:followEditorType", srcNode.getProperty("basis:followEditorType").getString());
				}
				if (srcNode.hasProperty("basis:followUserInternEditor")) {
					basisFolderFollowNode.setProperty("basis:followUserInternEditor", srcNode.getProperty("basis:followUserInternEditor").getString());
				}
				if (srcNode.hasProperty("basis:followGroupInternEditor")) {
					basisFolderFollowNode.setProperty("basis:followGroupInternEditor", srcNode.getProperty("basis:followGroupInternEditor").getString());
				}
				if (srcNode.hasProperty("basis:followExternEditor")) {
					basisFolderFollowNode.setProperty("basis:followExternEditor", srcNode.getProperty("basis:followExternEditor").getString());
				}
				if (srcNode.hasProperty("basis:followRequiredAction")) {
					basisFolderFollowNode.setProperty("basis:followRequiredAction", srcNode.getProperty("basis:followRequiredAction").getString());
				}
				if (srcNode.hasProperty("basis:followComments")) {
					basisFolderFollowNode.setProperty("basis:followComments", srcNode.getProperty("basis:followComments").getString());
				}
				if (srcNode.hasProperty("basis:followAnswerByDate")) {
					basisFolderFollowNode.setProperty("basis:followAnswerByDate", srcNode.getProperty("basis:followAnswerByDate").getDate());
				}
				session.save();
				basisFolderFollowNode.checkin();
				basisFolderFollowNode.checkout();				
			}
			else {//Add basis document
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
				Node basisDocumentNode = basisFolderNode.addNode(basisFolderNode.getName() + "-" + incre, BASIS_DOCUMENT_NODETYPE);
				basisDocumentNode.setProperty("exo:title", basisFolderNode.getProperty("exo:title").getString() + "-" + incre);
				if (srcNode.hasProperty("basis:docInOut")) {
					basisDocumentNode.setProperty("basis:docInOut", srcNode.getProperty("basis:docInOut").getString());
				}
				if (srcNode.hasProperty("basis:docType")) {
					basisDocumentNode.setProperty("basis:docType", srcNode.getProperty("basis:docType").getString());
				}
				if (srcNode.hasProperty("basis:docRegistrationDate")) {
					basisDocumentNode.setProperty("basis:docRegistrationDate", srcNode.getProperty("basis:docRegistrationDate").getDate());
				}
				if (srcNode.hasProperty("basis:docDate")) {
					basisDocumentNode.setProperty("basis:docDate", srcNode.getProperty("basis:docDate").getDate());
				}
				if (srcNode.hasProperty("basis:docReference")) {
					basisDocumentNode.setProperty("basis:docReference", srcNode.getProperty("basis:docReference").getString());
				}
				if (srcNode.hasProperty("basis:docKeywords")) {
					basisDocumentNode.setProperty("basis:docKeywords", srcNode.getProperty("basis:docKeywords").getValues());
				}
				if (srcNode.hasProperty("basis:docSenderType")) {
					basisDocumentNode.setProperty("basis:docSenderType", srcNode.getProperty("basis:docSenderType").getString());
				}
				if (srcNode.hasProperty("basis:docInternSender")) {
					basisDocumentNode.setProperty("basis:docInternSender", srcNode.getProperty("basis:docInternSender").getString());
				}
				if (srcNode.hasProperty("basis:docPredefinedExternSender")) {
					basisDocumentNode.setProperty("basis:docPredefinedExternSender", srcNode.getProperty("basis:docPredefinedExternSender").getString());
				}	
				if (srcNode.hasProperty("basis:docExternSenderName")) {
					basisDocumentNode.setProperty("basis:docExternSenderName", srcNode.getProperty("basis:docExternSenderName").getString());
				}
				if (srcNode.hasProperty("basis:docExternSenderAdress")) {
					basisDocumentNode.setProperty("basis:docExternSenderAdress", srcNode.getProperty("basis:docExternSenderAdress").getString());
				}	
				if (srcNode.hasProperty("basis:docExternSenderZipCode")) {
					basisDocumentNode.setProperty("basis:docExternSenderZipCode", srcNode.getProperty("basis:docExternSenderZipCode").getString());
				}	
				if (srcNode.hasProperty("basis:docExternSenderCity")) {
					basisDocumentNode.setProperty("basis:docExternSenderCity", srcNode.getProperty("basis:docExternSenderCity").getString());
				}	
				if (srcNode.hasProperty("basis:docExternSenderCountry")) {
					basisDocumentNode.setProperty("basis:docExternSenderCountry", srcNode.getProperty("basis:docExternSenderCountry").getString());
				}
				session.save();
				basisDocumentNode.checkin();
				basisDocumentNode.checkout();
				HttpSession httpSession = Util.getPortalRequestContext().getRequest().getSession();
			    httpSession.setAttribute("basisDocumentId", basisDocumentNode.getProperty("exo:title").getString());
				if (srcNode.getProperty("basis:folderConfigFollowDoc").getBoolean()){//Add basis follow document
					Node basisFollowNode = basisDocumentNode.addNode("FU" + "-000", BASIS_FOLLOW_NODETYPE);
					if (srcNode.hasProperty("basis:folderConfigFollowDocSendDate")) {
						basisFollowNode.setProperty("basis:followSendDate", srcNode.getProperty("basis:folderConfigFollowDocSendDate").getDate());
					}
					if (srcNode.hasProperty("basis:folderConfigFollowDocEditorType")) {
						basisFollowNode.setProperty("basis:followEditorType", srcNode.getProperty("basis:folderConfigFollowDocEditorType").getString());
					}
					if (srcNode.hasProperty("basis:folderConfigFollowDocUserInternEditor")) {
						basisFollowNode.setProperty("basis:followUserInternEditor", srcNode.getProperty("basis:folderConfigFollowDocUserInternEditor").getString());
					}
					if (srcNode.hasProperty("basis:folderConfigFollowDocGroupInternEditor")) {
						basisFollowNode.setProperty("basis:followGroupInternEditor", srcNode.getProperty("basis:folderConfigFollowDocGroupInternEditor").getString());
					}
					if (srcNode.hasProperty("basis:folderConfigFollowDocExternEditor")) {
						basisFollowNode.setProperty("basis:followExternEditor", srcNode.getProperty("basis:folderConfigFollowDocExternEditor").getString());
					}
					if (srcNode.hasProperty("basis:folderConfigFollowDocRequiredAction")) {
						basisFollowNode.setProperty("basis:followRequiredAction", srcNode.getProperty("basis:folderConfigFollowDocRequiredAction").getString());
					}
					if (srcNode.hasProperty("basis:folderConfigFollowDocComments")) {
						basisFollowNode.setProperty("basis:followComments", srcNode.getProperty("basis:folderConfigFollowDocComments").getString());
					}
					if (srcNode.hasProperty("basis:folderConfigFollowDocAnswerByDate")) {
						basisFollowNode.setProperty("basis:followAnswerByDate", srcNode.getProperty("basis:folderConfigFollowDocAnswerByDate").getDate());
					}
					session.save();
					basisFollowNode.checkin();
					basisFollowNode.checkout();
				}	
			}				
			srcNode.remove();
			session.save();
		} catch (Exception e) {
			logger.warning("Error in CreateFolderConfigTreeInterceptor script : " + e.getMessage());
		} finally {
			if (session != null) {
				session.logout();
			}
		}

	}

	public void setParams(String[] params) {
	}
}
