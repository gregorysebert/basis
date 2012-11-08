import org.exoplatform.services.cms.scripts.CmsScript;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.impl.core.SessionImpl;
import org.exoplatform.services.cms.JcrInputProperty;
import java.util.Map;
import java.util.Set;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Workspace;
import java.util.Properties;


public class PreRegularisationFolderInterceptor implements CmsScript {

    private RepositoryService repositoryService_;

    public PreRegularisationFolderInterceptor(RepositoryService repositoryService) {
        repositoryService_ = repositoryService;
    }

    public void execute(Object context) {

        org.exoplatform.services.jcr.impl.core.SessionImpl session = null;
        session = (SessionImpl) repositoryService_.getRepository("repository").getSystemSession("collaboration");
        println("   ---------------------------------------------------------------> ");

        Map inputValues = (Map) context;
        Set keys = inputValues.keySet();
        JcrInputProperty nodename = null;
        ValueFactory valueFactory = session.getValueFactory();
        String strNum = "";
        String folName = "";
        for (String key: keys) {
            JcrInputProperty prop = (JcrInputProperty) inputValues.get(key);

            println("   --> " + prop.getJcrPath() + "=== " + prop.getValue());
            if (prop.getJcrPath().equals("/node") && prop.getValue().equals("folder_temp")) {
                println("   --> " + prop.getJcrPath() + "=== " + prop.getValue());
                String values = (String) prop.getValue();
                String[] listvalue = values.split(";");
                Value[] propvalue = new Value[listvalue.length]

                for (int i = 0; i < listvalue.length; i++) {
                    propvalue[i] = valueFactory.createValue(listvalue[i]);
                    println("   --> " + listvalue[i]);
                }


                Date dateyear = new Date();
                int year = dateyear.getYear() + 1900;
                String strYear = String.valueOf(year);



                Node srcNode = (Node) session.getItem("/Files/Ruling/Regularisation");
                NodeIterator ni = srcNode.getNodes();
                Integer folNum = -1;
                while (ni.hasNext()) {
                    Node current = ((Node) ni.next());
                    if (current.getPrimaryNodeType().getName().equals("spff:rulingReguFolder")) {
                        String nodeName = current.getName();
                        if (nodeName.substring(2, 6).equals(strYear) && (Integer.parseInt(nodeName.substring(7, 11)) > folNum)) {
                            folNum = Integer.parseInt(nodeName.substring(7, 11));
                        }
                    }
                }


                srcNode = (Node) session.getItem("/Files/Ruling/Regularisation/Archives");
                ni = srcNode.getNodes();
                while (ni.hasNext()) {
                    Node current = ((Node) ni.next());
                    if (current.getPrimaryNodeType().getName().equals("spff:rulingReguFolder")) {
                        String nodeName = current.getName();
                        if (nodeName.substring(2, 6).equals(strYear) && (Integer.parseInt(nodeName.substring(7, 11)) > folNum)) {
                            folNum = Integer.parseInt(nodeName.substring(7, 11));
                        }
                    }
                }


                if (folNum == -1) {
                    strNum = "0001";
                }
                else {
                    folNum++;
                    strNum = folNum.toString();
                    for (int i = strNum.length(); i < 4; i++) {
                        strNum = "0".concat(strNum);
                    }
                }
                folName = "R." + strYear + "." + strNum;
                prop.setValue(folName);
            }
        }
    }

    public void setParams(String[] params) {}

}