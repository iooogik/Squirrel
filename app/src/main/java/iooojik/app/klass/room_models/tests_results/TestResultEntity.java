package iooojik.app.klass.room_models.tests_results;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import iooojik.app.klass.AppСonstants;

@Entity(tableName = AppСonstants.TABLE_TESTS_RESULTS)
public class TestResultEntity {
    @PrimaryKey(autoGenerate = true)
    long _id;
    String test_name;
    int result;

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getTest_name() {
        return test_name;
    }

    public void setTest_name(String test_name) {
        this.test_name = test_name;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
