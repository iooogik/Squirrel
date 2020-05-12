package iooojik.app.klass.models.isUserGetTest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataIsUserGetTest {
    @SerializedName("is_user_get_test")
    @Expose
    private List<IsUserGetTest> isUserGetTest = null;

    public List<IsUserGetTest> getIsUserGetTest() {
        return isUserGetTest;
    }

    public void setIsUserGetTest(List<IsUserGetTest> isUserGetTest) {
        this.isUserGetTest = isUserGetTest;
    }
}
