package com.example.squirrel;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import static com.example.squirrel.MainActivity.shopItems;


/**
 * A simple {@link Fragment} subclass.
 */
public class Shop extends Fragment implements View.OnClickListener {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    private Cursor userCursor;
    private String[] tempArrBool;
    private boolean[] booleans;
    private String[] tempArr;
    private View view;
    private ArrayList<String> Items = new ArrayList<>();
    private ArrayList<Boolean> Booleans = new ArrayList<>();
    public Shop() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.shop_fragment,
                container, false);

        ImageButton addButton = view.findViewById(R.id.addItemCheck);
        addButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getPoints();
    }

    public int getBtnID(){
        Bundle arguments = this.getArguments();
        assert arguments != null;
        return arguments.getInt("buttonID");
    }

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
        final TextView name = view.findViewById(R.id.editNameShopNote);

        mDb = mDBHelper.getReadableDatabase();

        userCursor =  mDb.rawQuery("Select * from Notes", null);
        userCursor.moveToPosition(getBtnID());
        name.setText(getBtnName());

        final String temp = userCursor.getString(7);
        String tempBool = userCursor.getString(6);
        tempArr = temp.split("\r\n|\r|\n");
        tempArrBool = tempBool.split("\r\n|\r|\n");
        booleans = new boolean[tempArrBool.length];

        for (int i = 0; i < tempArrBool.length; i++) {
            booleans[i] = Boolean.valueOf(tempArrBool[i]);
            Booleans.add(booleans[i]);
        }

        for(int i = 0; i < tempArr.length; i++){
            Items.add(tempArr[i]);
            addCheck(booleans[i], tempArr[i]);
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
                updDatabase("Notes", tv.getText().toString(), sendBool.toString());
            }
        });
        linear.addView(view2);
    }

    private void updDatabase(String databaseName, String name, String booleans){
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
        mDb.update(databaseName, cv, "id =" + (getBtnID() + 1), null);
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
                            mDb.update("Notes", cv, "id =" + (getBtnID() + 1),
                                    null);
                            addCheck(false, nameNote.getText().toString());
                        }
                    });

            AlertDialog dlg = builder.create();
            dlg.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Window v = ((AlertDialog)dialog).getWindow();
                    v.setBackgroundDrawableResource(R.drawable.alert_dialog_backgrond);
                    Button posButton = ((AlertDialog)dialog).
                            getButton(DialogInterface.BUTTON_POSITIVE);
                    posButton.setTypeface(tpf);
                    posButton.setTypeface(Typeface.DEFAULT_BOLD);
                    posButton.setTextColor(Color.WHITE);
                }
            });
            dlg.show();
        }
    }
}
