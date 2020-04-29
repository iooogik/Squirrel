package iooojik.app.klass.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.Database;
import iooojik.app.klass.R;

public class TodoItemsAdapter extends RecyclerView.Adapter<TodoItemsAdapter.ViewHolder>  {

    private List<ToDoItem> items;
    private LayoutInflater inflater;
    private Database mDBHelper;
    private SQLiteDatabase mDb;
    private Cursor cursor;
    private Context context;

    public TodoItemsAdapter(List<ToDoItem> items, Context context) {
        this.items = items;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        mDBHelper = new Database(this.context);
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();
        mDb = mDBHelper.getWritableDatabase();
        cursor = mDb.rawQuery("Select * from " + AppСonstants.TABLE_TODO_NAME, null);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_todo_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToDoItem item = items.get(position);
        cursor.moveToPosition(item.getId() - 1);
        holder.checkBox.setText(item.getText());
        holder.checkBox.setChecked(item.getChecked());
        holder.checkBox.setChecked(item.getChecked());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ContentValues contentValues = new ContentValues();
            contentValues.put(AppСonstants.TABLE_TODO_CHECKED, String.valueOf(isChecked));
            mDb.update(AppСonstants.TABLE_TODO_NAME, contentValues, "_id=" + item.getId(), null);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        ViewHolder(View view){
            super(view);
            checkBox = view.findViewById(R.id.checkBox);
        }
    }
}
