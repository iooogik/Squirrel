package iooogik.app.modelling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static Fragment currFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper mDBHelper = new DatabaseHelper(this);
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();

        createToolbar();
        updateList();
    }

    private void updateList(){
        LinearLayout linearLayout = findViewById(R.id.items_linear);

        @SuppressLint("InflateParams")
        View view1 = getLayoutInflater().inflate(R.layout.planet_item, null, false);
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
        });
        linearLayout.addView(view1);
        //геометрия
        View view2 = getLayoutInflater().inflate(R.layout.planet_item, null, false);
        FrameLayout frameLayout2 = view2.findViewById(R.id.frame_formulae);

        ImageView imageView2 = frameLayout2.findViewById(R.id.formulae);
        TextView desc2 = frameLayout2.findViewById(R.id.description);
        TextView nameTv2 = frameLayout2.findViewById(R.id.namePlanet);

        nameTv2.setText("Геометрия");
        Bitmap bitmapGeo = BitmapFactory.decodeResource(getResources(), R.drawable.geometry);
        imageView2.setImageBitmap(Bitmap.createScaledBitmap(bitmapGeo, width, height, false));
        desc2.setText(R.string.geometryTerm);

        view1.setOnClickListener(v -> {
            //открытие фрагмента с фигурами
        });
        linearLayout.addView(view2);

    }

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
                        new DividerDrawerItem(),

                        //2
                        new PrimaryDrawerItem().withName(R.string.textNotes).
                                withIdentifier(identifier++),

                        //3

                        new PrimaryDrawerItem().withName(R.string.drawer_item_qr).
                                withIdentifier(identifier++),
                        //4
                        new DividerDrawerItem(),

                        //5
                        new PrimaryDrawerItem().withName("Тесты")
                                .withIdentifier(identifier++),

                        //6
                        new DividerDrawerItem(),
                        //7
                        new PrimaryDrawerItem().withName(R.string.contacts).
                                withIdentifier(identifier++),
                        //8
                        //настройки
                        new PrimaryDrawerItem().withName("Настройки (в разработке)").
                                withIdentifier(identifier++).setEnabled(false),
                        //9
                        new DividerDrawerItem(),

                        //10

                        new SecondaryDrawerItem().withName("Игра от издателя")
                                .withIdentifier(identifier++)
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
                        if(frameLayout.getVisibility() != View.GONE) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }

                    } else if(position == 2){
                        FrameLayout frameLayout = findViewById(R.id.Mainframe);
                        Notes notes = new Notes();
                        showFragment(notes, frameLayout);
                        toolbar.setSubtitle(R.string.textNotes);
                        frameLayout.setVisibility(View.VISIBLE);
                    }

                    else if (position == 3) {
                        QR_READER.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                        QR_READER.putExtra(BarcodeCaptureActivity.UseFlash, false);
                        startActivity(QR_READER);
                    }

                    else if(position == 5){
                        FrameLayout frameLayout = findViewById(R.id.Mainframe);
                        Test test = new Test();
                        showFragment(test, frameLayout);
                        toolbar.setSubtitle("Тесты");
                        frameLayout.setVisibility(View.VISIBLE);
                    }

                    else if(position == 7){
                        FrameLayout frameLayout = findViewById(R.id.Mainframe);
                        Contacts contacts = new Contacts();
                        showFragment(contacts, frameLayout);
                        toolbar.setSubtitle(R.string.contacts);
                        frameLayout.setVisibility(View.VISIBLE);
                    }

                    else if(position == 10){
                        FrameLayout frameLayout = findViewById(R.id.Mainframe);
                        LifeAtSpace lifeAtSpace = new LifeAtSpace();
                        showFragment(lifeAtSpace, frameLayout);
                        toolbar.setSubtitle("Игра от издателя");

                        frameLayout.setVisibility(View.VISIBLE);
                    }

                });
        drawer.build();
        Objects.requireNonNull(toolbar.getNavigationIcon()).
                setColorFilter(ContextCompat.getColor(this, R.color.colorIcons),
                        PorterDuff.Mode.SRC_ATOP);

    }

    public void showFragment(Fragment fragment, FrameLayout frameLayout){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

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
