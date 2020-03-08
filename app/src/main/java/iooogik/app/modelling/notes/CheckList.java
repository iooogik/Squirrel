package iooogik.app.modelling.notes;


import android.annotation.SuppressLint;
import android.app.Activity;
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

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.filament.Material;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import iooogik.app.modelling.Database;
import iooogik.app.modelling.MainActivity;
import iooogik.app.modelling.NotificationReceiver;
import iooogik.app.modelling.R;
import iooogik.app.modelling.notes.NoteInterface;

import static android.content.Context.ALARM_SERVICE;



public class CheckList extends Fragment implements View.OnClickListener, NoteInterface {
    //переменная для работы с бд
    private Database mDBHelper;
    private SQLiteDatabase mDb;
    private View view;
    //список с элементами чек-листа
    private ArrayList<String> Items = new ArrayList<>();
    //список со значениями чек-листа
    private ArrayList<Boolean> Booleans = new ArrayList<>();
    //"Календарь" для получения даты от пользователя
    private Calendar calendar = Calendar.getInstance();
    private EditText nameNote, shortNote;

    public CheckList() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_check, container, false);
        //инициализация кнопок
        ImageButton buttonTimeSet = view.findViewById(R.id.buttonShopAlarm);
        buttonTimeSet.setOnClickListener(this);
        ImageButton buttonSave = view.findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(this);

        Notes.fab.setImageResource(R.drawable.baseline_add_white_24dp);
        Notes.fab.setVisibility(View.VISIBLE);
        Notes.fab.setOnClickListener(this);

        //получение элементов чек-лсита
        getPoints();

        return view;
    }

    @Override
    public int getButtonID(){
        Bundle arguments = this.getArguments();
        assert arguments != null;
        return arguments.getInt("button ID");
    }

    @Override
    public String getButtonName(){return null;}

    @Override
    public void updateFragment() {}

    @Override
    public void updateData(String databaseName, String name, String note, String shortNote) {
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
        mDb.update(databaseName, cv, "_id =" + (getButtonID()), null);

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
            Log.i("StandartNotes", String.valueOf(e));
        }

        Toast.makeText(getContext(), "Сохранено", Toast.LENGTH_SHORT).show();
    }


    private void getPoints(){
        //"открытие" бд
        mDBHelper = new Database(getActivity());
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();
        nameNote = view.findViewById(R.id.editNameShopNote);
        shortNote = view.findViewById(R.id.editNameShortShopNote);

        mDb = mDBHelper.getReadableDatabase();
        Cursor userCursor = mDb.rawQuery("Select * from Notes", null);
        userCursor.moveToPosition(getButtonID() - 1);
        nameNote.setText(userCursor.getString(userCursor.getColumnIndex("name")));
        shortNote.setText(userCursor.getString(userCursor.getColumnIndex("shortName")));
        final String TEMP = userCursor.getString(userCursor.getColumnIndex("points"));
        String tempBool = userCursor.getString(userCursor.getColumnIndex("isChecked"));

        try {
            Items.clear();
            Booleans.clear();
        } catch (Exception e){
            Log.i("getItems", String.valueOf(e));
        }
        LinearLayout linear = view.findViewById(R.id.markedScroll);
        linear.removeAllViews();

        if (TEMP != null && tempBool != null) {
            //"делим" полученный текст и добавляем в соответствующие списки
            String[] tempArr = TEMP.split("\r\n|\r|\n");
            String[] tempArrBool = tempBool.split("\r\n|\r|\n");
            boolean[] booleans = new boolean[tempArrBool.length];

            for (int i = 0; i < tempArrBool.length; i++) {
                booleans[i] = Boolean.valueOf(tempArrBool[i]);
                Booleans.add(booleans[i]);

                Items.add(tempArr[i]);
                addCheck(booleans[i], tempArr[i]);
            }
            isCompleted(!Booleans.contains(false));
        }
    }

    private void isCompleted(Boolean bool){
        ContentValues cv = new ContentValues();
        if(bool){
            cv.put("isCompleted", 1);
        }else {
            cv.put("isCompleted", 0);
        }

        //обновление базы данных
        mDb.update("Notes", cv, "_id=" + (getButtonID()), null);

        Notes.NOTES_ADAPTER.notifyDataSetChanged();
    }

    private void addCheck(boolean state, String nameCheck){
        //"слушатель" для нажатого элемента списка
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
            updateShopNotes("Notes", EDIT_SHOP_NAME.getText().toString(),
                    sendBool.toString());
            isCompleted(!Booleans.contains(false));
        });

        CHECK.setOnLongClickListener(v -> {

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            LinearLayout linearLayout = new LinearLayout(getContext());
            View view1 = getLayoutInflater().inflate(R.layout.edit_text, null, false);
            TextInputEditText editText = view1.findViewById(R.id.edit_text);
            TextInputLayout textInputLayout = view1.findViewById(R.id.text_input_layout);
            textInputLayout.setHint("Введите новое имя");
            linearLayout.addView(view1);
            builder.setView(linearLayout);
            builder.setPositiveButton("Изменить", (dialog, which) -> {
                mDb = mDBHelper.getWritableDatabase();
                int id = Items.indexOf(CHECK.getText());
                Items.set(id, editText.getText().toString());
                ContentValues cv = new ContentValues();
                StringBuilder stringBuilder = new StringBuilder();
                for(String item: Items) {
                    stringBuilder.append(item).append("\n");
                }
                cv.put("points", stringBuilder.toString());

                mDb.update("Notes", cv, "_id=" + getButtonID(), null);
                getPoints();

            });

            builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
            builder.create().show();
            return true;
        });

        linear.addView(view2);
    }

    @Override
    public void updateShopNotes(String databaseName, String name, String booleans){
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
        mDb.update(databaseName, cv, "_id=" + (getButtonID()), null);
    }

    @Override
    public void alarmDialog(final String TITLE, final String TEXT) {
        //создание уведомления
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
            args.putInt("btnId", getButtonID());
            args.putString("btnName", getButtonName());
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

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.buttonShopAlarm){
            //добавление уведомления
            alarmDialog(nameNote.getText().toString(), shortNote.getText().toString());
        }
        else if (v.getId() == R.id.fab){
            //добавление элемента
            final LinearLayout MAIN_LAYOUT  = new LinearLayout(getContext());
            final LinearLayout LAYOUT_1 = new LinearLayout(getContext());
            MAIN_LAYOUT.setOrientation(LinearLayout.VERTICAL);
            LAYOUT_1.setOrientation(LinearLayout.VERTICAL);
            //ввод названия заметки

            EditText namePoint = new EditText(getContext());

            namePoint.setHint("Введите текст пункта");
            LAYOUT_1.addView(namePoint);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            MAIN_LAYOUT.addView(LAYOUT_1);
            builder.setView(MAIN_LAYOUT);

            builder.setPositiveButton("Добавить",
                    (dialog, which) -> {
                if(!namePoint.getText().toString().isEmpty()) {
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
                    mDb.update("Notes", cv, "_id=" + (getButtonID()),
                            null);
                    addCheck(false, namePoint.getText().toString());
                } else {
                    Toast.makeText(getContext(),
                            "Что-то пошло не так. Проверьте, пожалуйста, название пункта.",
                            Toast.LENGTH_SHORT).show();
                }
                    });

            AlertDialog dlg = builder.create();

            dlg.show();

        } else if(v.getId() == R.id.buttonSave){
            updateData("Notes", nameNote.getText().toString(),
                    null, shortNote.getText().toString());
        }
    }
}
