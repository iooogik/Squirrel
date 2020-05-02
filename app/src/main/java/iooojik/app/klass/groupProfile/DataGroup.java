package iooojik.app.klass.groupProfile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataGroup {
    @SerializedName("groups")
    @Expose
    private Group groups;

    public DataGroup(Group groups) {
        this.groups = groups;
    }

    Group getGroups() {
        return groups;
    }

}
