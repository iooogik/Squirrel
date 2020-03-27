package iooogik.app.modelling.tests;

import android.content.Context;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import iooogik.app.modelling.R;


public class TestEditor extends Fragment implements View.OnClickListener {

    public TestEditor() {}

    private View view;
    private FloatingActionButton fab;
    private Context context;
    private List<View> questions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_test_editor, container, false);
        fab = getActivity().findViewById(R.id.fab);
        fab.show();
        fab.setOnClickListener(this);
        questions = new ArrayList<>();
        Button button = view.findViewById(R.id.collectTest);
        button.setOnClickListener(this);

        context = getContext();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:

                View q = getLayoutInflater().inflate(R.layout.dialog_edit_test, null);

                LinearLayout layout = view.findViewById(R.id.linear);

                Spinner spinner = q.findViewById(R.id.spinner);
                String[] trueAnsw = {"Первый ответ", "Второй ответ", "Третий ответ", "Четвёртый ответ"};
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

    public void CollectQuestionsAndSendThem(){
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
            trueAnswers.add(spinner.getSelectedItem().toString());
        }

        //собираем каждый массив, чтобы выполнить SQL-запрос

        StringBuilder builderQuestions = new StringBuilder();
        builderQuestions.append("'");
        for (String question: textQuestions) builderQuestions.append(question).append("\n");
        builderQuestions.append("'");

        StringBuilder builderTrueAnswers = new StringBuilder();
        builderTrueAnswers.append("'");
        for (String answ : trueAnswers) builderTrueAnswers.append(answ).append("\n");
        builderTrueAnswers.append("'");

        StringBuilder builderTextAnswers = new StringBuilder();
        builderTextAnswers.append("'");
        for (String answ : textAnswers) builderTextAnswers.append(answ).append("\n");
        builderTextAnswers.append("'");

        EditText name = view.findViewById(R.id.name);
        EditText description = view.findViewById(R.id.description);

        TextView textView = view.findViewById(R.id.textView2);
        textView.setText(createSQLandSendToDatabase(name.getText().toString(), description.getText().toString(),
                builderQuestions.toString(), builderTrueAnswers.toString(), builderTextAnswers.toString()));

    }

    public String createSQLandSendToDatabase(String name, String description, String textQuestions,
                                             String trueAnswers, String textAnswers){
        String SQL = "";

        SQL = "INSERT INTO Tests (name, description, questions, answers, textAnswers, trueAnswers, wrongAnswers)" +
                "VALUES (" + name + "," + description + "," + textQuestions + "," + trueAnswers + "," + textAnswers + ","
                + "0" + "," + "0" + ")";

        return SQL;
    }
}
