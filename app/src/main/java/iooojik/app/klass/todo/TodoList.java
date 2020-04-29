package iooojik.app.klass.todo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.Database;
import iooojik.app.klass.R;

public class TodoList extends Fragment implements View.OnClickListener{

    public TodoList() {}

    private View view;
    private FloatingActionButton fab;
    private Context context;
    private Fragment fragment;
    private SharedPreferences preferences;
    private Database mDBHelper;
    private SQLiteDatabase mDb;
    private Cursor cursor;
    private TodoItemsAdapter adapter;
    List<ToDoItem> items;

    @Override
    @SuppressLint("Recycle")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_todo_list, container, false);

        fab = getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.baseline_add_24);
        fab.show();
        fab.setOnClickListener(this);
        context = getContext();
        fragment = this;
        items = new ArrayList<>();
        preferences = getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);


        getActivity().runOnUiThread(() -> {
            mDBHelper = new Database(getContext());
            mDBHelper.openDataBase();
            mDBHelper.updateDataBase();
            mDb = mDBHelper.getReadableDatabase();
            cursor = mDb.rawQuery("Select * from " + AppСonstants.TABLE_TODO_NAME, null);
            cursor.moveToFirst();
            loadPoints();
        });

        return view;
    }

    private void loadPoints(){
        while (!cursor.isAfterLast()){
            items.add(new ToDoItem(cursor.getString(cursor.getColumnIndex("text")),
                    Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("checked"))),
                    cursor.getInt(cursor.getColumnIndex("_id"))));
            cursor.moveToNext();
        }

        adapter = new TodoItemsAdapter(items, context);
        RecyclerView recyclerView = view.findViewById(R.id.todo_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

    }

    private void addPoint(){

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

        final LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        //ввод пункта
        View view1 = getLayoutInflater().inflate(R.layout.edit_text, null, false);
        TextInputLayout textInputLayout = view1.findViewById(R.id.text_input_layout);
        textInputLayout.setHint("Введите название пункта");
        textInputLayout.setCounterEnabled(false);
        EditText namePoint = textInputLayout.getEditText();
        layout.addView(view1);
        builder.setView(layout);

        builder.setPositiveButton("Добавить", (dialog, which) -> {
            mDb = mDBHelper.getWritableDatabase();
            String text = namePoint.getText().toString();
            if (!text.trim().isEmpty()){
                ContentValues contentValues = new ContentValues();
                contentValues.put("text", text);
                contentValues.put("checked", "false");
                items.add(new ToDoItem(text, false, items.get(items.size() - 1).getId() + 1));
                mDb.insert(AppСonstants.TABLE_TODO_NAME, null, contentValues);
                adapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Отменить", (dialog, which) -> dialog.cancel());

        builder.create().show();


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                addPoint();
                break;
        }
    }
}
