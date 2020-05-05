package iooojik.app.klass.settings.private_policy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.R;
import iooojik.app.klass.api.TranslateApi;
import iooojik.app.klass.models.translation.TranslationResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class PrivatePolicy extends Fragment {

    public PrivatePolicy() {}

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_private_policy, container, false);
        TextView policy = view.findViewById(R.id.privatepolicy);
        policy.setText(R.string.privatePolicyText);
        TextView terms = view.findViewById(R.id.termsconditions);
        terms.setText(R.string.termsConditionsText);
        setHasOptionsMenu(true);
        return view;
    }

    private void translate(String lang){
        TextView policy = view.findViewById(R.id.privatepolicy);
        TextView terms = view.findViewById(R.id.termsconditions);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppСonstants.YANDEX_TRANSLATE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TranslateApi translateApi = retrofit.create(TranslateApi.class);

        Call<TranslationResponse> translationResponseCall = translateApi.translate(AppСonstants.YANDEX_TRANSLATE_API_KEY,
                policy.getText().toString(), lang,"plain");
        translationResponseCall.enqueue(new Callback<TranslationResponse>() {
            @Override
            public void onResponse(Call<TranslationResponse> call, Response<TranslationResponse> response) {
                if (response.code() == 200) {

                    policy.setText(response.body().getText().get(0));
                }
            }

            @Override
            public void onFailure(Call<TranslationResponse> call, Throwable t) {

            }
        });

        Call<TranslationResponse> translationResponseCall2 = translateApi.translate(AppСonstants.YANDEX_TRANSLATE_API_KEY,
                terms.getText().toString(), lang,"plain");
        translationResponseCall2.enqueue(new Callback<TranslationResponse>() {
            @Override
            public void onResponse(Call<TranslationResponse> call, Response<TranslationResponse> response) {
                if (response.code() == 200) {

                    terms.setText(response.body().getText().get(0));
                }
            }

            @Override
            public void onFailure(Call<TranslationResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

        getActivity().getMenuInflater().inflate(R.menu.menu_policy, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        String lang_original = "en";
        if (item.getItemId() == R.id.action_translate) {
            if (lang_original.equals("en")) translate("ru");
            else translate("en");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
