package basis.selector.service;

import java.io.InputStream;
import java.util.Scanner;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

public class BasisSelectorService {
    private String options;

    public String getOptions() {
        return options;
    }
    private static final String NT_RESOURCE = "nt:resource";

    private static final String JCR_CONTENT = "jcr:content";

    private static final String NT_FILE = "nt:file";

    private static final String JCR_DATA = "jcr:data";

    private InputStream getStream(Node selectorNode) {
        try {
            if (selectorNode != null && NT_FILE.equals(selectorNode.getPrimaryNodeType().getName())) {
                Node jcrContentNode = selectorNode.getNode(JCR_CONTENT);
                if (jcrContentNode != null && NT_RESOURCE.equals(jcrContentNode.getPrimaryNodeType().getName())) {
                    return jcrContentNode.getProperty(JCR_DATA).getStream();
                }
            }
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public BasisSelectorService(Node selectorNode, String field, String language) {
        InputStream is = this.getStream(selectorNode);
        Scanner scanner = new Scanner(is, "UTF-8");
        options = new String();
        String columns[] = scanner.nextLine().split(";");
        int i;
        for (i=1 ; i<columns.length ; i++) {
        	if (columns [i].equals(language)) {
        		break;
        	}
        }
        try {
            while (scanner.hasNextLine()) {
                columns = scanner.nextLine().split(";");
                if (columns[0].equals(field)) {
                	options = columns[i].replace('@', ',');
                	break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
        scanner.close();
    }
}
