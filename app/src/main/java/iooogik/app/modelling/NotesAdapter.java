package iooogik.app.modelling;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<Note> notes;
    Bundle bundle = new Bundle();

    NotesAdapter(Context context, List<Note> notes){
        this.notes = notes;
        this.inflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.note, parent, false);//поиск элемента списка
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder holder, int position) {
        //получение и установка данных в элемент
        Note note = notes.get(position);
        holder.name.setText(note.getName());
        holder.desc.setText(note.getDescription());

        Bitmap bitmap = note.getImage();

        if (bitmap != null) {
            ImageView img = holder.imageView;
            img.setMinimumHeight(150);
            img.setMinimumWidth(150);
            img.setImageBitmap(bitmap);
        }

        //выбор типа
        switch (note.getType()) {
            case "shop":
                holder.back.setBackgroundResource(R.drawable.red_custom_button);
                break;
            case "standart":
                holder.back.setBackgroundResource(R.drawable.green_custom_button);
                break;
            case "book":
                holder.back.setBackgroundResource(R.drawable.blue_custom_button);
                break;
        }
        //слушатель для открытия фрагмента с заметкой
        holder.frameLayout.setOnClickListener(v -> {
            bundle.putString("button name", note.getName());
            bundle.putInt("button ID", note.getId());

            AppCompatActivity activity = (AppCompatActivity) v.getContext();

            FrameLayout frameLayout = activity.findViewById(R.id.SecondaryFrame);
            frameLayout.setVisibility(View.VISIBLE);

            switch (note.getType()) {
                case "shop":
                    CheckList checkList = new CheckList();
                    checkList.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction()

                            .setCustomAnimations(R.anim.nav_default_enter_anim,
                                    R.anim.nav_default_exit_anim).

                            replace(R.id.SecondaryFrame, checkList,
                                    "secondFrame").commitAllowingStateLoss();
                    break;
                case "standart":
                    StandartNote standartNote = new StandartNote();
                    standartNote.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction()

                            .setCustomAnimations(R.anim.nav_default_enter_anim,
                                    R.anim.nav_default_exit_anim).

                            replace(R.id.SecondaryFrame, standartNote,
                                    "secondFrame").commitAllowingStateLoss();
                    break;
                case "book":
                    Book book = new Book();
                    book.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction()

                            .setCustomAnimations(R.anim.nav_default_enter_anim,
                                    R.anim.nav_default_exit_anim).

                            replace(R.id.SecondaryFrame, book,
                                    "secondFrame").commitAllowingStateLoss();
                    break;
                default:
                    Toast.makeText(v.getContext(), "Error",
                            Toast.LENGTH_SHORT).show();
                    break;
            }


        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        final ImageView imageView;
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
        }
    }
}
