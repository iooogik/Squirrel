package iooogik.app.modelling;

public class TestTheme {
    private String name;
    private String desc;
    private float rightAnswers;
    private float wrongAnswers;
    private boolean isPassed;


    TestTheme(String name, String desc, float rightAnswers, float wrongAnswers, boolean isPassed){
        this.name = name;
        this.desc = desc;
        this.rightAnswers = rightAnswers;
        this.wrongAnswers = wrongAnswers;
        this.isPassed = isPassed;
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

    public float getRightAnswers() {
        return rightAnswers;
    }

    public void setRightAnswers(float rightAnswers) {
        this.rightAnswers = rightAnswers;
    }

    public float getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(float wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }
}
