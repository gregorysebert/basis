/**
 * Created by IntelliJ IDEA.
 * User: jank
 * Date: 19/01/12
 * Time: 13:15
 * To change this template use File | Settings | File Templates.
 */

import be.spff.eruling.regularisation.nodes.FolderNode
import javax.jcr.Node
import javax.jcr.NodeIterator
import javax.jcr.Session
import javax.jcr.Value
import net.sf.jasperreports.engine.JRException
import net.sf.jasperreports.engine.JasperExportManager
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.data.JRCsvDataSource
import org.exoplatform.services.cms.scripts.CmsScript
import org.exoplatform.services.jcr.RepositoryService
import org.exoplatform.services.jcr.core.ManageableRepository;
import be.spff.eruling.regularisation.beans.CertificateInfo;
import be.spff.eruling.regularisation.beans.YearInfo;
import be.spff.eruling.regularisation.nodes.FolderNode;
import net.sf.jasperreports.engine.data.JRCsvDataSource;
import org.exoplatform.contact.service.Contact;
import org.exoplatform.contact.service.ContactService;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
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

/**
 * Created by IntelliJ IDEA.
 * User: JANSSENS-B
 * Date: 19/10/11
 * Time: 15:27
 * To change this template use File | Settings | File Templates.
 */


public class RegistreRulingStat implements CmsScript {
    private RepositoryService repositoryService_;
    public static final int FIRST_YEAR = 2006;

    private static final String WORKSPACE = "dev-monit";
    private final String REST_ADDRESS = "/rest/jcr/repository/" + WORKSPACE;

    public static final String JASPER_REPORT_FR = "/conf/RegistreRulingStatReport_fr.jasper";
    public static final String JASPER_REPORT_NL = "/conf/RegistreRulingStatReport_nl.jasper"


    public RegistreRulingStat(RepositoryService repositoryService) throws Exception {
        this.repositoryService_ = repositoryService;
    }

    public void execute(Object context) throws Exception {
        println("execute!!");

        Map statMap = new HashMap();

        try {
            String path = (String) context;
            String[] splittedContent = path.split("&workspaceName=");
            String nodePath = splittedContent[0];
            splittedContent = splittedContent[1].split("&repository=");
            String workspace = splittedContent[0];
            String repository = splittedContent[1];

            session = repositoryService_.getRepository(repository).getSystemSession(workspace);

            def statNode = (Node) session.getItem("/Files/Ruling/Regularisation/statistic");
            rootNode = session.getItem("/Files/Ruling/Regularisation");


            InputStream myjasperstream_fr = getReportTemplate(repository, JASPER_REPORT_FR);
            InputStream myjasperstream_nl = getReportTemplate(repository, JASPER_REPORT_NL);

            getStat(statMap, rootNode, session);

            println("StatMap : " + statMap.toMapString());

            String csvString = makeCsvFile(statMap);

            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");

            // Data sent to the report engine
            Map parameters = new HashMap();

            parameters.put("NbrYear", statMap.size());
            parameters.put("date", now);

            println("parameters : " + parameters.toString());

            JasperPrint jasperPrint_fr = null;
            JasperPrint jasperPrint_nl = null;

            try {
                Locale locale = new Locale("fr", "be");
                parameters.put("REPORT_LOCALE", locale);
                jasperPrint_fr = JasperFillManager.fillReport(myjasperstream_fr, parameters, getDataSource(csvString));

                locale = new Locale("nl", "be");
                parameters.put("REPORT_LOCALE", locale);
                jasperPrint_nl = JasperFillManager.fillReport(myjasperstream_nl, parameters, getDataSource(csvString));


                Node fileNode;
                Node resNode;
                Calendar lastModified;


                fileNode = statNode.addNode("Registre Regularisation Statistique_" + format.format(now) + ".pdf", NT_FILE);
                resNode = fileNode.addNode(JCR_CONTENT, NT_RESOURCE);
                resNode.setProperty(JCR_MIME_TYPE, "application/pdf");
                resNode.setProperty(JCR_ENCODING, "");
                resNode.setProperty(JCR_DATA, new ByteArrayInputStream(JasperExportManager.exportReportToPdf(jasperPrint_fr)));
                lastModified = Calendar.getInstance();
                resNode.setProperty(JCR_LAST_MODIFIED, lastModified);


                fileNode.getSession().save();
                resNode.getSession().save();

                fileNode = statNode.addNode("Statistische Gegevens Geregulariseerde Dossiers_" + format.format(now) + ".pdf", NT_FILE);
                resNode = fileNode.addNode(JCR_CONTENT, NT_RESOURCE);
                resNode.setProperty(JCR_MIME_TYPE, "application/pdf");
                resNode.setProperty(JCR_ENCODING, "");
                resNode.setProperty(JCR_DATA, new ByteArrayInputStream(JasperExportManager.exportReportToPdf(jasperPrint_nl)));
                lastModified = Calendar.getInstance();
                resNode.setProperty(JCR_LAST_MODIFIED, lastModified);

                //mynode.remove();
                statNode.getSession().save();
                fileNode.getSession().save();
                resNode.getSession().save();
            } catch (Exception e) {
                //mynode.remove();
                //mynode.getSession().save();
                e.printStackTrace();
                println("Exception in RegistreRulingStat - execute : " + e.getMessage());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            println("Exception in RegistreRulingStat - execute : " + e.getMessage());
        }
        finally {
            if (session != null) {
                session.logout();
            }
        }
    }

    void setParams(String[] strings) {

    }


    void getStat(Map statMap, rootNode, session) {
        try {
            Iterator iRootNode = rootNode.getNodes();

            while (iRootNode.hasNext()) {
                println("RootNode hasNext");
                Node child = (Node) iRootNode.next();
                String type = child.getProperty("jcr:primaryType").getString();
                println("child : " + child.getName() + " path : " + child.getPath());
                println("child type : " + type);

                if (type.equals("spff:rulingReguFolder")) {
                    println("rulingReguFolder");
                    int year = child.getProperty("spff:receptionDate").getDate().get(Calendar.YEAR);
                    println("year : " + year);

                    String sYear = year.toString();

                    if (!statMap.containsKey(sYear)) {
                        int i = FIRST_YEAR;
                        while (i <= year) {
                            if (!statMap.containsKey(i.toString())) {
                                ArrayList<String> array = new ArrayList<String>();

                                statMap.put(i.toString(), array);
                            }
                            i++;
                        }
                    }

                    getArray(statMap.get(sYear), child);
                }
                else if (type.equals("nt:folder") && child.getName().equals("Archives"))   //folder archive
                {
                    println("Archives");
                    Iterator it = child.getNodes();
                    while (it.hasNext()) {
                        Node archivesChild = it.next();
                        type = archivesChild.getProperty("jcr:primaryType").getString();

                        println("archivesChild : " + archivesChild.getName() + " path : " + archivesChild.getPath());
                        println("archivesChild type : " + type);

                        if (type.equals("spff:rulingReguFolder")) {
                            println("Archives rulingReguFolder");
                            int year = archivesChild.getProperty("spff:receptionDate").getDate().get(Calendar.YEAR);
                            println("year : " + year);

                            String sYear = year.toString();

                            if (!statMap.containsKey(sYear)) {
                                int i = FIRST_YEAR;
                                while (i <= year) {
                                    if (!statMap.containsKey(i.toString())) {
                                        ArrayList<String> array = new ArrayList<String>();

                                        statMap.put(i.toString(), array);
                                    }
                                    i++;
                                }
                            }

                            getArray(statMap.get(sYear), archivesChild);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            println("Exception in RegistreRulingStat - getStat : " + e.getMessage());
        }

    }

    /**
     * array[0] = # dossiers
     * array[1] = Autres revenus
     * array[2] = Revenus Professionnels
     * array[3] = Opérations TVA
     * array[4] = Total
     * array[5] = Autres revenus Payé
     * array[6] = Revenus Professionnels Payé
     * array[7] = Opérations TVA Payé
     * array[8] = Total Payé
     *
     * @param array
     * @param child
     */
    void getArray(ArrayList<String> array, Node child) {
        try {
            FolderNode folderNode = new FolderNode(child);
            def name = child.getName();
            def values;
            def valueDisplay;
            String pattern = "dd/mm/yyyy";
            SimpleDateFormat dateFormat = new SimpleDateFormat();
            dateFormat.applyPattern(pattern);
            Double sumYearDeclaredAmount2 = 0.0;
            Double sumYearDeclaredAmount3 = 0.0;
            Double sumYearDeclaredAmount1 = 0.0;
            Double sumYearDec = 0.0;
            Double sumYearProf_TL_Federal = 0.0;
            Double sumYearProf_TL_Urban = 0.0;
            Double sumYearOtherSuccession_TL_Bxl = 0.0;
            Double sumYearOtherSuccession_TL_Vl = 0.0;
            Double sumYearOtherSuccession_TL_Wa = 0.0;
            Double sumYearOtherSuccession_TL_Fed = 0.0;
            Double sumYearOtherRegist_TL_Bxl = 0.0;
            Double sumYearOtherRegist_TL_Vl = 0.0;
            Double sumYearOtherRegist_TL_Wa = 0.0;
            Double sumYearOtherRegist_TL_Fed = 0.0;
            Double sumYearOther_TL = 0.0;
            Double sumYearRealEstate_TotalLevy_Fed = 0.0;
            Double sumYearRealEstate_CalcTL_Urban = 0.0;
            Double sumYearInvestment_TL_Fed = 0.0;
            Double sumYearInvestment_TL_Urban = 0.0;
            Double sumYearVAT = 0.0;
            Map<String, Double> mapReTlCity = new HashMap<String, Double>();
            Map<String, Double> mapProfTlCity = new HashMap<String, Double>();
            Map<String, Double> otherInvestCity = new HashMap<String, Double>();
            Map<String, Double> mapYearVat = new HashMap<String, Double>();

            Double sumYearProf = 0.0;
            Double sumYearOtherSucc;
            Double sumYearOtherRegist;
            Double sumYearRE = 0.0;
            Double sumYearInvest = 0.0;
            Double totalOfSums;

            NodeIterator ni = child.getNodes();
            while (ni.hasNext()) {
                Node yearnode = (Node) ni.next();
                if (yearnode.getPrimaryNodeType().getName().equals("spff:rulingReguYear")) {
                    if (yearnode.hasProperty("spff:yearDeclaredAmount2")) {
                        sumYearDeclaredAmount2 += yearnode.getProperty("spff:yearDeclaredAmount2").getDouble();
                    }
                    if (yearnode.hasProperty("spff:yearDeclaredAmount3")) {
                        sumYearDeclaredAmount3 += yearnode.getProperty("spff:yearDeclaredAmount3").getDouble();
                    }
                    if (yearnode.hasProperty("spff:yearDeclaredAmount1")) {
                        sumYearDeclaredAmount1 += yearnode.getProperty("spff:yearDeclaredAmount1").getDouble();
                    }
                    if (yearnode.hasProperty("spff:yearProf_TL_Federal")) {
                        sumYearProf_TL_Federal += yearnode.getProperty("spff:yearProf_TL_Federal").getDouble();
                    }
                    if (yearnode.hasProperty("spff:yearProf_TL_Urban")) {
                        sumYearProf_TL_Urban += yearnode.getProperty("spff:yearProf_TL_Urban").getDouble();
                    }
                    if (yearnode.hasProperty("spff:yearOtherSuccession_TL_Bxl")) {
                        sumYearOtherSuccession_TL_Bxl += yearnode.getProperty("spff:yearOtherSuccession_TL_Bxl").getDouble();
                    }
                    if (yearnode.hasProperty("spff:yearOtherSuccession_TL_Vl")) {
                        sumYearOtherSuccession_TL_Vl += yearnode.getProperty("spff:yearOtherSuccession_TL_Vl").getDouble();
                    }
                    if (yearnode.hasProperty("spff:yearOtherSuccession_TL_Wa")) {
                        sumYearOtherSuccession_TL_Wa += yearnode.getProperty("spff:yearOtherSuccession_TL_Wa").getDouble();
                    }
                    if (yearnode.hasProperty("spff:yearOtherSuccession_TL_Fed")) {
                        sumYearOtherSuccession_TL_Fed += yearnode.getProperty("spff:yearOtherSuccession_TL_Fed").getDouble();
                    }
                    if (yearnode.hasProperty("spff:yearOtherRegist_TL_Bxl")) {
                        sumYearOtherRegist_TL_Bxl += yearnode.getProperty("spff:yearOtherRegist_TL_Bxl").getDouble();
                    }
                    if (yearnode.hasProperty("spff:yearOtherRegist_TL_Vl")) {
                        sumYearOtherRegist_TL_Vl += yearnode.getProperty("spff:yearOtherRegist_TL_Vl").getDouble();
                    }
                    if (yearnode.hasProperty("spff:yearOtherRegist_TL_Wa")) {
                        sumYearOtherRegist_TL_Wa += yearnode.getProperty("spff:yearOtherRegist_TL_Wa").getDouble();
                    }
                    if (yearnode.hasProperty("spff:yearOtherRegist_TL_Fed")) {
                        sumYearOtherRegist_TL_Fed += yearnode.getProperty("spff:yearOtherRegist_TL_Fed").getDouble();
                    }
                    if (yearnode.hasProperty("spff:yearOtherOther_TL_Total")) {
                        sumYearOther_TL += yearnode.getProperty("spff:yearOtherOther_TL_Total").getValues()[0].getDouble();
                    }
                    if (yearnode.hasProperty("spff:yearRealEstate_TotalLevy_Fed")) {
                        sumYearRealEstate_TotalLevy_Fed += yearnode.getProperty("spff:yearRealEstate_TotalLevy_Fed").getDouble();
                    }
                    if (yearnode.hasProperty("spff:yearRealEstate_CalcTL_Urban")) {
                        sumYearRealEstate_CalcTL_Urban += yearnode.getProperty("spff:yearRealEstate_CalcTL_Urban").getDouble();
                    }

                    if (yearnode.hasProperty("spff:yearInvestment_TL_Fed"))
                        sumYearInvestment_TL_Fed += yearnode.getProperty("spff:yearInvestment_TL_Fed").getDouble();

                    if (yearnode.hasProperty("spff:yearInvestment_TL_Urban")) {
                        sumYearInvestment_TL_Urban += yearnode.getProperty("spff:yearInvestment_TL_Urban").getDouble();
                    }

                    String postCode = yearnode.getProperty("spff:yearRealEstate_PostCode").getString()
                    if (mapReTlCity.containsKey(postCode))
                        mapReTlCity.put(postCode, mapReTlCity.get(postCode) + yearnode.getProperty("spff:yearRealEstate_CalcTL_City").getDouble());
                    else
                        mapReTlCity.put(postCode, yearnode.getProperty("spff:yearRealEstate_CalcTL_City").getDouble());

                    postCode = yearnode.getProperty("spff:yearPostCode").getString()
                    if (mapProfTlCity.containsKey(postCode))
                        mapProfTlCity.put(postCode, mapProfTlCity.get(postCode) + yearnode.getProperty("spff:yearProf_TL_City").getDouble());
                    else
                        mapProfTlCity.put(postCode, yearnode.getProperty("spff:yearProf_TL_City").getDouble());

                    postCode = yearnode.getProperty("spff:yearPostCode").getString()
                    if (otherInvestCity.containsKey(postCode))
                        otherInvestCity.put(postCode, otherInvestCity.get(postCode) + yearnode.getProperty("spff:yearInvestment_TL_City").getDouble());
                    else
                        otherInvestCity.put(postCode, yearnode.getProperty("spff:yearInvestment_TL_City").getDouble());

                    Value[] yearVatPercents = yearnode.getProperty("spff:yearVAT_Percent").getValues();
                    Value[] yearVatTls = yearnode.getProperty("spff:yearVAT_TL").getValues();
                    String[] listValuePercents = (yearVatPercents[0].getString()).split(";");
                    String[] listValueTls = (yearVatTls[0].getString()).split(";");
                    int i = 0;
                    for (String yearVatPercent : listValuePercents) {
                        String yearVatTl = listValueTls[i];
                        i++;
                        if (mapYearVat.containsKey(postCode))
                            mapYearVat.put(yearVatPercent, otherInvestCity.get(postCode) + Double.valueOf(yearVatTl));
                        else
                            mapYearVat.put(yearVatPercent, Double.valueOf(yearVatTl));
                    }
                    sumYearVAT += yearnode.getProperty("spff:yearVAT_Total_TL").getDouble();
                }
            }

            for (Iterator<String> pcitr = mapProfTlCity.keySet().iterator(); pcitr.hasNext();) {
                String TlCityPostCode = pcitr.next();
                sumYearProf += mapProfTlCity.get(TlCityPostCode);
            }

            for (Iterator<String> pcitr = mapReTlCity.keySet().iterator(); pcitr.hasNext();) {
                String ReTlCityPostCode = pcitr.next();
                sumYearRE += mapReTlCity.get(ReTlCityPostCode);
            }

            for (Iterator<String> pcitr = otherInvestCity.keySet().iterator(); pcitr.hasNext();) {
                String TlCityPostCode = pcitr.next();
                sumYearInvest += otherInvestCity.get(TlCityPostCode);
            }

            sumYearProf += sumYearProf_TL_Federal + sumYearProf_TL_Urban;
            sumYearOtherSucc = sumYearOtherSuccession_TL_Bxl + sumYearOtherSuccession_TL_Vl + sumYearOtherSuccession_TL_Wa + sumYearOtherSuccession_TL_Fed;
            sumYearOtherRegist = sumYearOtherRegist_TL_Bxl + sumYearOtherRegist_TL_Vl + sumYearOtherRegist_TL_Wa + sumYearOtherRegist_TL_Fed;
            sumYearRE += sumYearRealEstate_TotalLevy_Fed + sumYearRealEstate_CalcTL_Urban;
            sumYearInvest += sumYearInvestment_TL_Fed + sumYearInvestment_TL_Urban;
            totalOfSums = sumYearProf + sumYearOtherSucc + sumYearOtherRegist + sumYearRE + sumYearInvest + sumYearVAT;

            Double amount1 = child.getProperty("spff:declaredAmount1").getDouble();
            Double amount2 = child.getProperty("spff:declaredAmount2").getDouble();
            Double amount3 = child.getProperty("spff:declaredAmount3").getDouble();
            Double total = amount1 + amount2 + amount3;

            if (!array.empty) {
                int nbrDossier = Integer.parseInt(array.get(0));
                println("nbrDossier : " + nbrDossier);
                nbrDossier = nbrDossier + 1;
                println("nbrDossier : " + nbrDossier);
                array.set(0, nbrDossier.toString());
                array.set(1, ((Double.parseDouble(array.get(1))) + amount1).toString());
                array.set(2, ((Double.parseDouble(array.get(2))) + amount2).toString());
                array.set(3, ((Double.parseDouble(array.get(3))) + amount3).toString());
                array.set(4, ((Double.parseDouble(array.get(4))) + total).toString());
                array.set(5, ((Double.parseDouble(array.get(5))) + sumYearOtherSucc + sumYearOtherRegist + sumYearRE + sumYearInvest).toString());
                array.set(6, ((Double.parseDouble(array.get(6))) + sumYearProf).toString());
                array.set(7, ((Double.parseDouble(array.get(7))) + sumYearVAT).toString());
                array.set(8, ((Double.parseDouble(array.get(8))) + totalOfSums).toString());
            }
            else {
                array.add(0, "1");
                array.add(1, amount1.toString());
                array.add(2, amount2.toString());
                array.add(3, amount3.toString());
                array.add(4, total.toString());
                array.add(5, (sumYearOtherSucc + sumYearOtherRegist + sumYearRE + sumYearInvest).toString());
                array.add(6, sumYearProf.toString());
                array.add(7, sumYearVAT.toString());
                array.add(8, totalOfSums.toString());
            }

            println("array : " + array.toListString());
        }
        catch (Exception e) {
            e.printStackTrace();
            println("Exception in RegistreRulingStat - getArray : " + e.getMessage());
        }
    }

    private static JRCsvDataSource getDataSource(String csvString) throws JRException {
        String[] columnNames = ["year", "1", "2", "3", "4", "5", "6", "7", "8", "9"];

        ByteArrayInputStream bs = new ByteArrayInputStream(csvString.getBytes());
        JRCsvDataSource ds = new JRCsvDataSource(bs);
        ds.setFieldDelimiter((char) ';');
        ds.setRecordDelimiter(" ");
        ds.setColumnNames(columnNames);
        return ds;
    }

    private String makeCsvFile(Map statMap) {
        String csvString = "";

        int j = FIRST_YEAR;

        while (statMap.containsKey(j.toString())) {
            if (statMap.containsKey(j.toString())) {
                csvString += j.toString() + ";";
                ArrayList<String> arrayByYear = statMap.get(j.toString());
                for (int i = 0; i < 9; i++) {
                    if (arrayByYear.size() < 9)
                        csvString += "0;";
                    else
                        csvString += arrayByYear.get(i) + ";";
                }

                csvString += " ";
            }

            j++;
        }

        //csvString = csvString.replace(".", ",");

        println("cvsString : " + csvString);

        return csvString;
    }

    private InputStream getReportTemplate(String repositoryName, String pathName) throws Exception {
        Session session;
        session = repositoryService_.getRepository(repositoryName).getSystemSession(RegistreRulingStat.WORKSPACE);
        Node documentNode = (Node) session.getItem(pathName);
        if (documentNode != null && NT_FILE.equals(documentNode.getPrimaryNodeType().getName())) {
            Node node4 = documentNode.getNode(JCR_CONTENT);
            if (node4 != null && NT_RESOURCE.equals(node4.getPrimaryNodeType().getName())) {
                return node4.getProperty(JCR_DATA).getStream();
            }
        }
        return null;
    }

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
}