package iooojik.app.klass.models.file_info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FileObject {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("file_url")
    @Expose
    private String fileUrl;
    @SerializedName("user_email")
    @Expose
    private String userEmail;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
