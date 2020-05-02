package iooojik.app.klass.profile;

import android.content.Context;
import android.content.SharedPreferences;
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

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.R;
import iooojik.app.klass.models.pupil.PupilGroups;

public class PupilGroupsAdapter extends RecyclerView.Adapter<PupilGroupsAdapter.ViewHolder> {

    private List<PupilGroups> pupilGroups;
    public Fragment fragment;
    public Context context;
    public LayoutInflater inflater;
    private SharedPreferences preferences;


    PupilGroupsAdapter(List<PupilGroups> pupilGroups, Fragment fragment, Context context) {
        this.pupilGroups = pupilGroups;
        this.fragment = fragment;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        preferences = context.getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_group_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PupilGroups pupilGroup = pupilGroups.get(position);
        if (preferences.getInt(AppСonstants.SHOW_GROUP_ID, 0) == 1){
            holder.groupID.setVisibility(View.VISIBLE);
        } else holder.groupID.setVisibility(View.INVISIBLE);
        holder.groupName.setText(pupilGroup.getGroup_name());

        holder.groupID.setText(String.format("%s%s", holder.groupID.getText().toString(),
                String.valueOf(pupilGroup.getGroupId())));

        Bundle args = new Bundle();
        args.putString("groupID", pupilGroup.getGroupId());
        args.putString("groupName", pupilGroup.getGroup_name());
        holder.itemView.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(fragment);
            navController.navigate(R.id.groupProfile, args);
        });

    }

    @Override
    public int getItemCount() {
        return pupilGroups.size();
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
