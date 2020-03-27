package iooojik.app.klass.tests;

public class NewQuestion {

    String name;
    String description;
    String question;
    String textAnswers;
    String answers;

    public NewQuestion(String name, String description, String question, String textAnswers, String answers) {
        this.name = name;
        this.description = description;
        this.question = question;
        this.textAnswers = textAnswers;
        this.answers = answers;
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

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getTextAnswers() {
        return textAnswers;
    }

    public void setTextAnswers(String textAnswers) {
        this.textAnswers = textAnswers;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }
}
