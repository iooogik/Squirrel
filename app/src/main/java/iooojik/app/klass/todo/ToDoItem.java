package iooojik.app.klass.todo;

public class ToDoItem {
    private String text;
    private boolean checked;
    private int id;

    ToDoItem(String text, boolean checked, int id) {
        this.text = text;
        this.checked = checked;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    boolean getChecked() {
        return checked;
    }

}
