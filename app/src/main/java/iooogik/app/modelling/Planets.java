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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

public class Planets extends Fragment {

    Cursor userCursor;
    View view;
    private DatabaseHelper mDBHelper;
    static Typeface standartFont;

    @SuppressLint("StaticFieldLeak")
    public static Toolbar toolbar;

    public Planets(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.planets, container ,false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        standartFont = Typeface.createFromAsset(getContext().getAssets(), "rostelekom.otf");

        toolbar = view.findViewById(R.id.toolbar_main);

        mDBHelper = new DatabaseHelper(getContext());
        mDBHelper.openDataBase();
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }
        try {
            getPlanets();
        } catch (Exception e){
            Log.i("Planets", String.valueOf(e));
            //Toast.makeText(this, String.valueOf(e), Toast.LENGTH_LONG).show();
        }

    }



    private void getPlanets(){
        SQLiteDatabase mDb = mDBHelper.getReadableDatabase();
        userCursor =  mDb.rawQuery("Select * from Planets", null);
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
        LinearLayout linearLayout = view.findViewById(R.id.linear);
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
            Bundle args = new Bundle();
            args.putInt("_id", id);

            ScrollingArticle scrollingArticle = new ScrollingArticle();
            scrollingArticle.setArguments(args);
            showPlanetInfo(scrollingArticle);
        });

        linearLayout.addView(view1);
    }

    private void showPlanetInfo(Fragment fragment){
        FrameLayout frameLayout = view.findViewById(R.id.planets_frame);
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
        addTransaction.add(R.id.planets_frame, fragment,
                "planets_frame").commitAllowingStateLoss();
    }

}
