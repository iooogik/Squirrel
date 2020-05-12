package iooojik.app.klass.groupProfile;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.List;

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.Database;
import iooojik.app.klass.R;
import iooojik.app.klass.api.Api;
import iooojik.app.klass.group.GroupMatesAdapter;
import iooojik.app.klass.models.PostResult;
import iooojik.app.klass.models.ServerResponse;
import iooojik.app.klass.models.groups_messages.DataMessage;
import iooojik.app.klass.models.groups_messages.MessagesToGroup;
import iooojik.app.klass.models.isUserGetTest.DataIsUserGetTest;
import iooojik.app.klass.models.isUserGetTest.IsUserGetTest;
import iooojik.app.klass.models.matesList.DataUsersToGroup;
import iooojik.app.klass.models.matesList.Mate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GroupProfile extends Fragment implements View.OnClickListener{

    private View view;
    private String groupName = "", groupID = "";
    private Api api;
    private Context context;
    private SharedPreferences sharedPreferences;

    private Fragment fragment;
    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group_profile, container, false);
        sharedPreferences = getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        context = getContext();
        fragment = this;
        setInformation();
        Button leave = view.findViewById(R.id.leave_group);
        navController = NavHostFragment.findNavController(this);
        leave.setOnClickListener(this);
        return view;

    }

    private void setInformation(){
        getExtraData();
        TextView groupN = view.findViewById(R.id.group_name);
        groupN.setText(groupName);
        getGroupInformation();
        getTestTeacherInfo();
        getGroupMessage();
    }

    private void getGroupInformation() {
        doRetrofit();
        //получаем список одноклассников
        Call<ServerResponse<DataUsersToGroup>> response = api.getMatesList(AppСonstants.X_API_KEY, AppСonstants.GROUP_ID_FIELD, groupID);
        response.enqueue(new Callback<ServerResponse<DataUsersToGroup>>() {
            @Override
            public void onResponse(Call<ServerResponse<DataUsersToGroup>> call, Response<ServerResponse<DataUsersToGroup>> response) {
                if(response.code() == 200) {
                    ServerResponse<DataUsersToGroup> result = response.body();
                    List<Mate> mates = result.getData().getMates();
                    for (Mate mate : mates){
                        if (mate.getEmail().equals(sharedPreferences.getString(AppСonstants.USER_EMAIL, ""))){
                            sharedPreferences.edit().putString(AppСonstants.USER_CURR_GROUP_ID, mate.getId()).apply();
                            break;
                        }
                    }
                    GroupMatesAdapter groupmatesAdapter = new GroupMatesAdapter(context, mates, null, fragment, false);
                    RecyclerView recyclerView = view.findViewById(R.id.group_mates);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(groupmatesAdapter);

                } else {
                    Log.e("GETTING MATES", response.raw() + "");
                }
            }
            @Override
            public void onFailure(Call<ServerResponse<DataUsersToGroup>> call, Throwable t) {
                Log.e("GETTING MATES",t.toString());
            }
        });
    }

    private void getTestTeacherInfo(){
        doRetrofit();
        Call<ServerResponse<DataGroup>> responseCall = api.groupDetail(AppСonstants.X_API_KEY,
                sharedPreferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), Integer.parseInt(groupID));
        TextView test = view.findViewById(R.id.test);
        responseCall.enqueue(new Callback<ServerResponse<DataGroup>>() {
            @Override
            public void onResponse(Call<ServerResponse<DataGroup>> call, Response<ServerResponse<DataGroup>> response) {
                if (response.code() == 200) {
                    DataGroup groupInfo = response.body().getData();
                    Group group = groupInfo.getGroups();



                    Call<ServerResponse<DataIsUserGetTest>> serverResponseCall = api.isUserGetTest(AppСonstants.X_API_KEY,
                            sharedPreferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), AppСonstants.USER_EMAIL_FIELD,
                            sharedPreferences.getString(AppСonstants.USER_EMAIL, ""));

                    serverResponseCall.enqueue(new Callback<ServerResponse<DataIsUserGetTest>>() {
                        @Override
                        public void onResponse(Call<ServerResponse<DataIsUserGetTest>> call, Response<ServerResponse<DataIsUserGetTest>> response) {

                            if (response.code() == 200){
                                DataIsUserGetTest dataIsUserGetTest = response.body().getData();
                                Button execTest = view.findViewById(R.id.execTest);
                                boolean k = false;
                                for (IsUserGetTest isUserGetTest : dataIsUserGetTest.getIsUserGetTest()){

                                    if (isUserGetTest.getGroupId().equals(groupID) &&
                                            Integer.valueOf(isUserGetTest.getIs_Passed()) == 0){

                                        TextView teacher_name = view.findViewById(R.id.teacher_name);
                                        TextView teacher_email = view.findViewById(R.id.teacher_email);


                                        teacher_name.setText(String.format("%s%s", teacher_name.getText().toString()
                                                + " ", group.getAuthorName()));
                                        teacher_email.setText(String.format("%s%s", teacher_email.getText().toString()
                                                + " ", group.getAuthorEmail()));

                                        getTest(isUserGetTest, group);

                                        k = true;
                                        break;
                                    }
                                    else if (isUserGetTest.getGroupId().equals(groupID)){

                                        TextView teacher_name = view.findViewById(R.id.teacher_name);
                                        TextView teacher_email = view.findViewById(R.id.teacher_email);


                                        teacher_name.setText(String.format("%s%s", teacher_name.getText().toString()
                                                + " ", group.getAuthorName()));
                                        teacher_email.setText(String.format("%s%s", teacher_email.getText().toString()
                                                + " ", group.getAuthorEmail()));

                                        test.setTextColor(ContextCompat.getColor(context, R.color.Completed));
                                        test.setText("Тест доступен");
                                        execTest.setVisibility(View.VISIBLE);
                                        execTest.setTextColor(ContextCompat.getColor(getContext(),
                                                R.color.color_secondary_text));
                                        execTest.setEnabled(false);
                                        k = true;
                                    }else k = false;
                                }

                                if (!k){
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put(AppСonstants.USER_EMAIL_FIELD, sharedPreferences.getString(AppСonstants.USER_EMAIL, ""));
                                    map.put(AppСonstants.GROUP_ID_FIELD, groupID);
                                    map.put("is_Passed", "0");

                                    Call<ServerResponse<PostResult>> responseCall2 = api.addUserGetTest(AppСonstants.X_API_KEY,
                                            sharedPreferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), map);
                                    responseCall2.enqueue(new Callback<ServerResponse<PostResult>>() {
                                        @Override
                                        public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {
                                            if (response.code() == 200) {
                                                getTestTeacherInfo();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {

                                        }
                                    });

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ServerResponse<DataIsUserGetTest>> call, Throwable t) {

                        }
                    });



                }else {
                    Log.e("GET TEACHER INFO", String.valueOf(response.raw()));
                    test.setText("Тест не доступен");
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<DataGroup>> call, Throwable t) {
                Log.e("GET TEACHER INFO", String.valueOf(t));
            }
        });
    }

    private void getTest(IsUserGetTest isUserGetTest, Group group){
        TextView test = view.findViewById(R.id.test);
        Button execTest = view.findViewById(R.id.execTest);

        if (group.getTest().contains("INSERT")){

            test.setTextColor(ContextCompat.getColor(context, R.color.Completed));
            test.setText("Тест доступен");

            execTest.setVisibility(View.VISIBLE);
            execTest.setOnClickListener(v -> {
                HashMap<String, String> map = new HashMap<>();
                map.put("_id", isUserGetTest.getId());
                map.put("group_id", groupID);
                map.put(AppСonstants.USER_EMAIL_FIELD,
                        sharedPreferences.getString(AppСonstants.USER_EMAIL, ""));
                map.put(AppСonstants.TABLE_IS_PASSED, "1");
                map.put("is_Passed", "1");
                execTest.setTextColor(ContextCompat.getColor(getContext(),
                        R.color.color_secondary_text));
                execTest.setEnabled(false);

                Call<ServerResponse<PostResult>> call2 = api.postUserGetTest(AppСonstants.X_API_KEY,
                        sharedPreferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""),
                        map);
                call2.enqueue(new Callback<ServerResponse<PostResult>>() {
                    @Override
                    public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {
                        if (response.code() != 200) Log.e("rrrrr", String.valueOf(response.raw()));
                    }

                    @Override
                    public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {

                    }
                });

                try {
                    Database mDBHelper = new Database(getContext());
                    SQLiteDatabase mDb;
                    mDBHelper = new Database(getContext());
                    mDBHelper.openDataBase();
                    mDBHelper.updateDataBase();

                    mDb = mDBHelper.getWritableDatabase();
                    mDb.execSQL(group.getTest());

                    @SuppressLint("Recycle") Cursor cursor = mDb.rawQuery("Select * from " + AppСonstants.TABLE_TESTS, null);
                    cursor.moveToLast();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(AppСonstants.GROUP_ID_FIELD, Integer.parseInt(group.getId()));
                    mDb.update(AppСonstants.TABLE_TESTS, contentValues,
                            "_id=" + (cursor.getPosition() + 1), null);
                    Snackbar.make(getView(), "Тест получен!", Snackbar.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.i("LOAD TEST", String.valueOf(e));
                }

            });
        }else {
            test.setTextColor(ContextCompat.getColor(context, R.color.notCompleted));
            test.setText("Тест не доступен");
        }
    }

    private void doRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppСonstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
    }

    private void getExtraData(){
        Bundle bundle = this.getArguments();
        groupID = bundle.getString("groupID");
        groupName = bundle.getString("groupName");
    }

    private void getGroupMessage(){
        //метод получения сообщения от учителя
        doRetrofit();
        Call<ServerResponse<DataMessage>> call = api.getGroupMessage(AppСonstants.X_API_KEY,
                sharedPreferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), AppСonstants.GROUP_ID_FIELD, groupID);

        call.enqueue(new Callback<ServerResponse<DataMessage>>() {
            @Override
            public void onResponse(Call<ServerResponse<DataMessage>> call, Response<ServerResponse<DataMessage>> response) {
                if (response.code() == 200){
                    DataMessage dataMessage = response.body().getData();
                    List<MessagesToGroup> list = dataMessage.getMessagesToGroups();
                    if (list.size() != 0) {
                        MessagesToGroup message = list.get(0);
                        TextView textView = view.findViewById(R.id.teacher_message);
                        textView.setText(Html.fromHtml(message.getMessage()));
                    }else {
                        TextView message = view.findViewById(R.id.teacher_message);
                        message.setVisibility(View.GONE);
                    }
                }else Log.e("GETTING MESSAGE", String.valueOf(response.raw()));
            }

            @Override
            public void onFailure(Call<ServerResponse<DataMessage>> call, Throwable t) {
                Log.e("GETTING MESSAGE", String.valueOf(t));
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.leave_group) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
            builder.setMessage("Покинуть группу?");
            builder.setPositiveButton("Покинуть", (dialog, which) -> leaveGroup());
            builder.setNegativeButton("Остаться", (dialog, which) -> dialog.cancel());
            builder.create().show();
        }
    }

    private void leaveGroup(){
        doRetrofit();
        Call<ServerResponse<PostResult>> deleteUser = api.removeMate(AppСonstants.X_API_KEY,
                sharedPreferences.getString(AppСonstants.USER_CURR_GROUP_ID, ""));
        deleteUser.enqueue(new Callback<ServerResponse<PostResult>>() {
            @Override
            public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {
                if (response.code() == 200){
                    navController.navigate(R.id.nav_profile);
                }
                else Log.e("Deleting user", String.valueOf(response.raw()));
            }

            @Override
            public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {
                Log.e("Deleting user", String.valueOf(t));
            }
        });
    }
}
