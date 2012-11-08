/**
 * Created by IntelliJ IDEA.
 * User: JANSSENS-B
 * Date: 26/09/11
 * Time: 16:11
 * To change this template use File | Settings | File Templates.
 */

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.jcr.*;
import javax.jcr.Session;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.*;
import net.sf.jasperreports.engine.util.*;
import net.sf.jasperreports.engine.export.*;
import org.exoplatform.services.cms.scripts.CmsScript;
import org.exoplatform.services.cms.records.RecordsService;
import org.exoplatform.services.jcr.core.ExtendedNode;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import org.apache.commons.logging.Log;
import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.contact.service.AddressBook;
import org.exoplatform.contact.service.Contact;
import org.exoplatform.contact.service.ContactService;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.mail.MailService;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.container.*;
import org.exoplatform.services.organization.*;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.cms.drives.*;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.jcr.core.ManageableRepository;
import be.spff.eruling.regularisation.beans.CertificateInfo;
import be.spff.eruling.regularisation.beans.YearInfo;
import be.spff.eruling.regularisation.nodes.FolderNode;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRCsvDataSource;
import org.exoplatform.contact.service.Contact;
import org.exoplatform.contact.service.ContactService;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;


import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import java.text.SimpleDateFormat;

public class RulingStat implements CmsScript {

    public static final String RULINGSTAT_NODETYPE = "spff:rulingStat";
    private RepositoryService repositoryService_;

    public static final int FIRST_YEAR = 2006;

    private static final String WORKSPACE = "dev-monit";
    private final String REST_ADDRESS = "/rest/jcr/repository/" + WORKSPACE;

    public static final String JASPER_REPORT_FR = "/conf/rulingStatistic_fr.jasper";
    public static final String JASPER_REPORT_NL = "/conf/rulingStatistic_nl.jasper"

    /** NT_FOLDER */
    private static final String NT_FOLDER = "nt:folder";

    /** NT_RESOURCE */
    private static final String NT_RESOURCE = "nt:resource";

    /** JCR_CONTENT */
    private static final String JCR_CONTENT = "jcr:content";

    /** NT_FILE */
    private static final String NT_FILE = "nt:file";

    /** JCR_DATA */
    private static final String JCR_DATA = "jcr:data";

    /** JCR_LAST_MODIFIED */
    private static final String JCR_LAST_MODIFIED = "jcr:lastModified";

    /** JCR_ENCODING */
    private static final String JCR_ENCODING = "jcr:encoding";

    /** JCR_MIME_TYPE */
    private static final String JCR_MIME_TYPE = "jcr:mimeType";

    public RulingStat(RepositoryService repositoryService) throws Exception
	{
		this.repositoryService_ = repositoryService;
	}

    public Map getStat (Map statMap, rootNode, session)
    {
        try
        {
            Iterator iRootNode = rootNode.getNodes();

            while(iRootNode.hasNext())
            {
                println("RootNode hasNext");
                Node child = (Node) iRootNode.next();
                String type = child.getProperty("jcr:primaryType").getString();
                println("child : " + child.getName() + " path : " + child.getPath());
                println("child type : " + type);

                if (type.equals("spff:rulingReguFolder"))
                {
                    println("rulingReguFolder");
                    Node payementNode = (Node) session.getItem("/Files/Ruling/Regularisation/" + child.getName() + "/Payments");

                    println ("paymentNode name : " + payementNode.getName());
                    println("nbr de child : " + payementNode.getNodes().getSize());
                    Iterator iPayementNode = payementNode.getNodes();
                    while (iPayementNode.hasNext())
                    {
                        println("iPayement hasNext");

                        Node dossierNode = iPayementNode.next();

                        type = dossierNode.getProperty("jcr:primaryType").getString();
                        println("dossierNode Type = " + type);
                        if (type.equals("spff:rulingReguPayment"))
                        {
                            println("type.equals(spff:rulingReguPayment)");
                            int year = dossierNode.getProperty("spff:paymentDate").getDate().get(Calendar.YEAR);
                            println("year : " + year);

                            String sYear = year.toString();

                            if(!statMap.containsKey(sYear))
                            {
                                int i = FIRST_YEAR;
                                while(i <= year)
                                {
                                    if (!statMap.containsKey(i.toString()))
                                    {
                                        def array = [
                                            ["0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"],
                                            ["0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"],
                                        ] as String[][];

                                        statMap.put(i.toString(), array);
                                    }
                                    i++;
                                }
                            }
                            getArray(statMap.get(sYear), dossierNode);
                        }
                    }
                }
                else if (type.equals("nt:folder") && child.getName().equals("Archives"))   //folder archive
                {
                    println("Archives");
                    Iterator it = child.getNodes();
                    while (it.hasNext())
                    {
                        Node archivesChild = it.next();
                        type = archivesChild.getProperty("jcr:primaryType").getString();

                        if (type.equals("spff:rulingReguFolder"))
                        {
                            println("Archives rulingReguFolder");
                            Node payementNode = session.getItem("/Files/Ruling/Regularisation/Archives/" + archivesChild.getName() + "/Payments");

                            Iterator iPayementNode = payementNode.getNodes();
                            while (iPayementNode.hasNext())
                            {
                                println("Archives iPayement hasNext");

                                Node dossierNode = iPayementNode.next();

                                type = dossierNode.getProperty("jcr:primaryType").getString();
                                println("Archives payementNode Type = " + type);
                                if (type.equals("spff:rulingReguPayment"))
                                {
                                    int year = dossierNode.getProperty("spff:paymentDate").getDate().get(Calendar.YEAR);
                                    println("Archives year : " + year);

                                    String sYear = year.toString();
                                    if (!statMap.containsKey(sYear))
                                    {
                                        def array = [
                                                ["0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"],
                                                ["0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"],
                                        ] as String[][];

                                        statMap.put(sYear, array);
                                    }
                                    getArray(statMap.get(sYear), dossierNode);
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            println("Exception in RulingStat - getStat : " + e.getMessage());
        }

        return statMap;
    }

    public void getArray(array, child)
    {
        try
        {
            //january = 0, february = 1 ...
            int month = child.getProperty("spff:paymentDate").getDate().get(Calendar.MONTH);

            Double paidAmount = child.getProperty("spff:paidAmount").getDouble();


            //increment number of report
            int report = Integer.parseInt(array[0][month]);
            report++;
            array[0][month] = String.valueOf(report);

            //add final amount to total amount of the month
            Double amount = Double.parseDouble(array[1][month]);

            println("totalAmount per month : " + amount);
            println("paidAmount this file : " + paidAmount);

            amount += paidAmount;
            println("totalAmount per month : " + amount);


            //DecimalFormat amountFormat = new DecimalFormat("###,###.###");

            array[1][month] = amount;

            //println("totalAmount per  month format : " + array[1][month]);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            println("Exception in RulingStat - getArray : " + e.getMessage());
        }
    }

    public void execute(Object context) throws Exception {
        println("execute!!");
        //println("context : " + context);
        String path = (String) context;
        Session session = null;
        Node rootNode= null;

        Map statMap = new HashMap();

        try {
            String[] splittedPath = path.split("&workspaceName=");
            String[] splittedContent = splittedPath[1].split("&repository=");
            String repository = splittedContent[1];
            session = repositoryService_.getRepository(splittedContent[1])
                    .getSystemSession(splittedContent[0]);
            def mynode = (Node) session.getItem(splittedPath[0]);
            def statNode = (Node) session.getItem("/Files/Ruling/Regularisation/statistic");
            rootNode = session.getItem("/Files/Ruling/Regularisation");

            InputStream myjasperstream_fr = getReportTemplate(repository, JASPER_REPORT_FR);
            InputStream myjasperstream_nl = getReportTemplate(repository, JASPER_REPORT_NL);

            statMap = getStat(statMap, rootNode, session);

            String csvString = makeCsvFile(statMap);

            // Data sent to the report engine
            Map parameters = new HashMap();

            Double total = 0;
            Long totalDossier = 0;
            int x = FIRST_YEAR;
            while(statMap.containsKey(x.toString()))
            {
                def arrayByYear = statMap.get(x.toString());
                for (int i = 0 ; i<12; i++)
                {
                    total += Double.valueOf(arrayByYear[1][i]);
                    totalDossier += Integer.valueOf(arrayByYear[0][i]);
                }
                x++;
            }

           /* println("totalMontant : " + total.toString());
            println("totalDossier : " + totalDossier.toString());     */

            Date now = new Date();

            parameters.put("totalMontant", total);
            parameters.put("totalDossier", totalDossier);
            parameters.put("date", now);

            JasperPrint jasperPrint_fr = null;
            JasperPrint jasperPrint_nl = null;

            try
            {
                Locale locale = new Locale("fr", "be");
                parameters.put("REPORT_LOCALE", locale);
                println("parameters : " + parameters.toString());
                jasperPrint_fr = JasperFillManager.fillReport(myjasperstream_fr, parameters, getDataSource(csvString));

                locale = new Locale("nl", "be");
                parameters.put("REPORT_LOCALE", locale);
                println("parameters : " + parameters.toString());
                jasperPrint_nl = JasperFillManager.fillReport(myjasperstream_nl, parameters, getDataSource(csvString));

                Node fileNode;
                Node resNode;
                Calendar lastModified;
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");

                fileNode = statNode.addNode("Regularisation Statistique_" + format.format(now) + ".pdf", NT_FILE);
                resNode = fileNode.addNode(JCR_CONTENT, NT_RESOURCE);
                resNode.setProperty(JCR_MIME_TYPE, "application/pdf");
                resNode.setProperty(JCR_ENCODING, "");
                resNode.setProperty(JCR_DATA, new ByteArrayInputStream(JasperExportManager.exportReportToPdf(jasperPrint_fr)));
                lastModified = Calendar.getInstance();
                resNode.setProperty(JCR_LAST_MODIFIED, lastModified);


                fileNode.getSession().save();
                resNode.getSession().save();

                fileNode = statNode.addNode("Statistiek Regularisatie_" + format.format(now) + ".pdf", NT_FILE);
                resNode = fileNode.addNode(JCR_CONTENT, NT_RESOURCE);
                resNode.setProperty(JCR_MIME_TYPE, "application/pdf");
                resNode.setProperty(JCR_ENCODING, "");
                resNode.setProperty(JCR_DATA, new ByteArrayInputStream(JasperExportManager.exportReportToPdf(jasperPrint_nl)));
                lastModified = Calendar.getInstance();
                resNode.setProperty(JCR_LAST_MODIFIED, lastModified);

                mynode.remove();
                statNode.getSession().save();
                fileNode.getSession().save();
                resNode.getSession().save();
            } catch (Exception e) {
                mynode.remove();
                mynode.getSession().save();
                e.printStackTrace();
                println("Exception in RulingStat - execute : " + e.getMessage());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            println("Exception in RulingStat - execute : " + e.getMessage());
        }
        finally {
            if (session != null) {
                session.logout();
            }
        }
    }

    private static JRCsvDataSource getDataSource (String csvString) throws JRException
    {
        String[] columnNames = ["year", "report_0", "report_1", "report_2", "report_3", "report_4", "report_5", "report_6", "report_7", "report_8", "report_9", "report_10", "report_11", "amount_0", "amount_1", "amount_2", "amount_3", "amount_4", "amount_5", "amount_6", "amount_7", "amount_8", "amount_9", "amount_10", "amount_11"];

        ByteArrayInputStream bs = new ByteArrayInputStream(csvString.getBytes());
        JRCsvDataSource ds = new JRCsvDataSource(bs);
        ds.setFieldDelimiter((char)';');
        ds.setRecordDelimiter(" ");
        ds.setColumnNames(columnNames);
        return ds;
    }

    private String makeCsvFile (Map statMap)
    {
        String csvString = "";

        int j = FIRST_YEAR;

        while(statMap.containsKey(j.toString()))
        {
            if(statMap.containsKey(j.toString()))
            {
                csvString += j.toString() + ";";
                def arrayByYear = statMap.get(j.toString());
                for (int i = 0 ; i<12; i++)
                {
                    csvString  += arrayByYear[0][i] + ";";
                }

                for (int i = 0 ; i<12; i++)
                {
                    if (i<11)
                        csvString  += arrayByYear[1][i] + ";";
                    else
                        csvString  += arrayByYear[1][i];

                    csvString = csvString.replace(".", ",");
                }
                csvString += " ";
            }

            j++;
        }

        csvString = csvString.replace(",", ".");

        println("cvsString : " + csvString);

        return csvString;
    }

    private InputStream getReportTemplate(String repositoryName, String pathName) throws Exception
    {
        Session session;
        session = repositoryService_.getRepository(repositoryName).getSystemSession(RulingStat.WORKSPACE);

        Node documentNode = (Node) session.getItem(pathName);

        if (documentNode != null && NT_FILE.equals(documentNode.getPrimaryNodeType().getName())) {
            Node node4 = documentNode.getNode(JCR_CONTENT);
            if (node4 != null && NT_RESOURCE.equals(node4.getPrimaryNodeType().getName())) {
                return node4.getProperty(JCR_DATA).getStream();
            }
        }
        return null;
    }

    public void setParams(String[] params) {
    }
}