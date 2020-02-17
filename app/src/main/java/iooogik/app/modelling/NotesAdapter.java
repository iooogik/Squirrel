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

import androidx.annotation.NonNull;
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
        View view = inflater.inflate(R.layout.note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder holder, int position) {
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
        Notes notes = new Notes();
        holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("button name", note.getName());
                bundle.putInt("button ID", note.getId());

                switch (note.getType()) {
                    case "shop":
                        CheckList checkList = new CheckList();
                        notes.showFragment(checkList);
                        break;
                    case "standart":
                        StandartNote standartNote = new StandartNote();
                        notes.showFragment(standartNote);
                        break;
                    case "book":
                        Book book = new Book();
                        notes.showFragment(book);
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
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

        @Override
        public void onClick(View v) {

        }
    }
}
