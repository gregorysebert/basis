import java.text.SimpleDateFormat;
import java.util.logging.Logger;
import javax.jcr.lock.Lock;
import javax.jcr.*;
import org.exoplatform.services.cms.scripts.CmsScript;
import org.exoplatform.services.jcr.RepositoryService;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.exoplatform.portal.webui.util.Util;
import javax.jcr.RepositoryException;

public class CreateFolderTreeInterceptor implements CmsScript {

    private final String BO_ROOT_PATH = "Files/BO/";
	private final String BASIS_DATE_NODETYPE = "basis:basisDate";
	private final String BASIS_BASIS_DATE_NODETYPE = "basis:basisFolder";
	private final String BASIS_DOCUMENT_NODETYPE = "basis:basisDocument";
	private final String BASIS_FOLLOW_NODETYPE = "basis:basisFollow";
	private static Logger logger = Logger.getLogger("CreateFolderTreeInterceptor");
	private RepositoryService repositoryService_;
	private Node getBONode(Node currentNode) {
		   Node parentNode = currentNode.getParent();
		   if (! parentNode.isNodeType("basis:basisBO")) {
			   return getBONode(parentNode); 
		   }
		   return parentNode;
	}
	
	public CreateFolderTreeInterceptor(RepositoryService repositoryService) {
		repositoryService_ = repositoryService;
	}

	public void execute(Object context) {

        HttpServletRequest request = Util.getPortalRequestContext().getRequest();
        HttpServletResponse response = Util.getPortalRequestContext().getResponse();
        Cookie cookieDayCheck;
        Cookie cookieBoName;
        Cookie[] cookies = request.getCookies();
        for(int i=0; i < cookies.length; i++){
            if(cookies[i].getName().equals("dayCheck")){
                cookieDayCheck = cookies[i];
            }
            else if(cookies[i].getName().equals("boName")){
                cookieBoName = cookies[i];
            }
        }

		// get parameters from dialog ... when creating the action
		String path = (String) context;
		String[] splittedContent = path.split("&workspaceName=");
		String nodePath = splittedContent[0];
		String[] splittedContentAgain = splittedContent[1].split("&repository=");
		String workspace = splittedContentAgain[0];
		Session session = null;
		try {
            HttpSession httpSession = Util.getPortalRequestContext().getRequest().getSession();
			session = repositoryService_.getCurrentRepository().getSystemSession(workspace);
			Node nodeRoot = session.getRootNode();
			Node srcNode = (Node) session.getItem(nodePath);
			String BOName = getBONode(srcNode).getName();
            if (getBONode(srcNode).canAddMixin("mix:lockable")){
                getBONode(srcNode).addMixin("mix:lockable");
                getBONode(srcNode).save();
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat();
            dateFormat.applyPattern("dd-MM-yyyy");
            String[] alist = dateFormat.format(srcNode.getProperty("exo:dateCreated").getDate().getTime()).split("-");

            if(!cookieBoName.getValue().equals(BOName) || !cookieDayCheck.getValue().equals("Yes")){
                logger.info("CreateFolderTreeInterceptor  ---  Check node path basis date exist! ");
                if (!nodeRoot.hasNode(BO_ROOT_PATH + BOName + "/" + alist[2])) {
                    nodeRoot.getNode(BO_ROOT_PATH + BOName).addNode(alist[2], BASIS_DATE_NODETYPE);
                    session.save();
                }

                if (!nodeRoot.hasNode(BO_ROOT_PATH + BOName + "/" + alist[2] + "/" + alist[1])) {
                    nodeRoot.getNode(BO_ROOT_PATH + BOName + "/" + alist[2]).addNode(alist[1], BASIS_DATE_NODETYPE);
                    session.save();
                }

                if (!nodeRoot.hasNode(BO_ROOT_PATH + BOName + "/" + alist[2] + "/" + alist[1] + "/" + alist[0])) {
                    nodeRoot.getNode(BO_ROOT_PATH + BOName + "/" + alist[2] + "/" + alist[1]).addNode(alist[0], BASIS_DATE_NODETYPE);
                    session.save();
                }
                cookieDayCheck.setValue("Yes");
                response.addCookie(cookieDayCheck);
                logger.info("CreateFolderTreeInterceptor  ---  Attribute daycheck change to Yes! ");
            }
			//Add basis folder
			Node parentNode = nodeRoot.getNode(BO_ROOT_PATH + BOName + "/" + alist[2] + "/" + alist[1] + "/" + alist[0]);
			NodeIterator it = parentNode.getNodes();
            while(getBONode(srcNode).isLocked() == true){
                Logger.info("CreateFolderTreeInterceptor  ---  Src node is lock    Waiting 1sec!");
                sleep(1000);
            }
            Lock lock = getBONode(srcNode).lock(false,false);
            String incre = getBONode(srcNode).getProperty("basis:BOCount").getString() ;
            String basisFolderNodeTitle = BOName + "." + incre.substring(0, 2) + "." + incre.substring(2, 5) + "." + incre.substring(5);
            Node basisFolderNode = parentNode.addNode(BOName + "." + incre, BASIS_BASIS_DATE_NODETYPE);
            if (incre.charAt(7) == '9' && incre.charAt(6) != '9') {
                incre = (String)(incre.substring(0, 6) + (char)(incre.charAt(6)+1)) + "0";
            }
            else if (incre.charAt(7) == '9' && incre.charAt(6) == '9' && incre.charAt(5) != '9') {
                incre = (String)(incre.substring(0, 5) + (char)(incre.charAt(5)+1)) + "00";
            }
            else if (incre.charAt(7) == '9' && incre.charAt(6) == '9' && incre.charAt(5) == '9' && incre.charAt(4) != '9') {
                incre = (String)(incre.substring(0, 4) + (char)(incre.charAt(4)+1)) + "000";
            }
            else if (incre.charAt(7) == '9' && incre.charAt(6) == '9' && incre.charAt(5) == '9' && incre.charAt(4) == '9' && incre.charAt(3) != '9') {
                incre = (String)(incre.substring(0, 3) + (char)(incre.charAt(3)+1)) + "0000";
            }
            else if (incre.charAt(7) == '9' && incre.charAt(6) == '9' && incre.charAt(5) == '9' && incre.charAt(4) == '9' && incre.charAt(3) == '9' && incre.charAt(2) != '9') {
                incre = (String)(incre.substring(0, 2) + (char)(incre.charAt(2)+1)) + "00000";
            }
            else if (incre.charAt(7) == '9' && incre.charAt(6) == '9' && incre.charAt(5) == '9' && incre.charAt(4) == '9' && incre.charAt(3) == '9' && incre.charAt(2) == '9' && incre.charAt(1) != '9') {
                incre = (String)(incre.substring(0, 1) + (char)(incre.charAt(1)+1)) + "000000";
            }
            else if (incre.charAt(7) == '9' && incre.charAt(6) == '9' && incre.charAt(5) == '9' && incre.charAt(4) == '9' && incre.charAt(3) == '9' && incre.charAt(2) == '9' && incre.charAt(1) == '9') {
                incre = (String)((char)(incre.charAt(0)+1)) + "0000000";
            }
            else {
                incre = (String)(incre.substring(0, 7) + (char)(incre.charAt(7)+1));
            }
            getBONode(srcNode).setProperty("basis:BOCount", incre);
            getBONode(srcNode).save();
            getBONode(srcNode).unlock() ;
            basisFolderNode.setProperty("exo:title", basisFolderNodeTitle);
			if (srcNode.hasProperty("basis:folderLanguage")) {
				basisFolderNode.setProperty("basis:folderLanguage", srcNode.getProperty("basis:folderLanguage").getString());
			}
			if (srcNode.hasProperty("basis:folderType")) {
				basisFolderNode.setProperty("basis:folderType", srcNode.getProperty("basis:folderType").getString());
			}
			if (srcNode.hasProperty("basis:folderRegistrationDate")) {
				basisFolderNode.setProperty("basis:folderRegistrationDate", srcNode.getProperty("basis:folderRegistrationDate").getDate());
			}
			if (srcNode.hasProperty("basis:folderCloseBeforeDate")) {
				basisFolderNode.setProperty("basis:folderCloseBeforeDate", srcNode.getProperty("basis:folderCloseBeforeDate").getDate());
			}
			if (srcNode.hasProperty("basis:folderRNN")) {
				basisFolderNode.setProperty("basis:folderRNN", srcNode.getProperty("basis:folderRNN").getString());
			}
			if (srcNode.hasProperty("basis:folderInternSender")) {
				basisFolderNode.setProperty("basis:folderInternSender", srcNode.getProperty("basis:folderInternSender").getString());
			}
			if (srcNode.hasProperty("basis:folderExternalReference")) {
				basisFolderNode.setProperty("basis:folderExternalReference", srcNode.getProperty("basis:folderExternalReference").getString());
			}
			if (srcNode.hasProperty("basis:folderStatus")) {
				basisFolderNode.setProperty("basis:folderStatus", srcNode.getProperty("basis:folderStatus").getString());
			}
			if (srcNode.hasProperty("basis:folderCloseOnDate")) {
				basisFolderNode.setProperty("basis:folderCloseOnDate", srcNode.getProperty("basis:folderCloseOnDate").getDate());
			}
			if (srcNode.hasProperty("basis:folderComments")) {
				basisFolderNode.setProperty("basis:folderComments", srcNode.getProperty("basis:folderComments").getString());
			}
			//Add basis document
			Node basisDocumentNode = basisFolderNode.addNode(basisFolderNode.getName() + "-000", BASIS_DOCUMENT_NODETYPE);
			basisDocumentNode.setProperty("exo:title", basisFolderNodeTitle + "-000");
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
			if (srcNode.hasProperty("basis:docComments")) {
				basisDocumentNode.setProperty("basis:docComments", srcNode.getProperty("basis:docComments").getString());
			}
			//Add basis follow
			Node basisFollowNode = basisFolderNode.addNode("FU" + "-000", BASIS_FOLLOW_NODETYPE);
			if (srcNode.hasProperty("basis:followSendDate")) {
				basisFollowNode.setProperty("basis:followSendDate", srcNode.getProperty("basis:followSendDate").getDate());
			}
			if (srcNode.hasProperty("basis:followEditorType")) {
				basisFollowNode.setProperty("basis:followEditorType", srcNode.getProperty("basis:followEditorType").getString());
			}
			if (srcNode.hasProperty("basis:followUserInternEditor")) {
				basisFollowNode.setProperty("basis:followUserInternEditor", srcNode.getProperty("basis:followUserInternEditor").getString());
			}
			if (srcNode.hasProperty("basis:followGroupInternEditor")) {
				basisFollowNode.setProperty("basis:followGroupInternEditor", srcNode.getProperty("basis:followGroupInternEditor").getString());
			}
			if (srcNode.hasProperty("basis:followExternEditor")) {
				basisFollowNode.setProperty("basis:followExternEditor", srcNode.getProperty("basis:followExternEditor").getString());
			}
			if (srcNode.hasProperty("basis:followRequiredAction")) {
				basisFollowNode.setProperty("basis:followRequiredAction", srcNode.getProperty("basis:followRequiredAction").getString());
			}
			if (srcNode.hasProperty("basis:followComments")) {
				basisFollowNode.setProperty("basis:followComments", srcNode.getProperty("basis:followComments").getString());
			}
			if (srcNode.hasProperty("basis:followAnswerByDate")) {
				basisFollowNode.setProperty("basis:followAnswerByDate", srcNode.getProperty("basis:followAnswerByDate").getDate());
			}

			srcNode.remove();
			session.save();
			basisFolderNode.checkin();
			basisFolderNode.checkout();
		    basisDocumentNode.checkin();
			basisDocumentNode.checkout();
			basisFollowNode.checkin();
			basisFollowNode.checkout();
			httpSession.setAttribute("basisFolderNumber", basisFolderNodeTitle);
			httpSession.setAttribute("basisFolderNodePath", basisFolderNode.getPath());
			httpSession.setAttribute("basisDocumentId", basisDocumentNode.getProperty("exo:title").getString());
			httpSession.setAttribute("basisDocumentNodePath", basisDocumentNode.getPath());

			
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
