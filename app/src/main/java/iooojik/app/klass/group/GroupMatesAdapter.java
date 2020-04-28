package iooojik.app.klass.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.R;
import iooojik.app.klass.models.TestResults.TestsResult;
import iooojik.app.klass.models.matesList.Mate;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class GroupMatesAdapter extends RecyclerView.Adapter<GroupMatesAdapter.ViewHolder> {

    private Context context;
    private List<Mate> mates;
    private List<TestsResult> testsResults;
    private LayoutInflater inflater;

    public GroupMatesAdapter(Context context, List<Mate> mates, List<TestsResult> testsResults) {
        this.context = context;
        this.mates = mates;
        this.testsResults = testsResults;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_mate, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mate mate = mates.get(position);
        if (testsResults != null){
            boolean wasFound = false;
            for (TestsResult testResult: testsResults) {
                if (testResult.getUserEmail().equals(mate.getEmail())){
                    //ставим результат и показываем его
                    holder.progress.setVisibility(View.VISIBLE);
                    holder.text_result.setText(String.format("Тест был пройден на %s/100", testResult.getResult()));
                    holder.text_result.setTextColor(ContextCompat.getColor(context, R.color.Completed));

                    testsResults.remove(testResult);
                    wasFound = true;
                    break;
                }
            }

            if (!wasFound){
                holder.progress.setVisibility(View.VISIBLE);
                holder.text_result.setVisibility(View.VISIBLE);
                holder.text_result.setText("Тест ещё не был пройден");
                holder.text_result.setTextColor(ContextCompat.getColor(context, R.color.notCompleted));
            }
        }
        holder.email.setText(mate.getEmail());
        holder.name.setText(mate.getFullName());
        if (!mate.getAvatar().equals("null")){
            Picasso.get().load(AppСonstants.IMAGE_URL + mate.getAvatar())
                    .resize(100, 100)
                    .transform(new RoundedCornersTransformation(30, 5)).into(holder.img);

        }else holder.img.setImageResource(R.drawable.baseline_account_circle_24);



    }

    @Override
    public int getItemCount() {
        return mates.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView email;
        ImageView img;
        LinearLayout progress;
        TextView text_result;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imageView2);
            name = itemView.findViewById(R.id.textView);
            email = itemView.findViewById(R.id.textView2);
            progress = itemView.findViewById(R.id.linearLayout_result);
            text_result = itemView.findViewById(R.id.text_tint);
        }
    }
}
