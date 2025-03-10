package iooojik.app.klass.notes;


import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import iooojik.app.klass.Database;
import iooojik.app.klass.R;


public class Book extends Fragment implements View.OnClickListener {


    public Book() {}
    //перменная для работы с бд
    private Database mDBHelper;
    private LinearLayout linear;
    //список с картинками
    private ArrayList<Bitmap> IMAGES;
    //список с описаниями
    private ArrayList<String> DESCRIPTIONS;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);
        linear = view.findViewById(R.id.scroll);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //"открытие" бд
        mDBHelper = new Database(getActivity());
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();
        //инициализация списков
        IMAGES = new ArrayList<>();
        DESCRIPTIONS = new ArrayList<>();
        //получение информации
        getInformation();
    }

    private void getInformation(){
        for (int i = 0; i < 36; i++) {
            getImagesAndDescriptions(i);
            //установка полученной информации
            setInformation(i);
        }
    }

    @SuppressLint("Recycle")
    private void getImagesAndDescriptions(int position){
        SQLiteDatabase mDb = mDBHelper.getWritableDatabase();
        Cursor userCursor = mDb.rawQuery("Select * from Formulaes", null);
        userCursor.moveToPosition(position);
        try {
            //получение описания
            DESCRIPTIONS.add(userCursor.getString(2));
            if(userCursor.getString(2) == null){
                DESCRIPTIONS.add(null);
            }
        }catch (Exception e){
            Log.i("Book", String.valueOf(e));
        }
        //получение картинок
        byte[] bytesImg = userCursor.getBlob(3);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytesImg, 0, bytesImg.length);
        if(bitmap != null) {
            IMAGES.add(bitmap);
        }
    }

    @SuppressLint("InflateParams")
    private void setInformation(int pos){
        View view1 = getLayoutInflater().inflate(R.layout.recycler_view_book_item, null, false);
        ImageView imageView = view1.findViewById(R.id.formulae);
        TextView tv = view1.findViewById(R.id.description);
        if(tv.getVisibility() == View.GONE) {
            tv.setVisibility(View.VISIBLE);
        }
        Bitmap bitmap = IMAGES.get(pos);
        int width = bitmap.getWidth() * 3;
        int height = bitmap.getHeight() * 3;
        //установка картинки
        imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, width, height, false));
        //проверка на наличие описания и установка описания
        if (DESCRIPTIONS.get(pos) == null){
            tv.setVisibility(View.GONE);
        }else {
            tv.setText(DESCRIPTIONS.get(pos));
        }
        linear.addView(view1);
    }

    @Override
    public void onClick(View v) {

    }
}
