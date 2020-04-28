package iooojik.app.klass.models.groups_messages;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataMessage {

    @SerializedName("messages_to_groups")
    @Expose
    private List<MessagesToGroup> messagesToGroups = null;

    public List<MessagesToGroup> getMessagesToGroups() {
        return messagesToGroups;
    }

    public void setMessagesToGroups(List<MessagesToGroup> messagesToGroups) {
        this.messagesToGroups = messagesToGroups;
    }
}
