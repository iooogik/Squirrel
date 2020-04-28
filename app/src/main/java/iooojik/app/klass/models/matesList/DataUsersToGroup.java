package iooojik.app.klass.models.matesList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataUsersToGroup {

    @SerializedName("users_to_group")
    @Expose
    private List<Mate> mates = null;

    public List<Mate> getMates() {
        return mates;
    }

    public void setMates(List<Mate> mates) {
        this.mates = mates;
    }

}
