package iooojik.app.klass.models.caseData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CaseData {
    @SerializedName("cases_to_users")
    @Expose
    private List<CasesToUser> casesToUsers = null;

    public List<CasesToUser> getCasesToUsers() {
        return casesToUsers;
    }

    public void setCasesToUsers(List<CasesToUser> casesToUsers) {
        this.casesToUsers = casesToUsers;
    }
}
