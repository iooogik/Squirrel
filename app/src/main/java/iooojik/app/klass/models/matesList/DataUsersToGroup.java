package iooojik.app.klass.models.matesList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataUsersToGroup {

    @SerializedName("users_to_group")
    @Expose
    private List<Mates> mates = null;

    public List<Mates> getMates() {
        return mates;
    }

    public void setMates(List<Mates> mates) {
        this.mates = mates;
    }

}
