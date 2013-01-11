package searchBasis.portlet.component;

/**
 * Created with IntelliJ IDEA.
 * User: Kevin
 * Date: 9/01/13
 * Time: 15:36
 * To change this template use File | Settings | File Templates.
 */
public class NodeResult {

    private String name;
    private String path;

    public  NodeResult(String name, String path){
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
