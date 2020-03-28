package iooojik.app.klass.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import iooojik.app.klass.Database;
import iooojik.app.klass.R;

public class GetTestAdapter extends RecyclerView.Adapter<GetTestAdapter.ViewHolder>{
    private Context context;
    private List<Test> testList;
    private Fragment fragment;
    private LayoutInflater inflater;

    GetTestAdapter(Context context, List<Test> testList, Fragment fragment){
        this.context = context;
        this.testList = testList;
        this.fragment = fragment;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public GetTestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.group_item, parent, false);
        return new GetTestAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull GetTestAdapter.ViewHolder holder, int position) {
        Test test = testList.get(position);
        holder.name.setText(test.author);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Database mDBHelper;
                SQLiteDatabase mDb;

                mDBHelper = new Database(context);
                mDBHelper.openDataBase();
                mDBHelper.updateDataBase();

                mDb = mDBHelper.getWritableDatabase();

                mDb.execSQL(test.getSQL());

            }
        });
    }

    @Override
    public int getItemCount() {
        return testList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        LinearLayout linearLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.linearLayout = itemView.findViewById(R.id.linear);
            this.name = itemView.findViewById(R.id.groupName);
        }
    }

}
