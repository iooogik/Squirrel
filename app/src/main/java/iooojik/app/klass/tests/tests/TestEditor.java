package iooojik.app.klass.tests.tests;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.developer.filepicker.model.DialogConfigs;
import com.developer.filepicker.model.DialogProperties;
import com.developer.filepicker.view.FilePickerDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.Database;
import iooojik.app.klass.R;
import iooojik.app.klass.api.Api;
import iooojik.app.klass.api.FileUploadApi;
import iooojik.app.klass.models.PostResult;
import iooojik.app.klass.models.ServerResponse;
import iooojik.app.klass.models.isUserGetTest.DataIsUserGetTest;
import iooojik.app.klass.models.isUserGetTest.IsUserGetTest;
import iooojik.app.klass.models.test_results.DataTestResult;
import iooojik.app.klass.models.test_results.TestsResult;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
    private static List<AttachmentObject> attachmentObjects;
    private int id = -1;
    private Api api;
    private String groupName, groupAuthor, groupAuthorName;
    private String firstSel = "Первый ответ", secondSel = "Второй ответ",
    thirdSel = "Третий ответ", fourthSel = "Четвёртый ответ";
    private int time = 0;
    private EditText minutes, score;
    private CheckBox checkBox;
    private SharedPreferences preferences;
    private int numQuestions = 0;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_test_editor, container, false);
        context = getContext();
        minutes = view.findViewById(R.id.edit_text_minutes);
        checkBox = view.findViewById(R.id.check);

        preferences = getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);

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
        attachmentObjects = new ArrayList<>();
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
        switch (v.getId()) {
            case R.id.fab:
                numQuestions++;
                View q = getLayoutInflater().inflate(R.layout.recycler_view_edit_test, null);
                q.setTag(numQuestions);
                LinearLayout layout = view.findViewById(R.id.linear);

                Spinner spinner = q.findViewById(R.id.spinner);
                String[] trueAnsw = {firstSel, secondSel, thirdSel, fourthSel};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item, trueAnsw);
                spinner.setAdapter(adapter);
                TextView hint = q.findViewById(R.id.hint);
                q.setOnLongClickListener(v1 -> {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

                    builder.setMessage("Удалить вопрос?");

                    builder.setPositiveButton("Да", (dialog, which) -> {
                        layout.removeView(q);
                        questions.remove(q);
                        numQuestions--;
                    });
                    builder.setNegativeButton("Нет", (dialog, which) -> dialog.cancel());
                    builder.create().show();
                    return true;
                });

                Button addAttachment = q.findViewById(R.id.addAttachment);
                addAttachment.setOnClickListener(v12 -> addFile((Integer) q.getTag(), q));

                Button deleteAttachment = q.findViewById(R.id.deleteAttachment);
                deleteAttachment.setOnClickListener(v13 -> {
                    for (AttachmentObject object : attachmentObjects){
                        int objID = object.getNumQuestion();
                        if (objID == Integer.valueOf((String) q.getTag())){
                            attachmentObjects.remove(object);
                            hint.setVisibility(View.GONE);
                            break;
                        }
                    }
                });


                questions.add(q);
                layout.addView(q);
                break;
            case R.id.collectTest:
                if ((!(minutes.getText().toString().isEmpty()) || checkBox.isChecked()) && id != -1)
                    new Thread(this::uploadTest).start();
                else Snackbar.make(getView(), "Не все поля заполнены", Snackbar.LENGTH_LONG).show();
                break;

        }
    }

    private void addFile(int numQuestion, View box) {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;
        properties.show_hidden_files = false;

        FilePickerDialog dialog = new FilePickerDialog(getActivity(), properties);
        dialog.setTitle("Выберите файл");
        dialog.setDialogSelectionListener(files -> {
            String file_path = files[0];
            if (file_path != null && !file_path.trim().isEmpty()) {
                File file = new File(file_path);
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), file);
                String fileName = file.getName();
                int pointIndex = -1;
                if (fileName.contains(".")) {
                    pointIndex = fileName.lastIndexOf('.');

                    StringBuilder extension = new StringBuilder();
                    for (int i = pointIndex; i < fileName.length(); i++) {
                        extension.append(fileName.charAt(i));
                    }


                    fileName = UUID.randomUUID() + extension.toString();
                }

                MultipartBody.Part body =
                        MultipartBody.Part.createFormData("file", fileName.toLowerCase(), requestFile);
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(AppСonstants.NEW_API_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                FileUploadApi fileUploadApi = retrofit.create(FileUploadApi.class);
                Call<Void> resultCall = fileUploadApi.uploadFile(body);

                String finalFileName = fileName;
                resultCall.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() == 200){
                            Snackbar.make(view, "Добавлено", Snackbar.LENGTH_LONG).show();
                            attachmentObjects.add(new AttachmentObject(numQuestion,
                                    AppСonstants.IOOOJIK_BASE_URL + "project/" + finalFileName));

                        }
                        else Snackbar.make(view, "Что-то пошло не так", Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
                TextView hint = box.findViewById(R.id.hint);
                hint.setVisibility(View.GONE);
            }
        });
        dialog.show();
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
        List<String> scores = new ArrayList<>();
        int total_score = 0;



            for (int i = 0; i < questions.size(); i++){
                View tempQuestion = questions.get(i);
                String question;

                //вопрос
                EditText editableQuestion = tempQuestion.findViewById(R.id.question);
                question = editableQuestion.getText().toString();
                if (!question.isEmpty())
                    textQuestions.add(question);

                //баллы
                EditText scoreText = tempQuestion.findViewById(R.id.edit_text_score);
                String score = scoreText.getText().toString().trim();
                if (score.isEmpty()) score = "1";
                scores.add(score);
                score += Integer.valueOf(score.trim());

            }
            Log.e("ttttt", String.valueOf(questions.size() + " " + scores.size() + " " + textQuestions.size()));
            if (scores.size() == questions.size() && textQuestions.size() == questions.size()) {
                for (int i = 0; i < questions.size(); i++) {
                    View tempQuestion = questions.get(i);


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
                    if (spinner.getSelectedItem().toString().equals(firstSel))
                        trueAnswers.add(firstAnsw.getText().toString());
                    else if (spinner.getSelectedItem().toString().equals(secondSel))
                        trueAnswers.add(secondAnsw.getText().toString());
                    else if (spinner.getSelectedItem().toString().equals(thirdSel))
                        trueAnswers.add(thirdAnsw.getText().toString());
                    else if (spinner.getSelectedItem().toString().equals(fourthSel))
                        trueAnswers.add(fourthAnsw.getText().toString());


                }



                //собираем массивы, чтобы выполнить SQL-запрос
                //объединяем вопросы
                StringBuilder builderQuestions = new StringBuilder();
                builderQuestions.append("'");
                for (String question : textQuestions)
                    builderQuestions.append(question).append(testDivider);
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
                //объединяем все баллы за ответы
                StringBuilder builderScore = new StringBuilder();
                builderScore.append("'");
                for (String sc : scores) builderScore.append(sc).append(testDivider);
                builderScore.append("'");
                //объединяем все ссылки на прикрпелённые файлы
                StringBuilder fileBuilder = new StringBuilder();
                if (attachmentObjects.size() != 0) {
                    for (AttachmentObject file : attachmentObjects)
                        fileBuilder.append(file.getNumQuestion()).append(testDivider).append(file.getFileURL()).append(testDivider);
                }

                EditText name = view.findViewById(R.id.name);
                EditText description = view.findViewById(R.id.description);

                doRetrofit();

                HashMap<String, String> updateMap = new HashMap<>();
                String SQL = createSQL("'" + name.getText().toString() + "'",
                        "'" + description.getText().toString() + "'",
                        builderQuestions.toString(),
                        builderTrueAnswers.toString(),
                        builderTextAnswers.toString(),
                        textQuestions.size(),
                        builderScore.toString(),
                        String.valueOf(total_score));

                updateMap.put("_id", String.valueOf(id));
                updateMap.put("author_email", groupAuthor);
                updateMap.put("author_name", groupAuthorName);
                updateMap.put("name", groupName);
                updateMap.put("test", SQL);
                updateMap.put("count_questions", String.valueOf(questions.size()));
                Log.e("tttt", String.valueOf(attachmentObjects.size()));
                if (attachmentObjects.size() > 0)
                    updateMap.put("attachments", fileBuilder.toString());
                else updateMap.put("attachments", "null");

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
                        if (response.code() == 200) {
                            Snackbar.make(view, "Тест был успешно добавлен!", Snackbar.LENGTH_LONG).show();
                            //удаляем все результаты ученоков, ранее проходивших тест
                            doRetrofit();

                            Call<ServerResponse<DataTestResult>> response2 = api.getTestResults(AppСonstants.X_API_KEY,
                                    preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""),
                                    AppСonstants.GROUP_ID_FIELD, String.valueOf(id));

                            response2.enqueue(new Callback<ServerResponse<DataTestResult>>() {
                                @Override
                                public void onResponse(Call<ServerResponse<DataTestResult>> call, Response<ServerResponse<DataTestResult>> response) {
                                    if (response.code() == 200) {
                                        DataTestResult dataTestResult = response.body().getData();
                                        List<TestsResult> results = dataTestResult.getTestsResult();

                                        for (TestsResult result : results) {
                                            deleteResult(result);
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ServerResponse<DataTestResult>> call, Throwable t) {

                                }
                            });
                        } else {
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

                Call<ServerResponse<DataIsUserGetTest>> serverResponseCall = api.isUserGetTest(AppСonstants.X_API_KEY,
                        preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), AppСonstants.GROUP_ID_FIELD, String.valueOf(id));

                serverResponseCall.enqueue(new Callback<ServerResponse<DataIsUserGetTest>>() {
                    @Override
                    public void onResponse(Call<ServerResponse<DataIsUserGetTest>> call, Response<ServerResponse<DataIsUserGetTest>> response) {
                        if (response.code() == 200) {
                            List<IsUserGetTest> isUserGetTests = response.body().getData().getIsUserGetTest();
                            for (IsUserGetTest test : isUserGetTests) {
                                Call<ServerResponse<PostResult>> call2 = api.deleteUserGetTest(AppСonstants.X_API_KEY,
                                        preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), String.valueOf(test.getId()));

                                call2.enqueue(new Callback<ServerResponse<PostResult>>() {
                                    @Override
                                    public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {
                                        if (response.code() != 200)
                                            Log.e("DELETING TEST RESULT", String.valueOf(response.raw()));
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
            } else Snackbar.make(getView(), "Не все поля заполнены", Snackbar.LENGTH_SHORT).show();


    }

    private String createSQL(String name, String description, String textQuestions,
                                              String trueAnswers, String textAnswers, int size,
                             String score, String totalScore){
        String SQL = "";
        int scorePerAnsw = 10;
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
                AppСonstants.TABLE_GROUP_ID + ", " +
                AppСonstants.TABLE_SCORES + ", " +
                AppСonstants.TABLE_MAX_SCORE +
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
                id + "," +
                score + "," +
                totalScore +
                ")";

        return SQL;
    }
}
