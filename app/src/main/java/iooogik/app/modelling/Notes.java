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
import java.util.Locale;
import java.util.Objects;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Notes extends Fragment implements View.OnClickListener {

    //Переменная для работы с БД
    public static int id = 0;
    //Переменная для работы с БД
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    private StandartNote standartNote = new StandartNote();
    private CheckList checkListActivity = new CheckList();
    private Cursor userCursor;
    private SimpleCursorAdapter userAdapter;
    private EditText userFilter;
    protected static View view;

    static ArrayList<String> standartItems = new ArrayList<>();
    private static ArrayList<String> shopItems = new ArrayList<>();

    static ArrayAdapter<String> adapterStandartList;
    private static ArrayAdapter<String> adapterShopList;

    private LinearLayout MAIN_LAYOUT;
    private ListView STANDART_LIST;
    private ListView SHOP_LIST;
    private ListView BOOK_LIST;

    static ArrayList<String> dataProjects = new ArrayList<>();


    public Notes(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.notes, container ,false);
        MAIN_LAYOUT  = new LinearLayout(getContext());
        STANDART_LIST = view.findViewById(R.id.standartList);
        SHOP_LIST = view.findViewById(R.id.shopList);
        BOOK_LIST = view.findViewById(R.id.booksList);
        FloatingActionButton back = view.findViewById(R.id.back);
        back.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mDBHelper = new DatabaseHelper(getContext());
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();


        FloatingActionButton add = view.findViewById(R.id.addProject);

        add.setOnLongClickListener(v -> {

            MAIN_LAYOUT.setOrientation(LinearLayout.VERTICAL);
            int padding = 30;
            MAIN_LAYOUT.setPadding(padding, padding, padding, padding);

            final EditText name = new EditText(getContext());
            name.setHint("Введите имя");
            name.setTypeface(Planets.standartFont);
            name.setTextSize(18);
            name.setMinimumWidth(1500);

            MAIN_LAYOUT.addView(name);


            final AlertDialog.Builder BUILDER = new AlertDialog.Builder(v.getContext());
            BUILDER.setView(MAIN_LAYOUT);
            BUILDER.setPositiveButton(Html.fromHtml
                            ("<font color='#7AB5FD'>Добавить запись</font>"),
                    (dialog, which) -> {
                        mDb = mDBHelper.getWritableDatabase();
                        ContentValues cv = new ContentValues();
                        id++;
                        cv.put("id", id);
                        cv.put("name", name.getText().toString());
                        cv.put("shortName", "короткое описание");
                        cv.put("text", "hello, it's the best note ever");
                        //получение даты
                        Date currentDate = new Date();
                        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy",
                                Locale.getDefault());
                        String dateText = dateFormat.format(currentDate);
                        cv.put("date", dateText);
                        //запись
                        dataProjects.add(String.valueOf(id));
                        mDb.insert("Notes", null, cv);
                        mDb.close();
                    });
            BUILDER.setNegativeButton(Html.fromHtml
                    ("<font color='#7AB5FD'>Закрыть</font>"), (dialog, which) -> {

                    });

            AlertDialog dlg = BUILDER.create();
            dlg.show();



            return true;
        });

        add.setOnClickListener(this);

        //необходимо очистить содержимое, чтобы при старте активити не было повторяющихся элементов
        try {
            standartItems.clear();
            shopItems.clear();
            dataProjects.clear();
            adapterStandartList.clear();
            adapterShopList.clear();
        } catch (Exception e){
            Log.i("Notes", String.valueOf(e));
        }


        updProjects();

        userFilter = view.findViewById(R.id.search);

        userFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ListView listView = view.findViewById(R.id.standartList);
                ListView listView2 = view.findViewById(R.id.shopList);
                TextView tv5 = view.findViewById(R.id.textView5);

                if(!s.toString().isEmpty()) {
                    search();
                    try {
                        listView2.setVisibility(View.GONE);
                        tv5.setVisibility(View.GONE);
                    } catch (Exception e){
                        Toast.makeText(getContext(), String.valueOf(e),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    listView.setAdapter(adapterStandartList);
                    listView2.setAdapter(adapterShopList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.addProject){
            //кнопка "Добавить проект"
            final LinearLayout layout1 = new LinearLayout(getContext());
            MAIN_LAYOUT.setOrientation(LinearLayout.VERTICAL);
            layout1.setOrientation(LinearLayout.VERTICAL);
            //ввод названия заметки
            final EditText nameNote = new EditText(getContext());

            int padding = 70;

            MAIN_LAYOUT.setPadding(padding, padding, padding, padding);

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



            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());


            layout1.addView(spinner);
            MAIN_LAYOUT.addView(layout1);
            builder.setView(MAIN_LAYOUT);

            final String DB_TYPE_STNDRT = "standart";
            final String DB_TYPE_SHOP = "shop";

            builder.setPositiveButton("Добавить",
                    (dialog, which) -> {
                        mDb = mDBHelper.getWritableDatabase();

                        //addProject(nameNote.getText().toString(), true);
                        //добавление в бд и запись в строчки
                        ContentValues cv = new ContentValues();
                        cv.put("_id", id);
                        cv.put("name", nameNote.getText().toString());
                        cv.put("shortName", "короткое описание");
                        cv.put("text", "hello, it's the best note ever");
                        if(spinner.getSelectedItem().toString().equals(standartTextNote)){
                            cv.put("type", DB_TYPE_STNDRT);
                        }
                        else if(spinner.getSelectedItem().toString().equals(marckedList)){
                            cv.put("type", DB_TYPE_SHOP);
                        }
                            //получение даты
                        Date currentDate = new Date();
                        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy",
                                Locale.getDefault());
                        String dateText = dateFormat.format(currentDate);
                        cv.put("date", dateText);
                        //запись
                        dataProjects.add(nameNote.getText().toString());
                        mDb.insert("Notes", null, cv);
                        mDb.close();
                        id++;

                        if(spinner.getSelectedItem().toString().equals(standartTextNote)){
                            dataProjects.add(nameNote.getText().toString());
                            standartItems.add(nameNote.getText().toString());
                            adapterStandartList.notifyDataSetChanged();
                        }else if(spinner.getSelectedItem().toString().equals(marckedList)){
                            dataProjects.add(nameNote.getText().toString());
                            shopItems.add(nameNote.getText().toString());
                            adapterShopList.notifyDataSetChanged();
                        }
                    });

            AlertDialog dlg = builder.create();

            dlg.setOnShowListener(dialog -> {
                Window v = ((AlertDialog)dialog).getWindow();
                assert v != null;
                v.setBackgroundDrawableResource(R.drawable.alert_dialog_backgrond);
                Button posButton = ((AlertDialog)dialog).
                        getButton(DialogInterface.BUTTON_POSITIVE);
                posButton.setTypeface(Planets.standartFont);
                posButton.setTypeface(Typeface.DEFAULT_BOLD);
            });

            dlg.show();
        }
        else if(view.getId() == R.id.back){
            Intent main = new Intent(getContext(), MainActivity.class);
            startActivity(main);
        }
    }

    private String getType(String name){
        mDb = mDBHelper.getReadableDatabase();
        userCursor =  mDb.rawQuery("Select * from Notes", null);
        final int BTN_ID = dataProjects.indexOf(name);
        userCursor.moveToPosition(BTN_ID);

        return userCursor.getString(8);
    }

    private void showFragment(Fragment fragment){
        FrameLayout frameLayout = view.findViewById(R.id.SecondaryFrame);
        frameLayout.setVisibility(View.VISIBLE);


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

    //удаление проекта из активити и удаление его из бд
    private void delete(int selected){
        if(id >= 0) {
            //Toast.makeText(getApplicationContext(), getType(standartItems.get(selected - 1)),
              //      Toast.LENGTH_LONG).show();

            mDb = mDBHelper.getWritableDatabase();
            mDb.delete("Notes", "_id=" + (selected + 1), null);


            if(getType(dataProjects.get(selected)).equals("standart")){
                try {
                    standartItems.remove(selected);
                    adapterStandartList.notifyDataSetChanged();
                } catch (Exception e){
                    Toast.makeText(getContext(), String.valueOf(e),
                            Toast.LENGTH_LONG).show();
                }

            }else {
                try {
                    shopItems.remove(selected);
                    adapterShopList.notifyDataSetChanged();
                } catch (Exception e){
                    Toast.makeText(getContext(), String.valueOf(e),
                            Toast.LENGTH_LONG).show();
                }
            }
            dataProjects.remove(selected);

            if(id - 1 >=0){
                id--;
            } else {
                id = 0;
            }

            LinearLayout main = view.findViewById(R.id.main);
            main.setEnabled(true);
        }

    }

    //обновление проектов на активити
    private void updProjects(){
        //добавление новых проектов
        mDb = mDBHelper.getReadableDatabase();
        userCursor =  mDb.rawQuery("Select * from Notes", null);
        userCursor.moveToFirst();
        String item;

        while (!userCursor.isAfterLast()) {
            item = userCursor.getString(1); //колонки считаются с 0
            dataProjects.add(String.valueOf(item));

            if(getType(String.valueOf(item)).equals("standart")) {
                standartItems.add(String.valueOf(item));
            } else if(getType(String.valueOf(item)).equals("shop")){
                shopItems.add(String.valueOf(item));
            }

            userCursor.moveToNext();
        }
        userCursor.close();

        ArrayList<String> booksItems = new ArrayList<>();
        booksItems.add("Математические формулы");

        ArrayAdapter<String> adapterBookList = new ArrayAdapter<>
                (Objects.requireNonNull(getContext()),
                R.layout.item_project, booksItems);

        BOOK_LIST.setAdapter(adapterBookList);



        adapterShopList = new ArrayAdapter<>(getContext(),
                R.layout.item_project, shopItems);

        adapterStandartList = new ArrayAdapter<>(getContext(),
                R.layout.item_project, standartItems);

        STANDART_LIST.setAdapter(adapterStandartList);
        SHOP_LIST.setAdapter(adapterShopList);

        BOOK_LIST.setOnItemClickListener((parent, view, position, id)
                -> onItemListClicked(position, "book"));

        SHOP_LIST.setOnItemClickListener((parent, view, position, id)
                -> onItemListClicked(position, "shop"));

        STANDART_LIST.setOnItemClickListener((parent, view, position, id)
                -> onItemListClicked(position, "standart"));

        STANDART_LIST.setOnItemLongClickListener((parent, view, position, id)
                -> {
            STANDART_LIST.setEnabled(false);
            onItemLongListClicked(position, "standart");
            STANDART_LIST.setEnabled(true);
            return false;
        });

        SHOP_LIST.setOnItemLongClickListener((parent, view, position, id)
                -> {
            SHOP_LIST.setEnabled(false);
            onItemLongListClicked(position, "shop");
            SHOP_LIST.setEnabled(true);
            return false;
        });

        if(dataProjects.size() == 0){id = 0;}
        else {id = dataProjects.size() + 1;}
    }


    private void onItemListClicked(int position, String type){

        Bundle args = new Bundle();
        String name;

        switch (type) {
            case "standart":
                name = standartItems.get(position);
                args.putString("button name", name);
                args.putInt("buttonID", dataProjects.indexOf(name));
                MainActivity.currFragment = standartNote;
                standartNote.setArguments(args);
                showFragment(standartNote);

                break;
            case "shop":
                name = shopItems.get(position);
                args.putString("button name", name);

                args.putInt("buttonID", dataProjects.indexOf(name));
                checkListActivity.setArguments(args);
                MainActivity.currFragment = checkListActivity;

                showFragment(checkListActivity);

                break;
            case "book":
                Book book = new Book();
                showFragment(book);

                break;
        }

    }


    private void onItemLongListClicked(int position, String type){

        if(type.equals("shop")){
            position = position + standartItems.size();
        }


        mDb = mDBHelper.getReadableDatabase();
        userCursor =  mDb.rawQuery("Select * from Notes", null);


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        final LinearLayout mainLayout  = new LinearLayout(getContext());
        final LinearLayout layout1 = new LinearLayout(getContext());

        mainLayout.setOrientation(LinearLayout.VERTICAL);
        layout1.setOrientation(LinearLayout.VERTICAL);
        TextView tv = new TextView(getContext());
        tv.setMinHeight(25);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

        tv.setTextColor(Color.BLACK);
        tv.setTypeface(Planets.standartFont);
        tv.setTextSize(18);
        tv.setMinHeight(15);

        layout1.addView(tv);

        mainLayout.addView(layout1);
        builder.setView(mainLayout);



        userCursor.moveToPosition(position);
        String message = userCursor.getString(2);
        tv.setText(message);

        builder.setCancelable(true);
        final int finalPosition = position;
        builder.setPositiveButton(Html.fromHtml
                        ("<font color='#7AB5FD'>Удалить выбранную запись</font>"),
                (dialog, which) -> {
                    Toast.makeText(getContext(), String.valueOf(finalPosition),
                     Toast.LENGTH_LONG).show();
                    delete(finalPosition);
                });
        AlertDialog dlg = builder.create();
        LinearLayout main = view.findViewById(R.id.main);
        main.setEnabled(false);
        dlg.show();

    }

    private void search(){
        mDb = mDBHelper.getReadableDatabase();
        userCursor = mDb.rawQuery("select * from Notes", null);
        String[] headers = new String[]{"name"};
        userAdapter = new SimpleCursorAdapter(getContext(),
                android.R.layout.simple_list_item_activated_1,
                userCursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);



        if(!userFilter.getText().toString().isEmpty())
            userAdapter.getFilter().filter(userFilter.getText().toString());

        // установка слушателя изменения текста
        userFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            // при изменении текста выполняем фильтрацию
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView tv2 = view.findViewById(R.id.textView2);
                tv2.setText("Найдено");
                TextView tv5 = view.findViewById(R.id.textView5);
                ListView shopList = view.findViewById(R.id.shopList);
                ListView standartList = view.findViewById(R.id.standartList);
                if(!s.toString().isEmpty()) {
                    userAdapter.getFilter().filter(s.toString());
                    standartList.setAdapter(userAdapter);
                    tv5.setVisibility(View.GONE);
                    shopList.setVisibility(View.GONE);
                }else {
                    tv2.setText(R.string.strNameStandartNotes);
                    tv5.setVisibility(View.VISIBLE);
                    shopList.setVisibility(View.VISIBLE);
                    standartList.setAdapter(adapterStandartList);

                    //updProjects();
                }
            }
        });


        // устанавливаем провайдер фильтрации
        userAdapter.setFilterQueryProvider(constraint -> {

            if (constraint == null || constraint.length() == 0) {
                return mDb.rawQuery("select * from Notes", null);
            }
            else {
                return mDb.rawQuery("select * from Notes" + " where " +
                        "name" + " like ?", new String[]{"%" + constraint.toString() + "%"});
            }
        });
    }

}

