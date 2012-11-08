package be.spff.exo.scripts;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import javax.jcr.Node;

import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.impl.core.SessionImpl;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;

import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.services.cms.scripts.CmsScript;

public class FillSelectBoxWithFoldersForPayment implements CmsScript {

    private RepositoryService repositoryService_;
    public static final String REGULARISATION_PATH = "/Files/Ruling/Regularisation";
    public static final String FOLDER_NODETYPE = "spff:rulingReguFolder";
    public static final String WFLOW_STATUS = "spff:overviewStatusCheck";
    public static final String WAITING_PAYMENT = "Assign Leader";
    public static final String PAYMENT_PENDING = "Payment Pending";

    public FillSelectBoxWithFoldersForPayment(RepositoryService repositoryService) {
        repositoryService_ = repositoryService;
    }

    public void execute(Object context) {
        UIFormSelectBox selectBox = (UIFormSelectBox) context;
        SessionImpl session = null;
        try {
            session = (SessionImpl) repositoryService_.getCurrentRepository().getSystemSession("collaboration");
            Node srcNode = (Node) session.getItem(FillSelectBoxWithFoldersForPayment.REGULARISATION_PATH);
            Iterator<Node> nodeIterator = srcNode.getNodes();
            List options = new ArrayList();
            Node currNode;
            String nodeName;
            String status;
            while (nodeIterator.hasNext()) {
                currNode = nodeIterator.next();
                if (currNode.isNodeType(FillSelectBoxWithFoldersForPayment.FOLDER_NODETYPE)) {
                    status = currNode.getProperty(FillSelectBoxWithFoldersForPayment.WFLOW_STATUS).getValue().getString();
                    if ((status.equals(FillSelectBoxWithFoldersForPayment.WAITING_PAYMENT)) ||
                            (status.equals(FillSelectBoxWithFoldersForPayment.PAYMENT_PENDING))) {
                        nodeName = currNode.getName();
                        options.add(new SelectItemOption(nodeName, nodeName));
                    }
                    selectBox.setOptions(options);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setParams(String[] params) {
        //group=params[0];
    }
}