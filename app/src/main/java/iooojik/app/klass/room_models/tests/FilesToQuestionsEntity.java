package iooojik.app.klass.room_models.tests;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


import iooojik.app.klass.AppСonstants;

@Entity(tableName = AppСonstants.TABLE_FILES_TO_QUESTIONS)
public class FilesToQuestionsEntity {
    @PrimaryKey
    long test_id;
    int question_num;
    String file_url;

    public long getTest_id() {
        return test_id;
    }

    public void setTest_id(long test_id) {
        this.test_id = test_id;
    }

    public int getQuestion_num() {
        return question_num;
    }

    public void setQuestion_num(int question_num) {
        this.question_num = question_num;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }
}
