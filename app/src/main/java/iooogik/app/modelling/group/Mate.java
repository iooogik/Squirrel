package iooogik.app.modelling.group;

public class Mate {
    int id, averageScore;
    String name, email;

    public Mate(int id, int averageScore, String name, String email) {
        this.id = id;
        this.averageScore = averageScore;
        this.name = name;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public int getAverageScore() {
        return averageScore;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
