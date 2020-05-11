package iooojik.app.klass.models.test_results;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TestsResult {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("user_email")
    @Expose
    private String userEmail;
    @SerializedName("group_id")
    @Expose
    private String groupId;
    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("difficultiesCount")
    @Expose
    private String difficultiesCount;

    public String getDifficultiesCount() {
        return difficultiesCount;
    }

    public void setDifficultiesCount(String difficultiesCount) {
        this.difficultiesCount = difficultiesCount;
    }

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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
