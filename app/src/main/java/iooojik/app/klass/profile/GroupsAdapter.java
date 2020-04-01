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

import java.util.ArrayList;
import java.util.List;

import iooojik.app.klass.R;
import iooojik.app.klass.profile.teacher.GroupInfo;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {

    private Context context;
    private List<GroupInfo> classGroupInfos;
    private Fragment fragment;
    private LayoutInflater inflater;
    private List<Integer> colors;

    GroupsAdapter(Context context, List<GroupInfo> classGroupInfos, Fragment fragment){
        this.context = context;
        this.classGroupInfos = classGroupInfos;
        this.fragment = fragment;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        colors = new ArrayList<Integer>();
        colors.add(context.getColor(R.color.colorAccent));//0
        colors.add(context.getColor(R.color.color_primary_light));//1
        colors.add(context.getColor(R.color.colorWhite));//2
        colors.add(context.getColor(R.color.color_primary_text));//3
        View view = inflater.inflate(R.layout.group_item, parent, false);
        return new GroupsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupInfo groupInfo = classGroupInfos.get(position);
        if (position % 2 == 0){
            holder.itemView.setBackgroundColor(colors.get(0));
            holder.groupID.setTextColor(colors.get(2));
        } else {
            holder.itemView.setBackgroundColor(colors.get(2));
            holder.groupID.setTextColor(colors.get(3));
            holder.groupName.setTextColor(colors.get(3));
        }
        holder.groupName.setText(String.format("%s %s", holder.groupName.getText().toString(), groupInfo.getName()));
        holder.groupID.setText(String.format("%s%s", holder.groupID.getText().toString(), groupInfo.getId()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("id", Integer.parseInt(groupInfo.getId()));
                bundle.putString("groupAuthor", groupInfo.getAuthorEmail());
                bundle.putString("groupName", groupInfo.getName());
                NavController navController = NavHostFragment.findNavController(fragment);
                navController.navigate(R.id.nav_group, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return classGroupInfos.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        TextView groupName;
        TextView groupID;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.groupName = itemView.findViewById(R.id.groupName);
            this.groupID = itemView.findViewById(R.id.groupID);
        }
    }
}
