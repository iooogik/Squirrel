package iooogik.app.modelling;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    Cursor userCursor;
    private DatabaseHelper mDBHelper;
    public static Typeface standartFont;
    public static Fragment currFragment;
    @SuppressLint("StaticFieldLeak")
    public static Toolbar toolbar;
    @SuppressLint("StaticFieldLeak")
    public static Notes notes = new Notes();
    @SuppressLint("StaticFieldLeak")
    public static FrameLayout currFragmeLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        standartFont = Typeface.createFromAsset(getAssets(), "rostelekom.otf");


        createToolbar();

        toolbar = findViewById(R.id.toolbar_main);

        mDBHelper = new DatabaseHelper(this);
        mDBHelper.openDataBase();
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }
        try {
            getPlanets();
        } catch (Exception e){
            Log.i("MainActivity", String.valueOf(e));
            //Toast.makeText(this, String.valueOf(e), Toast.LENGTH_LONG).show();
        }



    }


    @SuppressWarnings("UnusedAssignment")
    private void createToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle("Планеты и Звёзды");

        final Intent QR_READER = new Intent(this, BarcodeCaptureActivity.class);
        int identifier = 0;

        Drawer drawer = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(

                        //0
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).
                                withIcon(FontAwesome.Icon.faw_home).withIdentifier(identifier),
                        //1
                        new PrimaryDrawerItem().withName(R.string.textNotes).
                                withIdentifier(identifier++),

                        //2
                        new PrimaryDrawerItem().withName(R.string.drawer_item_settings).
                                withIdentifier(identifier++),


                        //3
                        new DividerDrawerItem(),


                        //4
                        new SecondaryDrawerItem().withName(R.string.drawer_item_qr).
                                withIdentifier(identifier++),

                        //5

                        new SecondaryDrawerItem().withName(R.string.contacts).
                                withIdentifier(identifier++),

                        //6
                        new DividerDrawerItem(),

                        //7

                        new PrimaryDrawerItem().withName("Игра от издателя")
                                .withIdentifier(identifier++),

                        //8
                        new DividerDrawerItem(),

                        //9
                        new PrimaryDrawerItem().withName("Тесты")
                                .withIdentifier(identifier++)
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Скрываем клавиатуру при открытии Navigation Drawer
                        try {
                            InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.
                                    this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                            if (inputMethodManager != null) {
                                inputMethodManager.hideSoftInputFromWindow(Objects.
                                        requireNonNull(MainActivity.
                                                this.getCurrentFocus()).getWindowToken(), 0);
                            }
                        } catch (Exception e){
                            Log.i("MainActivity", String.valueOf(e));
                        }

                    }


                    @Override
                    public void onDrawerClosed(View drawerView) {}
                })
                .withOnDrawerItemClickListener((parent, view, position, id, drawerItem) -> {

                    if(position == 0){
                        if(currFragment != null) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }

                    } else if(position == 1){
                        FrameLayout frameLayout = findViewById(R.id.Mainframe);
                        showFragment(notes, frameLayout);
                        toolbar.setSubtitle(R.string.textNotes);
                        frameLayout.setVisibility(View.VISIBLE);
                    }

                    else if (position == 4) {
                        QR_READER.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                        QR_READER.putExtra(BarcodeCaptureActivity.UseFlash, false);
                        startActivity(QR_READER);
                    }

                    else if(position == 5){
                        FrameLayout frameLayout = findViewById(R.id.Mainframe);
                        Contacts contacts = new Contacts();
                        showFragment(contacts, frameLayout);
                        toolbar.setSubtitle(R.string.contacts);
                        frameLayout.setVisibility(View.VISIBLE);
                    }

                    else if(position == 7){
                        FrameLayout frameLayout = findViewById(R.id.Mainframe);
                        LifeAtSpace lifeAtSpace = new LifeAtSpace();
                        showFragment(lifeAtSpace, frameLayout);
                        toolbar.setSubtitle("Игра от издателя");

                        frameLayout.setVisibility(View.VISIBLE);
                    }

                    else if(position == 9){
                        FrameLayout frameLayout = findViewById(R.id.Mainframe);
                        Test test = new Test();
                        showFragment(test, frameLayout);
                        toolbar.setSubtitle("Тесты");
                        frameLayout.setVisibility(View.VISIBLE);
                    }

                });
        drawer.build();
        Objects.requireNonNull(toolbar.getNavigationIcon()).
                setColorFilter(ContextCompat.getColor(this, R.color.colorIcons),
                        PorterDuff.Mode.SRC_ATOP);

    }

    private void showFragment(Fragment fragment, FrameLayout frameLayout){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        try{
            FrameLayout main = findViewById(R.id.Mainframe);
            main.removeAllViews();
        } catch (Exception e){
            Log.i("MainActivity", "fail");
        }

        frameLayout.setVisibility(View.VISIBLE);

        if (fragment != null) {
            ft.remove(fragment).commitAllowingStateLoss();
        }


        currFragment = fragment;
        currFragmeLayout = frameLayout;

        FragmentTransaction addTransaction = fm.beginTransaction();
        addTransaction.setCustomAnimations
                (R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim);
        addTransaction.addToBackStack(null);
        assert fragment != null;
        addTransaction.add(R.id.Mainframe, fragment,
                "mainFrame").commitAllowingStateLoss();

    }

    @Override
    public void onBackPressed() {
        if(currFragment != null) {
            closeFragment(currFragment);
        } else {
            super.onBackPressed();
        }
    }

    private void closeFragment(Fragment fragment){

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.remove(fragment).commit();
        currFragmeLayout.removeAllViews();
        currFragmeLayout.setVisibility(View.GONE);
    }

    private void getPlanets(){
        SQLiteDatabase mDb = mDBHelper.getReadableDatabase();
        userCursor =  mDb.rawQuery(getString(R.string.SELECT_FROM_PLANETS), null);
        userCursor.moveToLast();
        String name, description;
        Bitmap bitmap;
        int max = userCursor.getInt(userCursor.getColumnIndex("_id"));
        userCursor.moveToFirst();
        for (int i = 0; i < max; i++) {

            name = userCursor.getString(userCursor.getColumnIndex("name"));
            description = userCursor.getString(userCursor.getColumnIndex("description"));
            byte[] bytesImg = userCursor.getBlob(userCursor.getColumnIndex("images"));
            bitmap =  BitmapFactory.decodeByteArray(bytesImg, 0, bytesImg.length);
            setInformation(name, description, bitmap, i);
            userCursor.moveToNext();
        }
    }

    private void setInformation(String name, String description, Bitmap bitmap, int id){
        LinearLayout linearLayout = findViewById(R.id.linear);
        @SuppressLint("InflateParams")
        View view1 = getLayoutInflater().inflate(R.layout.planet_item, null, false);
        FrameLayout frameLayout = view1.findViewById(R.id.frame_formulae);
        ImageView imageView = frameLayout.findViewById(R.id.formulae);
        TextView desc = frameLayout.findViewById(R.id.description);
        TextView nameTv = frameLayout.findViewById(R.id.namePlanet);
        nameTv.setText(name);
        int width = 300;
        int height = 300;
        imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, width, height, false));
        desc.setText(description);

        view1.setOnClickListener(v -> {
            Intent scrollView = new Intent(getApplicationContext(), ScrollingArticle.class);
            Bundle args = new Bundle();
            args.putInt("_id", id);
            scrollView.putExtras(args);
            startActivity(scrollView);
        });

        linearLayout.addView(view1);
    }

}
