package iooojik.app.klass.group;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.List;

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.R;
import iooojik.app.klass.api.Api;
import iooojik.app.klass.models.PostResult;
import iooojik.app.klass.models.ServerResponse;
import iooojik.app.klass.models.test_results.DataTestResult;
import iooojik.app.klass.models.test_results.TestsResult;
import iooojik.app.klass.models.matesList.DataUsersToGroup;
import iooojik.app.klass.models.matesList.Mate;
import iooojik.app.klass.models.paramUsers.ParamData;
import iooojik.app.klass.models.paramUsers.UserParams;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Group extends Fragment{

    public Group() {}

    private View view;
    //id группы
    private int groupID = -1;
    //название группы
    private String groupName;
    //уитель
    private String groupAuthor;
    private String groupAuthorName;
    private int id = -1;
    private Context context;
    //адаптер
    private GroupMatesAdapter groupmatesAdapter;
    //fab
    private FloatingActionButton fab;
    //апи
    private Api api;
    //список одногруппников
    private List<Mate> mates;
    //настройки
    private SharedPreferences preferences;
    private Fragment fragment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group, container, false);
        preferences = getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        //получение названия нажатого класса
        getGroupInfo();
        //контекст
        context = getContext();
        //получение списка одноклассников
        getGroupMates();

        fragment = this;

        //адаптер с одногруппникми и информацией о прохождении теста
        RecyclerView groupmates = view.findViewById(R.id.groupmates);
        groupmates.setLayoutManager(new LinearLayoutManager(context));
        groupmates.setAdapter(groupmatesAdapter);

        fab = getActivity().findViewById(R.id.fab);
        fab.show();
        fab.setImageResource(R.drawable.round_keyboard_arrow_up_24);
        enableBottomSheet();
        return view;
    }

    private void doRetrofit(){
        //базовый метод ретрофита
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppСonstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
    }

    private void getGroupMates(){

        //получаем список учеников(их полное имя и email) из бд
        doRetrofit();
        Call<ServerResponse<DataUsersToGroup>> response = api.getMatesList(AppСonstants.X_API_KEY, AppСonstants.GROUP_ID_FIELD, String.valueOf(id));

        response.enqueue(new Callback<ServerResponse<DataUsersToGroup>>() {
            @Override
            public void onResponse(Call<ServerResponse<DataUsersToGroup>> call, Response<ServerResponse<DataUsersToGroup>> response) {
                if(response.code() == 200) {
                    ServerResponse<DataUsersToGroup> result = response.body();
                    mates = result.getData().getMates();

                    Call<ServerResponse<DataTestResult>> call2 = api.getTestResults(AppСonstants.X_API_KEY,
                            preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), AppСonstants.GROUP_ID_FIELD, String.valueOf(id));
                    call2.enqueue(new Callback<ServerResponse<DataTestResult>>() {
                        @Override
                        public void onResponse(Call<ServerResponse<DataTestResult>> call, Response<ServerResponse<DataTestResult>> response) {
                            if (response.code() == 200){
                                //ставим адаптер
                                DataTestResult result = response.body().getData();
                                List<TestsResult> testsResults = result.getTestsResult();
                                groupmatesAdapter = new GroupMatesAdapter(context, mates, testsResults, fragment, true);
                                RecyclerView recyclerView = view.findViewById(R.id.groupmates);
                                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                recyclerView.setAdapter(groupmatesAdapter);
                            }
                        }

                        @Override
                        public void onFailure(Call<ServerResponse<DataTestResult>> call, Throwable t) {

                        }
                    });


                } else {
                    Log.e("GETTING MATES", response.raw() + "");
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<DataUsersToGroup>> call, Throwable t) {
                Log.e("GETTING MATES",t.toString());
                fab.hide();
            }
        });
    }

    private void getGroupInfo(){
        //получаем информацию о группе из предыдущего фрагмента
        Bundle args = this.getArguments();
        groupID = args.getInt("groupID");
        groupAuthor = args.getString("groupAuthor");
        groupName = args.getString("groupName");
        groupAuthorName = args.getString("groupAuthorName");
        id = args.getInt("id");
    }

    private void addNewUserWeb(String email) {
        //получение информации о добавляемом пользователе

        doRetrofit();

        Call<ServerResponse<ParamData>> call = api.getParamUser(AppСonstants.X_API_KEY,
                preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""),AppСonstants.EMAIL_FIELD, email);

        call.enqueue(new Callback<ServerResponse<ParamData>>() {
            @Override
            public void onResponse(Call<ServerResponse<ParamData>> call, Response<ServerResponse<ParamData>> response) {
                if (response.code() == 200){

                    ParamData paramData = response.body().getData();
                    if (paramData.getUserParams().size() > 0) {
                        UserParams userParams = paramData.getUserParams().get(0);

                        String avatar = userParams.getAvatar();
                        if (avatar == null || avatar.isEmpty()) {
                            avatar = "null";
                        }

                        HashMap<String, String> map = new HashMap<>();
                        map.put(AppСonstants.FULL_NAME_FIELD, userParams.getFullName());
                        map.put(AppСonstants.EMAIL_FIELD, userParams.getEmail());
                        map.put(AppСonstants.GROUP_ID_FIELD, String.valueOf(id));
                        map.put(AppСonstants.GROUP_NAME_FIELD, groupName);
                        map.put(AppСonstants.AVATAR_FIELD, avatar);

                        Call<ServerResponse<PostResult>> response2 = api.addUserToGroup(AppСonstants.X_API_KEY,
                                preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), map);

                        response2.enqueue(new Callback<ServerResponse<PostResult>>() {
                            @Override
                            public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {
                                if (response.code() == 200) {
                                    Snackbar.make(view, "Пользователь был успешно добавлен", Snackbar.LENGTH_LONG).show();
                                    getGroupMates();
                                } else Log.e("ADD MATE", String.valueOf(response.raw()) + map);
                            }

                            @Override
                            public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {
                                Log.e("ADD MATE", String.valueOf(t));
                            }
                        });
                    } else {
                        Snackbar.make(view, "Пользователь с указанным e-mail адресом не был найден.",
                                Snackbar.LENGTH_LONG).show();
                    }



                    } else {
                        Log.e("ADD MATE", String.valueOf(response.raw()));
                        Snackbar.make(view, "Что-то пошло не так!",
                            Snackbar.LENGTH_LONG).show();
                    }
            }
            @Override
            public void onFailure(Call<ServerResponse<ParamData>> call, Throwable t) {

            }
        });
    }

    private void addUser(){
        /*
         * MaterialAlertDialogBuilder для добавления нового ученика в группу
         * 1. пользователь вводит email и полное имя ученика, если он есть в базе, то
         * он добавляется в список и, соответсвенно, в бд
         */

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        @SuppressLint("InflateParams") View view1 = getLayoutInflater().inflate(R.layout.edit_text, null);
        TextInputEditText emailText = view1.findViewById(R.id.edit_text);

        TextInputLayout textInputLayout = view1.findViewById(R.id.text_input_layout);
        textInputLayout.setHint("Введите e-mail адрес");
        textInputLayout.setHelperTextEnabled(false);
        textInputLayout.setCounterEnabled(false);

        layout.addView(view1);

        builder.setPositiveButton("Добавить", (dialog, which) -> {
            String email = emailText.getText().toString().trim();
            boolean result = false;

            //проверяем, есть ли пользовтель в группе
            if (mates.size() == 0 && !email.equals(groupAuthor)) {
                result = true;
            }else {

                for (Mate mate : mates) {
                    if (email.equals(mate.getEmail())) {
                        result = false;
                        break;
                    } else result = true;
                }

            }

            //получаем пользовательскую информацию по email
            //если код == 200, то заносим пользователя в группу
            //иначе выдаём сообщение об ошибке

            if (result){
                addNewUserWeb(email);
            }
            else Snackbar.make(view, "Пользователь с указанным email-адресом уже есть в группе", Snackbar.LENGTH_LONG).show();

            fab.show();

        });
        builder.setOnCancelListener(dialog -> fab.show());
        builder.setView(layout);
        builder.create().show();
    }

    @SuppressLint("InflateParams")
    private void enableBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        View bottomSheet = getActivity().getLayoutInflater().inflate(R.layout.bottom_sheet_group_editor, null);

        bottomSheetDialog.setContentView(bottomSheet);

        Button add = bottomSheet.findViewById(R.id.add);
        add.setOnClickListener(v -> {
            addUser();
            bottomSheetDialog.hide();
        });

        Button sync = bottomSheet.findViewById(R.id.test_editor);
        sync.setOnClickListener(v -> {
            //редактор тестов
            Bundle bundle = new Bundle();
            bundle.putInt("id", id);
            bundle.putInt("groupID", groupID);
            bundle.putString("groupAuthor", groupAuthor);
            bundle.putString("groupAuthorName", groupAuthorName);
            bundle.putString("groupName", groupName);
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.nav_testEditor, bundle);
            bottomSheetDialog.hide();
        });


        Button download = bottomSheet.findViewById(R.id.add_message);
        download.setOnClickListener(v -> {
            addMessageToGroup();
            bottomSheetDialog.hide();
        });

        Button delete_test = bottomSheet.findViewById(R.id.delete_test);
        delete_test.setOnClickListener(v -> {
            HashMap<String, String> updateMap = new HashMap<>();

            updateMap.put("_id", String.valueOf(id));
            updateMap.put("author_email", groupAuthor);
            updateMap.put("author_name", groupAuthorName);
            updateMap.put("name", groupName);
            updateMap.put("test", "null");

            Call<ServerResponse<PostResult>> responseCall = api.updateTest(AppСonstants.X_API_KEY,
                    getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES,
                            Context.MODE_PRIVATE).getString(AppСonstants.AUTH_SAVED_TOKEN, ""),
                    updateMap);
            responseCall.enqueue(new Callback<ServerResponse<PostResult>>() {
                @Override
                public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {

                }

                @Override
                public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {

                }
            });
        });

        bottomSheetDialog.setOnCancelListener(dialog -> fab.show());


        fab.setOnClickListener(v -> {
            bottomSheetDialog.show();
            fab.hide();
        });

    }

    private void addMessageToGroup() {
        //создаём окно для ввода сообщения
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        @SuppressLint("InflateParams") View view1 = getLayoutInflater().inflate(R.layout.edit_text, null);
        TextInputEditText messageText = view1.findViewById(R.id.edit_text);
        messageText.setMaxLines(10);

        TextInputLayout textInputLayout = view1.findViewById(R.id.text_input_layout);
        textInputLayout.setHint("Введите ваше сообщение");
        textInputLayout.setHelperTextEnabled(false);
        textInputLayout.setCounterEnabled(false);

        layout.addView(view1);

        builder.setView(layout);

        //слушатели
        builder.setPositiveButton("Добавить", (dialog, which) -> {

            //запрос
            HashMap<String, String> map = new HashMap<>();
            map.put(AppСonstants.GROUP_ID_FIELD, String.valueOf(id));
            map.put(AppСonstants.MESSAGE_FIELD, messageText.getText().toString());
            Call<ServerResponse<PostResult>> addMessageCall = api.addGroupMessage(AppСonstants.X_API_KEY,
                    preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), map);
            addMessageCall.enqueue(new Callback<ServerResponse<PostResult>>() {
                @Override
                public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {
                    if (response.code() == 200) Snackbar.make(getView(), "Добавлено", Snackbar.LENGTH_LONG).show();
                    else {
                        Log.e("ADD GROUP MESSAGE", String.valueOf(response.raw()));
                        Snackbar.make(getView(), "К сожалению, произошла ошибка", Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {
                    Log.e("ADD GROUP MESSAGE", String.valueOf(t));
                    Snackbar.make(getView(), "К сожалению, произошла ошибка", Snackbar.LENGTH_LONG).show();
                }
            });
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
        builder.create().show();
        fab.show();
    }

}
