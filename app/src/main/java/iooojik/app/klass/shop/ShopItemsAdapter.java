package iooojik.app.klass.shop;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.R;
import iooojik.app.klass.api.Api;
import iooojik.app.klass.models.PostResult;
import iooojik.app.klass.models.ServerResponse;
import iooojik.app.klass.models.bonusCrate.CratesData;
import iooojik.app.klass.models.promocode.LasPromo;
import iooojik.app.klass.models.promocode.PromoData;
import iooojik.app.klass.models.shop.ShopItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShopItemsAdapter extends RecyclerView.Adapter<ShopItemsAdapter.ViewHolder>{

    private List<ShopItem> items;
    private Context context;
    private Fragment fragment;
    private LayoutInflater inflater;
    private SharedPreferences preferences;
    private Api api;
    private View view;

    ShopItemsAdapter(List<ShopItem> items, Context context, Fragment fragment,
                     SharedPreferences preferences, View view) {
        this.items = items;
        this.context = context;
        this.fragment = fragment;
        this.inflater = LayoutInflater.from(context);
        this.preferences = preferences;
        this.view = view;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_shop_item, parent, false);//поиск элемента списка
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShopItem shopItem = items.get(position);
        holder.name.setText(shopItem.getName());
        holder.description.setText(shopItem.getDescription());
        holder.price.setText(shopItem.getPrice());


        if (!shopItem.getLogoURL().trim().isEmpty()) {
            Picasso.with(context).load(shopItem.getLogoURL()).into(holder.logo);
        } else {
            holder.logo.setImageResource(R.drawable.baseline_account_circle_24);
        }



        holder.buy.setOnClickListener(v -> {
            int userCoins = preferences.getInt(AppСonstants.USER_COINS, 0);

            if (userCoins - Integer.parseInt(shopItem.getPrice()) >= 0){
            preferences.edit().putInt(AppСonstants.USER_COINS, userCoins - Integer.parseInt(shopItem.getPrice())).apply();

            TextView balance = fragment.getView().findViewById(R.id.balance);
            balance.setText(String.valueOf(preferences.getInt(AppСonstants.USER_COINS, 0)));
            //обновляем количество койнов у пользователя
            doRetrofit();
            HashMap<String, String> changes = new HashMap<>();
            changes.put(AppСonstants.USER_EMAIL_FIELD, preferences.getString(AppСonstants.USER_EMAIL, ""));
            changes.put(AppСonstants.ID_FIELD, String.valueOf(preferences.getInt(AppСonstants.ACHIEVEMENTS_ID, -1)));
            changes.put(AppСonstants.COINS_FIELD, String.valueOf(preferences.getInt(AppСonstants.USER_COINS, 0)));

            Call<ServerResponse<PostResult>> call = api.updateAchievement(AppСonstants.X_API_KEY,
                    preferences.getString(AppСonstants.STANDART_TOKEN, ""), changes);

            call.enqueue(new Callback<ServerResponse<PostResult>>() {
                @Override
                public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {
                    if (response.code() != 200)
                        Log.e("ADD ACHIEVEMENT", String.valueOf(response.raw()));
                }

                @Override
                public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {
                    Log.e("ADD ACHIEVEMENT", String.valueOf(t));
                }
            });

            //логирование покупки

            //получаем дату
            Date currentDate = new Date();
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            String dateText = dateFormat.format(currentDate);
            DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String timeText = timeFormat.format(currentDate);
            String date = dateText + " " + timeText;

            HashMap<String, String> map = new HashMap<>();
            map.put(AppСonstants.DATE_FIELD, date);
            map.put(AppСonstants.ITEM_ID_FIELD, shopItem.getItemId());
            map.put(AppСonstants.LOG_FIELD, "Совершена покупка " + shopItem.getName() + " за " + shopItem.getPrice());

            Call<ServerResponse<PostResult>> logCall = api.logBuying(AppСonstants.X_API_KEY,
                    preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), map);

            logCall.enqueue(new Callback<ServerResponse<PostResult>>() {
                @Override
                public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {

                }

                @Override
                public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {

                }
            });

            //получение покупки
            if (shopItem.getItemType().equals("las_promo")){
                Call<ServerResponse<PromoData>> getPromo = api.getPromo(AppСonstants.X_API_KEY,
                        preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""),
                        AppСonstants.ACTIVATED_FIELD, "0");
                getPromo.enqueue(new Callback<ServerResponse<PromoData>>() {
                    @Override
                    public void onResponse(Call<ServerResponse<PromoData>> call,
                                           Response<ServerResponse<PromoData>> response) {
                        if (response.code() == 200) {
                            //если получили промо-код, показываем AlertDialog с промокодом и
                            //деактивируем промо-код

                            PromoData data = response.body().getData();
                            List<LasPromo> promo_codes = data.getLasPromo();
                            if (!promo_codes.isEmpty()) {
                                LasPromo code = promo_codes.get(0);

                                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                                builder.setTitle("Покупка");
                                builder.setMessage("Поздравляем! Вы купили промо-код! После нажатия кнопки " +
                                        "Получить, ваш промокод будет скопирован в буфер обмена. Чтобы активировать " +
                                        "промо-код, зайдите в игру LifeAtSpace и вставьте промо-код в соотвествующее поле. " +
                                        "Если вы промахнётесь/забудете активировать промо-код, то он будет аннулирован!");

                                builder.setPositiveButton("Получить", (dialog, which) -> {
                                    ClipboardManager clipboard = (ClipboardManager)
                                            context.getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("", code.getPromo());
                                    clipboard.setPrimaryClip(clip);
                                    Snackbar.make(fragment.getView(), "Получено!",
                                            Snackbar.LENGTH_LONG).show();

                                });
                                builder.create().show();

                                //изменение полученного промо-кода на "неактивный"
                                HashMap<String, String> buying = new HashMap<>();
                                buying.put(AppСonstants.ID_FIELD, code.getId());
                                buying.put(AppСonstants.PROMO_FIELD, code.getPromo());
                                buying.put(AppСonstants.ACTIVATED_FIELD, "1");

                                Call<ServerResponse<PostResult>> changeState = api.changeStatePromo(AppСonstants.X_API_KEY,
                                        preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), buying);

                                changeState.enqueue(new Callback<ServerResponse<PostResult>>() {
                                    @Override
                                    public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {
                                        if (response.code() != 200)
                                            Log.e("CALLBACK", String.valueOf(response.raw()));
                                    }

                                    @Override
                                    public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {

                                    }
                                });

                            } else Snackbar.make(fragment.getView(),
                                    "К сожалению, промо-коды закончились. Повторите попытку позже.",
                                    Snackbar.LENGTH_LONG).show();
                        }
                        else Log.e("CALLBACK", String.valueOf(response.raw()));
                    }

                    @Override
                    public void onFailure(Call<ServerResponse<PromoData>> call, Throwable t) {

                    }
                });

            }
            else if (shopItem.getItemType().equals("bonus_crate")){
                fragment.getActivity().runOnUiThread(() -> {
                    doRetrofit();
                    preferences.edit().putString(AppСonstants.CASES, String.valueOf(Integer.valueOf(
                            preferences.getString(AppСonstants.CASES, "0" )) + 1)).apply();
                    Call<ServerResponse<CratesData>> getCrates = api.getCrates(AppСonstants.X_API_KEY,
                            preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), AppСonstants.USER_EMAIL_FIELD,
                            preferences.getString(AppСonstants.USER_EMAIL, ""));
                    getCrates.enqueue(new Callback<ServerResponse<CratesData>>() {
                        @Override
                        public void onResponse(Call<ServerResponse<CratesData>> call1, Response<ServerResponse<CratesData>> response) {
                            if (response.code() == 200){
                                TextView cratesCount = view.findViewById(R.id.crates_count);
                                cratesCount.setText(response.body().getData().getBonusCratesToUsers().get(0).getCount());
                                CratesData data = response.body().getData();
                                Snackbar.make(view, "Куплено", Snackbar.LENGTH_LONG).show();
                                if (data.getBonusCratesToUsers().size() == 0){
                                    addCrateInfo();
                                }else {
                                    updateCrateInfo(data.getBonusCratesToUsers().get(0).getId(), data.getBonusCratesToUsers().get(0).getCount());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ServerResponse<CratesData>> call1, Throwable t) {

                        }
                    });

                });
            }
            }
            else Snackbar.make(fragment.getView(), "Недостаточно средств", Snackbar.LENGTH_LONG).show();
        });

    }

    private void addCrateInfo() {
        doRetrofit();
        HashMap<String, String> map = new HashMap<>();
        map.put(AppСonstants.USER_EMAIL_FIELD, preferences.getString(AppСonstants.USER_EMAIL, ""));
        map.put(AppСonstants.COUNT_FIELD, "1");
        Call<ServerResponse<PostResult>> call = api.addCrate(AppСonstants.X_API_KEY, preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""),
                map);
        call.enqueue(new Callback<ServerResponse<PostResult>>() {
            @Override
            public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {

            }

            @Override
            public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {

            }
        });
    }

    private void updateCrateInfo(String id, String count) {
        doRetrofit();
        HashMap<String, String> map = new HashMap<>();
        map.put(AppСonstants.ID_FIELD, id);
        map.put(AppСonstants.USER_EMAIL_FIELD, preferences.getString(AppСonstants.USER_EMAIL, ""));
        map.put(AppСonstants.COUNT_FIELD, String.valueOf(Integer.valueOf(count) + 1));
        Call<ServerResponse<PostResult>> call = api.updateCrateInfo(AppСonstants.X_API_KEY,
                preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), map);
        call.enqueue(new Callback<ServerResponse<PostResult>>() {
            @Override
            public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {

            }

            @Override
            public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {

            }
        });
    }

    private void doRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppСonstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView logo;
        TextView name, description, price;
        Button buy;

        ViewHolder(View view){
            super(view);
            logo = view.findViewById(R.id.imageView2);
            name = view.findViewById(R.id.item_name);
            description = view.findViewById(R.id.item_description);
            price = view.findViewById(R.id.price);
            buy = view.findViewById(R.id.buy);
        }
    }
}
