package iooojik.app.klass.notes;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import iooojik.app.klass.Database;
import iooojik.app.klass.NotificationReceiver;
import iooojik.app.klass.R;
import iooojik.app.klass.qr.BarcodeCaptureActivity;

import static android.content.Context.ALARM_SERVICE;


public class StandartNote extends Fragment implements View.OnClickListener, NoteInterface {
    // пустой контсруктор
    public StandartNote(){}

    private View view;
    // переменные для работы с бд
    private Database mDBHelper;
    private SQLiteDatabase mDb;
    private Cursor userCursor;
    // "Календарь" для получения времени
    private Calendar calendar;
    private Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // получаем кнопки и "ставим" на них слушатели

        view = inflater.inflate(R.layout.fragment_standart_note,
                container, false);
        context = view.getContext();

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();

        ImageButton btnSave = view.findViewById(R.id.buttonSave);
        ImageButton btnShare = view.findViewById(R.id.buttonShare);
        ImageButton btnAlarm = view.findViewById(R.id.buttonAlarm);
        ImageButton btnQR = view.findViewById(R.id.buttonQR);

        btnSave.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnAlarm.setOnClickListener(this);
        btnQR.setOnClickListener(this);
        // получаем текущее состояние "календаря"
        calendar = Calendar.getInstance();
        updateFragment();

        return view;
    }

    @Nullable
    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public void onResume() {
        updateFragment();
        super.onResume();
    }

    @Override
    public void updateFragment(){
        /*
          Обновляем содержимое фрагмента
          "Открываем" бд
          получаем её содержимое
         */
        mDBHelper = new Database(getActivity());
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();
        EditText name = view.findViewById(R.id.editName);
        EditText note = view.findViewById(R.id.editNote);
        EditText shortNote = view.findViewById(R.id.shortNote);
        TextView decodedQR = view.findViewById(R.id.decodedqr);

        mDb = mDBHelper.getReadableDatabase();

        userCursor =  mDb.rawQuery("Select * from Notes WHERE _id=?", new String[]{String.valueOf(getButtonID())});
        userCursor.moveToFirst();
        // перемещаем курсор

        // устанавливаем дынные

        name.setText(userCursor.getString(userCursor.getColumnIndex("name")));
        shortNote.setText(userCursor.getString((userCursor.getColumnIndex("shortName"))));
        note.setText(userCursor.getString(userCursor.getColumnIndex("text")));


        ImageView img = view.findViewById(R.id.qr_view);
        LinearLayout linearLayout = view.findViewById(R.id.layout_img);

        if(!userCursor.isNull(userCursor.getColumnIndex("image"))){
            linearLayout.setVisibility(View.VISIBLE);
            img.setImageBitmap(setImage());
            img.setOnLongClickListener(v -> {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                LinearLayout linearLayout1 = new LinearLayout(getContext());

                TextView textView = new TextView(getContext());
                textView.setText(R.string.deleteQR);
                linearLayout1.setOrientation(LinearLayout.VERTICAL);
                linearLayout1.addView(textView);
                builder.setView(linearLayout1);
                builder.setPositiveButton("Да", (dialog, which) -> {
                    mDb = mDBHelper.getWritableDatabase();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("image", (byte[]) null);
                    mDb.update("Notes", contentValues, "_id=" + getButtonID(), null);
                    linearLayout.setVisibility(View.GONE);
                });
                builder.setNegativeButton("Нет", (dialog, which) -> dialog.cancel());
                builder.create().show();
                return true;
            });
            decodedQR.setText(userCursor.getString(userCursor.getColumnIndex("decodeQR")));
        }
    }

    @Override
    public void updateShopNotes(String databaseName, String name, String booleans) {

    }

    private Bitmap setImage(){
        // устанавливаем картинку на фрагмент
        mDb = mDBHelper.getWritableDatabase();
        userCursor =  mDb.rawQuery("Select * from Notes WHERE _id=?", new String[]{String.valueOf(getButtonID())});
        userCursor.moveToFirst();
        byte[] bytesImg = userCursor.getBlob(userCursor.getColumnIndex("image"));
        return BitmapFactory.decodeByteArray(bytesImg, 0, bytesImg.length);
    }

    @Override
    public int getButtonID(){
        // получаем id заметки
        Bundle arguments = this.getArguments();
        assert arguments != null;
        return arguments.getInt("button ID");
    }

    @Override
    public String getButtonName(){
        // получаем название заметки
        Bundle arguments = this.getArguments();
        assert arguments != null;
        return arguments.getString("button name");
    }

    @Override
    public void updateData(String databaseName, String name, String note, String shortNote){
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
    }

    private void share(){
        // Делимся заметкой
        TextView name = Objects.requireNonNull(getView()).findViewById(R.id.editName);
        TextView note = getView().findViewById(R.id.editNote);
        TextView shortNote = getView().findViewById(R.id.shortNote);

        LinearLayout linearLayout = getView().findViewById(R.id.layout_img);
        String sendText;
        if(linearLayout.getVisibility() == View.VISIBLE){
            sendText = name.getText().toString() + "[/name]" +
                    note.getText().toString() + "[/recycler_view_item_note]" +
                    shortNote.getText().toString() + "[/shortNote]"
                    + shortNote.getText().toString() + "[/QR]";
        } else {
            sendText = name.getText().toString() + "[/name]" +
                    note.getText().toString() + "[/recycler_view_item_note]" +
                    shortNote.getText().toString() + "[/shortNote]";
        }


    }

    @Override
    public void alarmDialog(final String title, final String text){
        // напоминание
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);


        DatePickerDialog dialog;
        final TimePickerDialog dialog2;

        dialog2 = new TimePickerDialog(context, (timePicker, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            Intent notificationIntent = new Intent(context, NotificationReceiver.class);

            Bundle args = new Bundle();
            args.putInt("btnId", getButtonID());
            args.putString("btnName", title);
            args.putString("title", title);
            args.putString("shortNote", text);

            notificationIntent.putExtras(args);
            notificationIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);


            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0,
                    notificationIntent, 0);

            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                    10000, pendingIntent);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Snackbar.make(view, "Уведомление установлено", Snackbar.LENGTH_LONG).show();

            mDb = mDBHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("isNotifSet", 1);
            mDb.update("Notes", contentValues, "_id=" + getButtonID(), null);
            //Notes.NOTES_ADAPTER.notifyDataSetChanged();

        }, hours, minutes, true);

        dialog = new DatePickerDialog(
                context,
                (view, year1, month1, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year1);
                    calendar.set(Calendar.MONTH, month1);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    dialog2.show();
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
        String shortText = shortNote.getText().toString();

        if(view.getId() == R.id.buttonSave){

            String dataName = "Notes";
            updateData(dataName, nameNote, Note, shortText);


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

            Snackbar.make(view, "Сохранено", Snackbar.LENGTH_LONG).show();

        } else if(view.getId() == R.id.buttonShare){

            if(note.getText().toString().length() <= 300) {
                    share();
            }
        } else if (view.getId() == R.id.buttonAlarm){
            alarmDialog(nameNote, shortText);
        } else if(view.getId() == R.id.buttonQR){
            Bundle args = new Bundle();
            args.putInt("id", getButtonID());
            Intent intent = new Intent(getContext(), BarcodeCaptureActivity.class);
            intent.putExtras(args);
            startActivity(intent);
        }
    }

}


