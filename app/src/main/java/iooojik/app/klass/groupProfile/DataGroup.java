package iooojik.app.klass.groupProfile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataGroup {
    @SerializedName("groups")
    @Expose
    private Group groups;

    public Group getGroups() {
        return groups;
    }

    public void setGroups(Group groups) {
        this.groups = groups;
    }
}
