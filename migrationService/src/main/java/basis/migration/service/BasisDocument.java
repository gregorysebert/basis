package basis.migration.service;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: gregorysebert
 * Date: 09/11/12
 * Time: 13:59
 * To change this template use File | Settings | File Templates.
 */
public class BasisDocument {

    private String docType;
    private Date docRegistrationDate;
    private Date docDate;
    private String docReference;
    private String docKeywords;
    private String docInternSender;
    private String docExternSenderName;
    private String docExternSenderAdress;
    private String docExternSenderZipCode;
    private String docExternSenderCity;
    private String docExternSenderCountry;
    private Date sysDate;


    public Date getSysDate() {
        return sysDate;
    }

    public void setSysDate(Date sysDate) {
        this.sysDate = sysDate;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public Date getDocRegistrationDate() {
        return docRegistrationDate;
    }

    public void setDocRegistrationDate(Date docRegistrationDate) {
        this.docRegistrationDate = docRegistrationDate;
    }

    public Date getDocDate() {
        return docDate;
    }

    public void setDocDate(Date docDate) {
        this.docDate = docDate;
    }

    public String getDocReference() {
        return docReference;
    }

    public void setDocReference(String docReference) {
        this.docReference = docReference;
    }

    public String getDocKeywords() {
        return docKeywords;
    }

    public void setDocKeywords(String docKeywords) {
        this.docKeywords = docKeywords;
    }

    public String getDocInternSender() {
        return docInternSender;
    }

    public void setDocInternSender(String docInternSender) {
        this.docInternSender = docInternSender;
    }

    public String getDocExternSenderName() {
        return docExternSenderName;
    }

    public void setDocExternSenderName(String docExternSenderName) {
        this.docExternSenderName = docExternSenderName;
    }

    public String getDocExternSenderAdress() {
        return docExternSenderAdress;
    }

    public void setDocExternSenderAdress(String docExternSenderAdress) {
        this.docExternSenderAdress = docExternSenderAdress;
    }

    public String getDocExternSenderZipCode() {
        return docExternSenderZipCode;
    }

    public void setDocExternSenderZipCode(String docExternSenderZipCode) {
        this.docExternSenderZipCode = docExternSenderZipCode;
    }

    public String getDocExternSenderCity() {
        return docExternSenderCity;
    }

    public void setDocExternSenderCity(String docExternSenderCity) {
        this.docExternSenderCity = docExternSenderCity;
    }

    public String getDocExternSenderCountry() {
        return docExternSenderCountry;
    }

    public void setDocExternSenderCountry(String docExternSenderCountry) {
        this.docExternSenderCountry = docExternSenderCountry;
    }
}
