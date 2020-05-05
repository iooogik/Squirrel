package iooojik.app.klass.notes;

import android.graphics.Bitmap;

public class NoteObject {
    private String name;
    private String description;
    private Bitmap image;
    private String type;
    private int id;
    private int dataID;

    public int getDataID() {
        return dataID;
    }

    public void setDataID(int dataID) {
        this.dataID = dataID;
    }

    NoteObject(String name, String description, Bitmap image, String type, int id, int dataID){
        this.name = name;
        this.description = description;
        this.image = image;
        this.type = type;
        this.id = id;
        this.dataID = dataID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
