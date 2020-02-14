package iooogik.app.modelling;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import iooogik.app.modelling.DatabaseHelper;
import iooogik.app.modelling.R;

public class Questions extends Fragment implements View.OnClickListener{

    public Questions() {}

    View view;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    private Cursor userCursor;

    private List<String> questions = new ArrayList<>();
    private List<String> answers = new ArrayList<>();
    private List<String> isTrue = new ArrayList<>();
    private int rightScore = 0;
    private int wrongScore = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_questions, container, false);
        mDBHelper = new DatabaseHelper(getContext());
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();
        getQuestions();
        getAnswers();
        setTest();
        wrongScore = questions.size();
        Button btn = view.findViewById(R.id.button);
        btn.setOnClickListener(this);
        return view;
    }

    private void setTest(){
        List<String> temp = new ArrayList<>(answers);
        for (int i = 0; i < (answers.size()/questions.size()); i++) {

            View view1 = getLayoutInflater().inflate(R.layout.question, null, false);

            //вопрос
            TextView nameNote = view1.findViewById(R.id.task);
            nameNote.setText(questions.get(i));



            RadioButton radioButton1 = view1.findViewById(R.id.radioButton1);
            radioButton1.setText(temp.get(0));

            radioButton1.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked && isTrue.contains(radioButton1.getText().toString())){
                    rightScore++;
                }else if (!isChecked && isTrue.contains(radioButton1.getText().toString())){
                    rightScore--;
                }
            });

            RadioButton radioButton2 = view1.findViewById(R.id.radioButton2);
            radioButton2.setText(temp.get(1));

            radioButton2.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked && isTrue.contains(radioButton2.getText().toString())){
                    rightScore++;
                }else if (!isChecked && isTrue.contains(radioButton2.getText().toString())){
                    rightScore--;
                }
            });

            RadioButton radioButton3 = view1.findViewById(R.id.radioButton3);
            radioButton3.setText(temp.get(2));

            radioButton3.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked && isTrue.contains(radioButton3.getText().toString())){
                    rightScore++;
                }else if (!isChecked && isTrue.contains(radioButton3.getText().toString())){
                    rightScore--;
                }
            });

            RadioButton radioButton4 = view1.findViewById(R.id.radioButton4);
            radioButton4.setText(temp.get(3));

            radioButton4.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked && isTrue.contains(radioButton4.getText().toString())){
                    rightScore++;
                }else if (!isChecked && isTrue.contains(radioButton4.getText().toString())){
                    rightScore--;
                }
            });

            temp.subList(0, 4).clear();

            //установка на активити
            LinearLayout linearLayout = view.findViewById(R.id.scrollQuestThemes);
            linearLayout.addView(view1);
        }
        temp.clear();
    }

    private int getBtnID(){
        Bundle arguments = this.getArguments();
        assert arguments != null;
        return arguments.getInt("button ID");
    }

    private void getAnswers(){
        mDb = mDBHelper.getReadableDatabase();
        userCursor = mDb.rawQuery("Select * from Tests", null);
        userCursor.moveToPosition(getBtnID());

        String TEMPansws = userCursor.getString(userCursor.getColumnIndex("textAnswers"));
        String[] answ = TEMPansws.split("\n");
        answers.addAll(Arrays.asList(answ));

        TEMPansws = userCursor.getString(userCursor.getColumnIndex("answers"));
        answ = TEMPansws.split("\n");

        isTrue.addAll(Arrays.asList(answ));
    }

    private void getQuestions() {
        mDb = mDBHelper.getReadableDatabase();
        userCursor = mDb.rawQuery("Select * from Tests", null);
        userCursor.moveToPosition(getBtnID());
        String TEMPquests = userCursor.getString(userCursor.getColumnIndex("questions"));
        String[] quests = TEMPquests.split("\n");
        questions.addAll(Arrays.asList(quests));
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button){
            Bundle bundle = new Bundle();
            bundle.putInt("Score", rightScore);
            bundle.putInt("wrongScore", wrongScore);



        }
    }
}
