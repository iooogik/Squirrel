package iooojik.app.klass.models.bonusCrate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CratesData {
    @SerializedName("bonus_crates_to_users")
    @Expose
    private List<BonusCratesToUser> bonusCratesToUsers = null;

    public List<BonusCratesToUser> getBonusCratesToUsers() {
        return bonusCratesToUsers;
    }

    public void setBonusCratesToUsers(List<BonusCratesToUser> bonusCratesToUsers) {
        this.bonusCratesToUsers = bonusCratesToUsers;
    }

}
