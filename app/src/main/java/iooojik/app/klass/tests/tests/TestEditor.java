package iooojik.app.klass.tests.tests;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.Database;
import iooojik.app.klass.R;
import iooojik.app.klass.api.Api;
import iooojik.app.klass.models.PostResult;
import iooojik.app.klass.models.ServerResponse;
import iooojik.app.klass.models.test_results.DataTestResult;
import iooojik.app.klass.models.test_results.TestsResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static iooojik.app.klass.AppСonstants.testDivider;


public class TestEditor extends Fragment implements View.OnClickListener {

    public TestEditor() {}

    private View view;
    private Context context;
    private List<View> questions;
    private int id = -1;
    private Api api;
    private String groupName, groupAuthor, groupAuthorName;
    private String firstSel = "Первый ответ", secondSel = "Второй ответ",
    thirdSel = "Третий ответ", fourthSel = "Четвёртый ответ";
    private int time = 0, scorePerAnsw = 10;
    private EditText minutes, score;
    private CheckBox checkBox;
    private SharedPreferences preferences;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_test_editor, container, false);
        context = getContext();
        minutes = view.findViewById(R.id.edit_text_minutes);
        checkBox = view.findViewById(R.id.check);
        score = view.findViewById(R.id.edit_text_score);

        preferences = getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);

        score.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (score.getText().toString().trim().isEmpty()) scorePerAnsw = 10;
                else scorePerAnsw = Integer.parseInt(score.getText().toString());
            }
        });

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                time = 0;
                minutes.setText("");
                minutes.setEnabled(false);
            }else {
                minutes.setEnabled(true);
            }
        });

        minutes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) checkBox.setEnabled(false); else checkBox.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!(minutes.getText().toString().trim().isEmpty())){
                    try {
                        time = Integer.parseInt(minutes.getText().toString().trim());
                    } catch (Exception e){
                        Snackbar.make(getView(), "Пожалуйста, введите корректное время", Snackbar.LENGTH_LONG).show();
                    }
                } else {time = 0;}
            }
        });

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.baseline_add_24);
        fab.show();
        fab.setOnClickListener(this);

        questions = new ArrayList<>();
        Button button = view.findViewById(R.id.collectTest);
        button.setOnClickListener(this);
        getGroupInfo();
        return view;
    }

    private void getGroupInfo(){
        Bundle bundle = this.getArguments();
        groupAuthor = bundle.getString("groupAuthor");
        groupAuthorName = bundle.getString("groupAuthorName");
        groupName = bundle.getString("groupName");
        id = bundle.getInt("id");
    }

    @SuppressLint("InflateParams")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:

                View q = getLayoutInflater().inflate(R.layout.recycler_view_edit_test, null);

                LinearLayout layout = view.findViewById(R.id.linear);

                Spinner spinner = q.findViewById(R.id.spinner);
                String[] trueAnsw = {firstSel, secondSel, thirdSel, fourthSel};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item, trueAnsw);
                spinner.setAdapter(adapter);

                q.setOnLongClickListener(v1 -> {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

                    builder.setMessage("Удалить вопрос?");

                    builder.setPositiveButton("Да", (dialog, which) -> layout.removeView(q));
                    builder.setNegativeButton("Нет", (dialog, which) -> dialog.cancel());
                    builder.create().show();
                    return true;
                });
                questions.add(q);
                layout.addView(q);
                break;
            case R.id.collectTest:

                if ((!(minutes.getText().toString().isEmpty()) || checkBox.isChecked()) && id != -1)
                    uploadTest();
                else Snackbar.make(getView(), "Не все поля заполнены", Snackbar.LENGTH_LONG).show();
                break;

        }
    }

    private void doRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppСonstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
    }

    private void deleteResult(TestsResult result){
        Call<ServerResponse<PostResult>> deleteCall = api.removeResult(AppСonstants.X_API_KEY, result.getId());
        deleteCall.enqueue(new Callback<ServerResponse<PostResult>>() {
            @Override
            public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {

            }

            @Override
            public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {

            }
        });
    }

    private void uploadTest(){
        List<String> textQuestions = new ArrayList<>();
        List<String> trueAnswers = new ArrayList<>();
        List<String> textAnswers = new ArrayList<>();

        //удаляем все результаты ученоков, проходивших тест
        doRetrofit();

        Call<ServerResponse<DataTestResult>> response = api.getTestResults(AppСonstants.X_API_KEY,
                preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), AppСonstants.GROUP_ID_FIELD, String.valueOf(id));

        response.enqueue(new Callback<ServerResponse<DataTestResult>>() {
            @Override
            public void onResponse(Call<ServerResponse<DataTestResult>> call, Response<ServerResponse<DataTestResult>> response) {
                if (response.code() == 200){
                    DataTestResult dataTestResult = response.body().getData();
                    List<TestsResult> results = dataTestResult.getTestsResult();

                    for (TestsResult result : results){
                        deleteResult(result);
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<DataTestResult>> call, Throwable t) {

            }
        });

        for (int i = 0; i < questions.size(); i++) {
            View tempQuestion = questions.get(i);
            String question;

            //вопрос
            EditText editableQuestion = tempQuestion.findViewById(R.id.question);
            question = editableQuestion.getText().toString();
            if (question.isEmpty()) question = "-";
            textQuestions.add(question);

            //первый ответ
            EditText firstAnsw = tempQuestion.findViewById(R.id.answ1);
            String answer = firstAnsw.getText().toString();
            if (answer.trim().isEmpty()) answer = "-";
            textAnswers.add(answer);

            //второй ответ
            EditText secondAnsw = tempQuestion.findViewById(R.id.answ2);
            answer = secondAnsw.getText().toString().trim();
            if (answer.isEmpty()) answer = "-";
            textAnswers.add(answer);

            //третий ответ
            EditText thirdAnsw = tempQuestion.findViewById(R.id.answ3);
            answer = thirdAnsw.getText().toString().trim();
            if (answer.isEmpty()) answer = "-";
            textAnswers.add(answer);

            //четвёртый ответ
            EditText fourthAnsw = tempQuestion.findViewById(R.id.answ4);
            answer = fourthAnsw.getText().toString().trim();
            if (answer.isEmpty()) answer = "-";
            textAnswers.add(answer);

            //правильный ответ
            Spinner spinner = tempQuestion.findViewById(R.id.spinner);
            if (spinner.getSelectedItem().toString().equals(firstSel)) trueAnswers.add(firstAnsw.getText().toString());
            else if (spinner.getSelectedItem().toString().equals(secondSel)) trueAnswers.add(secondAnsw.getText().toString());
            else if (spinner.getSelectedItem().toString().equals(thirdSel)) trueAnswers.add(thirdAnsw.getText().toString());
            else if (spinner.getSelectedItem().toString().equals(fourthSel)) trueAnswers.add(fourthAnsw.getText().toString());

        }

        //собираем каждый массив, чтобы выполнить SQL-запрос
        //объединяем вопросы
        StringBuilder builderQuestions = new StringBuilder();
        builderQuestions.append("'");
        for (String question: textQuestions) builderQuestions.append(question).append(testDivider);
        builderQuestions.append("'");
        //объединяем правильные оветы
        StringBuilder builderTrueAnswers = new StringBuilder();
        builderTrueAnswers.append("'");
        for (String answ : trueAnswers) builderTrueAnswers.append(answ).append(testDivider);
        builderTrueAnswers.append("'");
        //объединяем все ответы
        StringBuilder builderTextAnswers = new StringBuilder();
        builderTextAnswers.append("'");
        for (String answ : textAnswers) builderTextAnswers.append(answ).append(testDivider);
        builderTextAnswers.append("'");

        EditText name = view.findViewById(R.id.name);
        EditText description = view.findViewById(R.id.description);

        doRetrofit();

        HashMap<String, String> updateMap = new HashMap<>();
        String SQL = createSQL("'"+ name.getText().toString() + "'",
                "'"+ description.getText().toString() + "'",
                builderQuestions.toString(),
                builderTrueAnswers.toString(),
                builderTextAnswers.toString(), textQuestions.size());
        updateMap.put("_id", String.valueOf(id));
        updateMap.put("author_email", groupAuthor);
        updateMap.put("author_name", groupAuthorName);
        updateMap.put("name", groupName);
        updateMap.put("test", SQL);
        updateMap.put("count_questions", String.valueOf(questions.size()));

        Database mDBHelper;
        mDBHelper = new Database(getContext());
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();
        SQLiteDatabase mDb = mDBHelper.getWritableDatabase();
        mDb.execSQL(SQL);

        Call<ServerResponse<PostResult>> responseCall = api.updateTest(AppСonstants.X_API_KEY,
                getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES,
                        Context.MODE_PRIVATE).getString(AppСonstants.AUTH_SAVED_TOKEN, ""),
                updateMap);

        responseCall.enqueue(new Callback<ServerResponse<PostResult>>() {
            @Override
            public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {
                if (response.code()==200) {
                    Snackbar.make(view, "Тест был успешно добавлен!", Snackbar.LENGTH_LONG).show();
                }else {
                    Snackbar.make(view, "Что-то пошло не так. Код ошибки: " + response.code(),
                            Snackbar.LENGTH_LONG).show();
                    Log.e("ADD TEST", String.valueOf(response.raw()));
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {
                Log.e("ADD TEST", String.valueOf(t));
            }
        });
    }

    private String createSQL(String name, String description, String textQuestions,
                                              String trueAnswers, String textAnswers, int size){
        String SQL = "";
        SQL = "INSERT INTO Tests (" +
                AppСonstants.TABLE_NAME + ", " +
                AppСonstants.TABLE_DESCRIPTION + ", " +
                AppСonstants.TABLE_IS_PASSED + ", " +
                AppСonstants.TABLE_QUESTIONS + ", " +
                AppСonstants.TABLE_ANSWERS + ", " +
                AppСonstants.TABLE_TEXT_ANSWERS + ", " +
                AppСonstants.TABLE_TOTAL_SCORE + ", " +
                AppСonstants.TABLE_USER_SCORE + ", " +
                AppСonstants.TABLE_SCORE_QUEST + ", " +
                AppСonstants.TABLE_TIME + ", " +
                AppСonstants.TABLE_GROUP_ID +
                ") "

                +

                "VALUES (" +
                name + "," +
                description + "," +
                0 + "," +
                textQuestions + "," +
                trueAnswers + "," +
                textAnswers + "," +
                (scorePerAnsw * size) + "," +
                0 + "," +
                scorePerAnsw + "," +
                time + "," +
                id +")";

        return SQL;
    }
}
