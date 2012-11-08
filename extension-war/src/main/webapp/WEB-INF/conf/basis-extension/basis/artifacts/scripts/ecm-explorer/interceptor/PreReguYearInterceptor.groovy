import org.exoplatform.services.cms.scripts.CmsScript;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.impl.core.SessionImpl;
import org.exoplatform.services.cms.JcrInputProperty;
import javax.jcr.Node;
import java.util.Map;
import java.util.Set;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import org.exoplatform.services.jcr.impl.core.SessionImpl;


public class PreReguYearInterceptor implements CmsScript {

    private RepositoryService repositoryService_;

    public PreReguYearInterceptor(RepositoryService repositoryService) {
        repositoryService_ = repositoryService;
    }

    public void execute(Object context) {
        /*String path = (String) context;
          System.out.println("---------path---"+path);
          String[] splittedContent = path.split("&workspaceName=");
          String nodePathS = splittedContent[0];
          System.out.println("---------nodePathS ---"+nodePathS);
          splittedContent = splittedContent[1].split("&repository=");
          System.out.println("---------splittedContent ---"+splittedContent);
          String workspaceS = splittedContent[0];
          System.out.println("---------workspaceS ---"+workspaceS);
          String repositoryS = splittedContent[1];
          System.out.println("---------repositoryS ---"+repositoryS);*/
        SessionImpl session = null;

        try {
            String name = "";
            session = (SessionImpl) repositoryService_.getRepository("repository").getSystemSession("collaboration");
            Map inputValues = (Map) context;
            Set keys = inputValues.keySet();
            JcrInputProperty nodename = null;
            ValueFactory valueFactory = session.getValueFactory();
            String strNum = "";
            String folName = "";
            for (String key: keys) {
                JcrInputProperty prop = (JcrInputProperty) inputValues.get(key);
                println("   --> " + prop.getJcrPath() + "=== " + prop.getValue());
                if (prop.getJcrPath().equals("/node")) {
                    name = prop.getValue();
                }
            }
            String[] nodePath = name.split("-");
            String nodePathS = "/Files/Ruling/Regularisation/" + nodePath[0] + "/" + name;
            Node srcNode = (Node) session.getItem(nodePathS);
            srcNode.remove();
            session.save();
        }
        catch (Exception e) {
            System.out.println("Error in YearCreationInterceptor script : ");
            e.printStackTrace(System.out);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }

    public void setParams(String[] params) {}

}