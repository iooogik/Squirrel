package my.iooogik.Book;

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
    private SQLiteDatabase mDb;
    public static Typeface standartFont;
    public static Fragment currFragment;
    @SuppressLint("StaticFieldLeak")
    public static Toolbar toolbar;
    public static Notes notes = new Notes();


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


    private void createToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle("Планеты и Звёзды");

        final Intent qrReader = new Intent(this, BarcodeCaptureActivity.class);
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

                        new SecondaryDrawerItem().withName("Игра от издателя")
                                .withIdentifier(identifier++)

                        /*запятая после прдыдущего!
                        new SecondaryDrawerItem().withName(R.string.drawer_item_help).
                                withIcon(FontAwesome.Icon.faw_cog),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_open_source).
                                withIcon(FontAwesome.Icon.faw_question).setEnabled(false),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_contact).
                                withIcon(FontAwesome.Icon.faw_github).withBadge("12+").withIdentifier(1)

                         */
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
                            System.out.println(e);
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
                        showFragment(notes);
                        toolbar.setSubtitle(R.string.textNotes);
                        FrameLayout frameLayout = findViewById(R.id.Mainframe);
                        frameLayout.setVisibility(View.VISIBLE);
                    }

                    else if(position == 2){

                    }

                    else if (position == 4) {
                        qrReader.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                        qrReader.putExtra(BarcodeCaptureActivity.UseFlash, false);
                        startActivity(qrReader);
                    }

                    else if(position == 5){
                        Contacts contacts = new Contacts();
                        showFragment(contacts);
                        toolbar.setSubtitle(R.string.contacts);
                        FrameLayout frameLayout = findViewById(R.id.Mainframe);
                        frameLayout.setVisibility(View.VISIBLE);
                    }

                    else if(position == 7){
                        LifeAtSpace lifeAtSpace = new LifeAtSpace();
                        showFragment(lifeAtSpace);
                        toolbar.setSubtitle("Игра от издателя");
                        FrameLayout frameLayout = findViewById(R.id.Mainframe);
                        frameLayout.setVisibility(View.VISIBLE);
                    }

                });
        drawer.build();
        Objects.requireNonNull(toolbar.getNavigationIcon()).
                setColorFilter(ContextCompat.getColor(this, R.color.colorIcons), PorterDuff.Mode.SRC_ATOP);

    }

    private void showFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        currFragment = fragment;
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.Mainframe, fragment);
        ft.commit();
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
        final FrameLayout SecondaryFrame = findViewById(R.id.SecondaryFrame);
        final FrameLayout frame = findViewById(R.id.Mainframe);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.remove(fragment).commit();
        if(SecondaryFrame.getVisibility() == View.VISIBLE){

            SecondaryFrame.removeAllViews();
            SecondaryFrame.setVisibility(View.GONE);
            Toolbar toolbar = findViewById(R.id.toolbar_main);
            toolbar.setSubtitle(R.string.textNotes);
            currFragment = notes;

        }
    }

    private void getPlanets(){
        mDb = mDBHelper.getReadableDatabase();
        userCursor =  mDb.rawQuery("Select * from Planets", null);
        userCursor.moveToLast();
        String name = "", description = "";
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
