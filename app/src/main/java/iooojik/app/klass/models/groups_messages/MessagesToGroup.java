package iooojik.app.klass.models.groups_messages;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessagesToGroup {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("group_id")
    @Expose
    private String groupId;
    @SerializedName("message")
    @Expose
    private String message;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
