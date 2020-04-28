package iooojik.app.klass.models.paramUsers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ParamData {
    @SerializedName("user")
    @Expose
    private List<UserParams> userParams = null;

    public List<UserParams> getUserParams() {
        return userParams;
    }

    public void setUserParams(List<UserParams> userParams) {
        this.userParams = userParams;
    }
}
