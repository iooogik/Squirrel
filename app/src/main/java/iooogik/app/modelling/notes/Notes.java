package iooogik.app.modelling.notes;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import iooogik.app.modelling.Database;
import iooogik.app.modelling.R;

public class Notes extends Fragment implements View.OnClickListener {

    //Переменная для работы с БД
    private Database mDBHelper;
    private SQLiteDatabase mDb;

    private View VIEW;
    public static NotesAdapter NOTES_ADAPTER;
    public static FloatingActionButton fab;

    public static List<Note> ITEMS = new ArrayList<>();

    public Notes(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        VIEW = inflater.inflate(R.layout.fragment_notes, container ,false);

        //находим кнопку добавление заметок
        fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setImageResource(R.drawable.baseline_add_white_24dp);
        fab.setOnClickListener(this);
        // запускаем поток для обновления списка заметок
        startProcedures();
        return VIEW;
    }

    @Override
    public void onResume() {
        fab.setVisibility(View.VISIBLE);
        super.onResume();
    }

    @Override
    public void onPause() {
        fab.setVisibility(View.GONE);
        super.onPause();
    }

    @Override
    public void onStop() {
        fab.setVisibility(View.GONE);
        super.onStop();
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
                        userCursor.getInt(userCursor.getColumnIndex("_id"))));

            userCursor.moveToNext();

            bitmap = null;
        }


        userCursor.close();
        RecyclerView recyclerView = VIEW.findViewById(R.id.recycler_view);
        NOTES_ADAPTER = new NotesAdapter(getContext(), ITEMS, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(NOTES_ADAPTER);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.fab){
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(VIEW.getContext());

            final LinearLayout layout1 = new LinearLayout(getContext());
            layout1.setOrientation(LinearLayout.VERTICAL);
            //ввод названия заметки
            View view1 = getLayoutInflater().inflate(R.layout.edit_text, null, false);
            TextInputLayout textInputLayout = view1.findViewById(R.id.text_input_layout);
            EditText nameNote = textInputLayout.getEditText();

            layout1.addView(view1);

            final String standartTextNote = "Стандартная заметка";
            final String marckedList = "Маркированный список";

            final String[] types = new String[]{standartTextNote, marckedList};
            //выбор типа
            View view2 = getLayoutInflater().inflate(R.layout.spinner_item, null, false);
            TextInputLayout textInputLayout2 = view2.findViewById(R.id.spinner_layout);
            AutoCompleteTextView spinner = textInputLayout2.findViewById(R.id.filled_exposed_dropdown);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(VIEW.getContext(),
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
                        String name = nameNote.getText().toString();
                        String shortNote = "короткое описание";
                        String text = "Новая заметка";
                        String type = "";
                        mDb = mDBHelper.getWritableDatabase();

                        if(spinner.getText().toString().equals(standartTextNote)){
                            type = DB_TYPE_STNDRT;
                        } else if (spinner.getText().toString().equals(marckedList)){
                            type = DB_TYPE_SHOP;
                        }

                        //добавление в бд и обновление адаптера
                        ContentValues cv = new ContentValues();
                        cv.put("_id", id);
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

                        ITEMS.add(new Note(name, shortNote, null, type, id));
                        NOTES_ADAPTER.notifyDataSetChanged();
                    });
            builder.create().show();
        }
    }
}

