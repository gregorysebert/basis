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


public class PostReguYearInterceptor implements CmsScript {

    private RepositoryService repositoryService_;

    public PostReguYearInterceptor(RepositoryService repositoryService) {
        repositoryService_ = repositoryService;
    }

    public void execute(Object context) {
        /*String path = (String) context;
          String[] splittedContent = path.split("&workspaceName=");
          String nodePathS = splittedContent[0];
          splittedContent = splittedContent[1].split("&repository=");
          String workspaceS = splittedContent[0];
          String repositoryS = splittedContent[1];
          SessionImpl session = null;

          try
          {
              session = (SessionImpl) repositoryService_.getRepository(repositoryS).getSystemSession(workspaceS);
              Node srcNode = (Node) session.getItem(nodePathS);
              ValueFactory valueFactory = session.getValueFactory();

               String values =srcNode.getProperty("spff:yearVAT_Input").getValues()[0].getString();
               String[] listvalue = values.split(";");
               Value[] propvalue = new Value[listvalue.length]

               for (int i = 0; i < listvalue.length; i++) {
                    propvalue[i] = valueFactory.createValue(listvalue[i]);
                    println("   --> "+listvalue[i]);
              }

               srcNode.setProperty("spff:yearVAT_Input",propvalue);

              values =srcNode.getProperty("spff:yearVAT_Percent").getValues()[0].getString();
               listvalue = values.split(";");
               propvalue = new Value[listvalue.length]

               for (int i = 0; i < listvalue.length; i++) {
                    propvalue[i] = valueFactory.createValue(listvalue[i]);
                    println("   --> "+listvalue[i]);
              }

               srcNode.setProperty("spff:yearVAT_Percent",propvalue);

              values =srcNode.getProperty("spff:yearVAT_TL").getValues()[0].getString();
               listvalue = values.split(";");
               propvalue = new Value[listvalue.length]

               for (int i = 0; i < listvalue.length; i++) {
                    propvalue[i] = valueFactory.createValue(listvalue[i]);
                    println("   --> "+listvalue[i]);
              }

               srcNode.setProperty("spff:yearVAT_TL",propvalue);
              session.save();
          }
              catch (Exception e)
          {
              System.out.println("Error in YearCreationInterceptor script : ");
              e.printStackTrace(System.out);
          } finally
          {
              if (session != null)
              {
                  session.logout();
              }
          }*/
    }

    public void setParams(String[] params) {}

}