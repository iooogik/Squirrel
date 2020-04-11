package iooojik.app.klass.tests;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import iooojik.app.klass.Api;
import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.R;
import iooojik.app.klass.models.PostResult;
import iooojik.app.klass.models.ServerResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_test_editor, container, false);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.show();
        fab.setOnClickListener(this);
        questions = new ArrayList<>();
        Button button = view.findViewById(R.id.collectTest);
        button.setOnClickListener(this);
        getGroupInfo();

        context = getContext();
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

                layout.setOnLongClickListener(v1 -> {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                    LinearLayout layout1 = new LinearLayout(context);
                    TextView textView = new TextView(context);
                    textView.setText("Вы действительно хотите удалить вопрос?");
                    layout1.addView(textView);
                    builder.setView(layout1);
                    builder.setPositiveButton("Да", (dialog, which) -> layout.removeView(q));
                    builder.setNegativeButton("Нет", (dialog, which) -> dialog.cancel());
                    return true;
                });
                questions.add(q);
                layout.addView(q);
                break;
            case R.id.collectTest:
                CollectQuestionsAndSendThem();
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

    private void CollectQuestionsAndSendThem(){
        List<String> textQuestions = new ArrayList<>();
        List<String> trueAnswers = new ArrayList<>();
        List<String> textAnswers = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {
            View tempQuestion = questions.get(i);
            String question;

            //вопрос
            EditText editableQuestion = tempQuestion.findViewById(R.id.question);
            question = editableQuestion.getText().toString();
            textQuestions.add(question);
            //первый ответ
            EditText firstAnsw = tempQuestion.findViewById(R.id.answ1);
            textAnswers.add(firstAnsw.getText().toString());
            //второй ответ
            EditText secondAnsw = tempQuestion.findViewById(R.id.answ2);
            textAnswers.add(secondAnsw.getText().toString());
            //третий ответ
            EditText thirdAnsw = tempQuestion.findViewById(R.id.answ3);
            textAnswers.add(thirdAnsw.getText().toString());
            //четвёртый ответ
            EditText fourthAnsw = tempQuestion.findViewById(R.id.answ4);
            textAnswers.add(fourthAnsw.getText().toString());

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
        for (String question: textQuestions) builderQuestions.append(question).append("<br>");
        builderQuestions.append("'");
        //объединяем правильные оветы
        StringBuilder builderTrueAnswers = new StringBuilder();
        builderTrueAnswers.append("'");
        for (String answ : trueAnswers) builderTrueAnswers.append(answ).append("<br>");
        builderTrueAnswers.append("'");
        //объединяем все ответы
        StringBuilder builderTextAnswers = new StringBuilder();
        builderTextAnswers.append("'");
        for (String answ : textAnswers) builderTextAnswers.append(answ).append("<br>");
        builderTextAnswers.append("'");

        EditText name = view.findViewById(R.id.name);
        EditText description = view.findViewById(R.id.description);

        doRetrofit();

        HashMap<String, String> updateMap = new HashMap<>();

        updateMap.put("_id", String.valueOf(id));
        updateMap.put("author_email", groupAuthor);
        updateMap.put("author_name", groupAuthorName);
        updateMap.put("name", groupName);
        updateMap.put("test", createSQLandSendToDatabase("'"+ name.getText().toString() + "'",
                "'"+ description.getText().toString() + "'",
                builderQuestions.toString(),
                builderTrueAnswers.toString(),
                builderTextAnswers.toString()));

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

    private String createSQLandSendToDatabase(String name, String description, String textQuestions,
                                              String trueAnswers, String textAnswers){
        String SQL = "";

        SQL = "INSERT INTO Tests (name, description, isPassed, questions, answers, textAnswers, trueAnswers, wrongAnswers)" +
                "VALUES (" + name + "," + description + "," + 0 + "," + textQuestions + "," + trueAnswers + "," + textAnswers + ","
                + 0 + "," + 0 + ")";

        return SQL;
    }
}
