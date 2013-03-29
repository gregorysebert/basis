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
    private String user;
    private String reference;

    public  NodeResult(String name, String user, String reference){
        this.name = name;
        this.user = user;
        this.reference = reference;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
