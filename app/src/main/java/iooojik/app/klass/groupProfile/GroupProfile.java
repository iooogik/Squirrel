package iooojik.app.klass.groupProfile;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import iooojik.app.klass.Api;
import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.Database;
import iooojik.app.klass.R;
import iooojik.app.klass.group.GroupMatesAdapter;
import iooojik.app.klass.models.ServerResponse;
import iooojik.app.klass.models.matesList.DataUsersToGroup;
import iooojik.app.klass.models.matesList.Mate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GroupProfile extends Fragment {

    public GroupProfile() {}

    private View view;
    private String groupName = "", groupID = "";
    private Api api;
    private Context context;
    private SharedPreferences sharedPreferences;
    private Group group;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group_profile, container, false);
        sharedPreferences = getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        context = getContext();
        setInformation();
        return view;

    }

    private void setInformation(){
        getExtraData();
        TextView groupN = view.findViewById(R.id.group_name);
        groupN.setText(groupName);
        getGroupInformation();
        getTestTeacherInfo();

    }

    private void getGroupInformation() {
        doRetrofit();
        //получаем список одноклассников
        Call<ServerResponse<DataUsersToGroup>> response = api.getMatesList(AppСonstants.X_API_KEY, "group_id", groupID);

        response.enqueue(new Callback<ServerResponse<DataUsersToGroup>>() {
            @Override
            public void onResponse(Call<ServerResponse<DataUsersToGroup>> call, Response<ServerResponse<DataUsersToGroup>> response) {
                if(response.code() == 200) {
                    ServerResponse<DataUsersToGroup> result = response.body();
                    List<Mate> mates = result.getData().getMates();
                    GroupMatesAdapter groupmatesAdapter = new GroupMatesAdapter(context, mates, null);
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
                    group = groupInfo.getGroups();

                    TextView teacher_name = view.findViewById(R.id.teacher_name);
                    TextView teacher_email = view.findViewById(R.id.teacher_email);


                    teacher_name.setText(String.format("%s%s", teacher_name.getText().toString()
                            + " ", group.getAuthorName()));
                    teacher_email.setText(String.format("%s%s", teacher_email.getText().toString()
                            + " ", group.getAuthorEmail()));

                    test.setText(group.getTest());
                    if (group.getTest().contains("INSERT")){
                        test.setTextColor(ContextCompat.getColor(context, R.color.Completed));
                        test.setText("Тест доступен");
                        Button execTest = view.findViewById(R.id.execTest);
                        execTest.setVisibility(View.VISIBLE);
                        execTest.setOnClickListener(v -> {
                            try {
                                Database mDBHelper = new Database(getContext());
                                SQLiteDatabase mDb;
                                mDBHelper = new Database(getContext());
                                mDBHelper.openDataBase();
                                mDBHelper.updateDataBase();

                                mDb = mDBHelper.getWritableDatabase();
                                mDb.execSQL(group.getTest());

                                @SuppressLint("Recycle") Cursor cursor = mDb.rawQuery("Select * from Tests", null);
                                cursor.moveToLast();
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("group_id", Integer.parseInt(group.getId()));
                                mDb.update("Tests", contentValues, "_id=" + (cursor.getPosition() + 1), null);
                                Snackbar.make(getView(), "Тест получен!", Snackbar.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Log.i("LOAD TEST", String.valueOf(e));
                            }

                        });
                    }else {
                        test.setTextColor(ContextCompat.getColor(context, R.color.notCompleted));
                        test.setText("Тест не доступен");
                    }

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
}
