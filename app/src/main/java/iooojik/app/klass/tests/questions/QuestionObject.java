package iooojik.app.klass.tests.questions;

import java.util.List;

public class QuestionObject {
    private String question;
    private List<String> answers;
    private String trueAnswer;
    private String selectedAnswer;

    QuestionObject(String question, List<String> answers, String trueAnswer) {
        this.question = question;
        this.answers = answers;
        this.trueAnswer = trueAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public String getTrueAnswer() {
        return trueAnswer;
    }

    public void setTrueAnswer(String trueAnswer) {
        this.trueAnswer = trueAnswer;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(String selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }
}
