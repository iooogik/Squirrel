package iooojik.app.klass.models.notesData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NotesData {
    @SerializedName("notes")
    @Expose
    private List<OnlineNote> onlineNotes = null;

    public List<OnlineNote> getOnlineNotes() {
        return onlineNotes;
    }

    public void setOnlineNotes(List<OnlineNote> onlineNotes) {
        this.onlineNotes = onlineNotes;
    }
}
