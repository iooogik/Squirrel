package iooojik.app.klass.models.promocode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PromoData {
    @SerializedName("las_promo")
    @Expose
    private List<LasPromo> lasPromo = null;

    public List<LasPromo> getLasPromo() {
        return lasPromo;
    }

    public void setLasPromo(List<LasPromo> lasPromo) {
        this.lasPromo = lasPromo;
    }
}
