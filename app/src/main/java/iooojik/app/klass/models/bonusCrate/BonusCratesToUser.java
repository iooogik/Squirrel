package iooojik.app.klass.models.bonusCrate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BonusCratesToUser {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("user_email")
    @Expose
    private String userEmail;
    @SerializedName("count")
    @Expose
    private String count;

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

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
