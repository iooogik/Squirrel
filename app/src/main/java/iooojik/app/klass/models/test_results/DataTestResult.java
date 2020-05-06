package iooojik.app.klass.models.test_results;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataTestResult {
    @SerializedName("tests_result")
    @Expose
    private List<TestsResult> testsResult = null;

    public List<TestsResult> getTestsResult() {
        return testsResult;
    }

    public void setTestsResult(List<TestsResult> testsResult) {
        this.testsResult = testsResult;
    }
}
