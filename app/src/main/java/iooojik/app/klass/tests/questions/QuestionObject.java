package iooojik.app.klass.tests.questions;

import java.util.List;

public class QuestionObject {
    private String question;
    private List<String> answers;
    private String trueAnswer;
    private String selectedAnswer;
    private int score;
    private String fileURL;

    QuestionObject(String question, List<String> answers, String trueAnswer, int score, String fileURL) {
        this.question = question;
        this.answers = answers;
        this.trueAnswer = trueAnswer;
        this.score = score;
        this.fileURL = fileURL;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    String getTrueAnswer() {
        return trueAnswer;
    }

    public void setTrueAnswer(String trueAnswer) {
        this.trueAnswer = trueAnswer;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    void setSelectedAnswer(String selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }
}
