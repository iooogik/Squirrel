package iooojik.app.klass.tests.questions;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.Database;
import iooojik.app.klass.R;
import iooojik.app.klass.api.Api;
import iooojik.app.klass.models.PostResult;
import iooojik.app.klass.models.ServerResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static iooojik.app.klass.AppСonstants.testDivider;


public class Questions extends Fragment implements View.OnClickListener{

    public Questions() {}

    private View view;
    private Database mDBHelper;
    private SQLiteDatabase mDb;
    private Cursor userCursor;

    private List<QuestionObject> questionObjects;
    static List<QuestionsAdapter.ViewHolder> recyclerViewItems;
    static int userScore = 0;
    private int totalScore = 0;
    private Api api;
    private SharedPreferences preferences;
    private final Handler chrono = new Handler();
    private boolean running = true;
    private int seconds;
    private String testName = "";
    private Context context;
    static int difficultiesCount = 0;
    private boolean isTestEnd = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_questions, container, false);
        mDBHelper = new Database(getContext());
        mDBHelper.openDataBase();
        questionObjects = new ArrayList<>();
        recyclerViewItems = new ArrayList<>();
        preferences = getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        getInformation();


        Button completed = view.findViewById(R.id.send_answers);
        if (preferences.getString(AppСonstants.USER_ROLE, "").equals("pupil")){
            completed.setOnClickListener(this);
            setTimer();
        }else completed.setVisibility(View.GONE);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();



        return view;
    }

    @SuppressLint("Recycle")
    private void getInformation() {

        mDb = mDBHelper.getReadableDatabase();

        userCursor =  mDb.rawQuery("Select * from " + AppСonstants.TABLE_TESTS + " WHERE _id=?",
                new String[]{String.valueOf(getTestID())});
        userCursor.moveToFirst();

        Cursor fileCursor = mDb.rawQuery("Select * from " + AppСonstants.TABLE_FILES_TO_QUESTIONS + " WHERE test_id=?",
                new String[]{String.valueOf(getTestID())});
        fileCursor.moveToFirst();

        context = getContext();
        //получаем вопросы, ответы и правильные ответы
        String[] quests = userCursor.getString(userCursor.getColumnIndex(AppСonstants.TABLE_QUESTIONS))
                .split(Pattern.quote(testDivider));
        testName = userCursor.getString(userCursor.getColumnIndex(AppСonstants.TABLE_NAME));

        String[] answersArray = userCursor.getString(userCursor.getColumnIndex(AppСonstants.TABLE_TEXT_ANSWERS))
                .split(Pattern.quote(testDivider));

        String[] trueAnswersArray = userCursor.getString(userCursor.getColumnIndex(AppСonstants.TABLE_ANSWERS))
                .split(Pattern.quote(testDivider));

        String[] scoresArray = userCursor.getString(userCursor.getColumnIndex(AppСonstants.TABLE_SCORES))
                .split(Pattern.quote(testDivider));

        List<String> questions = new ArrayList<>(Arrays.asList(quests));
        List<String> answers = new ArrayList<>(Arrays.asList(answersArray));
        List<String> trueAnswers = new ArrayList<>(Arrays.asList(trueAnswersArray));
        List<String> scores = new ArrayList<>(Arrays.asList(scoresArray));
        Log.e("ttttt", scores.toString());
        int filesUsed = 0;
        for (int i = 0; i < questions.size(); i++) {
            String[] tempAnswers = new String[4];
            for (int j = 0; j < 4; j++) {
                tempAnswers[j] = answers.get(j);
            }

            String fileURL = "";
            if (fileCursor.getCount() > 0 && filesUsed < fileCursor.getCount()) {
                int fileQuestNum = fileCursor.getInt(fileCursor.getColumnIndex(AppСonstants.TABLE_QUESTION_NUM)) - 1;
                filesUsed++;
                if (fileQuestNum == i) {
                    fileURL = fileCursor.getString(fileCursor.getColumnIndex(AppСonstants.TABLE_FILE_URL));
                }
            }
            fileCursor.moveToNext();

            questionObjects.add(new QuestionObject(questions.get(i), Arrays.asList(tempAnswers),
                    trueAnswers.get(i),
                    Integer.valueOf(scores.get(i)),
                    fileURL));
            totalScore+=Integer.valueOf(scores.get(i));
            answers.subList(0, 4).clear();
        }
        QuestionsAdapter questionsAdapter = new QuestionsAdapter(getContext(), questionObjects);
        RecyclerView recyclerViewQuestions = view.findViewById(R.id.questions);
        recyclerViewQuestions.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewQuestions.setAdapter(questionsAdapter);
        //получаем файлы
    }

    private int getTestID(){
        Bundle arguments = this.getArguments();
        assert arguments != null;
        return arguments.getInt("test id");
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.send_answers){
            endTest(false);
        }
    }

    @SuppressLint("InflateParams")
    private void endTest(boolean isTime){
        isTestEnd = true;
        if (isTime){

            sendAnswers();
            showAnswers();
            running = false;

        } else {

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(view.getContext());
            final LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);


            TextView textView = new TextView(getContext());

            textView.setText("Вы действительно хотите завершить выполнение теста?");

            layout.addView(textView);
            builder.setView(layout);

            builder.setPositiveButton("Да", (dialog, which) -> {
                sendAnswers();
                showAnswers();
                running = false;
            });

            builder.setNegativeButton("Нет", (dialog, which) -> dialog.cancel());

            builder.create().show();
        }

    }

    private void showAnswers() {
        Button completed = view.findViewById(R.id.send_answers);
        completed.setVisibility(View.GONE);
        for (int i = 0; i < questionObjects.size(); i++) {
            QuestionObject object = questionObjects.get(i);
            QuestionsAdapter.ViewHolder recView = recyclerViewItems.get(i);
            List<RadioButton> radioButtons = new ArrayList<>();

            recView.firstAnswer.setEnabled(false);
            recView.secondAnswer.setEnabled(false);
            recView.thirdAnswer.setEnabled(false);
            recView.fourthAnswer.setEnabled(false);

            radioButtons.add(recView.firstAnswer);
            radioButtons.add(recView.secondAnswer);
            radioButtons.add(recView.thirdAnswer);
            radioButtons.add(recView.fourthAnswer);

            String answer = object.getTrueAnswer();

            for (RadioButton radioButton : radioButtons){
                if (radioButton.getText().toString().equals(answer) && radioButton.isChecked()){
                    radioButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.rightAnswer));
                } else if (!radioButton.isChecked() && radioButton.getText().toString().equals(answer)){
                    radioButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.wrongAnswer));
                }
            }

        }
    }

    private void doRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppСonstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
    }

    private void sendAnswers(){
        mDb = mDBHelper.getWritableDatabase();

        userCursor =  mDb.rawQuery("Select * from " + AppСonstants.TABLE_TESTS + " WHERE _id=?",
                new String[]{String.valueOf(getTestID())});
        userCursor.moveToFirst();

        ContentValues contentValues = new ContentValues();
        contentValues.put(AppСonstants.TABLE_USER_SCORE, userScore);
        contentValues.put(AppСonstants.TABLE_IS_PASSED, 1);
        mDb.update(AppСonstants.TABLE_TESTS, contentValues, "_id =" + (getTestID()), null);

        //отправка результатов

        doRetrofit();
        HashMap<String, String> map = new HashMap<>();
        map.put(AppСonstants.USER_EMAIL_FIELD, preferences.getString(AppСonstants.USER_EMAIL, ""));
        map.put(AppСonstants.GROUP_ID_FIELD, String.valueOf(userCursor.getInt(userCursor.getColumnIndex(AppСonstants.TABLE_GROUP_ID))));
        map.put(AppСonstants.DIFFICULTIES_FIELD, String.valueOf(difficultiesCount));
        map.put(AppСonstants.TABLE_RESULT,  String.valueOf(Math.round ((Float.valueOf(userScore)
                / Float.valueOf(totalScore)) * 100.0f)));

        Log.e("ttt", String.valueOf(Math.round ((Float.valueOf(userScore)
                / Float.valueOf(totalScore)) * 100.0f)));

        Log.e("trrr", map.values().toString());

        Call<ServerResponse<PostResult>> updateInfo = api.addResult(AppСonstants.X_API_KEY,
                preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), map);

        updateInfo.enqueue(new Callback<ServerResponse<PostResult>>() {
            @Override
            public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {
                if (response.code() != 200) Log.e("SENDING RESULT", String.valueOf(response.body()));
            }

            @Override
            public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {
                Log.e("SENDING RESULT", String.valueOf(t));
            }
        });

        map = new HashMap<>();

        map.put(AppСonstants.USER_EMAIL_FIELD, preferences.getString(AppСonstants.USER_EMAIL, ""));
        map.put(AppСonstants.TEST_NAME_FIELD, testName);
        map.put("result",  String.valueOf(Math.round ((Float.valueOf(userScore)
                / Float.valueOf(totalScore)) * 100.0f)));

        Call<ServerResponse<PostResult>> responseCall = api.addTestResult(AppСonstants.X_API_KEY,
                preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), map);
        responseCall.enqueue(new Callback<ServerResponse<PostResult>>() {
            @Override
            public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {
                if (response.code() != 200) Log.e("ADD TEST RESULT", String.valueOf(response.raw()));
            }

            @Override
            public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {
                Log.e("ADD TEST RESULT", String.valueOf(t));
            }
        });


        //изменяем значения койнов
        //если тест выполнен на 0-30%, то добавляем 1 койн, если на 31 - 60 %, то 3 койна,
        // если на 61 - 75 %, то 4 койна, если 76 - 100%, то 5 койнов


        HashMap<String, String> log = new HashMap<>();
        log.put("user_email", preferences.getString(AppСonstants.USER_EMAIL, ""));

        int percent = Integer.valueOf(Math.round ((Float.valueOf(userScore) / Float.valueOf(totalScore)) * 100.0f));

        int coins = preferences.getInt(AppСonstants.USER_COINS, 0);

        if (percent <= 30) AppСonstants.USER_COINS += 1;
        else if (percent >= 31 && percent <= 60) {
            coins += 3;
            log.put("achievement_change", "add " + 3 + " coins");
        }
        else if (percent >= 61 && percent <= 75) {
            coins += 4;
            log.put("achievement_change", "add " + 4 + " coins");
        }
        else if (percent >= 76 && percent <= 100) {
            coins += 5;
            log.put("achievement_change", "add " + 5 + " coins");
        }

        preferences.edit().putInt(AppСonstants.USER_COINS, coins).apply();

        //получаем дату
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String timeText = timeFormat.format(currentDate);

        String full_date = dateText + " " + timeText;

        log.put("date_change", full_date);

        //логируем койны
        Call<ServerResponse<PostResult>> call = api.logAchievement(AppСonstants.X_API_KEY,
                AppСonstants.AUTH_SAVED_TOKEN, log);

        call.enqueue(new Callback<ServerResponse<PostResult>>() {
            @Override
            public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {
                if (response.code() != 200) Log.e("LOG ACHIEVEMENT", String.valueOf(response.raw()));
            }

            @Override
            public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {
                Log.e("LOG ACHIEVEMENT", String.valueOf(t));
            }
        });

        //добавляем койны
        HashMap<String, String> changes = new HashMap<>();
        changes.put("user_email", preferences.getString(AppСonstants.USER_EMAIL, ""));
        changes.put("_id", String.valueOf(preferences.getInt(AppСonstants.ACHIEVEMENTS_ID, -1)));
        changes.put("coins", String.valueOf(preferences.getInt(AppСonstants.USER_COINS, 0)));

            Call<ServerResponse<PostResult>> call2 = api.updateAchievement(AppСonstants.X_API_KEY,
                    preferences.getString(AppСonstants.STANDART_TOKEN, ""), changes);

        call2.enqueue(new Callback<ServerResponse<PostResult>>() {
                @Override
                public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {
                    if (response.code() != 200) Log.e("ADD ACHIEVEMENT", String.valueOf(response.raw()));
                }

                @Override
                public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {
                    Log.e("ADD ACHIEVEMENT", String.valueOf(t));
                }
        });

    }

    @SuppressLint("DefaultLocale")
    private void setTimer(){
        userCursor =  mDb.rawQuery("Select * from " + AppСonstants.TABLE_TESTS + " WHERE _id=?",
                new String[]{String.valueOf(getTestID())});
        userCursor.moveToFirst();
        int time = userCursor.getInt(userCursor.getColumnIndex(AppСonstants.TABLE_TIME));
        TextView time_process = view.findViewById(R.id.timer);
        time_process.setVisibility(View.VISIBLE);
        running = true;
        seconds = time * 60;

        chrono.post(new Runnable() {
            @Override
            public void run() {
                if(running && seconds != 0 && context == getContext()) {
                    int minutes = (seconds % 3600) / 60;
                    int secon = seconds % 60;
                    String time = String.format("%02d:%02d", minutes, secon);
                    time_process.setText(time);
                    seconds--;
                    chrono.postDelayed(this, 1000);
                } else {
                    running = false;
                    if (context == getContext() && !isTestEnd) {
                        endTest(true);
                    }
                }
            }
        });
    }
}
