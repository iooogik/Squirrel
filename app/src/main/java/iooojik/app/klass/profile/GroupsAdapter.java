package iooojik.app.klass.profile;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import iooojik.app.klass.R;
import iooojik.app.klass.models.teacher.GroupInfo;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {

    private Context context;
    private List<GroupInfo> classGroupInfos;
    private Fragment fragment;
    private LayoutInflater inflater;

    GroupsAdapter(Context context, List<GroupInfo> classGroupInfos, Fragment fragment){
        this.context = context;
        this.classGroupInfos = classGroupInfos;
        this.fragment = fragment;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_group_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupInfo groupInfo = classGroupInfos.get(position);

        holder.groupName.setText(String.format("%s %s", holder.groupName.getText().toString(), groupInfo.getName()));
        holder.groupID.setText(String.format("%s%s", holder.groupID.getText().toString(), String.valueOf(groupInfo.getId())));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("id", Integer.parseInt(groupInfo.getId()));
                bundle.putString("groupAuthor", groupInfo.getAuthorEmail());
                bundle.putString("groupName", groupInfo.getName());
                bundle.putString("groupAuthorName", groupInfo.getAuthor_name());
                NavController navController = NavHostFragment.findNavController(fragment);
                navController.navigate(R.id.nav_group, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return classGroupInfos.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView groupName;
        TextView groupID;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.groupName = itemView.findViewById(R.id.groupName);
            this.groupID = itemView.findViewById(R.id.groupID);
        }
    }
}
