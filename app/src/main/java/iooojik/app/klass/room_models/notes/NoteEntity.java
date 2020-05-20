package iooojik.app.klass.room_models.notes;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import iooojik.app.klass.AppСonstants;

@Entity(tableName = AppСonstants.TABLE_NOTES)
public class NoteEntity {
    @PrimaryKey(autoGenerate = true)
    long _id;
    String name;
    String shortName;
    String text;
    String date;
    String image;
    String isChecked;
    String points;
    String type;
    String isCompleted;
    String decodeQR;
    String typeface;
    String fontSize;
    String isNotifSet;
    String permToSync;

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(String isChecked) {
        this.isChecked = isChecked;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(String isCompleted) {
        this.isCompleted = isCompleted;
    }

    public String getDecodeQR() {
        return decodeQR;
    }

    public void setDecodeQR(String decodeQR) {
        this.decodeQR = decodeQR;
    }

    public String getTypeface() {
        return typeface;
    }

    public void setTypeface(String typeface) {
        this.typeface = typeface;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public String getIsNotifSet() {
        return isNotifSet;
    }

    public void setIsNotifSet(String isNotifSet) {
        this.isNotifSet = isNotifSet;
    }

    public String getPermToSync() {
        return permToSync;
    }

    public void setPermToSync(String permToSync) {
        this.permToSync = permToSync;
    }
}
