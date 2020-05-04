package iooojik.app.klass.shop;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.R;
import iooojik.app.klass.api.Api;
import iooojik.app.klass.models.ServerResponse;
import iooojik.app.klass.models.shop.ShopData;
import iooojik.app.klass.models.shop.ShopItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Shop extends Fragment {

    public Shop() {}

    private Context context;
    private RecyclerView items;
    private Api api;
    private Fragment fragment;
    private SharedPreferences preferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);
        context = getContext();
        fragment = this;
        preferences = getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);

        TextView balance = view.findViewById(R.id.balance);
        balance.setText(String.valueOf(preferences.getInt(AppСonstants.USER_COINS, 0)));

        items = view.findViewById(R.id.items);
        getActivity().runOnUiThread(this::getItems);

        setHasOptionsMenu(true);
        return view;
    }

    private void doRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppСonstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
    }

    private void getItems() {
        doRetrofit();
        Call<ServerResponse<ShopData>> call = api.getShopItems(AppСonstants.X_API_KEY,
                preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""));

        call.enqueue(new Callback<ServerResponse<ShopData>>() {
            @Override
            public void onResponse(Call<ServerResponse<ShopData>> call, Response<ServerResponse<ShopData>> response) {
                if (response.code() != 200) Log.e("GETTING SHOP ITEMS", String.valueOf(response.raw()));
                else {
                    ShopData data = response.body().getData();
                    List<ShopItem> shopItems = data.getShop();

                    for (ShopItem shopItem : shopItems){
                        if (Integer.parseInt(shopItem.getVisible()) == 0)  shopItems.remove(shopItem);
                    }

                    ShopItemsAdapter adapter = new ShopItemsAdapter(shopItems, context, fragment, preferences);

                    items.setLayoutManager(new LinearLayoutManager(getContext()));
                    items.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<ShopData>> call, Throwable t) {
                Log.e("GETTING SHOP ITEMS", String.valueOf(t));
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }
}
