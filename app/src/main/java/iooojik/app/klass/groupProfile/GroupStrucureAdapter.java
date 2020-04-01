package iooojik.app.klass.groupProfile;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import iooojik.app.klass.R;

public class GroupStrucureAdapter extends RecyclerView.Adapter<GroupStrucureAdapter.ViewHolder> {
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView groupName;
        LinearLayout linearLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.linearLayout = itemView.findViewById(R.id.linear);
            this.groupName = itemView.findViewById(R.id.groupName);
        }
    }
}
