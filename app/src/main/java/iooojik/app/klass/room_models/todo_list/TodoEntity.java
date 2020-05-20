package iooojik.app.klass.room_models.todo_list;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import iooojik.app.klass.AppСonstants;

@Entity(tableName = AppСonstants.TABLE_TODO_NAME)
public class TodoEntity {
    @PrimaryKey(autoGenerate = true)
    long _id;
    String text;
    String checked;

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }
}
