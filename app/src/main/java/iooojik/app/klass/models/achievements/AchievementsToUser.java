package iooojik.app.klass.models.achievements;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AchievementsToUser {
    @SerializedName("user_email")
    @Expose
    private String userEmail;
    @SerializedName("coins")
    @Expose
    private String coins;
    @SerializedName("_id")
    @Expose
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }
}
