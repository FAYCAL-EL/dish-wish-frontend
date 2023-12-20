package uit.ensak.dish_wish_frontend.Command;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;
import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.location.Geocoder;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationRequest;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;
import uit.ensak.dish_wish_frontend.Models.Chef;
import uit.ensak.dish_wish_frontend.Models.Client;
import uit.ensak.dish_wish_frontend.Models.Command;
import uit.ensak.dish_wish_frontend.R;
import uit.ensak.dish_wish_frontend.databinding.ActivityMapsHomeBinding;

public class MapsHomeActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsHomeBinding binding;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;


    private LinearLayout mBottomSheetLayout;
    private BottomSheetBehavior sheetBehavior;
    private ImageView header_Arrow_Image;
    private Button chooseLocationButton;
    private Button pickTime;
    private Button pickDate;
    private Marker currentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng startPosition = new LatLng(34.26101, -6.5802);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPosition, 8.0f));

        mBottomSheetLayout = findViewById(R.id.bottom_sheet_layout);
        sheetBehavior = BottomSheetBehavior.from(mBottomSheetLayout);
        header_Arrow_Image = findViewById(R.id.bottom_sheet_arrow);
        chooseLocationButton = findViewById(R.id.ChooseLocation);
        Button sendCommandButton = findViewById(R.id.order);
        pickTime = findViewById(R.id.pickTime);
        pickDate = findViewById(R.id.pickDate);
        EditText DelivaryTime = findViewById(R.id.deliveryTime);
        EditText DelivaryDate = findViewById(R.id.deliveryDate);

        header_Arrow_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }

            }
        });
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                header_Arrow_Image.setRotation(slideOffset * 180);
            }
        });

        chooseLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        pickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialTimePicker picker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(9)
                        .setMinute(30)
                        .setTitleText("Select a deadline")
                        .build();

                picker.addOnCancelListener(dialogInterface -> {
                    Log.d("TimePicker", "Time picker cancelled");
                });

                picker.addOnPositiveButtonClickListener(r -> {
                    int hour = picker.getHour();
                    int minute = picker.getMinute();
                    String selectedTime = hour + ":" + minute;
                    DelivaryTime.setText(selectedTime);
                    Log.d("TimePicker", selectedTime);
                });

                picker.addOnDismissListener(dialogInterface -> {
                    Log.d("TimePicker", "Time picker dismissed");
                });

                picker.addOnNegativeButtonClickListener(dialogInterface -> {
                    Log.d("TimePicker", "Time picker failed");
                });

                picker.show(getSupportFragmentManager(), TAG);

            }
        });

        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();

                datePicker.addOnCancelListener(dialog -> {
                    Log.d("DatePicker", "Date picker cancelled");
                });

                datePicker.addOnDismissListener(dialog -> {
                    Log.d("DatePicker", "Date picker Dismiss");

                });

                datePicker.addOnPositiveButtonClickListener(selection -> {
                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    calendar.setTimeInMillis(selection);
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                    String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                    DelivaryDate.setText(selectedDate);
                    Log.d("DatePicker", selectedDate);
                });

                datePicker.addOnNegativeButtonClickListener(dialog -> {
                    Log.d("DatePicker", "Date picker failed");
                });

                datePicker.show(getSupportFragmentManager(), TAG);
            }
        });

        sendCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommandToBackend();
            }
        });


        // Check for location permission
       /* if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted
            mMap.setMyLocationEnabled(true);

            // Get the last known location of the user
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        // Got last known location. In some rare situations, this can be null.
                        if (location != null) {
                            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 1.0f));
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }*/

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                double tappedLatitude = latLng.latitude;
                double tappedLongitude = latLng.longitude;
                if (currentMarker != null) {
                    currentMarker.remove();
                }

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title("Order's Location")
                        .icon(BitmapFromVector(
                                getApplicationContext(),
                                R.drawable.dish));
                currentMarker = mMap.addMarker(markerOptions);
                updateBottomSheetWithLocation(tappedLatitude, tappedLongitude);
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

    }


    //method to change the marker's icon
    private BitmapDescriptor
    BitmapFromVector(Context context, int vectorResId)
    {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(
                vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void updateBottomSheetWithLocation(double latitude, double longitude) {
        String locationString = latitude + "," + longitude;

        if (mBottomSheetLayout != null) {
            TextView locationTextView = mBottomSheetLayout.findViewById(R.id.location);
            if (locationTextView != null) {
                locationTextView.setText(locationString);
            }
        }
    }

    private void sendCommandToBackend() {
        String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbWluZWVrOEBnbWFpbC5jb20iLCJpYXQiOjE3MDMwMjQ0MzUsImV4cCI6MTcwMzExMDgzNX0.Fo7dPoSIz51aRwCEeiIWRViZgNGeWpqC7eDQrhE9fgY";

        //form fields
        EditText Title = findViewById(R.id.title);
        String title = Title.getText().toString();
        EditText Description = findViewById(R.id.Description);
        String description = Description.getText().toString();
        EditText Serving = findViewById(R.id.serving);
        String serving = Serving.getText().toString();
        EditText Location = findViewById(R.id.location);
        String location = Location.getText().toString();
        EditText DelivaryDate = findViewById(R.id.deliveryDate);
        String delivaryDate = DelivaryDate.getText().toString();
        EditText DelivaryTime = findViewById(R.id.deliveryTime);
        String delivaryTime = DelivaryTime.getText().toString();
        String deadline =  delivaryDate + "/" + delivaryTime;
        Log.d("deadline", deadline);

        EditText Price = findViewById(R.id.price);
        String price = Price.getText().toString();



        if (isValidCommand(title, description, serving, location, delivaryDate,delivaryTime, price)) {

            // Create a Command object
            Command command = new Command();
            command.setTitle(title);
            command.setDescription(description);
            command.setServing(serving);
            command.setAddress(location);
            command.setDeadline(deadline);
            command.setPrice(price);

            // Mocking Chef and Client IDs
            Chef chef = new Chef();
            chef.setId(1L);
            command.setChef(chef);

            Client client = new Client();
            client.setId(2L);
            command.setClient(client);

            ApiService apiService = RetrofitClient.getApiService();
            Call<Void> call = apiService.createCommand("Bearer " + accessToken, command);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        showSuccessDialog();
                    } else {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        showErrorDialog();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showErrorDialog();
                }
            });
        }
    }

    private void showSuccessDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_command_created);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_edittext);
        dialog.show();
        Button tryAgainButton = dialog.findViewById(R.id.Ok);
        tryAgainButton.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    private void showErrorDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_command_failed);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_edittext);
        dialog.show();
        Button tryAgainButton = dialog.findViewById(R.id.tryagain);
        tryAgainButton.setOnClickListener(v -> {
            dialog.dismiss();
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });
    }

    private boolean isValidCommand(String title, String description, String serving, String address, String delivaryDate, String delivaryTime, String price) {
        boolean allFieldsFilled = !title.isEmpty() && !description.isEmpty() && !serving.isEmpty() && !address.isEmpty() && !delivaryDate.isEmpty() && !price.isEmpty() && !delivaryTime.isEmpty();
        if (!allFieldsFilled) {
            Toast.makeText(getApplicationContext(), "Please fill all details", Toast.LENGTH_LONG).show();
            return false;
        }

        boolean isServingValid = isValidServing(serving);
        boolean isDateValid = isValidDate(delivaryDate,delivaryTime);
        boolean isPriceValid = isValidPrice(price);

        return isServingValid && isPriceValid && isDateValid;
    }

    private boolean isValidServing(String serving) {
        try {
            double value = Double.parseDouble(serving);
            return true;
        } catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(),"Please enter a valid number as a portion",Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private boolean isValidDate(String dateString, String timeString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        try {
            dateFormat.parse(dateString);
            timeFormat.parse(timeString);
            return true;
        } catch (ParseException e) {
            Toast.makeText(getApplicationContext(), "Please enter a valid date and time", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private boolean isValidPrice(String price) {
        try {
            double value = Double.parseDouble(price);
            return true;
        } catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(), "Please enter a valid number for the price", Toast.LENGTH_LONG).show();
            return false;
        }
    }







}