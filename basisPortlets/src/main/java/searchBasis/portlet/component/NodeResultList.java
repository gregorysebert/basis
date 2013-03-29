package searchBasis.portlet.component;

import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.commons.utils.ListAccessImpl;
import org.exoplatform.commons.utils.PageListAccess;
import org.exoplatform.container.PortalContainer;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kevin
 * Date: 9/01/13
 * Time: 15:39
 * To change this template use File | Settings | File Templates.
 */
public class NodeResultList extends PageListAccess<NodeResult, List<String>> {

    private static final long serialVersionUID = 1L;

    public NodeResultList(List<String> node, int pageSize) {
        super(node, pageSize);
    }

    @Override
    protected ListAccess<NodeResult> create(List<String> node) throws Exception {
        List<NodeResult> listNodeResults = new ArrayList<NodeResult>();
        for(int i = 0; i<node.size(); i++){
            if(i%3 == 0){
                NodeResult nodeResult = new NodeResult(node.get(i), node.get(i+1), node.get(i+2));
                listNodeResults.add(nodeResult);
            }
        }
        return new ListAccessImpl<NodeResult>(NodeResult.class, listNodeResults);
    }
}
