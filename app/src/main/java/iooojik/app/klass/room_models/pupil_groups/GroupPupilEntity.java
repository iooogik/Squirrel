package iooojik.app.klass.room_models.pupil_groups;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import iooojik.app.klass.AppСonstants;

@Entity(tableName = AppСonstants.TABLE_PUPIL_GROUPS)
public class GroupPupilEntity {
    @PrimaryKey
    long group_id;
    String group_name;
    String author_email;
    String author_name;

    public String getAuthor_email() {
        return author_email;
    }

    public void setAuthor_email(String author_email) {
        this.author_email = author_email;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(long group_id) {
        this.group_id = group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }
}
