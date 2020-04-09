package iooojik.app.klass.models.getToken;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Expiration {
    @SerializedName("seconds")
    @Expose
    private Integer seconds;
    @SerializedName("hours")
    @Expose
    private Integer hours;

    public Integer getSeconds() {
        return seconds;
    }

    public void setSeconds(Integer seconds) {
        this.seconds = seconds;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

}
