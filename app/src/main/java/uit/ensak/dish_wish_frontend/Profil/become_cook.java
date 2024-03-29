package uit.ensak.dish_wish_frontend.Profil;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uit.ensak.dish_wish_frontend.Authentification.page_acceuil;
import uit.ensak.dish_wish_frontend.Command.MapsChefActivity;
import uit.ensak.dish_wish_frontend.Models.Chef;
import uit.ensak.dish_wish_frontend.R;
import uit.ensak.dish_wish_frontend.service.ApiServiceProfile;
import uit.ensak.dish_wish_frontend.service.RetrofitClient;

import android.content.SharedPreferences;
import android.os.Bundle;


import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class become_cook extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST_CARD = 1;
    private static final int PICK_IMAGE_REQUEST_CERTIF = 2;
    private byte[] byteIdCard = null;
    private byte[] byteCertificate = null;

    private String accessToken;
    private long userId;
    private Boolean isCook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_become_cook);

        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        accessToken = preferences.getString("accessToken", "");
        userId = preferences.getLong("userId", 0);
        isCook = preferences.getBoolean("isCook", false);

        Button btnScancard = findViewById(R.id.btnScancard);
        Button btnScancertif = findViewById(R.id.btnScancertif);
        ImageButton btnBack = findViewById(R.id.btnBack);
        Button  btnSubmit = findViewById(R.id.btnsubmit);

        btnScancard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGalleryPicker(PICK_IMAGE_REQUEST_CARD);
            }
        });
        btnScancertif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGalleryPicker(PICK_IMAGE_REQUEST_CERTIF);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (byteIdCard != null && byteCertificate != null) {
                    sendImagesToBackend(byteIdCard, byteCertificate);
                }
                else{
                showToast("Choose Id card and Certificate!");
                }
            }
        });
    }
    private void launchGalleryPicker( int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectedImageUri = data.getData();
        switch (requestCode) {
            case PICK_IMAGE_REQUEST_CARD:
                try {
                    byteIdCard = getImageBytes(selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case PICK_IMAGE_REQUEST_CERTIF:
                try {
                    byteCertificate = getImageBytes(selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    private byte[] getImageBytes(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private void sendImagesToBackend(byte[] idCardData, byte[] certificateData){

        RequestBody idCardRequestBody = RequestBody.create(MediaType.parse("image/*"), idCardData);
        RequestBody certificateRequestBody = RequestBody.create(MediaType.parse("image/*"), certificateData);
        MultipartBody.Part idCardPart = MultipartBody.Part.createFormData("idCard", "idCard.jpg", idCardRequestBody);
        MultipartBody.Part certificatePart = MultipartBody.Part.createFormData("certificate", "certificate.jpg", certificateRequestBody);
        ApiServiceProfile apiService = RetrofitClient.getApiServiceProfile();
        Call<Chef> call = apiService.becomeCook("Bearer " + accessToken, userId, idCardPart, certificatePart);
        call.enqueue(new Callback<Chef>() {
            @Override
            public void onResponse(Call<Chef> call, Response<Chef> response) {
                if (response.isSuccessful()) {
                    showToast("Now you are a cook!");
                    Intent intent = new Intent(become_cook.this, MapsChefActivity.class);

                    SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("isCook", true);
                    editor.apply();


                    startActivity(intent);
                }else{
                    showToast("Error during operation. Try again!");
                    Intent intent = new Intent(become_cook.this, become_cook.class);
                    startActivity(intent);
                }
            }
            @Override
            public void onFailure(Call<Chef> call, Throwable t) {
                showToast("Unavailable Server");
                Intent intent = new Intent(become_cook.this, become_cook.class);
                startActivity(intent);
            }
        });
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
