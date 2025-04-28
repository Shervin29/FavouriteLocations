package com.example.favouritelocations;

import android.Manifest;
import androidx.appcompat.app.AlertDialog;
import android.widget.EditText;
import android.widget.RatingBar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private LocationViewModel viewModel = new LocationViewModel();
    private Button addButton;
    private Button deleteButton;
    private LatLng selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the title bar text
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Shervin's Maps");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        addButton = findViewById(R.id.addButton);
        deleteButton = findViewById(R.id.deleteButton);

        addButton.setOnClickListener(v -> addFavouriteLocation());
        deleteButton.setOnClickListener(v -> deleteFavouriteLocation());

        // Initially disable the buttons
        addButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        checkLocationPermission();
        mMap.setOnMapClickListener(latLng -> {
            selectedLocation = latLng;
            addButton.setEnabled(true);
            deleteButton.setEnabled(true);
            deleteButton.setVisibility(View.VISIBLE); // Make delete button visible
            Log.d("MainActivity", "Map clicked at: " + latLng);
        });


        mMap.setOnCameraIdleListener(this::updateMarkers);

        updateMarkers();
    }

    private void addFavouriteLocation() {
        if (selectedLocation == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Favourite Location");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_location, null);
        builder.setView(dialogView);

        EditText titleInput = dialogView.findViewById(R.id.titleInput);
        EditText descriptionInput = dialogView.findViewById(R.id.descriptionInput);
        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String title = titleInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            float rating = ratingBar.getRating();

            if (!title.isEmpty()) {
                List<LocationData> locations = getSavedLocations();
                if (locations == null) {
                    locations = new ArrayList<>();
                }
                locations.add(new LocationData(selectedLocation.latitude, selectedLocation.longitude, title, description, rating));
                saveLocationsToSharedPreferences(locations);

                updateMarkers();
                deleteButton.setEnabled(true); // Enable delete button
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }


    private void deleteFavouriteLocation() {
        if (selectedLocation != null) {
            List<LocationData> locationDataList = getSavedLocations();

            if (locationDataList != null) {
                Log.d("DELETE", "Before deletion: " + locationDataList.size());

                // Use a small threshold for floating-point precision issues
                double threshold = 0.0001;
                boolean isDeleted = locationDataList.removeIf(location ->
                        Math.abs(location.latitude - selectedLocation.latitude) < threshold &&
                                Math.abs(location.longitude - selectedLocation.longitude) < threshold);

                Log.d("DELETE", "Location found and deleted: " + isDeleted);
                Log.d("DELETE", "After deletion: " + locationDataList.size());

                // Save the updated list back to SharedPreferences
                saveLocationsToSharedPreferences(locationDataList);
                updateMarkers();

                // Disable buttons and reset selection
                addButton.setEnabled(false);
                deleteButton.setEnabled(false);
                deleteButton.setVisibility(View.GONE);
                selectedLocation = null;
            } else {
                Log.d("DELETE", "No locations found in SharedPreferences.");
            }
        } else {
            Log.d("DELETE", "No location selected for deletion.");
        }
    }





    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
        locationResult.addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            }
        }
    }

    public void updateMarkers() {
        if (mMap == null) return;

        mMap.clear(); // Remove all markers first
        List<LocationData> locationDataList = getSavedLocations();

        if (locationDataList != null) {
            for (LocationData locationData : locationDataList) {
                LatLng locationLatLng = new LatLng(locationData.latitude, locationData.longitude);
                mMap.addMarker(new MarkerOptions().position(locationLatLng).title(locationData.title));
            }
        }

        Log.d("MAP", "Markers updated. Total locations: " + (locationDataList != null ? locationDataList.size() : 0));
    }




    private List<LocationData> getSavedLocations() {
        SharedPreferences sharedPreferences = getSharedPreferences("FavouriteLocations", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("locations", null);

        if (json == null) {
            Log.d("DEBUG", "No saved locations in SharedPreferences.");
            return null;
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<LocationData>>() {}.getType();
        List<LocationData> locationDataList = gson.fromJson(json, type);

        Log.d("DEBUG", "Retrieved locations: " + locationDataList.size());
        return locationDataList;
    }


    private void saveLocationsToSharedPreferences(List<LocationData> locationDataList) {
        SharedPreferences sharedPreferences = getSharedPreferences("FavouriteLocations", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(locationDataList);
        editor.putString("locations", json);
        editor.apply();
    }
}