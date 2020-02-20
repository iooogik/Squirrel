package iooogik.app.modelling;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.ALARM_SERVICE;



public class CheckList extends Fragment implements View.OnClickListener, NoteInterface {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    private View view;
    private ArrayList<String> Items = new ArrayList<>();
    private ArrayList<Boolean> Booleans = new ArrayList<>();
    private Calendar calendar = Calendar.getInstance();
    private EditText nameNote, shortNote;
    public CheckList() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_check, container, false);

        ImageButton addButton = view.findViewById(R.id.addItemCheck);
        addButton.setOnClickListener(this);

        ImageButton buttonTimeSet = view.findViewById(R.id.buttonShopAlarm);
        buttonTimeSet.setOnClickListener(this);

        FloatingActionButton back = view.findViewById(R.id.back);
        back.setOnClickListener(this);

        getPoints();

        return view;
    }


    @Override
    public int getBtnID(){
        Bundle arguments = this.getArguments();
        assert arguments != null;
        return arguments.getInt("button ID");
    }

    @Override
    public String getBtnName(){
        Bundle arguments = this.getArguments();
        assert arguments != null;
        return arguments.getString("button name");
    }

    @Override
    public void updFragment() {}

    @Override
    public void updData(String databaseName, String name, String note, String shortNote) {}


    private void getPoints(){
        /* БД ************************ */
        mDBHelper = new DatabaseHelper(getActivity());
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();
        nameNote = view.findViewById(R.id.editNameShopNote);
        shortNote = view.findViewById(R.id.shortShopNote);

        mDb = mDBHelper.getReadableDatabase();

        @SuppressLint("Recycle")
        Cursor userCursor = mDb.rawQuery("Select * from Notes", null);
        userCursor.moveToPosition(getBtnID());
        nameNote.setText(getBtnName());
        shortNote.setText(userCursor.getString(2));
        final String TEMP = userCursor.getString(7);
        String tempBool = userCursor.getString(6);

        if (TEMP != null && tempBool != null) {

            String[] tempArr = TEMP.split("\r\n|\r|\n");
            String[] tempArrBool = tempBool.split("\r\n|\r|\n");
            boolean[] booleans = new boolean[tempArrBool.length];

            for (int i = 0; i < tempArrBool.length; i++) {
                booleans[i] = Boolean.valueOf(tempArrBool[i]);
                Booleans.add(booleans[i]);

                Items.add(tempArr[i]);
                addCheck(booleans[i], tempArr[i]);
            }
        }
    }

    private void addCheck(boolean state, String nameCheck){

        LinearLayout linear = view.findViewById(R.id.markedScroll);
        @SuppressLint("InflateParams")
        View view2 = getLayoutInflater().inflate(R.layout.item_check, null);
        final CheckBox CHECK = view2.findViewById(R.id.checkBox);
        final EditText EDIT_SHOP_NAME = view.findViewById(R.id.editNameShopNote);
        CHECK.setChecked(state);
        CHECK.setText(nameCheck);

        CHECK.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int index = Items.indexOf(CHECK.getText().toString());
            Booleans.set(index, isChecked);
            StringBuilder sendBool = new StringBuilder();
            for (boolean aBoolean : Booleans) {
                sendBool.append(aBoolean).append("\n");
            }
            updShopNotes("Notes", EDIT_SHOP_NAME.getText().toString(),
                    sendBool.toString());
        });
        linear.addView(view2);
    }

    @Override
    public void updShopNotes(String databaseName, String name, String booleans){
        mDb = mDBHelper.getWritableDatabase();
        //код сохранения в бд
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("isChecked", booleans);
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy",
                Locale.getDefault());

        cv.put("date", dateFormat.format(currentDate));

        //обновление базы данных
        mDb.update(databaseName, cv, "_id=" + (getBtnID() + 1), null);
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.addItemCheck){
            final LinearLayout MAIN_LAYOUT  = new LinearLayout(getContext());
            final LinearLayout LAYOUT_1 = new LinearLayout(getContext());
            MAIN_LAYOUT.setOrientation(LinearLayout.VERTICAL);
            LAYOUT_1.setOrientation(LinearLayout.VERTICAL);
            //ввод названия заметки

            int padding = 70;

            MAIN_LAYOUT.setPadding(padding, padding, padding, padding);
            EditText namePoint = new EditText(getContext());
            namePoint.setTextColor(Color.BLACK);
            final Typeface TPF = Typeface.createFromAsset(getContext().getAssets(),
                    "rostelekom.otf");
            namePoint.setHint("Введите текст пункта");
            namePoint.setTypeface(TPF);
            namePoint.setTextSize(18);
            namePoint.setMinimumWidth(1500);
            LAYOUT_1.addView(namePoint);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            MAIN_LAYOUT.addView(LAYOUT_1);
            builder.setView(MAIN_LAYOUT);

            builder.setPositiveButton(Html.fromHtml
                            ("<font color='#7AB5FD'>Добавить</font>"),
                    (dialog, which) -> {
                        mDb = mDBHelper.getWritableDatabase();
                        ContentValues cv = new ContentValues();

                        Booleans.add(false);
                        Items.add(namePoint.getText().toString());

                        StringBuilder strBool = new StringBuilder();
                        StringBuilder strItems = new StringBuilder();

                        for (int i = 0; i < Booleans.size(); i++) {
                            strBool.append(Booleans.get(i)).append("\n");
                        }
                        for (int i = 0; i < Items.size(); i++) {
                            strItems.append(Items.get(i)).append("\n");
                        }

                        cv.put("isChecked", strBool.toString());
                        cv.put("points", strItems.toString());
                        //обновление базы данных
                        mDb.update("Notes", cv, "_id=" + (getBtnID() + 1),
                                null);
                        addCheck(false, namePoint.getText().toString());
                    });

            AlertDialog dlg = builder.create();

            dlg.show();

        } else if(v.getId() == R.id.buttonShopAlarm){

            alarmDialog(nameNote.getText().toString(), shortNote.getText().toString());

        } else if(v.getId() == R.id.back){
            FrameLayout frameLayout = Notes.VIEW.findViewById(R.id.SecondaryFrame);
            frameLayout.removeAllViews();
            frameLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void alarmDialog(final String TITLE, final String TEXT) {

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);


        DatePickerDialog dialog;
        final TimePickerDialog DIALOG_2;

        DIALOG_2 = new TimePickerDialog(view.getContext(), (timePicker, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            Intent notificationIntent = new Intent(view.getContext(),
                    NotificationReceiver.class);

            Bundle args = new Bundle();
            args.putInt("btnId", getBtnID());
            args.putString("btnName", getBtnName());
            args.putString("title", TITLE);
            args.putString("shortNote", TEXT);

            notificationIntent.putExtras(args);
            notificationIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),
                    1, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) Objects.requireNonNull(getContext()).
                    getSystemService(ALARM_SERVICE);


            assert alarmManager != null;
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(getContext(), "Уведомление установлено",
                    Toast.LENGTH_LONG).show();

        }, hours, minutes, true);

        dialog = new DatePickerDialog(
                view.getContext(),
                (view, year1, month1, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year1);
                    calendar.set(Calendar.MONTH, month1);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    DIALOG_2.show();
                },
                year, month, day);
        dialog.show();
    }
}
