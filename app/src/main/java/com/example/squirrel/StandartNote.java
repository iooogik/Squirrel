package com.example.squirrel;



import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class StandartNote extends Fragment implements View.OnClickListener {

    public StandartNote(){}

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    private Cursor userCursor;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.standart_note,
                container, false);

        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        updFragment();
    }

    private void updFragment(){
        /* БД ************************ */
        mDBHelper = new DatabaseHelper(getActivity());
        mDBHelper.openDataBase();
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }
        TextView name = getActivity().findViewById(R.id.editName);
        TextView note = getActivity().findViewById(R.id.editNote);
        TextView shortNote = getActivity().findViewById(R.id.shortNote);

        mDb = mDBHelper.getReadableDatabase();

        userCursor =  mDb.rawQuery("Select * from Notes", null);

        userCursor.moveToPosition(getBtnID());

        name.setText(getBtnName());
        shortNote.setText(userCursor.getString(2));
        note.setText(userCursor.getString(3));

        ImageView img = getActivity().findViewById(R.id.qr_view);
        LinearLayout linearLayout = getActivity().findViewById(R.id.layout_img);

        if(!userCursor.isNull(5)){
            linearLayout.setVisibility(View.VISIBLE);
            img.setImageBitmap(setImage());
            shortNote.setText("Расшифровка: " + shortNote.getText().toString());
            shortNote.setEnabled(false);
        }
    }

    private Bitmap setImage(){
        mDb = mDBHelper.getWritableDatabase();
        userCursor = mDb.rawQuery("Select * from Notes", null);

        userCursor.moveToPosition(getBtnID());
        byte[] bytesImg = userCursor.getBlob(5);
        return BitmapFactory.decodeByteArray(bytesImg, 0, bytesImg.length);
    }

    private int getBtnID(){
        Bundle arguments = this.getArguments();
        assert arguments != null;
        return arguments.getInt("buttonID");
    }

    private String getBtnName(){
        Bundle arguments = this.getArguments();
        assert arguments != null;
        return arguments.getString("button name");
    }

    private void updDatabase(String databaseName, String name, String note, String shortNote){
        mDb = mDBHelper.getWritableDatabase();

        //код сохранения в бд
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("shortName", shortNote);
        cv.put("text", note);

        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy",
                Locale.getDefault());

        cv.put("date", dateFormat.format(currentDate));

        //обновление базы данных
        mDb.update(databaseName, cv, "id =" + (getBtnID() + 1), null);
    }

    private void share(View view){
        TextView name = view.findViewById(R.id.editName);
        TextView note = view.findViewById(R.id.editNote);
        TextView shortNote = view.findViewById(R.id.shortNote);

        LinearLayout linearLayout = view.findViewById(R.id.layout_img);
        ImageView img = view.findViewById(R.id.qr_view);
        String IMGbytes = null;
        if(linearLayout.getVisibility() == View.VISIBLE){
            mDb = mDBHelper.getWritableDatabase();
            userCursor = mDb.rawQuery("Select * from Notes", null);
            userCursor.moveToPosition(getBtnID());
            byte[] bytesImg = userCursor.getBlob(5);
            IMGbytes = new BigInteger(1, bytesImg).toString();
        }

        String codedName = stringToBinary(name.getText().toString());

        byte[] tempMessage = name.getText().toString().getBytes(StandardCharsets.UTF_8);


        System.out.println(codedName);


        String messageQR;
        if(IMGbytes != null){
            messageQR = "[name]" + name.getText().toString() + "[/name]" +
                    "[note]" + note.getText().toString() + "[/note]" + "[shortNote]" +
                    shortNote.getText().toString() + "[/shortNote]" + "[img]" + IMGbytes + "[/img]";
        } else {
            messageQR = "[name]" + name.getText().toString() + "[/name]" +
                    "[note]" + note.getText().toString() + "[/note]" + "[shortNote]" +
                    shortNote.getText().toString() + "[/shortNote]";
        }
        /*
        System.out.println(messageQR);
        byte[] tempMessage = messageQR.getBytes(StandardCharsets.UTF_8);
        System.out.println(tempMessage.length);
        for (int i = 0; i < tempMessage.length; i++){
            System.out.println(tempMessage[i]);
        }

        //System.out.println(Arrays.toString(tempMessage));
        //System.out.println(stringToBinary(messageQR));
        //System.out.println(Integer.parseInt(stringToBinary(messageQR), 2));
        */


        Toast.makeText(view.getContext(), "удача", Toast.LENGTH_LONG).show();


    }

    private String stringToBinary(String s) {

        StringBuilder sent = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            sent.append(Integer.toBinaryString(c));
        }
        return sent.toString();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.save){
            String dataName = "Notes";
            TextView name = view.findViewById(R.id.editName);
            TextView note = view.findViewById(R.id.editNote);
            TextView shortNote = view.findViewById(R.id.shortNote);

            updDatabase(dataName, name.getText().toString(),
                    note.getText().toString(), shortNote.getText().toString());
        } else if(view.getId() == R.id.buttonShare){
            share(view);
        }
    }

}
