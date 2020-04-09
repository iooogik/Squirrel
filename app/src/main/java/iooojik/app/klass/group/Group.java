package iooojik.app.klass.group;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.List;

import iooojik.app.klass.Api;
import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.PostResult;
import iooojik.app.klass.R;
import iooojik.app.klass.models.ServerResponse;
import iooojik.app.klass.models.matesList.DataUsersToGroup;
import iooojik.app.klass.models.matesList.Mates;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Group extends Fragment implements View.OnClickListener{

    public Group() {}

    private View view;
    private int groupID = -1;
    private String groupName;
    private String groupAuthor;
    private int id = -1;
    private Context context;
    private GroupMatesAdapter groupmatesAdapter;
    private FloatingActionButton fab;
    private Api api;
    private Fragment fragment;
    private List<Mates> mates;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group, container, false);

        //получение названия нажатого класса
        getGroupID();
        //контекст
        context = getContext();
        //получение списка одноклассников
        getGroupMates();

        RecyclerView groupmates = view.findViewById(R.id.groupmates);
        groupmates.setLayoutManager(new LinearLayoutManager(context));
        groupmates.setAdapter(groupmatesAdapter);
        //конпка с открытием редактора тестов
        Button testEditor = view.findViewById(R.id.testEditor);
        testEditor.setOnClickListener(this);
        fragment = this;

        fab = getActivity().findViewById(R.id.fab);
        fab.show();
        fab.setOnClickListener(this);

        return view;
    }

    private void doRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppСonstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
    }

    private void getGroupMates(){

        //получаем список учеников(их полное имя и email) из бд
        doRetrofit();
        Call<ServerResponse<DataUsersToGroup>> response = api.getMatesList(AppСonstants.X_API_KEY, "group_id", String.valueOf(id));

        response.enqueue(new Callback<ServerResponse<DataUsersToGroup>>() {
            @Override
            public void onResponse(Call<ServerResponse<DataUsersToGroup>> call, Response<ServerResponse<DataUsersToGroup>> response) {
                if(response.code() == 200) {
                    ServerResponse<DataUsersToGroup> result = response.body();
                    mates = result.getData().getMates();

                    groupmatesAdapter = new GroupMatesAdapter(context, fragment, mates);
                    RecyclerView recyclerView = view.findViewById(R.id.groupmates);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(groupmatesAdapter);

                } else {
                    Log.e("GETTING MATES", response.raw() + "");
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<DataUsersToGroup>> call, Throwable t) {
                Log.e("GETTING MATES",t.toString());
                fab.hide();
                ImageView error = view.findViewById(R.id.errorImg);
                error.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getGroupID(){
        Bundle args = this.getArguments();
        groupID = args.getInt("groupID");
        groupAuthor = args.getString("groupAuthor");
        groupName = args.getString("groupName");
        id = args.getInt("id");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                /**MaterialAlertDialogBuilder для добавления нового ученика в группу
                 * 1. пользователь вводит email и полное имя ученика, если он есть в базе, то
                 * он добавляется в список и, соответсвенно, в бд
                 */

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);
                View view1 = getLayoutInflater().inflate(R.layout.edit_text, null);
                TextInputEditText emailText = view1.findViewById(R.id.edit_text);
                emailText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                TextInputLayout textInputLayout = view1.findViewById(R.id.text_input_layout);
                textInputLayout.setHint("Введите e-mail адрес");
                textInputLayout.setHelperTextEnabled(false);
                textInputLayout.setCounterEnabled(false);


                View view2 = getLayoutInflater().inflate(R.layout.edit_text, null);
                TextInputEditText nameSurname = view2.findViewById(R.id.edit_text);

                TextInputLayout textInputLayout2 = view2.findViewById(R.id.text_input_layout);
                textInputLayout2.setHint("Введите ФИО ученика");
                textInputLayout2.setHelperTextEnabled(false);
                textInputLayout2.setCounterEnabled(false);


                layout.addView(view2);
                layout.addView(view1);

                builder.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = emailText.getText().toString();
                        boolean result = false;

                        //проверяем, есть ли пользовтель в бд
                        if (mates.size() == 0) {
                            result = true;
                        }else {
                            for (Mates mate : mates) {
                                if (email.equals(mate.getEmail())) {
                                    result = false;
                                    break;
                                } else result = true;
                            }
                        }

                        if(result){
                            SharedPreferences preferences = getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);
                            //добавление пользователя
                            doRetrofit();
                            HashMap<String, String> map = new HashMap<>();
                            map.put("full_name", nameSurname.getText().toString());
                            map.put("email", emailText.getText().toString());
                            map.put("group_id", String.valueOf(id));
                            map.put("group_name", groupName);
                            Call<ServerResponse<PostResult>> response = api.addUserToGroup(AppСonstants.X_API_KEY,
                            preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), map);

                            response.enqueue(new Callback<ServerResponse<PostResult>>() {
                                @Override
                                public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {
                                    if (response.code() == 200) {
                                        Snackbar.make(view, "Пользователь был успешно добавлен", Snackbar.LENGTH_LONG).show();
                                        getGroupMates();
                                    }
                                    else Log.e("ADD MATE", String.valueOf(response.raw()));
                                }

                                @Override
                                public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {
                                    Log.e("ADD MATE", String.valueOf(t));
                                }
                            });

                        } else {
                            Snackbar.make(view, "Пользователь с указанным e-mail адресом не был найден." +
                                    "Пожалуйста, повторите попытку снова или напишите разработчику.",
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }
                });

                builder.setView(layout);
                builder.create().show();
                break;
            case R.id.testEditor:
                //редактор тестов
                Bundle bundle = new Bundle();
                bundle.putInt("id", id);
                bundle.putInt("groupID", groupID);
                bundle.putString("groupAuthor", groupAuthor);
                bundle.putString("groupName", groupName);
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(R.id.nav_testEditor, bundle);
        }
    }
}
