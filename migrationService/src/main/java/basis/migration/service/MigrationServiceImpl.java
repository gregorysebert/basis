package basis.migration.service;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;


public class MigrationServiceImpl implements MigrationService
{

    protected static Log log = ExoLogger.getLogger("basis.migration.service.MigrationServiceImpl");
    private String path;
    private String jcrpath;
    private String documentsErrorPath;
    private String documentsMigratePath;
    private String displayRNN;

    public MigrationServiceImpl() {
        log.debug("basis.migration.service.MigrationServiceImpl");
    }


    public Properties getPropsFile(String propsFileName ) {
        String propertiesFileName = "basis.properties";

        Properties props = new Properties();
        String path = System.getProperty("jboss.server.config.url")+"gatein/"+propertiesFileName;
        URL url = null;
        try {
            url = new URL(path);
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            if(new File(url.toURI()).exists()) {
                props.load(url.openStream());
                log.info("loaded application properties from file: " + path);
            } else {
                props = new Properties();
                URL myURL = this.getClass().getClassLoader().getResource(propsFileName);
                try
                {
                    if (myURL != null)
                        props.load(myURL.openStream());
                }
                catch(Exception e){
                }
                log.info("loaded application properties: /"+propertiesFileName);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return props;
    }


    @Override
    public void MigrateAll(String BO, String BOCountPattern) {
        //To change body of implemented methods use File | Settings | File Templates.
        Properties prop = getPropsFile("conf/application.properties");
        this.path = prop.getProperty(BO+".basis.documentsPath");
        this.documentsErrorPath = prop.getProperty(BO+".basis.documentsErrorPath");
        this.jcrpath = prop.getProperty(BO+".basis.jcrPath");
        this.documentsMigratePath = prop.getProperty(BO+".basis.documentsMigratedPath");
        this.displayRNN = prop.getProperty(BO+".basis.displayRNN");


        log.info("Starting Migration of fastdoc folder :" + path + " in jcr path :" + jcrpath );
        Session session = MigrationUtil.getSession();


        Node rootNode = null;
        try {
            rootNode = session.getRootNode();


        if (!rootNode.hasNode(jcrpath))
        {
            Node boNode = rootNode.addNode(jcrpath, "basis:basisBO");
            boNode.setProperty("basis:BOCount", "00000000");
            boNode.setProperty("basis:BODisplayRNN",  Boolean.valueOf(displayRNN).booleanValue());
            session.save();
        }

        } catch (RepositoryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        ;

        int i =1;
        int j=1;

        long begin = System.nanoTime();
        long beginlot = 0;
        double seconds = 0;
        long elapsedTime = 0;
        String file_beginlot = "";

        for(String fileName : MigrationUtil.getFileList(path))
        {
            if (j==1)
            {
            beginlot = System.nanoTime();
            file_beginlot = fileName;
            }

              MigrationUtil.addBasisDocument(session, BO, path + "/" + fileName,jcrpath,documentsErrorPath,documentsMigratePath, BOCountPattern);

              i++;


            if (j==40)
            {
                try {
                    session.save();
                    elapsedTime = System.nanoTime() - beginlot;
                    seconds = (double)elapsedTime / 1000000000.0;
                    log.info("Lot from :"+file_beginlot + " to " + fileName + " saved in :" +seconds + ", index : " + i) ;
                } catch (RepositoryException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                j=0;
            }
            j++;

        }

        try {
            session.save();
        } catch (RepositoryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        session.logout();

        elapsedTime = System.nanoTime() - begin;
        seconds = (double)elapsedTime / 1000000000.0;

        log.info("End Migration of fastdoc folder :" + path + " in jcr path :" + jcrpath + "in :"+seconds);
    }

    @Override
    public void MigrateDocument(String BO,String fileName) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
        log.info("Starting Migration of folder :" + path + "in jcr path :" + jcrpath );
        Session session = MigrationUtil.getSession();

        log.info("Migrating file : " + fileName);
        //MigrationUtil.readBasisFile(fileName);

    }
}
