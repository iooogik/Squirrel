package iooojik.app.klass.models.isUserGetTest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IsUserGetTest {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("group_id")
    @Expose
    private String groupId;
    @SerializedName("user_email")
    @Expose
    private String userEmail;
    @SerializedName("is_Passed")
    @Expose
    private String is_Passed;

    public String getIs_Passed() {
        return is_Passed;
    }

    public void setIs_Passed(String is_Passed) {
        this.is_Passed = is_Passed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
