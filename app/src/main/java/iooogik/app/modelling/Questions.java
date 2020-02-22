package iooogik.app.modelling;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Questions extends Fragment implements View.OnClickListener{

    public Questions() {}

    View view;
    private Database mDBHelper;
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
        mDBHelper = new Database(getContext());
        mDBHelper.openDataBase();

        getQuestions();
        getAnswers();
        setTest();
        wrongScore = questions.size();
        Button btn = view.findViewById(R.id.send_answers);
        btn.setOnClickListener(this);
        return view;
    }

    private void setTest(){
        List<String> temp = new ArrayList<>(answers);
        for (int i = 0; i < (answers.size()/questions.size() + 1); i++) {

            View view1 = getLayoutInflater().inflate(R.layout.item_question, null, false);

            //вопрос
            TextView nameNote = view1.findViewById(R.id.task);
            nameNote.setText(questions.get(i));



            RadioButton radioButton1 = view1.findViewById(R.id.radioButton1);
            radioButton1.setText(temp.get(0));

            radioButton1.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked && isTrue.contains(radioButton1.getText().toString())){
                    rightScore++;
                    Toast.makeText(getContext(), "true", Toast.LENGTH_SHORT).show();
                }else if (!isChecked && isTrue.contains(radioButton1.getText().toString())){
                    rightScore--;
                }
            });

            RadioButton radioButton2 = view1.findViewById(R.id.radioButton2);
            radioButton2.setText(temp.get(1));

            radioButton2.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked && isTrue.contains(radioButton2.getText().toString())){
                    rightScore++;
                    Toast.makeText(getContext(), "true", Toast.LENGTH_SHORT).show();
                }else if (!isChecked && isTrue.contains(radioButton2.getText().toString())){
                    rightScore--;
                }
            });

            RadioButton radioButton3 = view1.findViewById(R.id.radioButton3);
            radioButton3.setText(temp.get(2));

            radioButton3.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked && isTrue.contains(radioButton3.getText().toString())){
                    rightScore++;
                    Toast.makeText(getContext(), "true", Toast.LENGTH_SHORT).show();
                }else if (!isChecked && isTrue.contains(radioButton3.getText().toString())){
                    rightScore--;
                }
            });

            RadioButton radioButton4 = view1.findViewById(R.id.radioButton4);
            radioButton4.setText(temp.get(3));

            radioButton4.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked && isTrue.contains(radioButton4.getText().toString())){
                    rightScore++;
                    Toast.makeText(getContext(), "true", Toast.LENGTH_SHORT).show();
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

        answers.addAll(Arrays.asList(TEMPansws.split("\r\n|\r|\n")));

        TEMPansws = userCursor.getString(userCursor.getColumnIndex("answers"));

        isTrue.addAll(Arrays.asList(TEMPansws.split("\r\n|\r|\n")));
    }

    private void getQuestions() {
        mDb = mDBHelper.getReadableDatabase();
        userCursor = mDb.rawQuery("Select * from Tests", null);
        userCursor.moveToPosition(getBtnID());
        String TEMP_quests = userCursor.getString(userCursor.getColumnIndex("questions"));
        String[] quests = TEMP_quests.split("\r\n|\r|\n");
        questions.addAll(Arrays.asList(quests));
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.send_answers){

            mDb = mDBHelper.getWritableDatabase();
            userCursor = mDb.rawQuery("Select * from Tests", null);
            userCursor.moveToPosition(getBtnID());
            ContentValues contentValues = new ContentValues();
            contentValues.put("trueAnswers", rightScore);
            contentValues.put("wrongAnswers", wrongScore);
            contentValues.put("isPassed", 1);
            mDb.update("Tests", contentValues, "_id =" + (getBtnID() + 1), null);
            FrameLayout frameLayout = Test.VIEW.findViewById(R.id.test_frame);
            frameLayout.removeAllViews();
            frameLayout.setVisibility(View.GONE);

            TestTheme testTheme = Test.TEST_ITEMS.get(getBtnID());
            testTheme.setRightAnswers(rightScore);
            testTheme.setWrongAnswers(wrongScore);
            testTheme.setPassed(true);
            Test.TEST_ADAPTER.notifyDataSetChanged();
        }
    }
}
