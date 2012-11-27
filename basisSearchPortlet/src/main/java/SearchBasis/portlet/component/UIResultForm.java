package SearchBasis.portlet.component;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormStringInput;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.query.QueryResult;
import javax.portlet.PortletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: rousselle-k
 * Date: 23/11/12
 * Time: 12:47
 * To change this template use File | Settings | File Templates.
 */
@ComponentConfig(
        lifecycle = UIFormLifecycle.class,
        template =  "app:/groovy/SearchBasis/portlet/UIResultForm.gtmpl"
)
public class UIResultForm extends UIForm {

    private QueryResult queryResult;
    private NodeIterator nodeIterator;

    public UIResultForm () throws Exception{
    }

    public  void update()throws Exception{
        UISearchBasisPortlet uiSearchBasisPortlet = this.getAncestorOfType(UISearchBasisPortlet.class);
        try{
            if(!uiSearchBasisPortlet.getQueryResult().equals(null)){
                queryResult = uiSearchBasisPortlet.getQueryResult();
                nodeIterator = queryResult.getNodes();

                while(nodeIterator.hasNext()){
                    Node node = nodeIterator.nextNode();
                    addUIFormInput(new UIFormStringInput(node.getName(), node.getName(), null));
                    addUIFormInput(new UIFormStringInput(node.getPath(), node.getPath(), null));
                    System.out.print("node : " + node);
                }
            }
        }
        catch (NullPointerException ex){
            System.out.print("Null Pointer Exception" + ex);
        }

    }

    public QueryResult getQueryResult() {
        return queryResult;
    }

    public void setQueryResult(QueryResult queryResult) {
        this.queryResult = queryResult;
    }

    public NodeIterator getNodeIterator() {
        return nodeIterator;
    }

    public void setNodeIterator(NodeIterator nodeIterator) {
        this.nodeIterator = nodeIterator;
    }
}
