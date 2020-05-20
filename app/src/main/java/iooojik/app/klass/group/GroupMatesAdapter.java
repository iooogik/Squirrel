package iooojik.app.klass.group;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import java.util.List;

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.R;
import iooojik.app.klass.api.Api;
import iooojik.app.klass.models.PostResult;
import iooojik.app.klass.models.ServerResponse;
import iooojik.app.klass.models.test_results.TestsResult;
import iooojik.app.klass.models.matesList.Mate;
import iooojik.app.klass.room_models.mates.MateEntity;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GroupMatesAdapter extends RecyclerView.Adapter<GroupMatesAdapter.ViewHolder> {

    private Context context;
    private List<MateEntity> mates;

    private LayoutInflater inflater;
    private Api api;
    private Fragment fragment;
    private boolean isTeacher;


    public GroupMatesAdapter(Context context, List<MateEntity> mates,
                             Fragment fragment, boolean isTeacher) {
        this.context = context;
        this.mates = mates;
        this.fragment = fragment;
        this.isTeacher = isTeacher;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_mate, parent, false);
        return new ViewHolder(view);
    }

    @Override
    @SuppressLint("InflateParams")
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MateEntity mate = mates.get(position);

        if (mate.getTest_result() != -1){
            boolean wasFound = false;
            //ищем ученика с его результами
            holder.progress.setVisibility(View.VISIBLE);
            holder.text_result.setText(String.format("Тест был пройден на %s/100", String.valueOf(mate.getTest_result())));
            holder.text_result.setTextColor(ContextCompat.getColor(context, R.color.Completed));

            if (!wasFound){
                holder.progress.setVisibility(View.VISIBLE);
                holder.text_result.setVisibility(View.VISIBLE);
                holder.text_result.setText("Тест ещё не был пройден");
                holder.text_result.setTextColor(ContextCompat.getColor(context, R.color.notCompleted));
            }

            if (isTeacher) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(fragment.getActivity());
                View bottomSheet = fragment.getActivity().getLayoutInflater().
                        inflate(R.layout.bottom_sheet_delete, null);

                bottomSheetDialog.setContentView(bottomSheet);

                Button delete = bottomSheet.findViewById(R.id.delete);
                delete.setOnClickListener(v -> {
                    doRetrofit();
                    Call<ServerResponse<PostResult>> deleteUser = api.removeMate(AppСonstants.X_API_KEY, String.valueOf(mate.getMate_id()));
                    deleteUser.enqueue(new Callback<ServerResponse<PostResult>>() {
                        @Override
                        public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {
                            if (response.code() != 200)
                                Log.e("Deleting user", String.valueOf(response.raw()));
                        }

                        @Override
                        public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {
                            Log.e("Deleting user", String.valueOf(t));
                        }
                    });
                    mates.remove(mate);
                    notifyDataSetChanged();
                    bottomSheetDialog.hide();
                });

                Button cancel = bottomSheet.findViewById(R.id.cancel);
                cancel.setOnClickListener(v -> bottomSheetDialog.hide());

                holder.layout.setOnLongClickListener(v -> {
                    bottomSheetDialog.show();
                    return false;
                });
            }

        }
        holder.email.setText(mate.getMate_email());
        holder.name.setText(mate.getMate_name());

        if (!mate.getMate_avatar().equals("null") && !mate.getMate_avatar().isEmpty()){
            Picasso.with(context).load(AppСonstants.IMAGE_URL + mate.getMate_avatar())
                    .resize(100, 100)
                    .transform(new RoundedCornersTransformation(30, 5)).into(holder.img);

        }else holder.img.setImageResource(R.drawable.baseline_account_circle_24);



    }

    private void doRetrofit(){
        //базовый метод ретрофита
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppСonstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
    }

    @Override
    public int getItemCount() {
        return mates.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, email, text_result;
        ImageView img;
        LinearLayout progress;
        ConstraintLayout layout;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imageView2);
            name = itemView.findViewById(R.id.textView);
            email = itemView.findViewById(R.id.textView2);
            progress = itemView.findViewById(R.id.linearLayout_result);
            text_result = itemView.findViewById(R.id.text_tint);
            layout = itemView.findViewById(R.id.constraint);
        }
    }
}
