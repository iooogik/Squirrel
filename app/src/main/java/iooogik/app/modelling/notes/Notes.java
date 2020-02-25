package iooogik.app.modelling.notes;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.database.sqlite.SQLiteDatabase;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import iooogik.app.modelling.Database;
import iooogik.app.modelling.R;

public class Notes extends Fragment implements View.OnClickListener {

    //Переменная для работы с БД
    private Database mDBHelper;
    private SQLiteDatabase mDb;
    private Cursor userCursor;
    Bundle bundle = new Bundle();

    public static View VIEW;
    public static NotesAdapter NOTES_ADAPTER;

    public static List<Note> ITEMS = new ArrayList<>();

    public Notes(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        VIEW = inflater.inflate(R.layout.fragment_notes, container ,false);
        // запускаем поток для обновления списка заметок
        startProcedures();

        return VIEW;
    }

    private void startProcedures(){
        mDBHelper = new Database(getContext());
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();

        //необходимо очистить содержимое, чтобы при старте активити не было повторяющихся элементов
        try {
            ITEMS.clear();
        } catch (Exception e){
            Log.i("Notes", String.valueOf(e));
        }
        updProjects();

    }


    //обновление проектов на активити
    private void updProjects(){
        //добавление новых проектов
        mDb = mDBHelper.getReadableDatabase();
        userCursor = mDb.rawQuery("Select * from Notes", null);
        userCursor.moveToFirst();

        String name, desc, type;
        Bitmap bitmap = null;
        while (!userCursor.isAfterLast()) {

            name = String.valueOf(userCursor.getString(1)); //колонки считаются с 0

            type = userCursor.getString(8);

            desc = String.valueOf(userCursor.getString(2));

            byte[] bytesImg = userCursor.getBlob(userCursor.getColumnIndex("image"));

            if(bytesImg != null){
                bitmap = BitmapFactory.decodeByteArray(bytesImg, 0, bytesImg.length);
            }

            if(name != null || type != null)
                ITEMS.add(new Note(name, desc, bitmap, type,
                        userCursor.getInt(userCursor.getColumnIndex("_id")) - 1));

            userCursor.moveToNext();

            bitmap = null;
        }


        userCursor.close();
        RecyclerView recyclerView = VIEW.findViewById(R.id.recycler_view);
        NOTES_ADAPTER = new NotesAdapter(getContext(), ITEMS);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(NOTES_ADAPTER);

    }

    @Override
    public void onClick(View v) {

    }
}

