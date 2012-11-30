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
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class MigrationUtil {

    private static final String repository = "repository" ;
    private static final String workspace = "collaboration";
    private static final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    private static final SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
    private static final SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
    protected static Log log = ExoLogger.getLogger("basis.migration.service.MigrationServiceImpl");

    /*ACCNUM
    AFZEXP
    ANSDAT
    ANSWER
    DESTIN
    DIESER
    DOCDAT
    DOCNUM
    INSDAT
    KEYWDS
    KLANUM
    MINREV
    MINREVI
    MINREVO
    REVIS1
    REVIS1O
    SYSMOD
    TOEACC
    TYPDOC
    ZAAAFF*/


    private static final String docRegistrationDate = "docRegistrationDate";
    private static final String docDate = "docDate";
    private static final String DOSNUM  =   "DOSNUM";
    private static final String docReference = "docReference";
    private static final String docKeywords = "docKeywords";
    private static final String docInternSender = "docInternSender";
    private static final String docExternSenderName = "docExternSenderName";
    private static final String docExternSenderAdress = "docExternSenderAdress";
    private static final String docExternSenderZipCode = "docExternSenderZipCode";
    private static final String docExternSenderCity = "docExternSenderCity";
    private static final String docExternSenderCountry = "docExternSenderCountry";
    private static final String SYSDAT = "SYSDAT";
    private static final String DOCNUM = "DOCNUM";

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

    public static boolean addBasisDocument(Session session, String BO, String path, String jcrpath,String documentsErrorPath) {
        HashMap<String, String>  mapDoc=  readBasisFile(path,documentsErrorPath);
        try {

       BasisFolder basisFolder =   getBasisFolder(BO,mapDoc);
       BasisDocument  basisDoc =   getBasisDoc(BO,mapDoc);

       String basisFolderPath = createDateFolder(basisDoc.getSysDate(),session,jcrpath + "/" + BO);
       String folderPath = createBasisFolder(session,basisFolderPath,basisFolder);
       createBasisDocument(session,folderPath,basisDoc);

       session.save();

        } catch (RepositoryException e) {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        return true;
    }

    public static HashMap<String, String> readBasisFile(String path,String documentsErrorPath)
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

                    if (temp.length == 2)
                    {
                    dataBasis.put(temp[0],temp[1]);
                    }
                    else
                    {
                        fis.close();
                        bis.close();
                        dis.close();
                        file.renameTo(new File(documentsErrorPath+"/"+file.getName()));
                        log.error("Unable to read file" + path);
                    }
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
        } catch (Exception e){
        e.printStackTrace();
    }

    return dataBasis;
    }

    public static String createDateFolder(Date date, Session session, String path) throws RepositoryException {
        Node rootNode = session.getRootNode().getNode(path);


        log.info("Check YEAR folder: "+ yearFormat.format(date));
        if (!rootNode.hasNode(yearFormat.format(date)))
        {
            rootNode.addNode(yearFormat.format(date),"nt:unstructured");
            session.save();
        }

        rootNode = session.getRootNode().getNode(path+"/"+yearFormat.format(date));

        log.info("Check MONTH folder: "+ monthFormat.format(date));
        if (!rootNode.hasNode(monthFormat.format(date)))
        {
            rootNode.addNode(monthFormat.format(date),"nt:unstructured");
            session.save();
        }

        rootNode = session.getRootNode().getNode(path+"/"+yearFormat.format(date)+"/"+monthFormat.format(date));

        log.info("Check DAY folder: "+ dayFormat.format(date));
        if (!rootNode.hasNode(dayFormat.format(date)))
        {
            rootNode.addNode(dayFormat.format(date),"nt:unstructured");
            session.save();
        }
        return rootNode.getNode(dayFormat.format(date)).getPath().substring(1,rootNode.getNode(dayFormat.format(date)).getPath().length());
    }

    public static BasisDocument getBasisDoc(String BO,HashMap<String, String> docMap)
    {
        BasisDocument basisDoc= new BasisDocument();
        basisDoc.setDocId(BO+"-"+docMap.get(DOSNUM)+"-"+docMap.get(DOCNUM));
        basisDoc.setSysDate(new Date());
        return basisDoc;
    }

    public static BasisFolder getBasisFolder(String BO,HashMap<String, String> docMap)
    {
        BasisFolder basisFodler= new BasisFolder();
        basisFodler.setFolderId(BO+"-"+docMap.get(DOSNUM));
        return basisFodler;
    }

    public static String createBasisFolder(Session session, String path, BasisFolder basisFolder) throws RepositoryException {
        Node rootNode = session.getRootNode().getNode(path);
        Node nodeBasisFolder = null;
        String folderPath = "";
        if   (!rootNode.hasNode(basisFolder.getFolderId()))
        {
            nodeBasisFolder = rootNode.addNode(basisFolder.getFolderId(),"basis:basisFolder");
            nodeBasisFolder.setProperty("exo:title",basisFolder.getFolderId() ) ;
            nodeBasisFolder.setProperty("exo:name",basisFolder.getFolderId() ) ;
        }
        else  nodeBasisFolder =  rootNode.getNode(basisFolder.getFolderId());

        path = nodeBasisFolder.getPath().substring(1,nodeBasisFolder.getPath().length());
        session.save();

        return path;
        }
    public static void createBasisDocument(Session session, String path, BasisDocument basisDocument) throws RepositoryException {
        Node rootNode = session.getRootNode().getNode(path);
        if   (!rootNode.hasNode(basisDocument.getDocId()))
        {
            Node nodeBasisFolder = rootNode.addNode(basisDocument.getDocId(),"basis:basisDocument");
            nodeBasisFolder.setProperty("exo:title",basisDocument.getDocId() ) ;
            nodeBasisFolder.setProperty("exo:name",basisDocument.getDocId() ) ;
        }

        session.save();
    }

    }

