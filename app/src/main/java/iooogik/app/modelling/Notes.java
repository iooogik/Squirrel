package iooogik.app.modelling;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.ar.sceneform.rendering.Material;

public class Notes extends Fragment implements View.OnClickListener {

    //Переменная для работы с БД
    private Database mDBHelper;
    private SQLiteDatabase mDb;
    private Cursor userCursor;
    Bundle bundle = new Bundle();

    public static View VIEW;
    static NotesAdapter NOTES_ADAPTER;

    static List<Note> ITEMS = new ArrayList<>();

    public Notes(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        VIEW = inflater.inflate(R.layout.fragment_notes, container ,false);
        FloatingActionButton back = VIEW.findViewById(R.id.back);
        back.setOnClickListener(this);
        // запускаем поток для обновления списка заметок
        Thread startThread = new Thread(this::startProcedures);
        startThread.start();

        return VIEW;
    }

    private void startProcedures(){
        mDBHelper = new Database(getContext());
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();

        FloatingActionButton add = VIEW.findViewById(R.id.addProject);
        add.setOnClickListener(this);

        //необходимо очистить содержимое, чтобы при старте активити не было повторяющихся элементов
        try {
            ITEMS.clear();
        } catch (Exception e){
            Log.i("Notes", String.valueOf(e));
        }
        updProjects();

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        //кнопка "Добавить проект"
        if(view.getId() == R.id.addProject){

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(view.getContext(),
                    R.style.Theme_MaterialComponents_Light_Dialog);
            //создаём "поверхность" на alertDialog
            final LinearLayout layout1 = new LinearLayout(getContext());
            layout1.setOrientation(LinearLayout.VERTICAL);

            //получаем кастомную вьюшку и добаляем на alertDialog
            View view1 = getLayoutInflater().inflate(R.layout.edit_text, null, false);
            TextInputEditText nameNote = view1.findViewById(R.id.edit_text);
            layout1.addView(view1);
            //

            //получаем второую кастомную вьюшку со списком с типом заметок, устанавливаем адаптер
            //устанавливаем "слушатель" и ставим на alertDialog
            final String standartTextNote = "Стандартная заметка";
            final String marckedList = "Маркированный список";
            final String[] types = new String[]{standartTextNote, marckedList};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                    R.layout.support_simple_spinner_dropdown_item, types);

            View view2 = getLayoutInflater().inflate(R.layout.spinner_item, null, false);

            AutoCompleteTextView spinner =
                    view2.findViewById(R.id.filled_exposed_dropdown);

            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent,
                                           View itemSelected, int selectedItemPosition,
                                           long selectedId) {

                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                    ((TextView) parent.getChildAt(0)).setTextSize(18);
                    ((TextView) parent.getChildAt(0)).setTypeface(Planets.standartFont);

                }
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            layout1.addView(view2);
            //

            //ставим разметку на alertDialog
            builder.setView(layout1);

            final String DB_TYPE_STNDRT = "standart";
            final String DB_TYPE_SHOP = "shop";

            //обработка нажатия на кнопку
            builder.setPositiveButton("Добавить",
                    (dialog, which) -> {
                String name = nameNote.getText().toString();
                //проверяем, не пустое ли название
                if(!name.equals("")) {
                    String shortNote = "короткое описание";
                    String text = "Новая заметка";
                    String type = "";
                    mDb = mDBHelper.getWritableDatabase();

                    if (spinner.getText().toString().equals(standartTextNote)) {
                        type = DB_TYPE_STNDRT;
                    } else if (spinner.getText().toString().equals(marckedList)) {
                        type = DB_TYPE_SHOP;
                    }


                    //добавление в бд и запись в строчки
                    ContentValues cv = new ContentValues();

                    Note note = ITEMS.get(ITEMS.size() - 1);
                    cv.put("_id", note.getId() + 2);
                    Toast.makeText(getContext(), String.valueOf(note.getId() + 2), Toast.LENGTH_SHORT).show();
                    cv.put("name", name);
                    cv.put("shortName", shortNote);
                    cv.put("text", text);
                    cv.put("type", type);
                    //получение даты
                    Date currentDate = new Date();
                    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy",
                            Locale.getDefault());
                    String dateText = dateFormat.format(currentDate);
                    cv.put("date", dateText);
                    //запись
                    mDb.insert("Notes", null, cv);
                    mDb.close();

                    ITEMS.add(new Note(name, shortNote, null, type,
                            note.getId() + 2));

                    NOTES_ADAPTER.notifyDataSetChanged();
                }
            }).show();
        }
        else if(view.getId() == R.id.back){
            Intent main = new Intent(getContext(), MainActivity.class);
            startActivity(main);
        }
    }

    //обновление проектов на активити
    private void updProjects(){
        //добавление новых проектов
        mDb = mDBHelper.getReadableDatabase();
        userCursor = mDb.rawQuery("Select * from Notes", null);
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
                        userCursor.getInt(userCursor.getColumnIndex("_id")) - 1));

            userCursor.moveToNext();

            bitmap = null;
        }


        userCursor.close();
        RecyclerView recyclerView = VIEW.findViewById(R.id.recycler_view);
        NOTES_ADAPTER = new NotesAdapter(getContext(), ITEMS);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(NOTES_ADAPTER);

    }
}

