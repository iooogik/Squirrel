package com.example.squirrel;



import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.WriterException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


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

        ImageButton btnSave = view.findViewById(R.id.buttonSave);
        ImageButton btnShare = view.findViewById(R.id.buttonShare);
        btnSave.setOnClickListener(this);
        btnShare.setOnClickListener(this);

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

    private void share(){
        TextView name = getView().findViewById(R.id.editName);
        TextView note = getView().findViewById(R.id.editNote);
        TextView shortNote = getView().findViewById(R.id.shortNote);

        LinearLayout linearLayout = getView().findViewById(R.id.layout_img);
        String sendText;
        if(linearLayout.getVisibility() == View.VISIBLE){
            sendText = "[name]" + name.getText().toString() + "[/name]" +
                    "[note]" + note.getText().toString() + "[/note]" + "[shortNote]" +
                    shortNote.getText().toString() + "[/shortNote]" +
                    "[QR]" + shortNote.getText().toString() + "[/QR]";
        } else {
            sendText = "[name]" + name.getText().toString() + "[/name]" +
                    "[note]" + note.getText().toString() + "[/note]" + "[shortNote]" +
                    shortNote.getText().toString() + "[/shortNote]";
        }

        QR_Demo qr_demo = new QR_Demo();
        try {
            createDialog(qr_demo.encodeAsBitmap(sendText));
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("SetTextI18n")
    private void createDialog(Bitmap bitmap){

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LinearLayout mainLayout  = new LinearLayout(getActivity());
        LinearLayout layout1 = new LinearLayout(getActivity());

        mainLayout.setOrientation(LinearLayout.VERTICAL);
        layout1.setOrientation(LinearLayout.VERTICAL);

        int padding = 70;

        mainLayout.setPadding(padding, padding, padding, padding);

        ImageView imageView = new ImageView(getContext());
        imageView.setMinimumHeight(1000);
        imageView.setMinimumWidth(1000);
        imageView.setImageBitmap(bitmap);

        TextView tv = new TextView(getContext());
        tv.setMinHeight(25);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        tv.setText("Наведите второй телефон на QR-код, чтобы считать данные.");
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(18);
        tv.setMinHeight(15);

        layout1.addView(tv);
        layout1.addView(imageView);

        mainLayout.addView(layout1);
        builder.setView(mainLayout);

        builder.setCancelable(true);
        builder.setPositiveButton(Html.fromHtml
                        ("<font color='" + R.color.colorCursor + "'>Готово</font>"),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dlg = builder.create();
        dlg.show();
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
        if(view.getId() == R.id.buttonSave){

            String dataName = "Notes";
            EditText name = Objects.requireNonNull(getActivity()).findViewById(R.id.editName);
            EditText note = getActivity().findViewById(R.id.editNote);
            EditText shortNote = getActivity().findViewById(R.id.shortNote);

            updDatabase(dataName, name.getText().toString(), note.getText().toString(),
                    shortNote.getText().toString());

        } else if(view.getId() == R.id.buttonShare){
            share();
        }
    }

}
