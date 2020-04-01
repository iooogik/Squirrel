package iooojik.app.klass.profile.teacher;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataGroup {
    @SerializedName("groups")
    @Expose
    private List<GroupInfo> groupInfos = null;

    public List<GroupInfo> getGroupInfos() {
        return groupInfos;
    }

    public void setGroupInfos(List<GroupInfo> groupInfos) {
        this.groupInfos = groupInfos;
    }
}
