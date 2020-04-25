package iooojik.app.klass.models.achievements;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AchievementsData {
    @SerializedName("achievements_to_users")
    @Expose
    private List<AchievementsToUser> achievementsToUsers = null;

    public List<AchievementsToUser> getAchievementsToUsers() {
        return achievementsToUsers;
    }

    public void setAchievementsToUsers(List<AchievementsToUser> achievementsToUsers) {
        this.achievementsToUsers = achievementsToUsers;
    }
}
