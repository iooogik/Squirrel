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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Spinner;
import android.widget.TextView;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
        startProcedures();

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

            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

            final LinearLayout layout1 = new LinearLayout(getContext());
            layout1.setOrientation(LinearLayout.VERTICAL);
            //ввод названия заметки
            final EditText nameNote = new EditText(getContext());

            nameNote.setTextColor(Color.BLACK);
            nameNote.setHint("Введите название");
            nameNote.setTypeface(Planets.standartFont);
            nameNote.setTextSize(18);
            nameNote.setMinimumWidth(1500);
            layout1.addView(nameNote);

            final TextView tv = new TextView(getContext());
            tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            tv.setText("    Пожалуйста, введите название!");
            tv.setTextColor(Color.RED);
            tv.setTextSize(13);
            tv.setTypeface(Planets.standartFont);
            tv.setMinimumWidth(1500);
            tv.setVisibility(View.GONE);
            layout1.addView(tv);

            final String standartTextNote = "Стандартная заметка";
            final String marckedList = "Маркированный список";
            final String[] types = new String[]{standartTextNote, marckedList};
            //выбор типа
            final Spinner spinner = new Spinner(getContext());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                    android.R.layout.simple_spinner_dropdown_item, types);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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





            layout1.addView(spinner);
            builder.setView(layout1);

            final String DB_TYPE_STNDRT = "standart";
            final String DB_TYPE_SHOP = "shop";

            builder.setPositiveButton("Добавить",
                    (dialog, which) -> {
                String name = nameNote.getText().toString();
                String shortNote = "короткое описание";
                String text = "Новая заметка";
                String type = "";
                mDb = mDBHelper.getWritableDatabase();

                if(spinner.getSelectedItem().toString().equals(standartTextNote)){
                    type = DB_TYPE_STNDRT;
                } else if (spinner.getSelectedItem().toString().equals(marckedList)){
                    type = DB_TYPE_SHOP;
                }
                //добавление в бд и запись в строчки
                ContentValues cv = new ContentValues();
                cv.put("_id", userCursor.getInt(userCursor.getColumnIndex("_id")) + 1);
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
                        userCursor.getInt(userCursor.getColumnIndex("_id")) + 1));
                NOTES_ADAPTER.notifyDataSetChanged();
            });

            AlertDialog dlg = builder.create();

            dlg.show();
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

        int identificator = 0;
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
                        userCursor.getInt(userCursor.getColumnIndex("_id"))));

            userCursor.moveToNext();
            identificator++;
            bitmap = null;
        }
        userCursor.close();

        ITEMS.add(new Note("Математические формулы", "Математическая формула — " +
                "в математике, а также физике и прикладных науках, символическая запись " +
                "высказывания (которое выражает логическое суждение), либо формы высказывания.",
                bitmap, "book", identificator));

        RecyclerView recyclerView = VIEW.findViewById(R.id.recycler_view);
        NOTES_ADAPTER = new NotesAdapter(getContext(), ITEMS);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(NOTES_ADAPTER);

    }
}

