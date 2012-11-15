import java.text.SimpleDateFormat;
import java.util.logging.Logger;
import javax.jcr.Session;
import javax.jcr.Node;

import org.exoplatform.services.cms.scripts.CmsScript;
import org.exoplatform.services.jcr.RepositoryService;

public class CreateFolderTreeInterceptor implements CmsScript {

    private final String BO_ROOT_PATH = "Files/BO/";
	private final String FOLDER_NODETYPE = "nt:unstructured";
	private final String BASIS_DOCUMENT_NODETYPE = "basis:basisDocument";
	private final String BASIS_FOLDER_NODETYPE = "basis:basisFolder";
	private static Logger logger = Logger.getLogger("CreateFolderTreeInterceptor");
	private RepositoryService repositoryService_;

	public CreateFolderTreeInterceptor(RepositoryService repositoryService) {
		repositoryService_ = repositoryService;
	}

	public void execute(Object context) {

		// get parameters from dialog ... when creating the action
		String path = (String) context;
		String[] splittedContent = path.split("&workspaceName=");
		String nodePath = splittedContent[0];
		String[] splittedContentAgain = splittedContent[1].split("&repository=");
		String workspace = splittedContentAgain[0];
		String repository = splittedContentAgain[1];
		Session session = null;
		try {
			session = repositoryService_.getCurrentRepository().getSystemSession(workspace);
			Node nodeRoot = session.getRootNode();
			Node srcNode = (Node) session.getItem(nodePath);
			String srcNodeName = srcNode.getName();
			String BOName = srcNode.getParent().getName();

			// on replace le noeud au bon endroit
			SimpleDateFormat dateFormat = new SimpleDateFormat();
			dateFormat.applyPattern("dd-MM-yyyy");
			String[] alist = dateFormat.format(srcNode.getProperty("exo:dateCreated").getDate().getTime()).split("-");
			if (!nodeRoot.hasNode(BO_ROOT_PATH + BOName + "/" + alist[2])) {
				nodeRoot.getNode(BO_ROOT_PATH + BOName).addNode(alist[2], FOLDER_NODETYPE);
				session.save();
			}

			if (!nodeRoot.hasNode(BO_ROOT_PATH + BOName + "/" + alist[2] + "/" + alist[1])) {
				nodeRoot.getNode(BO_ROOT_PATH + BOName + "/" + alist[2]).addNode(alist[1], FOLDER_NODETYPE);
				session.save();
			}

			if (!nodeRoot.hasNode(BO_ROOT_PATH + BOName + "/" + alist[2] + "/" + alist[1] + "/" + alist[0])) {
				nodeRoot.getNode(BO_ROOT_PATH + BOName + "/" + alist[2] + "/" + alist[1]).addNode(alist[0], FOLDER_NODETYPE);
				session.save();
			}
			//Add basis folder
			Node parentNode = nodeRoot.getNode(BO_ROOT_PATH + BOName + "/" + alist[2] + "/" + alist[1] + "/" + alist[0]);
			Node basisFolderNode = parentNode.addNode("folder_"+ srcNodeName, BASIS_FOLDER_NODETYPE);
			
			if (srcNode.hasProperty("basis:folderLanguage")) {
			basisFolderNode.setProperty("basis:folderLanguage", srcNode.getProperty("basis:folderLanguage").getString());
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
			if (srcNode.hasProperty("basis:folderExternalReference")) {
			basisFolderNode.setProperty("basis:folderExternalReference", srcNode.getProperty("basis:folderExternalReference").getString());
			}
			if (srcNode.hasProperty("basis:folderStatus")) {
			basisFolderNode.setProperty("basis:folderStatus", srcNode.getProperty("basis:folderStatus").getString());
			}
			//Add basis document
			Node basisDocumentNode = basisFolderNode.addNode("document_"+ srcNodeName, BASIS_DOCUMENT_NODETYPE);

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
			srcNode.remove();
			session.save();
		} catch (Exception e) {
			logger.warning("Error in CreateFolderTreeInterceptor script : " + e.getMessage());
		} finally {
			if (session != null) {
				session.logout();
			}
		}

	}

	public void setParams(String[] params) {
	}
}
