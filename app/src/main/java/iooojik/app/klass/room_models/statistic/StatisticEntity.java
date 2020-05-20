package iooojik.app.klass.room_models.statistic;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import iooojik.app.klass.AppСonstants;

@Entity(tableName = AppСonstants.TABLE_STATISTIC)
public class StatisticEntity {
    @PrimaryKey(autoGenerate = true)
    private long _id;

    private int test_time;
    private int count_tests;
    private int score;
    private String date;

    public long getId() {
        return _id;
    }

    public void setId(long id) {
        this._id = id;
    }

    public int getTest_time() {
        return test_time;
    }

    public void setTest_time(int test_time) {
        this.test_time = test_time;
    }

    public int getCount_tests() {
        return count_tests;
    }

    public void setCount_tests(int count_tests) {
        this.count_tests = count_tests;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
