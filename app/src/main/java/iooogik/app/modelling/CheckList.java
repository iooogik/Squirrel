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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;



public class CheckList extends Fragment implements View.OnClickListener, NoteInterface {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    private Cursor userCursor;
    private String[] tempArrBool;
    private boolean[] booleans;
    private String[] tempArr;
    private View view;
    private ArrayList<String> Items = new ArrayList<>();
    private ArrayList<Boolean> Booleans = new ArrayList<>();
    private Calendar calendar = Calendar.getInstance();
    private EditText nameNote, shortNote;
    public CheckList() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.shop_fragment,
                container, false);

        ImageButton addButton = view.findViewById(R.id.addItemCheck);
        addButton.setOnClickListener(this);

        ImageButton buttonTimeSet = view.findViewById(R.id.buttonShopAlarm);
        buttonTimeSet.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getPoints();
    }

    @Override
    public int getBtnID(){
        Bundle arguments = this.getArguments();
        assert arguments != null;
        return arguments.getInt("buttonID");
    }

    @Override
    public void updFragment() {

    }

    @Override
    public void updData(String databaseName, String name, String note, String shortNote) {}

    @Override
    public String getBtnName(){
        Bundle arguments = this.getArguments();
        assert arguments != null;
        return arguments.getString("button name");
    }


    private void getPoints(){
        /* БД ************************ */
        mDBHelper = new DatabaseHelper(getActivity());
        mDBHelper.openDataBase();
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }
        nameNote = view.findViewById(R.id.editNameShopNote);
        shortNote = view.findViewById(R.id.shortShopNote);

        mDb = mDBHelper.getReadableDatabase();

        userCursor =  mDb.rawQuery("Select * from Notes", null);
        userCursor.moveToPosition(getBtnID());
        nameNote.setText(getBtnName());
        shortNote.setText(userCursor.getString(2));

        final String temp = userCursor.getString(7);
        String tempBool = userCursor.getString(6);
        if (temp != null && tempBool != null) {

            tempArr = temp.split("\r\n|\r|\n");
            tempArrBool = tempBool.split("\r\n|\r|\n");
            booleans = new boolean[tempArrBool.length];

            for (int i = 0; i < tempArrBool.length; i++) {
                booleans[i] = Boolean.valueOf(tempArrBool[i]);
                Booleans.add(booleans[i]);
            }

            for (int i = 0; i < tempArr.length; i++) {
                Items.add(tempArr[i]);
                addCheck(booleans[i], tempArr[i]);
            }
        }
    }

    private void addCheck(boolean state, String nameCheck){

        LinearLayout linear = view.findViewById(R.id.shopScroll);
        View view2 = getLayoutInflater().inflate(R.layout.item_check, null);
        final CheckBox check = view2.findViewById(R.id.checkBox);
        final EditText tv = view.findViewById(R.id.editNameShopNote);
        check.setChecked(state);
        check.setText(nameCheck);

        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int index = Items.indexOf(check.getText().toString());
                Booleans.set(index, isChecked);
                StringBuilder sendBool = new StringBuilder();
                for (boolean aBoolean : Booleans) {
                    sendBool.append(String.valueOf(aBoolean) + "\n");
                }
                updShopNotes("Notes", tv.getText().toString(), sendBool.toString());
            }
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


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.addItemCheck){
            final LinearLayout mainLayout  = new LinearLayout(view.getContext());
            final LinearLayout layout1 = new LinearLayout(view.getContext());
            mainLayout.setOrientation(LinearLayout.VERTICAL);
            layout1.setOrientation(LinearLayout.VERTICAL);
            //ввод названия заметки
            final EditText nameNote = new EditText(view.getContext());

            int padding = 70;

            mainLayout.setPadding(padding, padding, padding, padding);

            nameNote.setTextColor(Color.BLACK);
            final Typeface tpf = Typeface.createFromAsset(view.getContext().getAssets(),
                    "rostelekom.otf");
            nameNote.setHint("Введите текст пункта");
            nameNote.setTypeface(tpf);
            nameNote.setTextSize(18);
            nameNote.setMinimumWidth(1500);
            nameNote.setTextColor(Color.WHITE);
            layout1.addView(nameNote);

            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

            mainLayout.addView(layout1);
            builder.setView(mainLayout);

            builder.setPositiveButton(Html.fromHtml
                            ("<font color='#7AB5FD'>Добавить</font>"),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDb = mDBHelper.getWritableDatabase();
                            ContentValues cv = new ContentValues();

                            Booleans.add(false);
                            Items.add(nameNote.getText().toString());

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
                            addCheck(false, nameNote.getText().toString());
                        }
                    });

            AlertDialog dlg = builder.create();
            dlg.setOnShowListener(new DialogInterface.OnShowListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onShow(DialogInterface dialog) {
                    Window v = ((AlertDialog)dialog).getWindow();
                    v.setBackgroundDrawableResource(R.drawable.alert_dialog_backgrond);
                    Button posButton = ((AlertDialog)dialog).
                            getButton(DialogInterface.BUTTON_POSITIVE);
                    posButton.setTypeface(tpf);
                    posButton.setTypeface(Typeface.DEFAULT_BOLD);
                    posButton.setTextColor(R.color.colorFont);
                }
            });
            dlg.show();
        } else if(v.getId() == R.id.buttonShopAlarm){

            alarmDialog(nameNote.getText().toString(), shortNote.getText().toString());
        }
    }

    @Override
    public void alarmDialog(final String title, final String text) {

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
                notificationIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),
                        1, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

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
}
