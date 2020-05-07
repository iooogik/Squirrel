package iooojik.app.klass.tests.tests;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.Database;
import iooojik.app.klass.MainActivity;
import iooojik.app.klass.R;
import iooojik.app.klass.api.Api;
import iooojik.app.klass.models.ServerResponse;
import iooojik.app.klass.models.passed_test_result.DataPassedTest;
import iooojik.app.klass.models.passed_test_result.PassedTest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static iooojik.app.klass.AppСonstants.TABLE_ID;
import static iooojik.app.klass.AppСonstants.TABLE_TESTS;

public class Tests extends Fragment implements View.OnClickListener{

    private View view;

    //Переменная для работы с БД
    private Database mDBHelper;

    private static List<TestObject> TEST_ITEMS;
    private BottomSheetDialog bottomSheetDialog;
    private SharedPreferences preferences;
    private Api api;

    public Tests() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_test, container, false);

        FloatingActionButton floatingActionButton = getActivity().findViewById(R.id.fab);
        floatingActionButton.setImageResource(R.drawable.round_keyboard_arrow_up_24);
        floatingActionButton.show();
        floatingActionButton.setOnClickListener(this);
        TEST_ITEMS = new ArrayList<>();
        mDBHelper = new Database(getContext());
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();

        preferences = getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);

        enableBottomSheet();

        setHasOptionsMenu(true);
        loadAndSetThemes();
        return view;
    }

    @SuppressLint("InflateParams")
    private void enableBottomSheet() {
        bottomSheetDialog = new BottomSheetDialog(getActivity());
        View bottomSheet = getActivity().getLayoutInflater().inflate(R.layout.bottom_sheet_tests_result, null);
        //делаем запрос на получение результатов и ставим адаптер

        doRetrofit();
        Call<ServerResponse<DataPassedTest>> responseCall = api.getPassedTestResult(AppСonstants.X_API_KEY,
                preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""),
                AppСonstants.USER_EMAIL_FIELD, preferences.getString(AppСonstants.USER_EMAIL, ""));
        responseCall.enqueue(new Callback<ServerResponse<DataPassedTest>>() {
            @Override
            public void onResponse(Call<ServerResponse<DataPassedTest>> call, Response<ServerResponse<DataPassedTest>> response) {
                if (response.code() == 200){
                    RecyclerView results = bottomSheet.findViewById(R.id.results);
                    TestResultAdapter testResultAdapter = new TestResultAdapter(response.body().getData().getPassedTests(), getContext());
                    results.setLayoutManager(new LinearLayoutManager(getContext()));
                    results.setAdapter(testResultAdapter);
                } else Log.e("GETTING RESULTS", String.valueOf(response.raw()));
            }

            @Override
            public void onFailure(Call<ServerResponse<DataPassedTest>> call, Throwable t) {
                Log.e("GETTING RESULTS", String.valueOf(t));
            }
        });

        bottomSheetDialog.setContentView(bottomSheet);
    }

    private void doRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppСonstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
    }

    private void loadAndSetThemes(){
        SQLiteDatabase mDb = mDBHelper.getReadableDatabase();
        Cursor userCursor = mDb.rawQuery("Select * from " + TABLE_TESTS, null);
        userCursor.moveToFirst();
        String name, desc;

        //необходимо очистить содержимое, чтобы при старте активити не было повторяющихся элементов
        try {
            TEST_ITEMS.clear();
        } catch (Exception e){
            Log.i(TABLE_TESTS, String.valueOf(e));
        }

        while (!userCursor.isAfterLast()) {
            //получение имени
            name = String.valueOf(userCursor.getString(1));
            //получение описания
            desc = String.valueOf(userCursor.getString(2));
            //получение количества правильных и неправильных ответов
            int userScore = 0, totalScore = 0;

            userScore = userCursor.getInt(userCursor.getColumnIndex(AppСonstants.TABLE_USER_SCORE));
            totalScore = userCursor.getInt(userCursor.getColumnIndex(AppСonstants.TABLE_TOTAL_SCORE));

            int isPassedDB = userCursor.getInt(userCursor.getColumnIndex(AppСonstants.TABLE_IS_PASSED));
            boolean isPassed = false;
            isPassed = isPassedDB == 1;

            int id = userCursor.getInt(userCursor.getColumnIndex(TABLE_ID));
            //добавляем тест в recyclerView
            TEST_ITEMS.add(new TestObject(name, desc, userScore, totalScore, isPassed, id));
            userCursor.moveToNext();
        }
        userCursor.close();
        RecyclerView recyclerView = view.findViewById(R.id.test_items);
        TestsAdapter TEST_ADAPTER = new TestsAdapter(getContext(), TEST_ITEMS);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(TEST_ADAPTER);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.back){
            Intent main = new Intent(getContext(), MainActivity.class);
            startActivity(main);
        } else if (v.getId() == R.id.fab){
            bottomSheetDialog.show();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }
}
