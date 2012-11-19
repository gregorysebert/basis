package basis.migration.service;

/**
 * Created with IntelliJ IDEA.
 * User: gregorysebert
 * Date: 09/11/12
 * Time: 09:45
 * To change this template use File | Settings | File Templates.
 */


import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.impl.core.SessionImpl;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MigrationUtil {

    static final String repository = "repository" ;
    static final String workspace = "collaboration";

    public static Session getSession()
    {
        Session session = null;
        RepositoryService rs = (RepositoryService) ((PortalContainer) ExoContainerContext.getTopContainer()
                .getComponentInstanceOfType(PortalContainer.class))
                .getComponentInstanceOfType(RepositoryService.class);

        ManageableRepository manageableRepository;
        try {
            manageableRepository = rs.getRepository(repository);
            session = (SessionImpl) manageableRepository.getSystemSession(workspace);
        } catch (RepositoryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (RepositoryConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return session;
    }


    public static List<String> getFileList(String path)
    {
        String [] s = new File(path).list();
        List<String> filesList = new ArrayList<String>();
        for (int i=0; i<s.length;i++)
        {
                filesList.add(s[i]);
        }
        return filesList;
    }

    public static boolean addBasisDocument(Session session, String jcrBasisPath)
    {

         return true;
    }

    public static HashMap<String, String> readBasisFile(String path)
    {
        File file = new File(path);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;
        HashMap<String, String> dataBasis = new HashMap<String, String>();

        try {
            fis = new FileInputStream(file);

            // Here BufferedInputStream is added for fast reading.
            bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);

            // dis.available() returns 0 if the file does not have more lines.
            while (dis.available() != 0) {
                String[] FieldList = dis.readLine().split("</#FIELD>");
                for (String field : FieldList)
                {
                    field = field.replace("<#FIELD NAME = ","");
                    String[] temp = field.split(">");
                    System.out.println(temp[0]+":"+temp[1]);
                    dataBasis.put(temp[0],temp[1]);
                }

            }

            // dispose all the resources after using them.
            fis.close();
            bis.close();
            dis.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    return dataBasis;
    }


}
