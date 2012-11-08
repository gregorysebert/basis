package basis.migration.service;

import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.net.URL;
import java.util.Properties;


public class MigrationServiceImpl implements MigrationService
{

    protected static Log log = ExoLogger.getLogger("basis.migration.service.MigrationServiceImpl");


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

    public MigrationServiceImpl() throws RepositoryConfigurationException {
        log.debug("basis.migration.service.MigrationServiceImpl");
        Properties prop = getPropsFile("conf/application.properties");
        //sNodeRoot                       = prop.getProperty("olympia.stat.NodeRoot");
    }


    @Override
    public void MigrateAll(String folderPath) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void MigrateDocument(String docPath) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
