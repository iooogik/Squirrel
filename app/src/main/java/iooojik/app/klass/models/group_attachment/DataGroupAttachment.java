package iooojik.app.klass.models.group_attachment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataGroupAttachment {
    @SerializedName("files_to_groups")
    @Expose
    private List<AttachmentInfo> filesToGroups = null;

    public List<AttachmentInfo> getFilesToGroups() {
        return filesToGroups;
    }

    public void setFilesToGroups(List<AttachmentInfo> filesToGroups) {
        this.filesToGroups = filesToGroups;
    }
}
