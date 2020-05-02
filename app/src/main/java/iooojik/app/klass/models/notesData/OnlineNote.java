package iooojik.app.klass.models.notesData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OnlineNote {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("shortName")
    @Expose
    private String shortName;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("isChecked")
    @Expose
    private String isChecked;
    @SerializedName("points")
    @Expose
    private String points;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("isCompleted")
    @Expose
    private String isCompleted;
    @SerializedName("decodeQR")
    @Expose
    private String decodeQR;
    @SerializedName("isNotifSet")
    @Expose
    private String isNotifSet;
    @SerializedName("permToSync")
    @Expose
    private String permToSync;
    @SerializedName("typeface")
    @Expose
    private String typeface;
    @SerializedName("fontSize")
    @Expose
    private String fontSize;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
