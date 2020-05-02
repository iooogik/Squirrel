package iooojik.app.klass.todo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.Database;
import iooojik.app.klass.R;

public class TodoItemsAdapter extends RecyclerView.Adapter<TodoItemsAdapter.ViewHolder>  {

    private List<ToDoItem> items;
    private LayoutInflater inflater;
    private SQLiteDatabase mDb;
    private Cursor cursor;
    private Database mDBHelper;
    private Fragment fragment;

    TodoItemsAdapter(List<ToDoItem> items, Context context, Fragment fragment) {
        this.items = items;
        this.inflater = LayoutInflater.from(context);
        this.fragment = fragment;
        mDBHelper = new Database(context);
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
    @SuppressLint("InflateParams")
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

        //удаление заметки
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(fragment.getActivity());
        View bottomSheet = fragment.getActivity().getLayoutInflater().inflate(R.layout.bottom_sheet_delete, null);

        bottomSheetDialog.setContentView(bottomSheet);

        Button delete = bottomSheet.findViewById(R.id.delete);
        delete.setOnClickListener(v -> {

            mDb = mDBHelper.getWritableDatabase();
            mDb.delete("todo_list", "_id=" + (item.getId()), null);
            items.remove(item);
            notifyItemRemoved(position);
            bottomSheetDialog.hide();
        });

        Button cancel = bottomSheet.findViewById(R.id.cancel);
        cancel.setOnClickListener(v -> bottomSheetDialog.hide());

        holder.checkBox.setOnLongClickListener(v -> { bottomSheetDialog.show(); return true; });

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
