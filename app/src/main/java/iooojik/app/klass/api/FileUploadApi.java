package iooojik.app.klass.api;

import iooojik.app.klass.models.fileUpload.UploadResult;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FileUploadApi {

    @Multipart
    @POST("upload/")
    Call<UploadResult> uploadFile(@Part MultipartBody.Part file);

}
