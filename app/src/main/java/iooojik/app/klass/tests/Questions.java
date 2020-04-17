package iooojik.app.klass.tests;


import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import iooojik.app.klass.Api;
import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.Database;
import iooojik.app.klass.R;
import iooojik.app.klass.models.PostResult;
import iooojik.app.klass.models.ServerResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Questions extends Fragment implements View.OnClickListener{

    public Questions() {}

    private View view;
    private Database mDBHelper;
    private SQLiteDatabase mDb;
    private Cursor userCursor;

    private List<String> questions;
    private List<String> answers;
    private List<String> isTrue;
    private HashMap<Integer, Bitmap> images;
    private int rightScore = 0;
    private int wrongScore = 0;
    private Api api;
    private SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_questions, container, false);
        mDBHelper = new Database(getContext());
        mDBHelper.openDataBase();

        questions = new ArrayList<>();
        answers = new ArrayList<>();
        isTrue = new ArrayList<>();
        images= new HashMap<>();
        preferences = getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);

        getQuestions();
        getAnswers();
        getImages();
        setTest();
        wrongScore = questions.size();
        ImageView btn = view.findViewById(R.id.send_answers);
        btn.setOnClickListener(this);

        return view;
    }

    private void getImages() {
        mDb = mDBHelper.getReadableDatabase();
        userCursor =  mDb.rawQuery("Select * from picturesToQuestions WHERE test_id=?", new String[]{String.valueOf(getBtnID())});
        userCursor.moveToFirst();
        Bitmap bitmap;
        while (!userCursor.isAfterLast()){
            byte[] bytesImg = userCursor.getBlob(userCursor.getColumnIndex("image"));
            bitmap = BitmapFactory.decodeByteArray(bytesImg, 0, bytesImg.length);
            images.put(userCursor.getInt(userCursor.getColumnIndex("num_question")), bitmap);
            userCursor.moveToNext();
        }

    }

    private void setTest(){
        List<String> temp = answers;

        for (int i = 0; i < questions.size(); i++) {

            View view1 = getLayoutInflater().inflate(R.layout.recycler_view_item_question, null, false);
            //вопрос
            TextView nameNote = view1.findViewById(R.id.task);
            nameNote.setText(questions.get(i));

            for (Map.Entry entry : images.entrySet()){
                if ((int) entry.getKey() - 1 == i){
                    ImageView img = view1.findViewById(R.id.image);
                    img.setVisibility(View.VISIBLE);

                    img.setImageBitmap((Bitmap) entry.getValue());
                    break;
                }
            }

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

        userCursor =  mDb.rawQuery("Select * from Tests WHERE _id=?", new String[]{String.valueOf(getBtnID())});
        userCursor.moveToFirst();

        String TEMPansws = userCursor.getString(userCursor.getColumnIndex("textAnswers"));

        answers.addAll(Arrays.asList(TEMPansws.split("<br>")));

        TEMPansws = userCursor.getString(userCursor.getColumnIndex("answers"));

        isTrue.addAll(Arrays.asList(TEMPansws.split("<br>")));
        System.out.println(isTrue);
    }

    private void getQuestions() {
        mDb = mDBHelper.getReadableDatabase();

        userCursor =  mDb.rawQuery("Select * from Tests WHERE _id=?", new String[]{String.valueOf(getBtnID())});
        userCursor.moveToFirst();

        String TEMP_quests = userCursor.getString(userCursor.getColumnIndex("questions"));
        String[] quests = TEMP_quests.split("<br>");
        questions.addAll(Arrays.asList(quests));
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.send_answers){
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(view.getContext());
            final LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);

            View view1 = getLayoutInflater().inflate(R.layout.text_view, null, false);
            TextView textView = view1.findViewById(R.id.tv);
            textView.setText("Вы действительно хотите завершить выполнение теста?");

            layout.addView(view1);
            builder.setView(layout);

            builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mDb = mDBHelper.getWritableDatabase();

                    userCursor =  mDb.rawQuery("Select * from Tests WHERE _id=?", new String[]{String.valueOf(getBtnID())});
                    userCursor.moveToFirst();

                    ContentValues contentValues = new ContentValues();
                    contentValues.put("trueAnswers", rightScore);
                    contentValues.put("wrongAnswers", wrongScore);
                    contentValues.put("isPassed", 1);
                    mDb.update("Tests", contentValues, "_id =" + (getBtnID()), null);
                    FrameLayout frameLayout = Tests.VIEW.findViewById(R.id.test_frame);
                    frameLayout.removeAllViews();
                    frameLayout.setVisibility(View.GONE);

                    TestTheme testTheme = Tests.TEST_ITEMS.get(getBtnID() - 1);
                    testTheme.setRightAnswers(rightScore);
                    testTheme.setWrongAnswers(wrongScore);
                    testTheme.setPassed(true);
                    Tests.TEST_ADAPTER.notifyDataSetChanged();

                    doRetrofit();
                    HashMap<String, String> map = new HashMap<>();
                    map.put("user_email", preferences.getString(AppСonstants.USER_EMAIL, ""));
                    map.put("group_id", String.valueOf(userCursor.getString(userCursor.getColumnIndex("group_id"))));
                    float tempRightScore = (float) rightScore;
                    float tempWrongScore = (float) wrongScore;
                    map.put("result",  String.valueOf((tempRightScore / tempWrongScore) * 100.0f));



                    Call<ServerResponse<PostResult>> updateInfo = api.addResult(
                            AppСonstants.X_API_KEY, preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), map);

                    updateInfo.enqueue(new Callback<ServerResponse<PostResult>>() {
                        @Override
                        public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {
                            if (response.code() != 200) Log.e("SENDING RESULT", String.valueOf(response.raw()));
                        }

                        @Override
                        public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {
                            Log.e("SENDING RESULT", String.valueOf(t));
                        }
                    });


                }
            });

            builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.create().show();
        }
    }

    private void doRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppСonstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
    }
}
