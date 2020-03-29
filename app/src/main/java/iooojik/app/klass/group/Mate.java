package iooojik.app.klass.group;

public class Mate {
    String name, email, group;

     Mate(String name, String email, String group) {
        this.name = name;
        this.email = email;
        this.group = group;
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
