package iooogik.app.modelling;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class ScrollingArticle extends Fragment implements View.OnClickListener{

    private Cursor userCursor;
    View view;

    public ScrollingArticle(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_scrolling_article, container ,false);

        FloatingActionButton back = view.findViewById(R.id.back);

        back.setOnClickListener(this);



        Bundle args = this.getArguments();
        assert args != null;
        int id = args.getInt("_id");

        DatabaseHelper mDBHelper = new DatabaseHelper(getContext());
        mDBHelper.openDataBase();

        mDBHelper.updateDataBase();

        SQLiteDatabase mDb = mDBHelper.getReadableDatabase();
        userCursor =  mDb.rawQuery("Select * from Planets", null);

        userCursor.moveToPosition(id);

        TextView textView = view.findViewById(R.id.article_text);

        textView.setText(Html.fromHtml(getEditedText()));
        ImageView imageView = view.findViewById(R.id.sc_back);

        Bitmap bitmap;

        byte[] bytesImg = userCursor.getBlob(userCursor.getColumnIndex("images"));
        bitmap =  BitmapFactory.decodeByteArray(bytesImg, 0, bytesImg.length);

        imageView.setImageBitmap(bitmap);

        return view;
    }

    private String getEditedText(){
        String tempText = userCursor.getString(userCursor.getColumnIndex("article"));
        StringBuilder stringBuilder = new StringBuilder();
        try {
            for (int i = 0; i < tempText.length(); i++) {
                stringBuilder.append(tempText.charAt(i));
            }
        }catch (Exception e){
            Log.i("scrollingArticle", String.valueOf(e));
        }


        return stringBuilder.toString();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.back){
            showPlanetInfo(new Planets());
        }
    }

    private void showPlanetInfo(Fragment fragment){


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
        addTransaction.add(R.id.Mainframe, fragment,
                "mainFrame").commitAllowingStateLoss();

        Objects.requireNonNull(getView()).setVisibility(View.GONE);
    }

}
