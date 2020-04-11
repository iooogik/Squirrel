package iooojik.app.klass.notes;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import iooojik.app.klass.Api;
import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.Database;
import iooojik.app.klass.R;
import iooojik.app.klass.models.PostResult;
import iooojik.app.klass.models.ServerResponse;
import iooojik.app.klass.models.notesData.Data;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Notes extends Fragment {

    //Переменная для работы с БД
    private Database mDBHelper;
    private SQLiteDatabase mDb;
    private Cursor userCursor;

    private View view;
    private Context context;
    private NotesAdapter NOTES_ADAPTER;
    private FabSpeedDial fabSpeedDial;
    private Api api;
    private SharedPreferences preferences;

    static List<Note> ITEMS = new ArrayList<>();

    public Notes(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notes, container ,false);
        preferences = getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        context = view.getContext();
        startProcedures();

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();

        fabSpeedDial = getActivity().findViewById(R.id.fab_dial);
        fabSpeedDial.show();
        fabSpeedDial.setMenuListener(new FabSpeedDial.MenuListener() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.action_upload_notes:
                        uploadNotes();
                        break;
                    case R.id.action_download_notes:
                        downloadNotes();
                        break;
                    case R.id.action_add_note:
                        addNote();
                        break;
                }
                return true;
            }

            @Override
            public void onMenuClosed() {

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Скрываем клавиатуру при открытии Navigation Drawer
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().
                    getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(Objects.
                        requireNonNull(getActivity().getCurrentFocus()).
                        getWindowToken(), 0);
            }
        } catch (Exception e){
            Log.i("Notes", String.valueOf(e));
        }

        synchronized (NOTES_ADAPTER){
            NOTES_ADAPTER.notify();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fabSpeedDial.hide();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fabSpeedDial.hide();
    }

    private void startProcedures(){
        mDBHelper = new Database(getContext());
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();

        //необходимо очистить содержимое, чтобы при старте активити не было повторяющихся элементов
        try {
            ITEMS.clear();
        } catch (Exception e){
            Log.i("Notes", String.valueOf(e));
        }
        updProjects();

    }

    //обновление проектов на активити
    private void updProjects(){
        //добавление новых проектов
        mDb = mDBHelper.getReadableDatabase();
        Cursor userCursor = mDb.rawQuery("Select * from Notes", null);
        userCursor.moveToFirst();

        String name, desc, type;
        Bitmap bitmap = null;
        while (!userCursor.isAfterLast()) {

            name = String.valueOf(userCursor.getString(1)); //колонки считаются с 0

            type = userCursor.getString(8);

            desc = String.valueOf(userCursor.getString(2));

            byte[] bytesImg = userCursor.getBlob(userCursor.getColumnIndex("image"));

            if(bytesImg != null){
                bitmap = BitmapFactory.decodeByteArray(bytesImg, 0, bytesImg.length);
            }

            if(name != null || type != null)
                ITEMS.add(new Note(name, desc, bitmap, type,
                        userCursor.getInt(userCursor.getColumnIndex("_id")), -1));

            userCursor.moveToNext();

            bitmap = null;
        }


        userCursor.close();
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        NOTES_ADAPTER = new NotesAdapter(getContext(), ITEMS, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(NOTES_ADAPTER);
    }

    private void addNote(){

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

        final LinearLayout layout1 = new LinearLayout(getContext());
        layout1.setOrientation(LinearLayout.VERTICAL);
        //ввод названия заметки
        View view1 = getLayoutInflater().inflate(R.layout.edit_text, null, false);
        TextInputLayout textInputLayout = view1.findViewById(R.id.text_input_layout);
        textInputLayout.setHint("Введите имя заметки");
        EditText nameNote = textInputLayout.getEditText();

        layout1.addView(view1);

        final String standartTextNote = "Стандартная заметка";
        final String marckedList = "Маркированный список";

        final String[] types = new String[]{standartTextNote, marckedList};
        //выбор типа
        View view2 = getLayoutInflater().inflate(R.layout.spinner_item, null, false);
        TextInputLayout textInputLayout2 = view2.findViewById(R.id.spinner_layout);
        AutoCompleteTextView spinner = textInputLayout2.findViewById(R.id.filled_exposed_dropdown);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_dropdown_item, types);

        spinner.setAdapter(adapter);

        layout1.addView(view2);
        builder.setView(layout1);

        final String DB_TYPE_STNDRT = "standart";
        final String DB_TYPE_SHOP = "shop";
        Note note = ITEMS.get(ITEMS.size() - 1);
        int id = note.getId() + 1;

        builder.setPositiveButton("Добавить",
                (dialog, which) -> {
                    if(!nameNote.getText().toString().isEmpty() &&
                            !spinner.getText().toString().isEmpty()) {
                        String name = nameNote.getText().toString();
                        String shortNote = "короткое описание";
                        String text = "Новая заметка";
                        String type = "";
                        mDb = mDBHelper.getWritableDatabase();

                        if (spinner.getText().toString().equals(standartTextNote)) {
                            type = DB_TYPE_STNDRT;
                        } else if (spinner.getText().toString().equals(marckedList)) {
                            type = DB_TYPE_SHOP;
                        }

                        //добавление в бд и обновление адаптера
                        ContentValues cv = new ContentValues();
                        cv.put("_id", id);
                        cv.put("name", name);
                        cv.put("shortName", shortNote);
                        cv.put("text", text);
                        cv.put("type", type);
                        cv.put("isNotifSet", 0);
                        cv.put("permToSync", 1);
                        //получение даты
                        Date currentDate = new Date();
                        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy",
                                Locale.getDefault());
                        String dateText = dateFormat.format(currentDate);
                        cv.put("date", dateText);
                        //запись
                        mDb.insert("Notes", null, cv);

                        ITEMS.add(new Note(name, shortNote, null, type, id, -1));
                        NOTES_ADAPTER.notifyDataSetChanged();
                    } else {
                        Snackbar.make(view, "Что-то пошло не так. Проверьте, пожалуйста, название и выбранный тип.",
                                Snackbar.LENGTH_LONG).show();
                    }
                });
        builder.create().show();
    }

    private void doRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppСonstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
    }

    private void uploadNotes(){
        //получаем id каждой заметки из списка ITEMS и узнаём, можно ли добавлять картинку в базу
        List<Note> uploadNotes = new ArrayList<>();
        mDb = mDBHelper.getReadableDatabase();

        for (Note note: ITEMS){
            int id = note.getId();

            userCursor =  mDb.rawQuery("Select * from Notes WHERE _id=?", new String[]{String.valueOf(id)});
            userCursor.moveToFirst();

            if (userCursor.getInt(userCursor.getColumnIndex("permToSync")) == 1){
                uploadNotes.add(note);
            }
        }

        doRetrofit();
        //получаем все пользовательские заметки
        //удаляем все пользовательские заметки
        if (uploadNotes.size() > 0) clearNotes();

        for (Note note : uploadNotes){
            //заносим каждую заметку в базу
            HashMap<String, String> map = new HashMap<>();
            userCursor =  mDb.rawQuery("Select * from Notes WHERE _id=?", new String[]{String.valueOf(note.getId())});

            userCursor.moveToFirst();
            String name = String.valueOf(userCursor.getString(userCursor.getColumnIndex("name")));
            //собираем данные
            String shortName = String.valueOf(userCursor.getString(userCursor.getColumnIndex("shortName")));
            String text = String.valueOf(userCursor.getString(userCursor.getColumnIndex("text")));
            String date = String.valueOf(userCursor.getString(userCursor.getColumnIndex("date")));
            String type = String.valueOf(userCursor.getString(userCursor.getColumnIndex("type")));
            String isNotifSet = String.valueOf(userCursor.getInt(userCursor.getColumnIndex("isNotifSet")));
            String permToSync = String.valueOf(userCursor.getInt(userCursor.getColumnIndex("permToSync")));
            String isChecked =  String.valueOf(userCursor.getString(userCursor.getColumnIndex("isChecked")));
            String points = String.valueOf(userCursor.getString(userCursor.getColumnIndex("points")));
            String isCompleted = String.valueOf(userCursor.getString(userCursor.getColumnIndex("isCompleted")));
            String decodeQR = String.valueOf(userCursor.getString(userCursor.getColumnIndex("decodeQR")));
            String image = Arrays.toString(userCursor.getBlob(userCursor.getColumnIndex("image")));
            //добавляем данные в map
            map.put("user_id", String.valueOf(getUserID()));
            map.put("name", name);
            map.put("shortName", shortName);
            map.put("text", text);
            map.put("date", date);
            map.put("type", type);
            map.put("isNotifSet", isNotifSet);
            map.put("permToSync", permToSync);
            map.put("isChecked", isChecked);
            map.put("points", points);
            map.put("isCompleted", isCompleted);
            map.put("decodeQR", decodeQR);
            map.put("image", image);
            //отправляем данные
            Call<ServerResponse<PostResult>> call = api.uploadNotes(AppСonstants.X_API_KEY,
                    preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""),
                    map);

            call.enqueue(new Callback<ServerResponse<PostResult>>() {
                @Override
                public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {
                    if (response.code() != 200) Log.e("UPLOAD NOTES", String.valueOf(response.raw()));
                }

                @Override
                public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {
                    Log.e("UPLOAD NOTES", String.valueOf(t));
                }
            });
        }

        Snackbar.make(view, "Ваши заметки успешно загружены на сервер!", Snackbar.LENGTH_LONG).show();

    }

    private void clearNotes(){
        doRetrofit();
        Call<ServerResponse<Data>> call = api.getNotes(AppСonstants.X_API_KEY, "user_id", String.valueOf(getUserID()));
        call.enqueue(new Callback<ServerResponse<Data>>() {
            @Override
            public void onResponse(Call<ServerResponse<Data>> call, Response<ServerResponse<Data>> response) {
                if (response.code() == 200){
                    List<iooojik.app.klass.models.notesData.Note> notes =
                            response.body().getData().getNotes();
                    removeNotes(notes);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<Data>> call, Throwable t) {

            }
        });
    }

    private void removeNotes(List<iooojik.app.klass.models.notesData.Note> notes) {
        for (iooojik.app.klass.models.notesData.Note note : notes){
            Call<ServerResponse<PostResult>> call = api.removeNotes(AppСonstants.X_API_KEY, String.valueOf(note.getId()));
            call.enqueue(new Callback<ServerResponse<PostResult>>() {
                @Override
                public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {
                    if (response.code() != 200) Log.e("REMOVE NOTE", response.raw() + " " + note.getId());
                }

                @Override
                public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {
                    Log.e("REMOVE NOTE", String.valueOf(t));
                }
            });
        }
    }

    private int getUserID(){
        int userId = -1;
        /*
        mDBHelper = new Database(context);
        mDBHelper = new Database(context);
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();
        mDb = mDBHelper.getReadableDatabase();
        userCursor = mDb.rawQuery("Select * from Profile WHERE _id=?", new String[]{String.valueOf(0)});
        userCursor.moveToFirst();

         */

        userId = Integer.parseInt(preferences.getString(AppСonstants.USER_ID, ""));
        return userId;
    }

    private void downloadNotes(){
        doRetrofit();
        Call<ServerResponse<Data>> call = api.getNotes(AppСonstants.X_API_KEY, "user_id", String.valueOf(getUserID()));
        call.enqueue(new Callback<ServerResponse<Data>>() {
            @Override
            public void onResponse(Call<ServerResponse<Data>> call, Response<ServerResponse<Data>> response) {
                if (response.code() == 200){
                    List<iooojik.app.klass.models.notesData.Note> notes =
                            response.body().getData().getNotes();
                    for (iooojik.app.klass.models.notesData.Note note : notes){
                        boolean result = false;
                        for (Note note2 : ITEMS){
                            if (note2.getName().equals(note.getName())) {result = false; break;}
                            else result = true;
                        }
                        if (result) {
                            String name = note.getName();
                            //собираем данные
                            String shortName = note.getShortName();
                            String text = note.getText();

                            String type = note.getType();

                            String points = note.getPoints();
                            String isCompleted = note.getIsCompleted();
                            String decodeQR = note.getDecodeQR();

                            mDb = mDBHelper.getWritableDatabase();
                            ContentValues cv = new ContentValues();

                            cv.put("name", name);
                            cv.put("shortName", shortName);
                            cv.put("text", text);
                            cv.put("type", type);
                            cv.put("isNotifSet", 0);
                            cv.put("permToSync", 1);
                            cv.put("points", points);
                            cv.put("isCompleted", isCompleted);
                            cv.put("decodeQR", decodeQR);
                            //получение даты
                            Date currentDate = new Date();
                            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy",
                                    Locale.getDefault());
                            String dateText = dateFormat.format(currentDate);
                            cv.put("date", dateText);
                            //запись
                            mDb.insert("Notes", null, cv);

                            ITEMS.add(new Note(name, shortName, null, type,
                                    ITEMS.get(0).getId() + 1, Integer.parseInt(note.getId())));
                            NOTES_ADAPTER.notifyDataSetChanged();
                        }
                    }
                } else Log.e("GET NOTES", String.valueOf(response.raw()));
            }

            @Override
            public void onFailure(Call<ServerResponse<Data>> call, Throwable t) {

            }
        });
        Snackbar.make(view, "Вы успешно загрузили заметки!", Snackbar.LENGTH_LONG).show();
    }

}

