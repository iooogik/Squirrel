package iooojik.app.klass.models.promocode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LasPromo {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("promo")
    @Expose
    private String promo;
    @SerializedName("activated")
    @Expose
    private String activated;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPromo() {
        return promo;
    }

    public void setPromo(String promo) {
        this.promo = promo;
    }

    public String getActivated() {
        return activated;
    }

    public void setActivated(String activated) {
        this.activated = activated;
    }
}
