package uit.ensak.dish_wish_frontend.Profil;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Callback;
import retrofit2.Response;
import uit.ensak.dish_wish_frontend.Authentification.CreateAccount;
import uit.ensak.dish_wish_frontend.Authentification.page_acceuil;
import uit.ensak.dish_wish_frontend.Models.Chef;
import uit.ensak.dish_wish_frontend.Models.Client;
import uit.ensak.dish_wish_frontend.R;
import uit.ensak.dish_wish_frontend.dto.ChefDTO;
import uit.ensak.dish_wish_frontend.dto.DietDTO;
import uit.ensak.dish_wish_frontend.service.ApiServiceProfile;

public class view_profile extends AppCompatActivity {

    static final int REQUEST_CODE_CHANGE_PROFILE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;
    static final int REQUEST_PICK_IMAGE = 3;

    private ChefDTO chefDTO;


    private TextView textViewFirstName, textViewLastName, textViewAddress, textViewBio, textViewDiet, textViewPHONE_NUMBER, textViewAllergies, textViewBioContent;
    private Spinner spinnerAllergies;
    private ImageView profileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("userId", 3L);
        editor.putString("accessToken", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjaGF5bWEyMDEwOUBnbWFpbC5tYSIsImlhdCI6MTcwNTEwMjIyNywiZXhwIjoxNzA1MTg4NjI3fQ.m2KZ6tfyofV9mk8M9-lxh_oG04l1kOD4jBMvhuILNag");
        editor.putBoolean("isCook", false);
        editor.apply();
        Boolean isCook = preferences.getBoolean("isCook", false);

        setContentView(R.layout.activity_view_profile);

        textViewFirstName = findViewById(R.id.textViewActualFirstName);
        textViewLastName = findViewById(R.id.textViewActualLastName);
        textViewAddress = findViewById(R.id.textViewActualAddress);
        textViewDiet = findViewById(R.id.textViewActualDiet);
        textViewPHONE_NUMBER = findViewById(R.id.textViewActualPhoneNumber);
        textViewAllergies = findViewById(R.id.textViewActualAllergies);
        profileImageView = findViewById(R.id.portrait_of);
        textViewBioContent = findViewById(R.id.textViewActualBio);
        TextView textViewBioTitle = findViewById(R.id.textViewBio);

        if (isCook) {
            textViewBioTitle.setVisibility(View.VISIBLE);
            textViewBioContent.setVisibility(View.VISIBLE);
            getChef(new ApiChefCallback() {
                @Override
                public void onChefReceived(Chef chef) {
                    textViewFirstName.setText(chef.getFirstName());
                    textViewLastName.setText(chef.getLastName());
                    textViewAddress.setText(chef.getAddress());
                    textViewPHONE_NUMBER.setText(chef.getPhoneNumber());
                    if (chef.getDiet() != null) {
                        textViewDiet.setText(chef.getDiet().getTitle());
                    }
                    textViewAllergies.setText(chef.getAllergies());
                    textViewBioContent.setText(chef.getBio());
                }

                @Override
                public void onFailure(String errorMessage) {

                }
            });
        } else {
            textViewBioTitle.setVisibility(View.INVISIBLE);
            textViewBioContent.setVisibility(View.INVISIBLE);
            getClient(new ApiClientCallback() {
                @Override
                public void onClientReceived(Client client) {
                    textViewFirstName.setText(client.getFirstName());
                    textViewLastName.setText(client.getLastName());
                    textViewAddress.setText(client.getAddress());
                    textViewPHONE_NUMBER.setText(client.getPhoneNumber());
                    if (client.getDiet() != null) {
                        textViewDiet.setText(client.getDiet().getTitle());
                    }
                    textViewAllergies.setText(client.getAllergies());
                }

                @Override
                public void onFailure(String errorMessage) {

                }
            });
        }

        getClientProfile();


//        textViewFirstName = findViewById(R.id.textViewActualFirstName);
//        textViewLastName = findViewById(R.id.textViewActualLastName);
//        textViewAddress = findViewById(R.id.textViewActualAddress);
//        textViewDiet = findViewById(R.id.textViewActualDiet);
//        textViewBio = findViewById(R.id.textViewActualBio);
//        textViewPHONE_NUMBER = findViewById(R.id.textViewActualPhoneNumber);
//        textViewAllergies = findViewById(R.id.textViewActualAllergies);
//       profileImageView = findViewById(R.id.portrait_of);
        ImageButton btnBack = findViewById(R.id.btnBack);

        Button btnChange = findViewById(R.id.btnchange);
        Button btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view_profile.this, change_profile.class);
                intent.putExtra("CURRENT_FIRST_NAME", textViewFirstName.getText().toString());
                intent.putExtra("CURRENT_LAST_NAME", textViewLastName.getText().toString());
                intent.putExtra("CURRENT_ADDRESS", textViewAddress.getText().toString());
                intent.putExtra("CURRENT_PHONE_NUMBER", textViewPHONE_NUMBER.getText().toString());
                intent.putExtra("CURRENT_ALLERGY", textViewAllergies.getText().toString());
                if (isCook) {
                    intent.putExtra("CURRENT_BIO", textViewBio.getText().toString());
                }
                intent.putExtra("CURRENT_DIET", textViewDiet.getText().toString());
                startActivityForResult(intent, REQUEST_CODE_CHANGE_PROFILE);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to delete your account?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAccount();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private void deleteAccount() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        Long userId = preferences.getLong("userId", 0);
        String authToken = preferences.getString("accessToken", "");

        ApiServiceProfile apiService = RetrofitClientProfile.getApiService();
        Call<Void> call = apiService.deleteUserAccount("Bearer " + authToken, userId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Intent loginIntent = new Intent(view_profile.this, CreateAccount.class);
                    Toast.makeText(view_profile.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                    startActivity(loginIntent);
                } else {
                    Intent loginIntent = new Intent(view_profile.this, view_profile.class);
                    Toast.makeText(view_profile.this, "Error deleting the account. Please try again", Toast.LENGTH_SHORT).show();
                    startActivity(loginIntent);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(view_profile.this, "Error deleting the account. Please try again", Toast.LENGTH_SHORT).show();
                Intent loginIntent = new Intent(view_profile.this, view_profile.class);
                startActivity(loginIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {

            String newFirstName = data.getStringExtra("NEW_FIRST_NAME");
            String newLastName = data.getStringExtra("NEW_LAST_NAME");
            String newAddress = data.getStringExtra("NEW_ADDRESS");
            String newBio = data.getStringExtra("NEW_BIO");
            String newDiet = data.getStringExtra("NEW_DIET");
            String newPhoneNumber = data.getStringExtra("NEW_PHONE_NUMBER");
            String newAllergy = data.getStringExtra("NEW_ALLERGY");

            Bitmap newProfileImageBitmap = data.getParcelableExtra("NEW_PROFILE_IMAGE_BITMAP");

            if (newProfileImageBitmap != null) {
                profileImageView.setImageBitmap(getRoundedBitmap(newProfileImageBitmap));
            } else {

            }
            SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
            Boolean isCook = preferences.getBoolean("isCook", false);

            textViewFirstName.setText(newFirstName);
            textViewLastName.setText(newLastName);
            textViewAddress.setText(newAddress);
            textViewPHONE_NUMBER.setText(newPhoneNumber);
            textViewDiet.setText(newDiet);
            textViewAllergies.setText(newAllergy);
            if(isCook) {
                textViewBio.setText(newBio);
            }

            DietDTO dietDTO = new DietDTO();
            ChefDTO chefDTO = new ChefDTO();

            chefDTO.setFirstName(newFirstName);
            chefDTO.setLastName(newLastName);
            chefDTO.setAddress(newAddress);
            chefDTO.setPhoneNumber(newPhoneNumber);
            chefDTO.setAllergies(newAllergy);
            dietDTO.setTitle(newDiet);
            chefDTO.setDietDTO(dietDTO);
            if (isCook) {
                chefDTO.setBio(newBio);
            }
            updateUser(chefDTO, newProfileImageBitmap);
        }
        if ((requestCode == REQUEST_IMAGE_CAPTURE || requestCode == REQUEST_PICK_IMAGE) && resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                profileImageView.setImageBitmap(imageBitmap);
            } else if (requestCode == REQUEST_PICK_IMAGE) {
                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    profileImageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getClient(ApiClientCallback apiClientCallback) {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        Long userId = preferences.getLong("userId", 0);
        String authToken = preferences.getString("accessToken", "");
        ApiServiceProfile apiService = RetrofitClientProfile.getApiService();
        Call<Client> call = apiService.getClientById("Bearer " + authToken, userId);
        call.enqueue(new Callback<Client>() {
            @Override
            public void onResponse(Call<Client> call, Response<Client> response) {
                if (response.isSuccessful()) {
                    Client client = response.body();
                    apiClientCallback.onClientReceived(client);
                } else {
                    apiClientCallback.onFailure("Error during user fetching " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Client> call, Throwable t) {
                t.printStackTrace();
                apiClientCallback.onFailure("Unavailable Server " + t.getMessage());
            }
        });
    }

    private void getChef(ApiChefCallback apiChefCallback) {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        Long userId = preferences.getLong("userId", 0);
        String authToken = preferences.getString("accessToken", "");

        ApiServiceProfile apiService = RetrofitClientProfile.getApiService();

        Call<Chef> call = apiService.getChefById("Bearer " + authToken, userId);

        call.enqueue(new Callback<Chef>() {
            @Override
            public void onResponse(Call<Chef> call, Response<Chef> response) {
                if (response.isSuccessful()) {
                    Chef chef = response.body();
                    apiChefCallback.onChefReceived(chef);
                } else {
                    apiChefCallback.onFailure("Error during user fetching " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Chef> call, Throwable t) {

                t.printStackTrace();
                apiChefCallback.onFailure("Unavailable Server " + t.getMessage());
            }
        });
    }

    private Bitmap getRoundedBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int diameter = Math.min(width, height);

        Bitmap output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, diameter, diameter);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(diameter / 2f, diameter / 2f, diameter / 2f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, null, rect, paint);

        return output;
    }

    private void updateUser(ChefDTO chefDTO, Bitmap imageBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageBytes);
        MultipartBody.Part photoPart = MultipartBody.Part.createFormData("photo", "photo.jpg", requestFile);
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        Long userId = preferences.getLong("userId", 0);
        String authToken = preferences.getString("accessToken", "");
        ApiServiceProfile apiService = RetrofitClientProfile.getApiService();
        Call<Client> call = apiService.updateClient("Bearer " + authToken, userId, chefDTO, photoPart);
        call.enqueue(new Callback<Client>() {
            @Override
            public void onResponse(Call<Client> call, Response<Client> response) {
                if (response.isSuccessful()) {
                    Client client = response.body();
                    Toast.makeText(view_profile.this, "User updated succesfully", Toast.LENGTH_SHORT).show();


                } else {
                    Intent intent = new Intent(view_profile.this, view_profile.class);
                    Toast.makeText(view_profile.this, "Error during updating", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<Client> call, Throwable t) {
                Intent intent = new Intent(view_profile.this, view_profile.class);
                Toast.makeText(view_profile.this, "Unavailable Server", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                startActivity(intent);
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void getClientProfile() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        Long userId = preferences.getLong("userId", 0);
        String authToken = preferences.getString("accessToken", "");

        ApiServiceProfile apiService = RetrofitClientProfile.getApiService();

        Call<ResponseBody> call = apiService.getClientProfile("Bearer " + authToken, userId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        Bitmap newProfileImageBitmap = BitmapFactory.decodeStream(response.body().byteStream());
                        profileImageView.setImageBitmap(getRoundedBitmap(newProfileImageBitmap));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                     Toast.makeText(view_profile.this, "Error during user profile fetching " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(view_profile.this, "Unavailable Sever " ,Toast.LENGTH_SHORT).show();
            }
        });
    }
}