package iooogik.app.modelling;



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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.WriterException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.ALARM_SERVICE;


public class StandartNote extends Fragment implements View.OnClickListener, NoteInterface {

    public StandartNote(){}

    @SuppressLint("StaticFieldLeak")
    public static View view;
    private Database mDBHelper;
    private SQLiteDatabase mDb;
    private Cursor userCursor;
    private Calendar calendar = Calendar.getInstance();


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = inflater.inflate(R.layout.fragment_standart_note,
                container, false);

        ImageButton btnSave = view.findViewById(R.id.buttonSave);
        ImageButton btnShare = view.findViewById(R.id.buttonShare);
        ImageButton btnAlarm = view.findViewById(R.id.buttonAlarm);
        FloatingActionButton back = view.findViewById(R.id.back);
        back.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnAlarm.setOnClickListener(this);

        return view;
    }

    @Override
    public void updFragment(){
        /* БД ************************ */
        mDBHelper = new Database(getActivity());
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();
        EditText name = view.findViewById(R.id.editName);
        EditText note = view.findViewById(R.id.editNote);
        EditText shortNote = view.findViewById(R.id.shortNote);

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

    @Override
    public void updShopNotes(String databaseName, String name, String booleans) {

    }

    private Bitmap setImage(){
        mDb = mDBHelper.getWritableDatabase();
        userCursor = mDb.rawQuery("Select * from Notes", null);

        userCursor.moveToPosition(getBtnID());
        byte[] bytesImg = userCursor.getBlob(5);
        return BitmapFactory.decodeByteArray(bytesImg, 0, bytesImg.length);
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
    public void updData(String databaseName, String name, String note, String shortNote){
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
        TextView name = Objects.requireNonNull(getView()).findViewById(R.id.editName);
        TextView note = getView().findViewById(R.id.editNote);
        TextView shortNote = getView().findViewById(R.id.shortNote);

        LinearLayout linearLayout = getView().findViewById(R.id.layout_img);
        String sendText;
        if(linearLayout.getVisibility() == View.VISIBLE){
            sendText = name.getText().toString() + "[/name]" +
                    note.getText().toString() + "[/item_note]" +
                    shortNote.getText().toString() + "[/shortNote]"
                    + shortNote.getText().toString() + "[/QR]";
        } else {
            sendText = name.getText().toString() + "[/name]" +
                    note.getText().toString() + "[/item_note]" +
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
        tv.setTypeface(Planets.standartFont);
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
                (dialog, which) -> dialog.cancel());
        AlertDialog dlg = builder.create();
        dlg.show();
    }

    @Override
    public void alarmDialog(final String title, final String text){

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);


        DatePickerDialog dialog;
        final TimePickerDialog dialog2;

        dialog2 = new TimePickerDialog(view.getContext(), (timePicker, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            Intent notificationIntent = new Intent(view.getContext(),
                    NotificationReceiver.class);

            Bundle args = new Bundle();
            args.putInt("btnId", getBtnID());
            args.putString("btnName", title);
            args.putString("title", title);
            args.putString("shortNote", text);

            notificationIntent.putExtras(args);
            notificationIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),
                    0, notificationIntent,
                    0);

            AlarmManager alarmManager = (AlarmManager) Objects.requireNonNull(getContext()).
                    getSystemService(ALARM_SERVICE);

            assert alarmManager != null;
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                    10000, pendingIntent);

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
            updData(dataName, nameNote, Note, shortText);


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
            Notes.NOTES_ADAPTER.notifyDataSetChanged();
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
            alarmDialog(nameNote, shortText);
        }else if (view.getId() == R.id.back){

            FrameLayout frameLayout = Notes.VIEW.findViewById(R.id.SecondaryFrame);
            frameLayout.removeAllViews();
            frameLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updFragment();
    }

}


