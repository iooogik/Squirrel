package iooogik.app.modelling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Database mDBHelper;
    private SQLiteDatabase mDb;
    public static FloatingActionButton FAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Database mDBHelper = new Database(this);
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();
        FAB = findViewById(R.id.fab);
        createToolbar();
        updateList();
        FrameLayout frameLayout = findViewById(R.id.Mainframe);
        frameLayout.removeAllViews();
    }

    private void updateList(){
        LinearLayout linearLayout = findViewById(R.id.items_linear);

        @SuppressLint("InflateParams")
        View view1 = getLayoutInflater().inflate(R.layout.item_planet, null, false);
        //астрономия
        FrameLayout frameLayout = view1.findViewById(R.id.frame_formulae);
        ImageView imageView = frameLayout.findViewById(R.id.formulae);
        TextView desc = frameLayout.findViewById(R.id.description);
        TextView nameTv = frameLayout.findViewById(R.id.namePlanet);
        nameTv.setText("Астрономия");

        int width = 300;
        int height = 300;
        Bitmap bitmapAstro = BitmapFactory.decodeResource(getResources(), R.drawable.astronomy_logo);
        imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmapAstro, width, height, false));
        desc.setText(R.string.astronomyTerm);

        view1.setOnClickListener(v -> {
            Planets planets = new Planets();
            FrameLayout frameLayout1 = findViewById(R.id.Mainframe);
            showFragment(planets, frameLayout1);
            fabPlanets();
        });

        linearLayout.addView(view1);

        //геометрия
        View view2 = getLayoutInflater().inflate(R.layout.item_planet, null, false);
        FrameLayout frameLayout2 = view2.findViewById(R.id.frame_formulae);

        ImageView imageView2 = frameLayout2.findViewById(R.id.formulae);
        TextView desc2 = frameLayout2.findViewById(R.id.description);
        TextView nameTv2 = frameLayout2.findViewById(R.id.namePlanet);

        nameTv2.setText("Геометрия");
        Bitmap bitmapGeo = BitmapFactory.decodeResource(getResources(), R.drawable.geometry);
        imageView2.setImageBitmap(Bitmap.createScaledBitmap(bitmapGeo, width, height, false));
        desc2.setText(R.string.geometryTerm);

        view2.setOnClickListener(v -> {
            //открытие фрагмента с фигурами
            GeometricFigures figures = new GeometricFigures();
            FrameLayout frameLayout1 = findViewById(R.id.Mainframe);
            showFragment(figures, frameLayout1);
            fabPlanets();
        });
        linearLayout.addView(view2);

    }

    private void createToolbar(){

        BottomAppBar bottomAppBar = findViewById(R.id.bar);

        setSupportActionBar(bottomAppBar);


        final Intent QR_READER = new Intent(this, BarcodeCaptureActivity.class);
        int identificator = 0;


        Drawer drawer = new Drawer()
                .withActivity(this)
                .withToolbar(bottomAppBar)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(

                        //0
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).
                                withIcon(FontAwesome.Icon.faw_home).withIdentifier(identificator),

                        //1
                        new DividerDrawerItem(),

                        //2
                        new PrimaryDrawerItem().withName(R.string.textNotes).
                                withIcon(FontAwesome.Icon.faw_file_text).
                                withIdentifier(identificator++),

                        //3

                        new PrimaryDrawerItem().withName(R.string.drawer_item_qr).
                                withIcon(FontAwesome.Icon.faw_qrcode).
                                withIdentifier(identificator++),
                        //4
                        new DividerDrawerItem(),

                        //5
                        new PrimaryDrawerItem().withName("Тесты")
                                .withIcon(FontAwesome.Icon.faw_tasks)
                                .withIdentifier(identificator++),

                        //6
                        new DividerDrawerItem(),
                        //7
                        new PrimaryDrawerItem().withName(R.string.contacts)
                                .withIcon(FontAwesome.Icon.faw_phone_square).
                                withIdentifier(identificator++),
                        //8
                        //настройки
                        new PrimaryDrawerItem().withName("Настройки (в разработке)")
                                .withIcon(FontAwesome.Icon.faw_wrench).
                                withIdentifier(identificator++).setEnabled(false),
                        //9
                        new DividerDrawerItem(),

                        //10

                        new SecondaryDrawerItem().withName("Игра от издателя")
                                .withIcon(FontAwesome.Icon.faw_rocket)
                                .withIdentifier(identificator++)
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Скрываем клавиатуру при открытии Navigation Drawer
                        try {
                            InputMethodManager inputMethodManager = (InputMethodManager)
                                    getSystemService(Activity.INPUT_METHOD_SERVICE);
                            if (inputMethodManager != null) {
                                inputMethodManager.hideSoftInputFromWindow(Objects.
                                        requireNonNull(
                                                getCurrentFocus()).getWindowToken(), 0);
                            }
                        } catch (Exception e){
                            Log.i("Planets", String.valueOf(e));
                        }

                    }


                    @Override
                    public void onDrawerClosed(View drawerView) {}
                })
                .withOnDrawerItemClickListener((parent, view, position, id, drawerItem) -> {

                    if(position == 0){
                        FrameLayout frameLayout = findViewById(R.id.Mainframe);
                        if(frameLayout.getVisibility() == View.VISIBLE) {
                            frameLayout.setVisibility(View.GONE);
                        }
                        fabMain();
                        ScrollView scrollView = findViewById(R.id.scrollMain);
                        scrollView.setVisibility(View.VISIBLE);

                    } else if(position == 2){
                        FrameLayout frameLayout = findViewById(R.id.Mainframe);
                        Notes notes = new Notes();
                        showFragment(notes, frameLayout);
                        fabNotes();
                        frameLayout.setVisibility(View.VISIBLE);
                    }

                    else if (position == 3) {
                        QR_READER.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                        QR_READER.putExtra(BarcodeCaptureActivity.UseFlash, false);
                        startActivity(QR_READER);
                    }

                    else if(position == 5){
                        FrameLayout frameLayout = findViewById(R.id.Mainframe);
                        Tests test = new Tests();
                        showFragment(test, frameLayout);
                        fabTests();
                        frameLayout.setVisibility(View.VISIBLE);
                    }

                    else if(position == 7){
                        FrameLayout frameLayout = findViewById(R.id.Mainframe);
                        Contacts contacts = new Contacts();
                        showFragment(contacts, frameLayout);
                        fabContacts();
                        frameLayout.setVisibility(View.VISIBLE);
                    }

                    else if(position == 10){
                        FrameLayout frameLayout = findViewById(R.id.Mainframe);
                        LifeAtSpace lifeAtSpace = new LifeAtSpace();
                        showFragment(lifeAtSpace, frameLayout);
                        fabLifeAtSpace();
                        frameLayout.setVisibility(View.VISIBLE);
                    }

                });
        drawer.build();
    }

    private void fabNotes(){

        FAB.setVisibility(View.VISIBLE);

        FAB.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.baseline_add_white_24dp));


        FAB.setOnClickListener(v -> {

            mDBHelper = new Database(Notes.VIEW.getContext());
            mDBHelper.openDataBase();
            mDBHelper.updateDataBase();

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext(),
                    R.style.Theme_MaterialComponents_Light_Dialog);
            //создаём "поверхность" на alertDialog
            final LinearLayout layout1 = new LinearLayout(Notes.VIEW.getContext());
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
            ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(Notes.VIEW.getContext()),
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

                            Note note = Notes.ITEMS.get(Notes.ITEMS.size() - 1);
                            cv.put("_id", note.getId() + 2);
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

                            Notes.ITEMS.add(new Note(name, shortNote, null, type,
                                    note.getId() + 2));

                            Notes.NOTES_ADAPTER.notifyDataSetChanged();
                        }
                    }).show();
        });
    }

    private void fabTests(){
        if(FAB.getVisibility() == View.VISIBLE)
            FAB.setVisibility(View.GONE);
    }

    private void fabPlanets(){
        if(FAB.getVisibility() == View.VISIBLE)
            FAB.setVisibility(View.GONE);
    }

    private void fabContacts(){
        if(FAB.getVisibility() == View.VISIBLE)
            FAB.setVisibility(View.GONE);
    }

    private void fabLifeAtSpace(){
        if(FAB.getVisibility() == View.VISIBLE)
            FAB.setVisibility(View.GONE);
    }

    private void fabMain(){
        if(FAB.getVisibility() == View.VISIBLE)
            FAB.setVisibility(View.GONE);
    }

    public void showFragment(Fragment fragment, FrameLayout frameLayout){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ScrollView scrollView = findViewById(R.id.scrollMain);
        scrollView.setVisibility(View.GONE);

        try{
            FrameLayout main = findViewById(R.id.Mainframe);
            main.removeAllViews();
        } catch (Exception e){
            Log.i("Planets", "fail");
        }

        frameLayout.setVisibility(View.VISIBLE);

        if (fragment != null) {
            ft.remove(fragment).commitAllowingStateLoss();
        }


        FragmentTransaction addTransaction = fm.beginTransaction();
        addTransaction.setCustomAnimations
                (R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim);
        addTransaction.addToBackStack(null);
        assert fragment != null;
        addTransaction.add(R.id.Mainframe, fragment,
                "mainFrame").commitAllowingStateLoss();

    }

    @Override
    public void onBackPressed() {}
}
