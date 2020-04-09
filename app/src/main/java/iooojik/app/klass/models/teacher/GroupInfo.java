package iooojik.app.klass.models.teacher;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroupInfo {


    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("author_email")
    @Expose
    private String authorEmail;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("test")
    @Expose
    private String test;

    @SerializedName("author_name")
    @Expose
    private String author_name;

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
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
