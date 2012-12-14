package basis.migration.service;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: gregorysebert
 * Date: 13/12/12
 * Time: 15:11
 * To change this template use File | Settings | File Templates.
 */
public class BasisFiche {
    private String ficheId;
    private Date followSendDate;
    private String followEditorType;
    private String followUserInternEditor;
    private String followGroupInternEditor;
    private String followExternEditor;
    private String followRequiredAction;
    private String followComments;
    private Date followAnswerByDate;

    public String getFicheId() {
        return ficheId;
    }

    public void setFicheId(String ficheId) {
        this.ficheId = ficheId;
    }

    public Date getFollowSendDate() {
        return followSendDate;
    }

    public void setFollowSendDate(Date followSendDate) {
        this.followSendDate = followSendDate;
    }

    public String getFollowEditorType() {
        return followEditorType;
    }

    public void setFollowEditorType(String followEditorType) {
        this.followEditorType = followEditorType;
    }

    public String getFollowUserInternEditor() {
        return followUserInternEditor;
    }

    public void setFollowUserInternEditor(String followUserInternEditor) {
        this.followUserInternEditor = followUserInternEditor;
    }

    public String getFollowGroupInternEditor() {
        return followGroupInternEditor;
    }

    public void setFollowGroupInternEditor(String followGroupInternEditor) {
        this.followGroupInternEditor = followGroupInternEditor;
    }

    public String getFollowExternEditor() {
        return followExternEditor;
    }

    public void setFollowExternEditor(String followExternEditor) {
        this.followExternEditor = followExternEditor;
    }

    public String getFollowRequiredAction() {
        return followRequiredAction;
    }

    public void setFollowRequiredAction(String followRequiredAction) {
        this.followRequiredAction = followRequiredAction;
    }

    public String getFollowComments() {
        return followComments;
    }

    public void setFollowComments(String followComments) {
        this.followComments = followComments;
    }

    public Date getFollowAnswerByDate() {
        return followAnswerByDate;
    }

    public void setFollowAnswerByDate(Date followAnswerByDate) {
        this.followAnswerByDate = followAnswerByDate;
    }

}
