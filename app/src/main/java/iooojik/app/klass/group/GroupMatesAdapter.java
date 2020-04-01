package iooojik.app.klass.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import iooojik.app.klass.R;
import iooojik.app.klass.group.matesList.Mates;

public class GroupMatesAdapter extends RecyclerView.Adapter<GroupMatesAdapter.ViewHolder> {

    private Context context;
    private Fragment fragment;
    private List<Mates> mates;
    private LayoutInflater inflater;

    public GroupMatesAdapter(Context context, Fragment fragment, List<Mates> mates) {
        this.context = context;
        this.fragment = fragment;
        this.mates = mates;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_mate, parent, false);
        return new GroupMatesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mates mate = mates.get(position);
        holder.email.setText(mate.getEmail());
        holder.name.setText(mate.getFullName());
        holder.img.setImageResource(R.drawable.baseline_account_circle_24);
    }

    @Override
    public int getItemCount() {
        return mates.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView email;
        ImageView img;
        ConstraintLayout constraintLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imageView2);
            name = itemView.findViewById(R.id.textView);
            email = itemView.findViewById(R.id.textView2);
            constraintLayout = itemView.findViewById(R.id.constraint);
        }
    }
}
