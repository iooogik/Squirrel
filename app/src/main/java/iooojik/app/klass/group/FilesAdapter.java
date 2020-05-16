package iooojik.app.klass.group;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.R;
import iooojik.app.klass.api.Api;
import iooojik.app.klass.models.PostResult;
import iooojik.app.klass.models.ServerResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//адаптер для списка файлов
public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ViewHolder> {

    private List<FileInfo> infoList;
    private LayoutInflater inflater;
    private SharedPreferences preferences;
    private Api api;
    private Context context;

    FilesAdapter(List<FileInfo> infoList, Context context, SharedPreferences preferences) {
        this.infoList = infoList;
        this.inflater = LayoutInflater.from(context);
        this.preferences = preferences;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_file_item, parent, false);//поиск элемента списка
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //получаем объект файла
        FileInfo fileInfo = infoList.get(position);
        //показываем соответствующую файлу иконку
        if (fileInfo.getRes_id() != 0)
            holder.imageView.setImageResource(fileInfo.getRes_id());
        else holder.imageView.setImageResource(R.drawable.blank);
        //показываем название файла
        holder.name.setText(String.format("%s...", fileInfo.getName()));

        doRetrofit();
        holder.itemView.setOnClickListener(v -> {
            //слушатель для нажатого файла
            //если нажать на файл, то он покажется на "стене" группы
            HashMap<String, String> map = new HashMap<>();

            map.put(AppСonstants.GROUP_ID_FIELD, String.valueOf(Group.id));
            map.put(AppСonstants.FILE_URL_FIELD, fileInfo.getFileURL());

            Call<ServerResponse<PostResult>> addAttachment = api.addAttachment(AppСonstants.X_API_KEY,
                    preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), map);
            addAttachment.enqueue(new Callback<ServerResponse<PostResult>>() {
                @Override
                public void onResponse(Call<ServerResponse<PostResult>> call1, Response<ServerResponse<PostResult>> response1) {
                    if (response1.code() == 200){
                        Toast.makeText(context, "Добавлено", Toast.LENGTH_SHORT).show();
                        //Snackbar.make(view, "Добавлено", Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ServerResponse<PostResult>> call1, Throwable t) {

                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    private void doRetrofit(){
        //базовый метод ретрофита
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppСonstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        final ImageView imageView;
        final TextView name;

        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView4);
            name = view.findViewById(R.id.file_name);
        }
    }
}
