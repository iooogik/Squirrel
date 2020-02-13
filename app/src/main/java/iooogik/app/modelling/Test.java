package iooogik.app.modelling;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Test extends Fragment implements View.OnClickListener{

    View view;
    private ArrayList<String> testTitles;

    //Переменная для работы с БД
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    Bundle bundle = new Bundle();
    Cursor userCursor;


    public Test() {
        testTitles = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_test, container, false);
        FloatingActionButton back = view.findViewById(R.id.back);
        back.setOnClickListener(this);


        mDBHelper = new DatabaseHelper(getContext());
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();
        loadAndSetThemes();
        return view;
    }

    private void loadAndSetThemes(){
        mDb = mDBHelper.getReadableDatabase();
        userCursor = mDb.rawQuery("Select * from Tests", null);
        userCursor.moveToFirst();
        String name, desc;
        int identificator = 0;
        while (!userCursor.isAfterLast()) {

            name = String.valueOf(userCursor.getString(1)); //колонки считаются с 0
            testTitles.add(name);
            desc = String.valueOf(userCursor.getString(2));

            identificator = testTitles.size() - 1;

            if(name != null)
                addToScroll(name, desc, identificator);
            userCursor.moveToNext();
        }
    }

    private void addToScroll(String name, String desc, int identificator){
        View view1 = getLayoutInflater().inflate(R.layout.note, null, false);
        //изменяем задний фон в зависимости от типа заметок
        LinearLayout back = view1.findViewById(R.id.background);

        back.setBackgroundResource(R.drawable.pink_custom_button);

        ImageView img = view1.findViewById(R.id.subImg);
        img.setVisibility(View.GONE);
        //заголовок
        TextView nameNote = view1.findViewById(R.id.name);
        nameNote.setText(name);
        //описание
        TextView description = view1.findViewById(R.id.description);
        description.setText(desc);
        //обработка нажатия на view
        view1.setOnClickListener(v -> {
            bundle.putString("button name", name);
            bundle.putInt("button ID", identificator);
            Questions questions = new Questions();
            showFragment(questions);
        });
        //установка на активити
        LinearLayout linearLayout = view.findViewById(R.id.scrollTestThemes);
        linearLayout.addView(view1);
    }

    private void showFragment(Fragment fragment){
        FrameLayout frameLayout = view.findViewById(R.id.test_frame);
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
        addTransaction.add(R.id.test_frame, fragment,
                "testFrame").commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.back){
            Intent main = new Intent(getContext(), MainActivity.class);
            startActivity(main);
        }
    }
}
