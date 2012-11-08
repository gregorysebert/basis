package be.bull.exo.scripts;

import javax.jcr.Session;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Workspace;

import org.exoplatform.services.cms.scripts.CmsScript;
import org.exoplatform.services.jcr.RepositoryService;



public class RegualrisationPaymentInterceptor implements CmsScript {

    /** REGULARISATION PATH*/
    private static final String REGULARISATION_PATH = "/Files/Ruling/Regularisation";

    /** FOLDER NAME*/
    private static final String FOLDER_NAME = "spff:folder";

    /** PAYMENT DATE*/
    private static final String PAYMENT_DATE = "spff:paymentDate";

    /** NAME_PREFIX*/
    private static final String NAME_PREFIX = "PAYMENT_";

    private RepositoryService repositoryService_;

    Node currentNode;

    public RegualrisationPaymentInterceptor(RepositoryService repositoryService) {
        repositoryService_ = repositoryService;
    }

    public void execute(Object context) {
        String path = (String) context;
        Session session = null;
        try {
            System.out.println("test_debug_1");
            String[] splittedPath = path.split("&workspaceName=");
            String[] splittedContent = splittedPath[1].split("&repository=");
            session = repositoryService_.getRepository(splittedContent[1])
                    .getSystemSession(splittedContent[0]);
            currentNode = (Node) session.getItem(splittedPath[0]);

            String folderName = currentNode.getProperty(this.FOLDER_NAME).getValue().getString();
            //String payDate = currentNode.getProperty(this.PAYMENT_DATE).getValue().getString();

            if (currentNode.getName().equals("payment_temp")) {
                Node srcNode = (Node) session.getItem("/Files/Ruling/Regularisation/" + folderName + "/Payments/");
                NodeIterator ni = srcNode.getNodes();
                Integer payNum = -1;
                while (ni.hasNext()) {
                    Node current = ((Node) ni.next());
                    if (current.getPrimaryNodeType().getName().equals("spff:rulingReguPayment")) {
                        String nodeName = current.getName();
                        if (Integer.parseInt(nodeName.substring(12, 14)) > payNum) {
                            payNum = Integer.parseInt(nodeName.substring(12, 14));
                        }
                    }
                }
                String strNum = "";
                if (payNum == -1) {
                    strNum = "01";
                }
                else {
                    payNum++;
                    strNum = payNum.toString();
                    for (int i = strNum.length(); i < 2; i++) {
                        strNum = "0".concat(strNum);
                    }
                }

                //String newPath= this.REGULARISATION_PATH+"/"+folderName+"/Payments/"+this.NAME_PREFIX+payDate.substring(0,10);
                String newPath = this.REGULARISATION_PATH + "/" + folderName + "/Payments/" + folderName + "-" + strNum;
                System.out.println("currentNode.getPath()" + currentNode.getPath());
                System.out.println("newPath" + newPath);
                session.move(currentNode.getPath(), newPath);
                session.save();
            }
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