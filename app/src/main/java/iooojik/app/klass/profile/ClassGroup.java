package iooojik.app.klass.profile;

import java.util.List;

class ClassGroup {
    private int id;
    private String name;
    private int count;
    private List<String> users;

    ClassGroup(int id, String name, List<String> users){
        this.id = id;
        this.name = name;
        this.users = users;
        this.count = users.size();
    }
}
