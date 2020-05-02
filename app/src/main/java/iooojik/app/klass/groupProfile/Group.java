package iooojik.app.klass.groupProfile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Group {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("author_email")
    @Expose
    private String authorEmail;
    @SerializedName("author_name")
    @Expose
    private String authorName;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("test")
    @Expose
    private String test;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

}
