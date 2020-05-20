package iooojik.app.klass.room_models.mates;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import iooojik.app.klass.AppСonstants;

@Entity(tableName = AppСonstants.TABLE_MATES_LIST)
public class MateEntity {
    @PrimaryKey(autoGenerate = true)
    long _id;
    long mate_id;
    long mate_group_id;
    int test_result;
    String mate_name;
    String mate_email;
    String mate_avatar;

    public int getTest_result() {
        return test_result;
    }

    public void setTest_result(int test_result) {
        this.test_result = test_result;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public long getMate_id() {
        return mate_id;
    }

    public void setMate_id(long mate_id) {
        this.mate_id = mate_id;
    }

    public long getMate_group_id() {
        return mate_group_id;
    }

    public void setMate_group_id(long mate_group_id) {
        this.mate_group_id = mate_group_id;
    }

    public String getMate_name() {
        return mate_name;
    }

    public void setMate_name(String mate_name) {
        this.mate_name = mate_name;
    }

    public String getMate_email() {
        return mate_email;
    }

    public void setMate_email(String mate_email) {
        this.mate_email = mate_email;
    }

    public String getMate_avatar() {
        return mate_avatar;
    }

    public void setMate_avatar(String mate_avatar) {
        this.mate_avatar = mate_avatar;
    }
}
