package iooojik.app.klass.notes;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import iooojik.app.klass.Database;
import iooojik.app.klass.NotificationReceiver;
import iooojik.app.klass.R;

import static android.content.Context.ALARM_SERVICE;



public class CheckList extends Fragment implements View.OnClickListener {
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
        view = inflater.inflate(R.layout.fragment_check_list, container, false);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);

        fab.setImageResource(R.drawable.baseline_add_24);
        fab.show();
        fab.setOnClickListener(this);
        setHasOptionsMenu(true);
        //получение элементов чек-лсита
        getPoints();

        return view;
    }

    private int getButtonID(){
        //получение id нажатой "кнопки"
        Bundle arguments = this.getArguments();
        assert arguments != null;
        return arguments.getInt("button ID");
    }

    private String getButtonName(){return null;}

    private void updateData(String name, String shortNote) {
        mDb = mDBHelper.getWritableDatabase();
        setHasOptionsMenu(true);
        //код сохранения в бд
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("shortName", shortNote);
        //получение даты
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy",
                Locale.getDefault());
        cv.put("date", dateFormat.format(currentDate));

        //обновление базы данных
        mDb.update("Notes", cv, "_id =" + (getButtonID()), null);

        // Скрываем клавиатуру при открытии Navigation Drawer
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().
                    getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e){
            Log.i("StandartNotes", String.valueOf(e));
        }
        Snackbar.make(view, "Сохранено", Snackbar.LENGTH_LONG).show();
    }

    @SuppressLint("Recycle")
    private void getPoints(){
        //"открытие" бд
        mDBHelper = new Database(getActivity());
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();
        nameNote = view.findViewById(R.id.editNameShopNote);
        shortNote = view.findViewById(R.id.editNameShortShopNote);
        mDb = mDBHelper.getReadableDatabase();

        final String TEMP;
        String tempBool;
        Cursor userCursor = mDb.rawQuery("Select * from Notes WHERE _id=?", new String[]{String.valueOf(getButtonID())});

        userCursor.moveToFirst();
        nameNote.setText(userCursor.getString(userCursor.getColumnIndex("name")));
        String shortName = userCursor.getString(userCursor.getColumnIndex("shortName"));
        if (!shortName.isEmpty() && !shortName.toString().equals("null"))
        shortNote.setText(shortName);

        TEMP = userCursor.getString(userCursor.getColumnIndex("points"));
        tempBool = userCursor.getString(userCursor.getColumnIndex("isChecked"));


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
                booleans[i] = Boolean.parseBoolean(tempArrBool[i]);
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

    }

    private void addCheck(boolean state, String nameCheck){
        //"слушатель" для нажатого элемента списка
        LinearLayout linear = view.findViewById(R.id.markedScroll);
        @SuppressLint("InflateParams") View view2 = getLayoutInflater().inflate(R.layout.item_check, null);
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
            updateShopNotes(EDIT_SHOP_NAME.getText().toString(),
                    sendBool.toString());
            isCompleted(!Booleans.contains(false));
        });

        CHECK.setOnLongClickListener(v -> {
            //слушатель для изменения названия пункта

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            LinearLayout linearLayout = new LinearLayout(getContext());
            @SuppressLint("InflateParams") View view1 = getLayoutInflater().inflate(R.layout.edit_text, null, false);
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

    private void updateShopNotes(String name, String booleans){
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
        mDb.update("Notes", cv, "_id=" + (getButtonID()), null);
    }

    private void alarmDialog(final String title, final String text) {
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
            args.putString("title", title);
            args.putString("shortNote", text);

            notificationIntent.putExtras(args);
            notificationIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),
                    1, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);


            assert alarmManager != null;
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), pendingIntent);

            Snackbar.make(view, "Уведомление установлено", Snackbar.LENGTH_LONG).show();

            mDb = mDBHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("isNotifSet", 1);
            mDb.update("Notes", contentValues, "_id=" + getButtonID(), null);

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
    @SuppressLint("InflateParams")
    public void onClick(View v) {
        if (v.getId() == R.id.fab){
            //добавление элемента
            final LinearLayout MAIN_LAYOUT  = new LinearLayout(getContext());
            final LinearLayout LAYOUT_1 = new LinearLayout(getContext());
            MAIN_LAYOUT.setOrientation(LinearLayout.VERTICAL);
            LAYOUT_1.setOrientation(LinearLayout.VERTICAL);
            //ввод названия заметки
            View view1 = getLayoutInflater().inflate(R.layout.edit_text, null, false);
            TextInputEditText editText = view1.findViewById(R.id.edit_text);
            TextInputLayout textInputLayout = view1.findViewById(R.id.text_input_layout);
            textInputLayout.setHelperTextEnabled(false);
            textInputLayout.setCounterEnabled(false);
            textInputLayout.setHint("Введите новый пункт");

            LAYOUT_1.addView(view1);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            MAIN_LAYOUT.addView(LAYOUT_1);
            builder.setView(MAIN_LAYOUT);

            builder.setPositiveButton("Добавить",
                    (dialog, which) -> {
                if(!editText.getText().toString().isEmpty()) {
                    mDb = mDBHelper.getWritableDatabase();
                    ContentValues cv = new ContentValues();

                    Booleans.add(false);
                    Items.add(editText.getText().toString());

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
                    addCheck(false, editText.getText().toString());
                } else {
                    Snackbar.make(view, "Что-то пошло не так. Проверьте, пожалуйста, название пункта.",
                            Snackbar.LENGTH_LONG).show();
                }
                    });

            AlertDialog dlg = builder.create();

            dlg.show();

        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_check_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_notif:
                alarmDialog(nameNote.getText().toString(), shortNote.getText().toString());
                return true;
            case R.id.action_save:
                updateData(nameNote.getText().toString(), shortNote.getText().toString());
                return true;
        }
        return false;
    }
}
