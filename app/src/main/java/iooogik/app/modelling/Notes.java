package iooogik.app.modelling;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Notes extends Fragment implements View.OnClickListener {

    //Переменная для работы с БД
    public static int id = 0;
    //Переменная для работы с БД
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    Bundle bundle = new Bundle();

    public static View view;

    public static ArrayList<String> items = new ArrayList<>();

    public Notes(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.notes, container ,false);
        FloatingActionButton back = view.findViewById(R.id.back);
        back.setOnClickListener(this);
        return view;
    }

    private void startProcedures(){
        mDBHelper = new DatabaseHelper(getContext());
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();

        FloatingActionButton add = view.findViewById(R.id.addProject);
        add.setOnClickListener(this);

        //необходимо очистить содержимое, чтобы при старте активити не было повторяющихся элементов
        try {
            items.clear();
        } catch (Exception e){
            Log.i("Notes", String.valueOf(e));
        }


        updProjects();

    }

    @Override
    public void onResume() {
        super.onResume();
        startProcedures();
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
                addToScroll(type, name, shortNote, id);
                mDb.insert("Notes", null, cv);
                mDb.close();
                id++;
                items.add(nameNote.getText().toString());
            });

            AlertDialog dlg = builder.create();

            dlg.show();
        }
        else if(view.getId() == R.id.back){
            Intent main = new Intent(getContext(), MainActivity.class);
            startActivity(main);
        }
    }

    private void showFragment(Fragment fragment){
        FrameLayout frameLayout = view.findViewById(R.id.SecondaryFrame);
        frameLayout.setVisibility(View.VISIBLE);

        fragment.setArguments(bundle);

        FragmentManager fm = getFragmentManager();
        assert fm != null;
        FragmentTransaction ft = fm.beginTransaction();

        if (fragment != null) {
            ft.remove(fragment).commitAllowingStateLoss();
        }

        FragmentTransaction addTransaction = fm.beginTransaction();
        addTransaction.setCustomAnimations
                (R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim);
        addTransaction.addToBackStack(null);
        assert fragment != null;
        addTransaction.add(R.id.SecondaryFrame, fragment,
                "secondFrame").commitAllowingStateLoss();
    }



    //обновление проектов на активити
    private void updProjects(){
        //добавление новых проектов
        mDb = mDBHelper.getReadableDatabase();
        Cursor userCursor = mDb.rawQuery("Select * from Notes", null);
        userCursor.moveToFirst();
        String item;

        int identificator;
        String name, desc, type;

        while (!userCursor.isAfterLast()) {

            name = String.valueOf(userCursor.getString(1)); //колонки считаются с 0
            items.add(name);
            type = userCursor.getString(8);

            desc = String.valueOf(userCursor.getString(2));

            identificator = items.size() - 1;

            userCursor.moveToNext();
            if(name != null || type != null)
            addToScroll(type, name, desc, identificator);
        }
        userCursor.close();

        ArrayList<String> booksItems = new ArrayList<>();
        booksItems.add("Математические формулы");

    }

    private void addToScroll(String type, String name, String desc, int identificator){
        View view1 = getLayoutInflater().inflate(R.layout.note, null, false);
        //изменяем задний фон в зависимости от типа заметок
        LinearLayout back = view1.findViewById(R.id.background);
        if (type.equals("shop")) {
            back.setBackgroundResource(R.drawable.red_custom_button);
        } else if (type.equals("standart")){
            back.setBackgroundResource(R.drawable.green_custom_button);
        }
        //заголовок
        TextView nameNote = view1.findViewById(R.id.name);
        nameNote.setText(name);
        //описание
        TextView description = view1.findViewById(R.id.description);
        description.setText(desc);
        //обработка нажатия на view
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("button name", name);
                bundle.putInt("button ID", identificator);

                if (type.equals("shop")) {
                    CheckList checkList = new CheckList();
                    showFragment(checkList);
                } else if (type.equals("standart")){
                    StandartNote standartNote = new StandartNote();
                    showFragment(standartNote);
                }
            }
        });
        //установка на активити
        LinearLayout linearLayout = view.findViewById(R.id.scrollNotes);
        linearLayout.addView(view1);
    }

}

