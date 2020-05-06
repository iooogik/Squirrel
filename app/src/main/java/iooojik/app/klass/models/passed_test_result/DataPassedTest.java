package iooojik.app.klass.models.passed_test_result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataPassedTest {
    @SerializedName("passed_tests")
    @Expose
    private List<PassedTest> passedTests = null;

    public List<PassedTest> getPassedTests() {
        return passedTests;
    }

    public void setPassedTests(List<PassedTest> passedTests) {
        this.passedTests = passedTests;
    }

}
