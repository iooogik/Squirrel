package iooojik.app.klass.notes;

import android.annotation.SuppressLint;
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
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import iooojik.app.klass.Database;
import iooojik.app.klass.NotificationReceiver;
import iooojik.app.klass.R;
import iooojik.app.klass.qr.BarcodeCaptureActivity;

import static android.content.Context.ALARM_SERVICE;


public class StandartNote extends Fragment{
    // пустой контсруктор
    public StandartNote(){}

    private View view;
    // переменные для работы с бд
    private Database mDBHelper;
    private SQLiteDatabase mDb;
    // "Календарь" для получения времени
    private Calendar calendar;
    private Context context;
    private BottomSheetDialog openTextSettings;
    private EditText noteText;
    private int textSize = -1;
    private int typeFace = Typeface.NORMAL;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // получаем кнопки и "ставим" на них слушатели

        view = inflater.inflate(R.layout.fragment_standart_note, container, false);
        context = view.getContext();

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();
        setHasOptionsMenu(true);

        // получаем текущее состояние "календаря"
        calendar = Calendar.getInstance();
        noteText = view.findViewById(R.id.editNote);
        MaterialToolbar materialToolbar = getActivity().findViewById(R.id.bar);
        materialToolbar.inflateMenu(R.menu.menu_standart_note);

        updateFragment();
        textSettings();
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

    @SuppressLint("Recycle")
    private void updateFragment(){
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

        Cursor userCursor = mDb.rawQuery("Select * from Notes WHERE _id=?", new String[]{String.valueOf(getButtonID())});
        userCursor.moveToFirst();
        // перемещаем курсор

        // устанавливаем данные
        String text = userCursor.getString((userCursor.getColumnIndex("shortName")));
        if (text != null && !text.toString().equals("null") && !text.isEmpty())
            shortNote.setText(userCursor.getString((userCursor.getColumnIndex("shortName"))));
        name.setText(userCursor.getString(userCursor.getColumnIndex("name")));

        note.setText(userCursor.getString(userCursor.getColumnIndex("text")));


        ImageView img = view.findViewById(R.id.qr_view);
        LinearLayout linearLayout = view.findViewById(R.id.layout_img);

        if(!userCursor.isNull(userCursor.getColumnIndex("decodeQR"))){
            linearLayout.setVisibility(View.VISIBLE);
            try {
                img.setImageBitmap(encodeAsBitmap(userCursor.getString(userCursor.getColumnIndex("decodeQR"))));
            } catch (WriterException e) {
                e.printStackTrace();
            }
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

        textSize = userCursor.getInt(userCursor.getColumnIndex("fontSize"));
        typeFace = userCursor.getInt(userCursor.getColumnIndex("typeface"));
        if (textSize <= 0) textSize = 14;
        noteText.setTextSize(Float.valueOf(textSize));
        noteText.setTypeface(null, typeFace);
    }

    private Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, 200, 200, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 200, 0, 0, w, h);
        return bitmap;
    }

    private int getButtonID(){
        // получаем id заметки
        Bundle arguments = this.getArguments();
        assert arguments != null;
        return arguments.getInt("button ID");
    }

    private void updateData(String databaseName, String name, String note, String shortNote){
        mDb = mDBHelper.getWritableDatabase();

        //код сохранения в бд
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("shortName", shortNote);
        cv.put("text", note);
        cv.put("typeface", typeFace);
        cv.put("fontSize", textSize);

        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy",
                Locale.getDefault());

        cv.put("date", dateFormat.format(currentDate));

        //обновление базы данных
        mDb.update(databaseName, cv, "_id =" + (getButtonID()), null);
    }

    @SuppressLint("ShortAlarm")
    private void alarmDialog(final String title, final String text){
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_standart_note, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final EditText name = view.findViewById(R.id.editName);
        final EditText note = view.findViewById(R.id.editNote);
        EditText shortNote = view.findViewById(R.id.shortNote);

        String nameNote = name.getText().toString();
        String Note = note.getText().toString();
        String shortText = shortNote.getText().toString();

        switch (item.getItemId()){
            case R.id.action_notif:
                alarmDialog(nameNote, shortText);
                return true;
            case R.id.action_save:
                String dataName = "Notes";
                updateData(dataName, nameNote, Note, shortText);

                // Скрываем клавиатуру при открытии Navigation Drawer
                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().
                            getSystemService(Activity.INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(requireActivity().getCurrentFocus().
                                getWindowToken(), 0);
                    }
                } catch (Exception e) {
                    Log.i("StandartNotes", String.valueOf(e));
                }

                Snackbar.make(view, "Сохранено", Snackbar.LENGTH_LONG).show();
                return true;
            case R.id.action_read_qr:
                Bundle args = new Bundle();
                args.putInt("id", getButtonID());
                Intent intent = new Intent(getContext(), BarcodeCaptureActivity.class);
                intent.putExtras(args);
                startActivity(intent);
                return true;
            case R.id.textSettings:
                openTextSettings.show();
                return true;
        }
        return false;
    }

    @SuppressLint("InflateParams")
    private void textSettings(){
        openTextSettings = new BottomSheetDialog(getActivity());
        View bottomSheet = getActivity().getLayoutInflater().inflate(R.layout.bottom_sheet_text_settings, null);
        openTextSettings.setContentView(bottomSheet);
        EditText fontSize = bottomSheet.findViewById(R.id.font_size);
        fontSize.setText(String.valueOf(textSize));

        fontSize.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {



                if (s.toString().isEmpty()){
                    fontSize.setText("14");
                    noteText.setTextSize(14.0f);
                    textSize = 14;
                } else if (!s.toString().isEmpty() && Float.parseFloat(s.toString()) > 0) {
                    noteText.setTextSize(Float.parseFloat(s.toString()));
                    textSize = Integer.parseInt(s.toString());
                }
            }
        });

        String[] list_items = new String[]{"Обычный", "Полужирный", "Курсив", "Полужирный + Курсив"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.list_item, list_items);
        AutoCompleteTextView spinner = bottomSheet.findViewById(R.id.drop_down);
        spinner.setAdapter(adapter);
        switch (typeFace){
            case Typeface.NORMAL:
                spinner.setText(list_items[0], false);
                break;
            case Typeface.BOLD:
                spinner.setText(list_items[1], false);
                break;
            case Typeface.ITALIC:
                spinner.setText(list_items[2], false);
                break;
            case Typeface.BOLD_ITALIC:
                spinner.setText(list_items[3], false);
                break;
        }
        spinner.setOnItemClickListener((parent, view, position, id) -> {
            typeFace = 0;
            switch (position){
                case 0:
                    typeFace = Typeface.NORMAL;
                    break;
                case 1:
                    typeFace += Typeface.BOLD;
                    break;
                case 2:
                    typeFace += Typeface.ITALIC;
                    break;
                case 3:
                    typeFace += Typeface.BOLD_ITALIC;
                    break;
            }

            noteText.setTypeface(null, typeFace);

        });


    }
}


