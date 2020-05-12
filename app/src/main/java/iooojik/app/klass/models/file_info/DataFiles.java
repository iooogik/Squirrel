package iooojik.app.klass.models.file_info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataFiles {
    @SerializedName("files_to_users")
    @Expose
    private List<FileObject> filesToUsers = null;

    public List<FileObject> getFilesToUsers() {
        return filesToUsers;
    }

    public void setFilesToUsers(List<FileObject> filesToUsers) {
        this.filesToUsers = filesToUsers;
    }
}
