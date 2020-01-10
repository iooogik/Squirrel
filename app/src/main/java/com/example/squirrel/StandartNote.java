package com.example.squirrel;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.google.zxing.WriterException;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.ALARM_SERVICE;


public class StandartNote extends Fragment implements View.OnClickListener {

    public StandartNote(){}

    public static View view;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    private Calendar calendar = Calendar.getInstance();
    private EditText name;
    private EditText shortNote;
    private EditText note;

    private Cursor userCursor;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = inflater.inflate(R.layout.standart_note,
                container, false);

        ImageButton btnSave = view.findViewById(R.id.buttonSave);
        ImageButton btnShare = view.findViewById(R.id.buttonShare);
        ImageButton btnAlarm = view.findViewById(R.id.buttonAlarm);
        btnSave.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnAlarm.setOnClickListener(this);


        final EditText name = view.findViewById(R.id.editName);
        final EditText shortNote = view.findViewById(R.id.shortNote);

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                TextView tv = view.findViewById(R.id.nameWarn);
                if(name.getText().toString().length() > 30){
                    if(tv.getVisibility() != View.VISIBLE) {
                        tv.setVisibility(View.VISIBLE);
                    }
                } else {
                    if(tv.getVisibility() != View.GONE) {
                        tv.setVisibility(View.GONE);
                    }
                }
            }
        });

        shortNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                TextView tv = view.findViewById(R.id.shortNoteWarn);
                if(shortNote.getText().toString().length() > 100){
                    if(tv.getVisibility() != View.VISIBLE) {
                        tv.setVisibility(View.VISIBLE);
                    }
                } else {
                    if(tv.getVisibility() != View.GONE) {
                        tv.setVisibility(View.GONE);
                    }
                }
            }
        });
        updFragment();
        return view;
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
        name = view.findViewById(R.id.editName);
        note = view.findViewById(R.id.editNote);
        shortNote = view.findViewById(R.id.shortNote);

        mDb = mDBHelper.getReadableDatabase();

        userCursor =  mDb.rawQuery("Select * from Notes", null);

        userCursor.moveToPosition(getBtnID());

        name.setText(getBtnName());
        shortNote.setText(userCursor.getString(2));
        note.setText(userCursor.getString(3));

        ImageView img = view.findViewById(R.id.qr_view);
        LinearLayout linearLayout = view.findViewById(R.id.layout_img);

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
        mDb.update(databaseName, cv, "_id =" + (getBtnID() + 1), null);
    }

    private void share(){
        TextView name = getView().findViewById(R.id.editName);
        TextView note = getView().findViewById(R.id.editNote);
        TextView shortNote = getView().findViewById(R.id.shortNote);

        LinearLayout linearLayout = getView().findViewById(R.id.layout_img);
        String sendText;
        if(linearLayout.getVisibility() == View.VISIBLE){
            sendText = name.getText().toString() + "[/name]" +
                    note.getText().toString() + "[/note]" +
                    shortNote.getText().toString() + "[/shortNote]"
                    + shortNote.getText().toString() + "[/QR]";
        } else {
            sendText = name.getText().toString() + "[/name]" +
                    note.getText().toString() + "[/note]" +
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

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

    private void alarmDialog(final String title, final String text){

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);


        DatePickerDialog dialog;
        final TimePickerDialog dialog2;

        dialog2 = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                Intent notificationIntent = new Intent(view.getContext(),
                        NotificationReceiver.class);

                Bundle args = new Bundle();
                args.putInt("btnId", getBtnID());
                args.putString("btnName", getBtnName());
                args.putString("title", title);
                args.putString("shortNote", text);

                notificationIntent.putExtras(args);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),
                        1, notificationIntent,
                        0);

                AlarmManager alarmManager = (AlarmManager) getContext().
                        getSystemService(ALARM_SERVICE);



                alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(), pendingIntent);
                Toast.makeText(getContext(), "Уведомление установлено",
                        Toast.LENGTH_LONG).show();

            }

        }, hours, minutes, true);

        dialog = new DatePickerDialog(
                view.getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        dialog2.show();
                    }
                },
                year, month, day);
        dialog.show();
    }

    @Override
    public void onClick(final View view) {

        final EditText name = Objects.requireNonNull(getActivity()).findViewById(R.id.editName);
        final EditText note = getActivity().findViewById(R.id.editNote);
        EditText shortNote = getActivity().findViewById(R.id.shortNote);

        String nameNote = name.getText().toString();
        String Note = note.getText().toString();
        String shortnote = shortNote.getText().toString();

        if(view.getId() == R.id.buttonSave){

            String dataName = "Notes";
            updDatabase(dataName, nameNote, Note, shortnote);

            MainActivity.standartItems.set(getBtnID(), nameNote);
            MainActivity.adapterStndrtList.notifyDataSetChanged();

            // Скрываем клавиатуру при открытии Navigation Drawer
            try {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().
                        getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(Objects.
                            requireNonNull(getActivity().getCurrentFocus()).
                            getWindowToken(), 0);
                }
            } catch (Exception e){
                System.out.println(e);
            }

            Toast.makeText(getContext(), "Сохранено", Toast.LENGTH_LONG).show();

        } else if(view.getId() == R.id.buttonShare){
            if(note.getText().toString().length() <= 300) {
                    share();
                }
            else {
                note.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        TextView tv = view.findViewById(R.id.noteWarn);
                        if(note.getText().toString().length() > 300){
                            if(tv.getVisibility() != View.VISIBLE) {
                                tv.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if(tv.getVisibility() != View.GONE) {
                                tv.setVisibility(View.GONE);
                            }
                        }
                    }
                });


            }
        } else if (view.getId() == R.id.buttonAlarm){
            alarmDialog(nameNote, shortnote);
        }
    }
}


