package iooojik.app.klass.auth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataToken {

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("expiration")
    @Expose
    private Expiration expiration;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Expiration getExpiration() {
        return expiration;
    }

    public void setExpiration(Expiration expiration) {
        this.expiration = expiration;
    }

}
