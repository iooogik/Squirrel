package my.iooogik.Book;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.IOException;
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
    View view;

    static ArrayList<String> standartItems = new ArrayList<>();
    private static ArrayList<String> shopItems = new ArrayList<>();

    static ArrayAdapter<String> adapterStndrtList;
    private static ArrayAdapter<String> adapterShopList;

    static ArrayList<String> dataProjects = new ArrayList<String>();


    public Notes(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.notes, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mDBHelper = new DatabaseHelper(getContext());
        mDBHelper.openDataBase();
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }


        FloatingActionButton add = view.findViewById(R.id.addProject);

        final LinearLayout mainLayout  = new LinearLayout(getContext());

        add.setOnLongClickListener(v -> {

            mainLayout.setOrientation(LinearLayout.VERTICAL);
            int padding = 30;
            mainLayout.setPadding(padding, padding, padding, padding);

            final EditText name = new EditText(getContext());
            name.setHint("Введите имя");
            name.setTypeface(MainActivity.standartFont);
            name.setTextSize(18);
            name.setMinimumWidth(1500);

            mainLayout.addView(name);


            final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setView(mainLayout);
            builder.setPositiveButton(Html.fromHtml
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
            builder.setNegativeButton(Html.fromHtml
                    ("<font color='#7AB5FD'>Закрыть</font>"), (dialog, which) -> {

                    });

            AlertDialog dlg = builder.create();
            dlg.show();



            return true;
        });

        add.setOnClickListener(this);

        //необходимо очистить содержимое, чтобы при старте активити не было повторяющихся элементов
        try {
            standartItems.clear();
            shopItems.clear();
            dataProjects.clear();
            adapterStndrtList.clear();
            adapterShopList.clear();
        } catch (Exception e){
            System.out.println(e);
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
                    listView.setAdapter(adapterStndrtList);
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
            final LinearLayout mainLayout  = new LinearLayout(getContext());
            final LinearLayout layout1 = new LinearLayout(getContext());
            mainLayout.setOrientation(LinearLayout.VERTICAL);
            layout1.setOrientation(LinearLayout.VERTICAL);
            //ввод названия заметки
            final EditText nameNote = new EditText(getContext());

            int padding = 70;

            mainLayout.setPadding(padding, padding, padding, padding);

            nameNote.setTextColor(Color.BLACK);
            nameNote.setHint("Введите название");
            nameNote.setTypeface(MainActivity.standartFont);
            nameNote.setTextSize(18);
            nameNote.setMinimumWidth(1500);
            layout1.addView(nameNote);

            final TextView tv = new TextView(getContext());
            tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            tv.setText("    Пожалуйста, введите название!");
            tv.setTextColor(Color.RED);
            tv.setTextSize(13);
            tv.setTypeface(MainActivity.standartFont);
            tv.setMinimumWidth(1500);
            tv.setVisibility(View.GONE);
            layout1.addView(tv);
            final String stndrtTextNote = "Стандартная заметка";
            final String marckedList = "Маркированный список";
            final String[] types = new String[]{stndrtTextNote, marckedList};
            //выбор типа
            final Spinner spinner = new Spinner(getContext());
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Objects.requireNonNull(getContext()),
                    android.R.layout.simple_spinner_dropdown_item, types);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent,
                                           View itemSelected, int selectedItemPosition,
                                           long selectedId) {

                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                    ((TextView) parent.getChildAt(0)).setTextSize(18);
                    ((TextView) parent.getChildAt(0)).setTypeface(MainActivity.standartFont);

                }
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });



            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());


            layout1.addView(spinner);
            mainLayout.addView(layout1);
            builder.setView(mainLayout);

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
                        if(spinner.getSelectedItem().toString().equals(stndrtTextNote)){
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

                        if(spinner.getSelectedItem().toString().equals(stndrtTextNote)){
                            dataProjects.add(nameNote.getText().toString());
                            standartItems.add(nameNote.getText().toString());
                            adapterStndrtList.notifyDataSetChanged();
                        }else if(spinner.getSelectedItem().toString().equals(marckedList)){
                            dataProjects.add(nameNote.getText().toString());
                            shopItems.add(nameNote.getText().toString());
                            adapterShopList.notifyDataSetChanged();
                        }
                    });

            AlertDialog dlg = builder.create();

            dlg.setOnShowListener(dialog -> {
                Window v = ((AlertDialog)dialog).getWindow();
                v.setBackgroundDrawableResource(R.drawable.alert_dialog_backgrond);
                Button posButton = ((AlertDialog)dialog).
                        getButton(DialogInterface.BUTTON_POSITIVE);
                posButton.setTypeface(MainActivity.standartFont);
                posButton.setTypeface(Typeface.DEFAULT_BOLD);
            });

            dlg.show();
        }
    }

    private String getType(String name){
        mDb = mDBHelper.getReadableDatabase();
        userCursor =  mDb.rawQuery("Select * from Notes", null);
        final int btnID = dataProjects.indexOf(name);
        userCursor.moveToPosition(btnID);

        return userCursor.getString(8);
    }

    private void showFragment(Fragment fragment){
        FragmentManager fm = getFragmentManager();
        assert fm != null;

        FragmentTransaction ft = fm.beginTransaction();
        MainActivity.currFragment = fragment;
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.frame, fragment);
        ft.commit();
    }

    //удаление проекта из активити и удаление его из бд
    private void delete(int selected, String name){
        if(id >= 0) {
            //Toast.makeText(getApplicationContext(), getType(standartItems.get(selected - 1)),
              //      Toast.LENGTH_LONG).show();

            mDb = mDBHelper.getWritableDatabase();
            mDb.delete("Notes", "_id=" + (selected + 1), null);


            if(getType(dataProjects.get(selected)).equals("standart")){
                try {
                    standartItems.remove(selected);
                    adapterStndrtList.notifyDataSetChanged();
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
    public void updProjects(){
        //добавление новых проектов
        mDb = mDBHelper.getReadableDatabase();
        userCursor =  mDb.rawQuery("Select * from Notes", null);
        userCursor.moveToFirst();
        String item = "";

        final ListView standartList = view.findViewById(R.id.standartList);
        final ListView shopList = view.findViewById(R.id.shopList);
        final ListView bookList = view.findViewById(R.id.booksList);

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

        final ArrayList<String> booksItems = new ArrayList<>();
        booksItems.add("Математические формулы");

        ArrayAdapter<String> adapterBookList = new ArrayAdapter<>(getContext(),
                R.layout.item_project, booksItems);

        bookList.setAdapter(adapterBookList);

        adapterShopList = new ArrayAdapter<>(getContext(),
                R.layout.item_project, shopItems);

        adapterStndrtList = new ArrayAdapter<>(getContext(),
                R.layout.item_project, standartItems);

        standartList.setAdapter(adapterStndrtList);
        shopList.setAdapter(adapterShopList);

        shopList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemListClicked(position, "shop");
            }
        });

        standartList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemListClicked(position, "standart");
            }
        });

        standartList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position,
                                           long id) {
                standartList.setEnabled(false);
                onItemLongListClicked(position, "standart");
                standartList.setEnabled(true);
                return false;
            }
        });

        shopList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position,
                                           long id) {
                shopList.setEnabled(false);
                onItemLongListClicked(position, "shop");
                shopList.setEnabled(true);
                return false;
            }
        });

        if(dataProjects.size() == 0){id = 0;}
        else {id = dataProjects.size() + 1;}
    }


    private void onItemListClicked(int position, String type){

        FrameLayout frameLayout = view.findViewById(R.id.frame);
        frameLayout.setVisibility(View.VISIBLE);
        Bundle args = new Bundle();
        String name = null;
        if(type.equals("standart")){
            name = standartItems.get(position);
        }
        else if(type.equals("shop")){
            name = shopItems.get(position);
        }

        if(name != null) {

            args.putString("button name", name);

            MainActivity.toolbar.setSubtitle(name);
            if(getType(name).equals("standart")) {

                args.putInt("buttonID", dataProjects.indexOf(name));
                standartNote.setArguments(args);
                showFragment(standartNote);
            } else if(getType(name).equals("shop")){

                args.putInt("buttonID", dataProjects.indexOf(name));
                checkListActivity.setArguments(args);
                showFragment(checkListActivity);
            }
        }
    }

    private void onItemLongListClicked(int position, String type){

        final ListView standartList = view.findViewById(R.id.standartList);
        final ListView shopList = view.findViewById(R.id.shopList);
        final String name;
        if(type.equals("shop")){
            position = position + standartItems.size();
            name = String.valueOf(shopList.getItemAtPosition(position));
        }else {
            name = String.valueOf(standartList.getItemAtPosition(position));
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
        tv.setTypeface(MainActivity.standartFont);
        tv.setTextSize(18);
        tv.setMinHeight(15);

        layout1.addView(tv);

        mainLayout.addView(layout1);
        builder.setView(mainLayout);



        userCursor.moveToPosition(position);
        String message = userCursor.getString(2);
        tv.setText("\n  " + message);

        builder.setCancelable(true);
        final int finalPosition = position;
        builder.setPositiveButton(Html.fromHtml
                        ("<font color='#7AB5FD'>Удалить выбранную запись</font>"),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), String.valueOf(finalPosition),
                         Toast.LENGTH_LONG).show();
                        delete(finalPosition, name);
                    }
                });
        AlertDialog dlg = builder.create();
        LinearLayout main = view.findViewById(R.id.main);
        main.setEnabled(false);
        dlg.show();

    }

    private void closeFragment(Fragment fragment){
        final FrameLayout frameLayout = view.findViewById(R.id.frame);
        if(frameLayout.getVisibility() == View.VISIBLE){
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            ft.remove(fragment).commit();
            frameLayout.removeAllViews();
            frameLayout.setVisibility(View.GONE);
            Toolbar toolbar = view.findViewById(R.id.toolbar_main);
            toolbar.setSubtitle(R.string.textNotes);

        }
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
                    standartList.setAdapter(adapterStndrtList);

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

