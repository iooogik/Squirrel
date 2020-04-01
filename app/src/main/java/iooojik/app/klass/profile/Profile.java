package iooojik.app.klass.profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import iooojik.app.klass.Api;
import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.Database;
import iooojik.app.klass.R;
import iooojik.app.klass.models.ServerResponse;
import iooojik.app.klass.profile.pupil.DataPupilList;
import iooojik.app.klass.profile.pupil.PupilGroups;
import iooojik.app.klass.profile.teacher.AddGroupResult;
import iooojik.app.klass.profile.teacher.DataGroup;
import iooojik.app.klass.profile.teacher.GroupInfo;
import iooojik.app.klass.profile.userDetail.DataUser;
import iooojik.app.klass.profile.userDetail.DetailedGroup;
import iooojik.app.klass.profile.userDetail.User_;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Profile extends Fragment implements View.OnClickListener {
    public Profile(){}

    private View view;
    private List<String> groupList;
    private List<Test> testList;
    private FloatingActionButton fab;
    private String teacherRole = "teacher", pupilRole = "pupil";
    private String userRole = "";
    private GroupsAdapter groupsAdapter;
    private Context context;
    private Fragment fragment;
    private SharedPreferences preferences;
    private String email, fullName, role, userName;
    private Api api;
    private NavController navController;
    private int userID;



    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        preferences = getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        //список с группами(для учителей)
        groupList = new ArrayList<>();
        //получаем fab и ставим слушатель на него
        fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(this);
        //список с активными тестами
        testList = new ArrayList<>();
        //получение пользовательской информации
        setUserInformation();
        //получение текущего фрагмента, чтобы использовать его в адаптере
        fragment = this;
        //контекст
        context = getContext();
        //запрос на разрешение использования камеры
        int permissionStatus = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        if (!(permissionStatus == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, 1);
        }
        //
        Button exitProfile = view.findViewById(R.id.exitProfile);
        exitProfile.setOnClickListener(this);

        return view;
    }

    private void getActiveTests() {
        //получаем к каким группам относится пользователь
        doRetrofit();
        Call<ServerResponse<DataPupilList>> responseCall = api.getPupilActiveGroups(AppСonstants.X_API_KEY,
                preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""),
                "email", email);

        responseCall.enqueue(new Callback<ServerResponse<DataPupilList>>() {
            @Override
            public void onResponse(Call<ServerResponse<DataPupilList>> call, Response<ServerResponse<DataPupilList>> response) {
                if (response.code() == 200){
                    DataPupilList dataPupilList = response.body().getData();
                    List<PupilGroups> pupilGroups = dataPupilList.getPupilGroups();
                    PupilGroupsAdapter groupsAdapter = new PupilGroupsAdapter(pupilGroups, fragment, context);
                    RecyclerView recyclerView = view.findViewById(R.id.classes);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(groupsAdapter);
                }else {
                    Log.e("GET PUPIL GROUPS", String.valueOf(response.raw()));
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<DataPupilList>> call, Throwable t) {
                Log.e("GET PUPIL GROUPS", String.valueOf(t));
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setUserInformation() {
        //получаем и устанавливаем пользовательскую информацию

        TextView Email = view.findViewById(R.id.email);
        TextView name = view.findViewById(R.id.name);
        TextView surname = view.findViewById(R.id.surname);
        TextView role = view.findViewById(R.id.role);

        Database mDbHelper = new Database(getContext());
        SQLiteDatabase mDb = mDbHelper.getReadableDatabase();
        Cursor userCursor;
        userCursor =  mDb.rawQuery("Select * from Profile WHERE _id=?", new String[]{String.valueOf(0)});
        userCursor.moveToFirst();

        fullName = userCursor.getString(userCursor.getColumnIndex("full_name"));
        email = userCursor.getString(userCursor.getColumnIndex("email"));
        userName = userCursor.getString(userCursor.getColumnIndex("username"));
        Email.setText(email);
        name.setText(fullName);
        surname.setText(userName);
        getUserID();
        getUserRole();

        userRole = userCursor.getString(userCursor.getColumnIndex("type"));

        if (userRole.equals(teacherRole)){
            getGroupsFromDatabase();
            fab.show();
            fab.setImageResource(R.drawable.round_add_24);
        } else {
            fab.hide();
            getActiveTests();
        }

        switch (userRole.toLowerCase()){
            case "teacher":
                role.setText("учитель");
                break;
            case "pupil":
                role.setText("учащийся");
                break;
        }

        /**
         * 1. если стоит учительский профиль, то убираем ученический профиль, изменяем поле "роль",
         * показываем fab и ставим адаптер для RecyclerView(id = classes). GroupsAdapter(контекст, список с группами, текущий фрагмент)
         * 2. если ученический профиль, то убираем учительский, ставим соответсвующую роль, убираем fab
         * и ставим адаптер на RecyclerView(id = teachers) и получаем список активных тестов
         */

    }

    private void getUserID(){
        Database mDBHelper = new Database(getContext());
        SQLiteDatabase mDb;
        mDBHelper = new Database(getContext());
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();
        mDb = mDBHelper.getReadableDatabase();
        Cursor userCursor = mDb.rawQuery("Select * from Profile WHERE _id=?",
                new String[]{String.valueOf(0)});
        userCursor.moveToFirst();

        userID = Integer.parseInt(userCursor.getString(userCursor.getColumnIndex("id")));

    }

    private void getUserRole(){

        doRetrofit();
        Call<ServerResponse<DataUser>> response = api.getUserDetail(AppСonstants.X_API_KEY,
                preferences.getString(AppСonstants.AUTH_SAVED_TOKEN,""), userID);

        response.enqueue(new Callback<ServerResponse<DataUser>>() {
            @Override
            public void onResponse(Call<ServerResponse<DataUser>> call,
                                   Response<ServerResponse<DataUser>> response) {

                if(response.code() == 200) {

                    ServerResponse<DataUser> result = response.body();
                    User_ user = result.getData().getUser();

                    DetailedGroup detailedGroup = user.getGroup().get(0);
                    String type = detailedGroup.getName();

                    Database mDBHelper = new Database(getContext());
                    SQLiteDatabase mDb;
                    mDBHelper = new Database(getContext());
                    mDBHelper.openDataBase();
                    mDBHelper.updateDataBase();

                    mDb = mDBHelper.getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    cv.put("type", type.toLowerCase());
                    mDb.update("Profile", cv, "_id=0", null);

                } else {
                    Log.e("GETTING USER DETAIL", response.raw() + " " +response.code());
                }


            }
            @Override
            public void onFailure(Call<ServerResponse<DataUser>> call, Throwable t) {
                Log.e("GETTING USER DETAIL",t.toString());
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

    private void getGroupsFromDatabase(){
        doRetrofit();
        Call<ServerResponse<DataGroup>> response = api.getGroups(AppСonstants.X_API_KEY, "author_email", email);

        response.enqueue(new Callback<ServerResponse<DataGroup>>() {
            @Override
            public void onResponse(Call<ServerResponse<DataGroup>> call, Response<ServerResponse<DataGroup>> response) {
                if(response.code() == 200) {
                    ServerResponse<DataGroup> result = response.body();
                    List<GroupInfo> groupInfos = result.getData().getGroupInfos();
                    groupsAdapter = new GroupsAdapter(context, groupInfos, fragment);
                    RecyclerView recyclerView = view.findViewById(R.id.classes);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(groupsAdapter);

                } else {
                    Log.e("GETTING GROUPS", response.raw() + "");
                }
            }
            @Override
            public void onFailure(Call<ServerResponse<DataGroup>> call, Throwable t) {
                Log.e("GETTING GROUPS",t.toString());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                //добавление класса в учительский профиль
                if(userRole.equals(teacherRole)){
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                    View view1 = getLayoutInflater().inflate(R.layout.edit_text, null);
                    TextInputLayout textInputLayout = view1.findViewById(R.id.text_input_layout);
                    textInputLayout.setHint("Введите класс");
                    textInputLayout.setCounterMaxLength(3);
                    EditText name = view1.findViewById(R.id.edit_text);

                    builder.setView(view1);

                    builder.setPositiveButton("Добавить", (dialog, which) -> {
                        //заносим в базу данных
                        doRetrofit();

                        String nameGroup = name.getText().toString();

                        HashMap<String, String> post = new HashMap<>();
                        post.put("author_email", email);
                        post.put("name", nameGroup);
                        post.put("test", "q");
                        post.put("author_name", fullName);

                        Log.e("creds", email + " " + nameGroup + " ");

                        Call<ServerResponse<AddGroupResult>> responseCall = api.addGroup(
                                AppСonstants.X_API_KEY, preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""),
                                post);

                        responseCall.enqueue(new Callback<ServerResponse<AddGroupResult>>() {
                            @Override
                            public void onResponse(Call<ServerResponse<AddGroupResult>> call, Response<ServerResponse<AddGroupResult>> response) {
                                if (response.code() != 200){
                                    Log.e("Add Group", String.valueOf(response.raw()));
                                }else {
                                    getGroupsFromDatabase();
                                }
                            }

                            @Override
                            public void onFailure(Call<ServerResponse<AddGroupResult>> call, Throwable t) {
                                Log.e("Add Group", String.valueOf(t));
                            }
                        });

                    });
                    builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
                    builder.create().show();
                }
                break;
            case R.id.exitProfile:
                getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE)
                        .edit().putString(AppСonstants.AUTH_SAVED_TOKEN, "").apply();
                BottomAppBar bottomAppBar = getActivity().findViewById(R.id.bar);
                bottomAppBar.setVisibility(View.GONE);
                navController = NavHostFragment.findNavController(this);
                navController.navigate(R.id.nav_signIn);
                //убираем шторку
                DrawerLayout mDrawerLayout = getActivity().findViewById(R.id.drawer_layout);
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

}
