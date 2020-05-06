package iooojik.app.klass.models.passed_test_result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PassedTest {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("user_email")
    @Expose
    private String userEmail;
    @SerializedName("test_name")
    @Expose
    private String testName;
    @SerializedName("result")
    @Expose
    private String result;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
