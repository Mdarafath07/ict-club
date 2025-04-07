package com.example.ictclubcompact;



import android.content.Context;
import android.net.Uri;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ImgBBUploader {
    private static final String BASE_URL = "https://api.imgbb.com/1/";
    private final ImgBBApiService apiService;

    public ImgBBUploader() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ImgBBApiService.class);
    }

    public void uploadImage(Context context, Uri imageUri, String apiKey, UploadCallback callback) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            byte[] bytes = IOUtils.toByteArray(inputStream);

            RequestBody requestFile = RequestBody.create(
                    MediaType.parse("image/*"),
                    bytes
            );

            MultipartBody.Part body = MultipartBody.Part.createFormData(
                    "image",
                    "profile_" + System.currentTimeMillis() + ".jpg",
                    requestFile
            );

            apiService.uploadImage(apiKey, body).enqueue(new Callback<ImgBBResponse>() {
                @Override
                public void onResponse(Call<ImgBBResponse> call, Response<ImgBBResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body().getData().getDisplayUrl());
                    } else {
                        callback.onError("Upload failed");
                    }
                }

                @Override
                public void onFailure(Call<ImgBBResponse> call, Throwable t) {
                    callback.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    public interface UploadCallback {
        void onSuccess(String imageUrl);
        void onError(String error);
    }
}