package iooojik.app.klass.models.shop;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class ShopData {
    @SerializedName("shop")
    @Expose
    private List<ShopItem> shop = null;

    public List<ShopItem> getShop() {
        return shop;
    }

    public void setShop(List<ShopItem> shop) {
        this.shop = shop;
    }
}
