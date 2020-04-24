package iooojik.app.klass.tests;

public class TestTheme {
    private String name;
    private String desc;
    private int userScore;
    private int wrongAnswers;
    private boolean isPassed;
    private int id;


    public TestTheme(String name, String desc, int userScore, int wrongAnswers, boolean isPassed, int id) {
        this.name = name;
        this.desc = desc;
        this.userScore = userScore;
        this.wrongAnswers = wrongAnswers;
        this.isPassed = isPassed;
        this.id = id;
    }

    public int getUserScore() {
        return userScore;
    }

    public void setUserScore(int userScore) {
        this.userScore = userScore;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(int wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isPassed() {
        return isPassed;
    }

    public void setPassed(boolean passed) {
        isPassed = passed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
