package iooojik.app.klass.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
import java.util.Objects;

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.R;
import iooojik.app.klass.api.Api;
import iooojik.app.klass.models.PostResult;
import iooojik.app.klass.models.ServerResponse;
import iooojik.app.klass.models.TestResults.DataTestResult;
import iooojik.app.klass.models.TestResults.TestsResult;
import iooojik.app.klass.models.matesList.DataUsersToGroup;
import iooojik.app.klass.models.matesList.Mate;
import iooojik.app.klass.models.teacher.GroupInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {

    private List<GroupInfo> classGroupInfos;
    private Fragment fragment;
    private LayoutInflater inflater;
    private SharedPreferences preferences;
    private Api api;

    GroupsAdapter(Context context, List<GroupInfo> classGroupInfos, Fragment fragment){
        this.classGroupInfos = classGroupInfos;
        this.fragment = fragment;
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
        GroupInfo groupInfo = classGroupInfos.get(position);
        if (preferences.getInt(AppСonstants.SHOW_GROUP_ID, 0) == 1){
            holder.groupID.setVisibility(View.VISIBLE);
        } else holder.groupID.setVisibility(View.INVISIBLE);


        holder.groupName.setText(String.format("%s %s", holder.groupName.getText().toString(), groupInfo.getName()));
        holder.groupID.setText(String.format("%s%s", holder.groupID.getText().toString(), String.valueOf(groupInfo.getId())));
        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("id", Integer.parseInt(groupInfo.getId()));
            bundle.putString("groupAuthor", groupInfo.getAuthorEmail());
            bundle.putString("groupName", groupInfo.getName());
            bundle.putString("groupAuthorName", groupInfo.getAuthor_name());
            NavController navController = NavHostFragment.findNavController(fragment);
            navController.navigate(R.id.nav_group, bundle);
        });
        //слушатель удаления группы
        Objects.requireNonNull(holder.itemView).setOnLongClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(fragment.getActivity());
            View bottomSheet = fragment.getActivity().getLayoutInflater().inflate(R.layout.bottom_sheet_delete, null);
            bottomSheetDialog.setContentView(bottomSheet);
            bottomSheetDialog.show();

            Button delete = bottomSheetDialog.findViewById(R.id.delete);
            delete.setOnClickListener(v1 -> fragment.getActivity().runOnUiThread(() -> {
                deleteGroup(groupInfo);
                classGroupInfos.remove(groupInfo);
                notifyDataSetChanged();
                if (classGroupInfos.size() == 0){
                    TextView warn = fragment.getView().findViewById(R.id.notif_text);
                    warn.setVisibility(View.VISIBLE);
                }
                bottomSheetDialog.hide();
            }));
            return true;
        });

    }

    private void deleteGroup(GroupInfo groupInfo){
        doRetrofit();
        //удаляем группу
        Call<ServerResponse<PostResult>> deleteGroupCall = api.removeGroup(AppСonstants.X_API_KEY, groupInfo.getId());
        deleteGroupCall.enqueue(new Callback<ServerResponse<PostResult>>() {
            @Override
            public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {

            }

            @Override
            public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {

            }
        });
        //удаляем результаты учеников, принадлежащих этой группе
        Call<ServerResponse<DataTestResult>> response = api.getTestResults(AppСonstants.X_API_KEY,
                preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), "group_id", String.valueOf(groupInfo.getId()));
        response.enqueue(new Callback<ServerResponse<DataTestResult>>() {
            @Override
            public void onResponse(Call<ServerResponse<DataTestResult>> call, Response<ServerResponse<DataTestResult>> response) {
                if (response.code() == 200){
                    DataTestResult dataTestResult = response.body().getData();
                    List<TestsResult> results = dataTestResult.getTestsResult();

                    for (TestsResult result : results){
                        Call<ServerResponse<PostResult>> responseCall = api.removeResult(AppСonstants.X_API_KEY, result.getId());
                        responseCall.enqueue(new Callback<ServerResponse<PostResult>>() {
                            @Override
                            public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {

                            }

                            @Override
                            public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<DataTestResult>> call, Throwable t) {

            }
        });
        //удаляем привязку учеников к группе
        Call<ServerResponse<DataUsersToGroup>> responseCall = api.getMatesList(AppСonstants.X_API_KEY, "group_id", groupInfo.getId());
        responseCall.enqueue(new Callback<ServerResponse<DataUsersToGroup>>() {
            @Override
            public void onResponse(Call<ServerResponse<DataUsersToGroup>> call, Response<ServerResponse<DataUsersToGroup>> response) {
                if(response.code() == 200) {
                    ServerResponse<DataUsersToGroup> result = response.body();
                    List<Mate> mates = result.getData().getMates();
                    for (Mate mate : mates){
                        Call<ServerResponse<PostResult>> responseCall2 = api.removeMate(AppСonstants.X_API_KEY, mate.getId());
                        responseCall2.enqueue(new Callback<ServerResponse<PostResult>>() {
                            @Override
                            public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {

                            }

                            @Override
                            public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {

                            }
                        });
                    }
                } else {
                    Log.e("GETTING MATES", response.raw() + "");
                }
            }
            @Override
            public void onFailure(Call<ServerResponse<DataUsersToGroup>> call, Throwable t) {
                Log.e("GETTING MATES",t.toString());
            }
        });
    }

    private void doRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppСonstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
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
