package iooojik.app.klass.tests.tests;

public class TestObject {
    private String name;
    private String desc;
    private int userScore;
    private int wrongAnswers;
    private boolean isPassed;
    private int id;


    TestObject(String name, String desc, int userScore, int wrongAnswers, boolean isPassed, int id) {
        this.name = name;
        this.desc = desc;
        this.userScore = userScore;
        this.wrongAnswers = wrongAnswers;
        this.isPassed = isPassed;
        this.id = id;
    }

    int getUserScore() {
        return userScore;
    }

    int getWrongAnswers() {
        return wrongAnswers;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    boolean isPassed() {
        return isPassed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String getDesc() {
        return desc;
    }

}
