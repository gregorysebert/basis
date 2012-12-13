package basis.migration.service;

/**
 * Created with IntelliJ IDEA.
 * User: gregorysebert
 * Date: 09/11/12
 * Time: 14:11
 * To change this template use File | Settings | File Templates.
 */
public class BasisFolder {

    private String folderId;
    private String folderLanguage;
    private String folderRegistrationDate;
    private String folderCloseBeforeDate;
    private String folderComments;

    public String getFolderComments() {
        return folderComments;
    }

    public void setFolderComments(String folderComments) {
        this.folderComments = folderComments;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getFolderLanguage() {
        return folderLanguage;
    }

    public void setFolderLanguage(String folderLanguage) {
        this.folderLanguage = folderLanguage;
    }

    public String getFolderRegistrationDate() {
        return folderRegistrationDate;
    }

    public void setFolderRegistrationDate(String folderRegistrationDate) {
        this.folderRegistrationDate = folderRegistrationDate;
    }

    public String getFolderCloseBeforeDate() {
        return folderCloseBeforeDate;
    }

    public void setFolderCloseBeforeDate(String folderCloseBeforeDate) {
        this.folderCloseBeforeDate = folderCloseBeforeDate;
    }

    public String getFolderRNN() {
        return folderRNN;
    }

    public void setFolderRNN(String folderRNN) {
        this.folderRNN = folderRNN;
    }

    public String getFolderExternalReference() {
        return folderExternalReference;
    }

    public void setFolderExternalReference(String folderExternalReference) {
        this.folderExternalReference = folderExternalReference;
    }

    public String getFolderStatus() {
        return folderStatus;
    }

    public void setFolderStatus(String folderStatus) {
        this.folderStatus = folderStatus;
    }

    private String folderRNN;
    private String folderExternalReference;
    private String folderStatus;


}
