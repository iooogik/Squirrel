package iooogik.app.modelling;


import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Book extends Fragment {


    public Book() {}

    private DatabaseHelper mDBHelper;
    private LinearLayout linear;
    private ArrayList<Bitmap> IMAGES;
    private ArrayList<String> DESCRIPTION;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);
        linear = view.findViewById(R.id.scroll);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mDBHelper = new DatabaseHelper(getActivity());
        mDBHelper.openDataBase();
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        IMAGES = new ArrayList<>();
        DESCRIPTION = new ArrayList<>();

        getInformation();
    }

    private void getInformation(){
        for (int i = 0; i < 36; i++) {
            getImagesAndDescriptions(i);
            setInformation(i);
        }

    }

    private void getImagesAndDescriptions(int position){
        SQLiteDatabase mDb = mDBHelper.getWritableDatabase();
        @SuppressLint("Recycle")
        Cursor userCursor = mDb.rawQuery(String.valueOf(R.string.SELECT_FROM_NOTES),
                null);
        userCursor.moveToPosition(position);
        try {
            DESCRIPTION.add(userCursor.getString(2));
            if(userCursor.getString(2) == null){
                DESCRIPTION.add("");
            }
        }catch (Exception e){
            Log.i("Book", String.valueOf(e));
        }
        byte[] bytesImg = userCursor.getBlob(3);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytesImg, 0, bytesImg.length);
        if(bitmap != null) {
            IMAGES.add(bitmap);
        }
    }

    private void setInformation(int pos){
        @SuppressLint("InflateParams")
        View view1 = getLayoutInflater().inflate(R.layout.book_item, null, false);
        FrameLayout frameLayout = view1.findViewById(R.id.frame_formulae);
        ImageView imageView = frameLayout.findViewById(R.id.formulae);
        TextView tv = frameLayout.findViewById(R.id.description);
        Bitmap bitmap = IMAGES.get(pos);
        int width = bitmap.getWidth() * 3;
        int height = bitmap.getHeight() * 3;

        imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, width, height, false));
        tv.setText(DESCRIPTION.get(pos));
        linear.addView(view1);
    }

}
