package iooojik.app.klass.models.pupil;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataPupilList {

    @SerializedName("users_to_group")
    @Expose
    private List<PupilGroups> pupilGroups = null;

    public List<PupilGroups> getPupilGroups() {
        return pupilGroups;
    }

    public void setPupilGroups(List<PupilGroups> pupilGroups) {
        this.pupilGroups = pupilGroups;
    }

}
