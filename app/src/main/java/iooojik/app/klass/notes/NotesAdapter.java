package iooojik.app.klass.notes;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.ArrayList;
import java.util.List;

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.Database;
import iooojik.app.klass.R;

import static iooojik.app.klass.AppСonstants.APP_PREFERENCES_SHOW_BOOK_MATERIALS;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<Note> notes;
    //Переменные для работы с БД
    private Database mDBHelper;
    private SQLiteDatabase mDb;
    private Bundle bundle = new Bundle();
    private Fragment fragment;
    private Context context;
    private Cursor userCursor;
    private Filter filter;

    NotesAdapter(Context context, List<Note> notes, Fragment fragment){
        this.notes = notes;
        this.inflater = LayoutInflater.from(context);
        this.fragment = fragment;
        this.context = context;
        filter = new ItemFilter();
    }


    @NonNull
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_item_note, parent, false);//поиск элемента списка
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder holder, int position) {
        mDBHelper = new Database(context);
        mDBHelper.openDataBase();
        //получение и установка данных в элемент
        Note note = notes.get(position);
        SharedPreferences settings = context.getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        int val = settings.getInt(APP_PREFERENCES_SHOW_BOOK_MATERIALS, 0);

        NavController navHostFragment = NavHostFragment.findNavController(fragment);

        if (!(note.getName().equals("Математические формулы") && val == 1)) {
            holder.name.setText(note.getName());
            holder.desc.setText(note.getDescription());

            mDb = mDBHelper.getReadableDatabase();

            userCursor =  mDb.rawQuery("Select * from Notes WHERE _id=?",
                    new String[]{String.valueOf(note.getId())});
            userCursor.moveToFirst();
            if(!userCursor.isNull(userCursor.getColumnIndex("decodeQR"))){
                Bitmap bitmap = null;
                try {
                    bitmap = encodeAsBitmap(userCursor.getString(userCursor.getColumnIndex("decodeQR")));
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                ImageView img = holder.imageView;
                img.setMinimumHeight(150);
                img.setMinimumWidth(150);
                img.setImageBitmap(bitmap);

                img.setOnLongClickListener(view -> {

                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                    TextView textView = new TextView(builder.getContext());
                    textView.setText("Вы действительно хотите удалить  QR-код?");
                    LinearLayout layout = new LinearLayout(context);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.addView(textView);
                    builder.setView(layout);

                    builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mDb = mDBHelper.getReadableDatabase();

                            userCursor =  mDb.rawQuery("Select * from Notes WHERE _id=?",
                                    new String[]{String.valueOf(note.getId())});
                            userCursor.moveToFirst();

                            if(userCursor.getBlob(userCursor.getColumnIndex("image")) != null){
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("image", (byte[]) null);
                                mDb.update("Notes", contentValues, "_id="+ position, null);
                            }
                        }
                    });

                    builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    builder.create().show();


                    return false;
                });
            }

            if (note.getType().equals("shop")){


                if(userCursor.getInt(userCursor.getColumnIndex("isCompleted")) == 1){
                    holder.completed.setVisibility(View.VISIBLE);
                }else if(userCursor.getInt(userCursor.getColumnIndex("isCompleted")) == 0){
                    holder.completed.setVisibility(View.GONE);
                }
            }

            mDb = mDBHelper.getReadableDatabase();

            userCursor =  mDb.rawQuery("Select * from Notes WHERE _id=?",
                    new String[]{String.valueOf(note.getId())});
            userCursor.moveToFirst();

            if(userCursor.getInt(userCursor.getColumnIndex("isNotifSet")) == 1){
                holder.isNotifSet.setVisibility(View.VISIBLE);
            }else if(userCursor.getInt(userCursor.getColumnIndex("isNotifSet")) == 0){
                holder.isNotifSet.setVisibility(View.GONE);
            }

            //слушатель для открытия фрагмента с заметкой
            holder.frameLayout.setOnClickListener(v -> {
                bundle.putString("button name", note.getName());
                bundle.putInt("button ID", note.getId());

                switch (note.getType()) {
                    case "shop":
                        navHostFragment.navigate(R.id.nav_checkList, bundle);
                        break;
                    case "standart":
                        navHostFragment.navigate(R.id.nav_standart_note, bundle);
                        break;
                    case "book":
                        navHostFragment.navigate(R.id.nav_book, bundle);
                        break;
                    default:
                        Snackbar.make(holder.itemView, "Error", Snackbar.LENGTH_LONG).show();
                        break;
                }

            });

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(fragment.getActivity());
            View bottomSheet = fragment.getActivity().getLayoutInflater().inflate(R.layout.bottom_sheet_delete, null);

            bottomSheetDialog.setContentView(bottomSheet);

            Button delete = bottomSheet.findViewById(R.id.delete);
            delete.setOnClickListener(v -> {
                mDb = mDBHelper.getWritableDatabase();
                mDb.delete("Notes", "_id=" + (note.getId()), null);
                int pos = Notes.ITEMS.indexOf(note);
                Notes.ITEMS.remove(note);
                notifyItemRemoved(pos);
                bottomSheetDialog.hide();
            });

            Button cancel = bottomSheet.findViewById(R.id.cancel);
            cancel.setOnClickListener(v -> bottomSheetDialog.hide());

            holder.frameLayout.setOnLongClickListener(v -> {
                bottomSheetDialog.show();
                return true;
            });
        } else {
            holder.frameLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        final ImageView imageView, completed, isNotifSet;
        final TextView name, desc;
        final LinearLayout back;
        final FrameLayout frameLayout;
        ViewHolder(View view){
            super(view);
            frameLayout = view.findViewById(R.id.frame);
            imageView = view.findViewById(R.id.subImg);
            name = view.findViewById(R.id.name);
            desc = view.findViewById(R.id.description);
            back = view.findViewById(R.id.background);
            isNotifSet = view.findViewById(R.id.notif);
            completed = view.findViewById(R.id.completed);
        }
    }

    Filter getFilter() {
        return filter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();


            FilterResults results = new FilterResults();

            final List<Note> list = notes;
            int count = list.size();
            final List<Note> nlist = new ArrayList<Note>(count);



            String filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).getName();

                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(list.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notes = (ArrayList<Note>) results.values;
            notifyDataSetChanged();
        }

    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
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

}
