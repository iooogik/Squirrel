package iooojik.app.klass.room_models.tests;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import iooojik.app.klass.AppСonstants;

@Entity(tableName = AppСonstants.TABLE_TESTS)
public class TestEntity {
    @PrimaryKey(autoGenerate = true)
    long _id;
    String name;
    String description;
    String isPassed;
    String questions;
    String answers;
    String textAnswers;
    String trueAnswers;
    String time;
    String totalScore;
    String userScore;
    String scoreForQuest;
    String group_id;
    String score;
    String max_score;

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsPassed() {
        return isPassed;
    }

    public void setIsPassed(String isPassed) {
        this.isPassed = isPassed;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public String getTextAnswers() {
        return textAnswers;
    }

    public void setTextAnswers(String textAnswers) {
        this.textAnswers = textAnswers;
    }

    public String getTrueAnswers() {
        return trueAnswers;
    }

    public void setTrueAnswers(String trueAnswers) {
        this.trueAnswers = trueAnswers;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    public String getUserScore() {
        return userScore;
    }

    public void setUserScore(String userScore) {
        this.userScore = userScore;
    }

    public String getScoreForQuest() {
        return scoreForQuest;
    }

    public void setScoreForQuest(String scoreForQuest) {
        this.scoreForQuest = scoreForQuest;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getMax_score() {
        return max_score;
    }

    public void setMax_score(String max_score) {
        this.max_score = max_score;
    }
}
