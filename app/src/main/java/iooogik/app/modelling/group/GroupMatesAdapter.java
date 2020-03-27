package iooogik.app.modelling.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import iooogik.app.modelling.R;

public class GroupMatesAdapter extends RecyclerView.Adapter<GroupMatesAdapter.ViewHolder> {

    private Context context;
    private Fragment fragment;
    private List<String> mates;
    private LayoutInflater inflater;

    GroupMatesAdapter(Context context, Fragment fragment, List<String> mates) {
        this.context = context;
        this.fragment = fragment;
        this.mates = mates;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.group_item, parent, false);
        return new GroupMatesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String mate = mates.get(position);
        holder.name.setText(mate);
    }

    @Override
    public int getItemCount() {
        return mates.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.groupName);
        }
    }
}
