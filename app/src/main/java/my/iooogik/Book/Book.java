package my.iooogik.Book;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

    private View view;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    private Cursor userCursor;
    LinearLayout linear;
    ArrayList<Bitmap> IMAGES;
    ArrayList<String> DESCRIPTION;
    LayoutInflater layoutInflater;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_book, container, false);
        layoutInflater = inflater;
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
        mDb = mDBHelper.getWritableDatabase();
        userCursor = mDb.rawQuery("Select * from Formulaes", null);
        userCursor.moveToPosition(position);
        try {
            DESCRIPTION.add(userCursor.getString(2));
            if(userCursor.getString(2) == null){
                DESCRIPTION.add("");
            }
        }catch (Exception e){
            System.out.println(e);
        }
        byte[] bytesImg = userCursor.getBlob(3);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytesImg, 0, bytesImg.length);
        if(bitmap != null) {
            IMAGES.add(bitmap);
        }
    }

    private void setInformation(int pos){
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
