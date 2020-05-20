package iooojik.app.klass.group;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.filepicker.model.DialogConfigs;
import com.developer.filepicker.model.DialogProperties;
import com.developer.filepicker.view.FilePickerDialog;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.R;
import iooojik.app.klass.api.Api;
import iooojik.app.klass.api.FileUploadApi;
import iooojik.app.klass.models.PostResult;
import iooojik.app.klass.models.ServerResponse;
import iooojik.app.klass.models.file_info.DataFiles;
import iooojik.app.klass.models.file_info.FileObject;
import iooojik.app.klass.models.teacher.DataGroup;
import iooojik.app.klass.models.teacher.GroupInfo;
import iooojik.app.klass.models.test_results.DataTestResult;
import iooojik.app.klass.models.test_results.TestsResult;
import iooojik.app.klass.models.matesList.DataUsersToGroup;
import iooojik.app.klass.models.matesList.Mate;
import iooojik.app.klass.models.paramUsers.ParamData;
import iooojik.app.klass.models.paramUsers.UserParams;
import iooojik.app.klass.room_models.mates.MateEntity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static iooojik.app.klass.AppСonstants.database;


public class Group extends Fragment{

    public Group() {}

    private View view;
    //id группы
    private int groupID;
    //название группы
    private String groupName;
    //учитель
    private String groupAuthor;
    private String groupAuthorName;
    public static int id = -1;
    private Context context;
    //fab
    private FloatingActionButton fab;
    //апи
    private Api api;
    //список одногруппников
    private List<Mate> mates;
    //настройки
    private SharedPreferences preferences;
    private Fragment fragment;
    private BottomSheetDialog bottomSheetDialog;
    private BottomSheetDialog fileBottomSheetDialog;
    private int matesCount = 0;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group, container, false);
        //настройки
        preferences = getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        //потоки получения данных
        new Thread(this::getGroupInfo).start();

        Thread threadSetMates = new Thread(this::setMates);
        Thread threadGetMates = new Thread(this::getGroupMates);
        threadSetMates.start();
        try {
            threadSetMates.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadGetMates.start();


        //контекст
        context = getContext();
        //текущий фрагмент
        fragment = this;

        fab = getActivity().findViewById(R.id.fab);
        fab.show();
        fab.setImageResource(R.drawable.round_keyboard_arrow_up_24);
        fab.setOnClickListener(v -> bottomSheetDialog.show());
        setHasOptionsMenu(true);
        return view;
    }

    private void doRetrofit(){
        //базовый метод ретрофита
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppСonstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
    }

    private void setMates(){
        List<MateEntity> mateEntities = database.matesDao().getAllByGroupId(Integer.valueOf(id));
        matesCount = mateEntities.size();
        if (matesCount > 0) {
            getActivity().runOnUiThread(() -> {
                GroupMatesAdapter groupmatesAdapter = new GroupMatesAdapter(context, mateEntities, fragment, true);
                RecyclerView recyclerView = view.findViewById(R.id.groupmates);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(groupmatesAdapter);
            });
        }

    }

    private void getGroupMates(){

        //получаем список учеников(их полное имя и email) из бд
        doRetrofit();
        Call<ServerResponse<DataUsersToGroup>> response = api.getMatesList(AppСonstants.X_API_KEY,
                AppСonstants.GROUP_ID_FIELD, String.valueOf(id));

        response.enqueue(new Callback<ServerResponse<DataUsersToGroup>>() {
            @Override
            public void onResponse(Call<ServerResponse<DataUsersToGroup>> call, Response<ServerResponse<DataUsersToGroup>> response) {
                if(response.code() == 200) {
                    ServerResponse<DataUsersToGroup> result = response.body();
                    //список одногруппников
                    mates = result.getData().getMates();
                    boolean anyChanges = false;
                    if (mates.size() != matesCount) {
                        for (Mate mate : mates) {
                            MateEntity entity = new MateEntity();
                            entity.setMate_name(mate.getFullName());
                            entity.setMate_email(mate.getEmail());
                            entity.setMate_avatar(mate.getAvatar());
                            entity.setMate_id(Integer.valueOf(mate.getId()));
                            entity.setMate_group_id(Integer.valueOf(id));
                            entity.setTest_result(-1);
                            database.matesDao().insert(entity);
                        }
                        anyChanges = true;
                    }  else if (!anyChanges){
                        for (Mate mate : mates){
                            MateEntity entity = database.matesDao().getById(Integer.valueOf(mate.getId()));
                            if (
                                    !(entity.getMate_avatar().equals(mate.getAvatar())
                                            || entity.getMate_name().equals(mate.getFullName())
                                            || entity.getMate_email().equals(mate.getEmail()))

                            ) {
                                entity.setMate_name(mate.getFullName());
                                entity.setMate_email(mate.getEmail());
                                entity.setMate_avatar(mate.getAvatar());
                                database.matesDao().update(entity);
                                anyChanges = true;
                            }
                        }
                    }

                    if (anyChanges) new Thread(Group.this::setMates).start();

                    getActivity().runOnUiThread(() -> enableBottomSheet());


                } else {
                    Log.e("GETTING MATES", String.valueOf(response.raw()));
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<DataUsersToGroup>> call, Throwable t) {
                Log.e("GETTING MATES",t.toString());
                fab.hide();
            }
        });
    }

    private void getGroupInfo(){
        //получаем информацию о группе из предыдущего фрагмента
        Bundle args = this.getArguments();
        groupID = args.getInt("groupID");
        groupAuthor = args.getString("groupAuthor");
        groupName = args.getString("groupName");
        groupAuthorName = args.getString("groupAuthorName");
        id = args.getInt("id");
    }

    private void addNewUserWeb(String email) {
        //получение информации о пользователе
        doRetrofit();

        Call<ServerResponse<ParamData>> call = api.getParamUser(AppСonstants.X_API_KEY,
                preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""),AppСonstants.EMAIL_FIELD, email);

        call.enqueue(new Callback<ServerResponse<ParamData>>() {
            @Override
            public void onResponse(Call<ServerResponse<ParamData>> call, Response<ServerResponse<ParamData>> response) {
                if (response.code() == 200){

                    ParamData paramData = response.body().getData();
                    if (paramData.getUserParams().size() > 0) {
                        UserParams userParams = paramData.getUserParams().get(0);
                        //аватар
                        String avatar = userParams.getAvatar();
                        if (avatar == null || avatar.isEmpty()) {
                            avatar = "null";
                        }

                        HashMap<String, String> map = new HashMap<>();

                        map.put(AppСonstants.FULL_NAME_FIELD, userParams.getFullName());
                        map.put(AppСonstants.EMAIL_FIELD, userParams.getEmail());
                        map.put(AppСonstants.GROUP_ID_FIELD, String.valueOf(id));
                        map.put(AppСonstants.GROUP_NAME_FIELD, groupName);
                        map.put(AppСonstants.AVATAR_FIELD, avatar);

                        Call<ServerResponse<PostResult>> response2 = api.addUserToGroup(AppСonstants.X_API_KEY,
                                preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), map);

                        response2.enqueue(new Callback<ServerResponse<PostResult>>() {
                            @Override
                            public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {
                                if (response.code() == 200) {
                                    Snackbar.make(view, "Пользователь был успешно добавлен", Snackbar.LENGTH_LONG).show();
                                    getGroupMates();
                                } else Log.e("ADD MATE", String.valueOf(response.raw()) + map);
                            }

                            @Override
                            public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {
                                Log.e("ADD MATE", String.valueOf(t));
                            }
                        });
                    } else {
                        Snackbar.make(view, "Пользователь с указанным e-mail адресом не был найден.",
                                Snackbar.LENGTH_LONG).show();
                    }

                    } else {
                        Log.e("ADD MATE", String.valueOf(response.raw()));
                        Snackbar.make(view, "Что-то пошло не так!",
                            Snackbar.LENGTH_LONG).show();
                    }
            }
            @Override
            public void onFailure(Call<ServerResponse<ParamData>> call, Throwable t) {

            }
        });
    }

    private void addUser(){
        /*
         * MaterialAlertDialogBuilder для добавления нового ученика в группу
         * пользователь вводит email и полное имя ученика, если он есть в базе, то
         * он добавляется в список и в соответствующую таблицу
         */

        //Dialog для ввода email пользователя
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        @SuppressLint("InflateParams") View view1 = getLayoutInflater().inflate(R.layout.edit_text, null);
        TextInputEditText emailText = view1.findViewById(R.id.edit_text);

        TextInputLayout textInputLayout = view1.findViewById(R.id.text_input_layout);
        textInputLayout.setHint("Введите e-mail адрес");
        textInputLayout.setHelperTextEnabled(false);
        textInputLayout.setCounterEnabled(false);

        layout.addView(view1);

        builder.setPositiveButton("Добавить", (dialog, which) -> {
            String email = emailText.getText().toString().trim();
            boolean result = false;

            //проверяем, есть ли пользовтель в группе
            if (mates.size() == 0 && !email.equals(groupAuthor)) {
                result = true;
            }else {

                for (Mate mate : mates) {
                    if (email.equals(mate.getEmail())) {
                        result = false;
                        break;
                    } else result = true;
                }

            }

            //получаем пользовательскую информацию по email
            //если код == 200, то заносим пользователя в группу
            //иначе выдаём сообщение об ошибке

            if (result){
                addNewUserWeb(email);
            }
            else Snackbar.make(view, "Пользователь с указанным email-адресом уже есть в группе", Snackbar.LENGTH_LONG).show();

            fab.show();

        });
        builder.setOnCancelListener(dialog -> fab.show());
        builder.setView(layout);
        builder.create().show();
    }

    @SuppressLint("InflateParams")
    private void enableBottomSheet() {

        bottomSheetDialog = new BottomSheetDialog(getActivity());
        View bottomSheet = getActivity().getLayoutInflater().inflate(R.layout.bottom_sheet_group_editor, null);

        Call<ServerResponse<DataTestResult>> call2 = api.getTestResults(AppСonstants.X_API_KEY,
                preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""),
                AppСonstants.GROUP_ID_FIELD, String.valueOf(id));

        call2.enqueue(new Callback<ServerResponse<DataTestResult>>() {
            @Override
            public void onResponse(Call<ServerResponse<DataTestResult>> call, Response<ServerResponse<DataTestResult>> response) {
                if (response.code() == 200){
                    //ставим адаптер
                    DataTestResult result = response.body().getData();
                    List<TestsResult> testsResults = result.getTestsResult();
                    //инициализируем нижнее меню
                    Call<ServerResponse<DataGroup>> call2 = api.getGroupsById(AppСonstants.X_API_KEY, "_id", String.valueOf(id));
                    call2.enqueue(new Callback<ServerResponse<DataGroup>>() {
                        @SuppressLint("DefaultLocale")
                        @Override
                        public void onResponse(Call<ServerResponse<DataGroup>> call, Response<ServerResponse<DataGroup>> response) {
                            if (response.code()==200) {
                                if (response.body().getData() != null) {


                                    PieChart pieChart = bottomSheet.findViewById(R.id.chart);
                                    if (!response.body().getData().getGroupInfos().get(0).getTest().equals("null")) {

                                        if (testsResults.size() > 0) {
                                            int countDiff = 0;
                                            for (TestsResult result : testsResults) {
                                                countDiff += Integer.valueOf(result.getDifficultiesCount());
                                            }
                                            List<GroupInfo> dataGroups = response.body().getData().getGroupInfos();
                                            //показываем диаграмму, показывающую процент заданий с затруднениями
                                            List<Float> score = new ArrayList<>();

                                            float rightScore = Float.valueOf(countDiff);
                                            float wrongScore =
                                                    Float.valueOf(Integer.valueOf(dataGroups.get(0).getCount_questions()) * testsResults.size());

                                            TextView textView = bottomSheetDialog.findViewById(R.id.dif_percent);
                                            textView.setText(String.format("%s %d%%", textView.getText(), Math.round(rightScore / wrongScore * 100)));

                                            score.add((rightScore / wrongScore) * 100);
                                            score.add(100 - (rightScore / wrongScore) * 100);

                                            //преобразуем в понятные для диаграммы данные
                                            List<PieEntry> entries = new ArrayList<>();
                                            for (int i = 0; i < score.size(); i++)
                                                entries.add(new PieEntry(score.get(i), i));
                                            PieDataSet pieDataSet = new PieDataSet(entries, "");
                                            //устанавливаем цвета
                                            List<Integer> colors = new ArrayList<>();
                                            int green = Color.parseColor("#56CF54");
                                            int red = Color.parseColor("#FF5252");
                                            colors.add(red);
                                            colors.add(green);

                                            pieDataSet.setColors(colors);

                                            PieData pieData = new PieData(pieDataSet);
                                            //анимация
                                            pieChart.animateY(500);
                                            //убираем надписи
                                            Description description = new Description();
                                            description.setText("");
                                            pieChart.setDescription(description);

                                            pieChart.getLegend().setFormSize(0f);
                                            pieData.setValueTextSize(0f);

                                            pieChart.setTransparentCircleRadius(0);

                                            pieChart.setHoleRadius(0);
                                            pieChart.setData(pieData);
                                        }
                                        else {
                                            pieChart.setVisibility(View.GONE);
                                            TextView textView = bottomSheet.findViewById(R.id.dif_percent);
                                            textView.setVisibility(View.GONE);
                                        }

                                    } else {
                                        pieChart.setVisibility(View.GONE);
                                        TextView textView = bottomSheet.findViewById(R.id.dif_percent);
                                        textView.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ServerResponse<DataGroup>> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<DataTestResult>> call, Throwable t) {

            }
        });

        //добавление ученика в группу
        Button add = bottomSheet.findViewById(R.id.add);
        add.setOnClickListener(v -> {
            addUser();
            bottomSheetDialog.hide();
        });

        //открытие редакторов теста
        Button test_editor = bottomSheet.findViewById(R.id.test_editor);
        test_editor.setOnClickListener(v -> {
            //редактор тестов
            Bundle bundle = new Bundle();
            bundle.putInt("id", id);
            bundle.putInt("groupID", groupID);
            bundle.putString("groupAuthor", groupAuthor);
            bundle.putString("groupAuthorName", groupAuthorName);
            bundle.putString("groupName", groupName);
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.nav_testEditor, bundle);
            bottomSheetDialog.hide();
        });

        //добавление сообщения группе
        Button add_message = bottomSheet.findViewById(R.id.add_message);
        add_message.setOnClickListener(v -> {
            addMessageToGroup();
            bottomSheetDialog.hide();
        });

        //удаление активного теста
        Button delete_test = bottomSheet.findViewById(R.id.delete_test);
        delete_test.setOnClickListener(v -> {
            HashMap<String, String> updateMap = new HashMap<>();

            updateMap.put("_id", String.valueOf(id));
            updateMap.put("author_email", groupAuthor);
            updateMap.put("author_name", groupAuthorName);
            updateMap.put("name", groupName);
            updateMap.put("test", "null");

            Call<ServerResponse<PostResult>> responseCall = api.updateTest(AppСonstants.X_API_KEY,
                    getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES,
                            Context.MODE_PRIVATE).getString(AppСonstants.AUTH_SAVED_TOKEN, ""),
                    updateMap);
            responseCall.enqueue(new Callback<ServerResponse<PostResult>>() {
                @Override
                public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {

                }

                @Override
                public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {

                }
            });
        });

        //окно с файлами
        fileBottomSheetDialog = new BottomSheetDialog(getActivity());
        View fileBottomSheet = getActivity().getLayoutInflater().inflate(R.layout.bottom_sheet_file_selector, null);

        Call<ServerResponse<DataFiles>> serverResponseCall = api.getUserFiles(AppСonstants.X_API_KEY,
                preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), AppСonstants.USER_EMAIL_FIELD,
                preferences.getString(AppСonstants.USER_EMAIL,""));

        serverResponseCall.enqueue(new Callback<ServerResponse<DataFiles>>() {
            @Override
            public void onResponse(Call<ServerResponse<DataFiles>> call, Response<ServerResponse<DataFiles>> response) {
                if (response.code() == 200){

                    List<FileObject> fileObjects = response.body().getData().getFilesToUsers();
                    List<FileInfo> files = new ArrayList<>();
                    for (FileObject file : fileObjects){


                        int pointIndex = file.getFileUrl().lastIndexOf('.');
                        StringBuilder extension = new StringBuilder();
                        for (int i = pointIndex + 1; i < file.getFileUrl().length(); i++) {
                            extension.append(file.getFileUrl().charAt(i));
                        }
                        StringBuilder fileName = new StringBuilder();
                        pointIndex = file.getFileUrl().lastIndexOf('/');

                        for (int i = pointIndex + 1; i < file.getFileUrl().length(); i++) {
                            fileName.append(file.getFileUrl().charAt(i));
                        }

                        files.add(new FileInfo(
                                fragment.getResources().
                                        getIdentifier(extension.toString(), "drawable", "iooojik.app.klass"),
                                fileName.toString(), file.getFileUrl()));
                    }

                    FilesAdapter adapter = new FilesAdapter(files, getContext(), preferences);
                    RecyclerView recyclerView = fileBottomSheet.findViewById(R.id.rec_view);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                            LinearLayoutManager.HORIZONTAL, true));
                    recyclerView.setAdapter(adapter);
                }
                else Log.e("GETTING FILES", String.valueOf(response.raw()));
            }

            @Override
            public void onFailure(Call<ServerResponse<DataFiles>> call, Throwable t) {
                Log.e("GETTING FILES", String.valueOf(t));
            }
        });
        fileBottomSheetDialog.setContentView(fileBottomSheet);

        //открытие окна загрузки файлов
        Button chooseFile = bottomSheet.findViewById(R.id.choose_file);
        chooseFile.setOnClickListener(v -> fileBottomSheetDialog.show());

        bottomSheetDialog.setOnCancelListener(dialog -> fab.show());
        bottomSheetDialog.setContentView(bottomSheet);

    }

    private void addMessageToGroup() {
        //создаём окно для ввода сообщения
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        @SuppressLint("InflateParams") View view1 = getLayoutInflater().inflate(R.layout.edit_text, null);
        TextInputEditText messageText = view1.findViewById(R.id.edit_text);
        messageText.setMaxLines(10);

        TextInputLayout textInputLayout = view1.findViewById(R.id.text_input_layout);
        textInputLayout.setHint("Введите ваше сообщение");
        textInputLayout.setHelperTextEnabled(false);
        textInputLayout.setCounterEnabled(false);

        layout.addView(view1);

        builder.setView(layout);

        //слушатели
        builder.setPositiveButton("Добавить", (dialog, which) -> {

            //запрос
            HashMap<String, String> map = new HashMap<>();
            map.put(AppСonstants.GROUP_ID_FIELD, String.valueOf(id));
            map.put(AppСonstants.MESSAGE_FIELD, messageText.getText().toString());
            Call<ServerResponse<PostResult>> addMessageCall = api.addGroupMessage(AppСonstants.X_API_KEY,
                    preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), map);
            addMessageCall.enqueue(new Callback<ServerResponse<PostResult>>() {
                @Override
                public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {
                    if (response.code() == 200) Snackbar.make(getView(), "Добавлено", Snackbar.LENGTH_LONG).show();
                    else {
                        Log.e("ADD GROUP MESSAGE", String.valueOf(response.raw()));
                        Snackbar.make(getView(), "К сожалению, произошла ошибка", Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {
                    Log.e("ADD GROUP MESSAGE", String.valueOf(t));
                    Snackbar.make(getView(), "К сожалению, произошла ошибка", Snackbar.LENGTH_LONG).show();
                }
            });
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
        builder.create().show();
        fab.show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_group, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_upload) {
            uploadFile();
            return true;
        }
        return false;
    }

    private void uploadFile(){
        //окно выбора файлов
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
                //запрос
                Call<Void> resultCall = fileUploadApi.uploadFile(body);
                String finalFileName = fileName;

                resultCall.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() == 200) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put(AppСonstants.USER_EMAIL_FIELD, preferences.getString(AppСonstants.USER_EMAIL, ""));
                            map.put(AppСonstants.FILE_URL_FIELD, AppСonstants.IOOOJIK_BASE_URL + "project/" + finalFileName);

                            Call<ServerResponse<PostResult>> serverResponseCall = api.addFileInfo(AppСonstants.X_API_KEY,
                                    preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), map);
                            serverResponseCall.enqueue(new Callback<ServerResponse<PostResult>>() {
                                @Override
                                public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {
                                    if (response.code() == 200)
                                        Snackbar.make(getView(), "Файл успешно загружен", Snackbar.LENGTH_LONG).show();
                                    @SuppressLint("InflateParams")
                                    View fileBottomSheet = getActivity().getLayoutInflater().inflate(R.layout.bottom_sheet_file_selector, null);

                                    Call<ServerResponse<DataFiles>> serverResponseCall = api.getUserFiles(AppСonstants.X_API_KEY,
                                            preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), AppСonstants.USER_EMAIL_FIELD,
                                            preferences.getString(AppСonstants.USER_EMAIL,""));

                                    serverResponseCall.enqueue(new Callback<ServerResponse<DataFiles>>() {
                                        @Override
                                        public void onResponse(Call<ServerResponse<DataFiles>> call, Response<ServerResponse<DataFiles>> response) {
                                            if (response.code() == 200){

                                                List<FileObject> fileObjects = response.body().getData().getFilesToUsers();
                                                List<FileInfo> files = new ArrayList<>();
                                                for (FileObject file : fileObjects){

                                                    int pointIndex = file.getFileUrl().lastIndexOf('.');
                                                    StringBuilder extension = new StringBuilder();
                                                    for (int i = pointIndex + 1; i < file.getFileUrl().length(); i++) {
                                                        extension.append(file.getFileUrl().charAt(i));
                                                    }
                                                    StringBuilder fileName = new StringBuilder();
                                                    pointIndex = file.getFileUrl().lastIndexOf('/');

                                                    for (int i = pointIndex + 1; i < file.getFileUrl().length(); i++) {
                                                        fileName.append(file.getFileUrl().charAt(i));
                                                    }


                                                    files.add(new FileInfo(fragment.getResources().
                                                                    getIdentifier(extension.toString(), "drawable", "iooojik.app.klass"),
                                                            fileName.toString(), file.getFileUrl()));
                                                }

                                                FilesAdapter adapter = new FilesAdapter(files, getContext(), preferences);
                                                RecyclerView recyclerView = fileBottomSheet.findViewById(R.id.rec_view);
                                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                                                        LinearLayoutManager.HORIZONTAL, true));
                                                recyclerView.setAdapter(adapter);
                                            }
                                            else Log.e("GETTING FILES", String.valueOf(response.raw()));
                                        }

                                        @Override
                                        public void onFailure(Call<ServerResponse<DataFiles>> call, Throwable t) {
                                            Log.e("GETTING FILES", String.valueOf(t));
                                        }
                                    });
                                    fileBottomSheetDialog.setContentView(fileBottomSheet);
                                }

                                @Override
                                public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {

                                }
                            });
                        } else Log.e("UPLOADING FILE", String.valueOf(response.raw()));

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("UPLOADING FILE", t + " " + file.getPath());
                    }
                });
            }
        });
        dialog.show();

    }


}
