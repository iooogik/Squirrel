package iooojik.app.klass.profile;

import java.util.List;

class ClassGroup {
    private int id; //id группы из бд
    private String name; //название группы
    private int count; //количество участников
    private List<String> users; //список с пользователями

    ClassGroup(int id, String name, List<String> users){
        this.id = id;
        this.name = name;
        this.users = users;
        this.count = users.size();
    }
}
