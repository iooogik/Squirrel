package my.iooogik.Book;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    Cursor userCursor;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    public static Typeface standartFont;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        standartFont = Typeface.createFromAsset(getAssets(), "rostelekom.otf");


        createToolbar();

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
        int identifier = 1;

        new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).
                                withIcon(FontAwesome.Icon.faw_home).withIdentifier(identifier),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_settings).
                                withIdentifier(identifier++),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_qr).
                                withIdentifier(identifier++)


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
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id, IDrawerItem drawerItem) {

                        if(position == 0){

                        } else if(position == 1){

                        }
                        else if (position == 2) {
                            qrReader.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                            qrReader.putExtra(BarcodeCaptureActivity.UseFlash, false);
                            startActivity(qrReader);
                        }

                    }
                })
                .build();

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
