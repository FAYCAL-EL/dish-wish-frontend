package uit.ensak.dish_wish_frontend.Command;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uit.ensak.dish_wish_frontend.Models.Command;
import uit.ensak.dish_wish_frontend.R;
import uit.ensak.dish_wish_frontend.databinding.ActivityMapsChefBinding;

public class MapsChefActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsChefBinding binding;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private Map<String, Command> markerCommandMap = new HashMap<>();
    final ArrayList<Command> commandList = new ArrayList<>();
    private ImageView arrow;
    private ImageView arrow_popup;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsChefBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        retryRequest();

        // Check for location permission
        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
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
                            mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location").icon(BitmapFromVector(
                                    getApplicationContext(),
                                    R.drawable.yourposition)));
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }*/

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Command associatedCommand = getCommandFromMarker(marker);
                showCommandDetailsPopup(associatedCommand);
                return true;
            }
        });
    }


    private void retryRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbWluZWVrOEBnbWFpbC5jb20iLCJpYXQiOjE3MDMwMjQ0MzUsImV4cCI6MTcwMzExMDgzNX0.Fo7dPoSIz51aRwCEeiIWRViZgNGeWpqC7eDQrhE9fgY";

                ApiService apiService = RetrofitClient.getApiService();
                Call<List<Command>> call = apiService.getCommands("Bearer " + accessToken);

                call.enqueue(new Callback<List<Command>>() {
                    @Override
                    public void onResponse(Call<List<Command>> call, Response<List<Command>> response) {
                        if (response.isSuccessful()) {
                            List<Command> receivedCommands = response.body();
                            if (receivedCommands != null) {
                                for (Command command : receivedCommands) {
                                    commandList.add(command);
                                    addMarkersToMap(commandList);
                                }
                            }
                        } else {
                            showCustomPopup();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Command>> call, Throwable t) {
                        showCustomPopup();
                    }
                });
            }
        }).start();
    }

    private void showCustomPopup() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_command_failed);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_edittext);
        dialog.show();
        Button tryAgainButton = dialog.findViewById(R.id.tryagain);
        tryAgainButton.setOnClickListener(v -> {
            retryRequest();
            dialog.dismiss();
        });
    }

    private void addMarkersToMap(List<Command> commandList) {
        if (mMap != null && commandList != null) {
            for (Command command : commandList) {
                String address = command.getAddress();
                String title = command.getTitle();
                String[] latLng = address.split(",");

                if (latLng.length == 2) {
                    double latitude = Double.parseDouble(latLng[0]);
                    double longitude = Double.parseDouble(latLng[1]);

                    LatLng location = new LatLng(latitude, longitude);
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(location)
                            .title(title)
                            .icon(BitmapFromVector(
                                    getApplicationContext(),
                                    R.drawable.dishmarker));
                    Marker marker = mMap.addMarker(markerOptions);
                    linkMarkerToCommand(marker, command);
                }
            }
        }
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


    private void showCommandDetailsPopup(Command associatedCommand) {
        if (associatedCommand != null) {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.popup_command_details);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_edittext);

            String address = associatedCommand.getAddress();
            String[] latLng = address.split(",");

            if (latLng.length == 2) {
                double latitude = Double.parseDouble(latLng[0]);
                double longitude = Double.parseDouble(latLng[1]);

                // Perform reverse geocoding to get the location name from latitude and longitude
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses != null && addresses.size() > 0) {
                        String locationName = addresses.get(0).getAdminArea();
                        TextView location = dialog.findViewById(R.id.location);
                        location.setText(locationName);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            TextView title = dialog.findViewById(R.id.title);
            TextView description = dialog.findViewById(R.id.Description);
            TextView serving = dialog.findViewById(R.id.serving);
            TextView delivary = dialog.findViewById(R.id.delivary);
            TextView price = dialog.findViewById(R.id.price);

            title.setText(associatedCommand.getTitle());
            description.setText(associatedCommand.getDescription());
            serving.setText(associatedCommand.getServing());
            delivary.setText(associatedCommand.getDeadline());
            price.setText(associatedCommand.getPrice() + "DH");

            dialog.show();

            arrow = dialog.findViewById(R.id.animation);
            if (arrow != null) {
                arrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }

            Button cookButton = dialog.findViewById(R.id.cook);
            cookButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog popup = new Dialog(MapsChefActivity.this);
                    popup.setContentView(R.layout.popup_negotiate);
                    popup.getWindow().setBackgroundDrawableResource(R.drawable.rounded_edittext);
                    dialog.dismiss();
                    EditText pricefield = popup.findViewById(R.id.price);
                    pricefield.setText(associatedCommand.getPrice() + "DH");
                    popup.show();

                    Button addButton = popup.findViewById(R.id.addButton);
                    Button subtractButton = popup.findViewById(R.id.subtractButton);
                    addButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String priceText = pricefield.getText().toString();
                            int currentPrice = Integer.parseInt(priceText.replace("DH", ""));
                            currentPrice += 5;
                            pricefield.setText(currentPrice + "DH");
                        }
                    });

                    subtractButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String priceText = pricefield.getText().toString();
                            int currentPrice = Integer.parseInt(priceText.replace("DH", ""));
                            currentPrice -= 5;
                            if (currentPrice < 0) {
                                currentPrice = 0;
                            }
                            pricefield.setText(currentPrice + "DH");
                        }
                    });

                    arrow_popup = popup.findViewById(R.id.animation);
                    if (arrow_popup != null) {
                        arrow_popup.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popup.dismiss();
                                dialog.show();
                            }
                        });
                    }

                }
            });


        }
    }


    private void linkMarkerToCommand(Marker marker, Command command) {
        markerCommandMap.put(marker.getId(), command);
    }

    private Command getCommandFromMarker(Marker marker) {
        return markerCommandMap.get(marker.getId());
    }



}