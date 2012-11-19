package basis.migration.service;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.jcr.Session;
import java.net.URL;
import java.util.Properties;


public class MigrationServiceImpl implements MigrationService
{

    protected static Log log = ExoLogger.getLogger("basis.migration.service.MigrationServiceImpl");
    private String path;
    private String jcrpath;

    public MigrationServiceImpl() {
        log.debug("basis.migration.service.MigrationServiceImpl");
        Properties prop = getPropsFile("conf/application.properties");
        this.path = prop.getProperty("basis.documentsPath");
        this.jcrpath = prop.getProperty("basis.jcrPath");
    }


    public Properties getPropsFile(String propsFileName )
    {
        Properties prop = new Properties();
        URL myURL = this.getClass().getClassLoader().getResource(propsFileName);
        try
        {
            if (myURL != null)
                prop.load(myURL.openStream());
        }
        catch(Exception e){
        }
         return prop;
    }


    @Override
    public void MigrateAll() throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
        log.info("Starting Migration of folder :" + path + "in jcr path :" + jcrpath );
        Session session = MigrationUtil.getSession();

        for(String fileName : MigrationUtil.getFileList(path))
        {
              log.info("Migrating file : " + fileName);

              MigrationUtil.readBasisFile(path+"/"+fileName);
              //Node rootNode = session.getRootNode().getNode(jcrpath);
              session.save();
        }
        session.logout();
    }

    @Override
    public void MigrateDocument(String fileName) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
        log.info("Starting Migration of folder :" + path + "in jcr path :" + jcrpath );
        Session session = MigrationUtil.getSession();

        log.info("Migrating file : " + fileName);
        MigrationUtil.readBasisFile(fileName);

    }
}
