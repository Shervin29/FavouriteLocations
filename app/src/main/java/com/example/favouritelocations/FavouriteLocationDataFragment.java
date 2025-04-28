package com.example.favouritelocations;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavouriteLocationDataFragment extends Fragment {

    private EditText titleEditText;
    private EditText descriptionEditText;
    private RatingBar ratingBar;
    private Button saveButton;
    private Button cancelButton;
    private Button deleteButton;
    private LocationViewModel viewModel;
    private LatLng latLng;

    public FavouriteLocationDataFragment(LocationViewModel viewModel, LatLng latLng) {
        this.viewModel = viewModel;
        this.latLng = latLng;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite_location_data, container, false);

        titleEditText = view.findViewById(R.id.titleEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        ratingBar = view.findViewById(R.id.ratingBar);
        saveButton = view.findViewById(R.id.saveButton);
        cancelButton = view.findViewById(R.id.cancelButton);
        deleteButton = view.findViewById(R.id.deleteButton);

        if (viewModel.selectedLatLng != null){
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.GONE);
        }

        saveButton.setOnClickListener(v -> saveLocationData());
        cancelButton.setOnClickListener(v -> dismissFragment());
        deleteButton.setOnClickListener(v -> deleteLocationData());

        return view;
    }

    private void saveLocationData() {
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        float rating = ratingBar.getRating();

        LocationData locationData = new LocationData(latLng.latitude, latLng.longitude, title, description, rating);

        saveLocationToSharedPreferences(locationData);
        dismissFragment();
        if (getActivity() instanceof MainActivity){
            ((MainActivity) getActivity()).updateMarkers();
        }

    }

    private void deleteLocationData(){
        List<LocationData> locationDataList = getSavedLocations();

        if (locationDataList != null) {
            locationDataList.removeIf(location ->
                    location.latitude == latLng.latitude && location.longitude == latLng.longitude);

            saveLocationsToSharedPreferences(locationDataList);
            dismissFragment();
            if (getActivity() instanceof MainActivity){
                ((MainActivity) getActivity()).updateMarkers();
            }
        }
    }

    private void dismissFragment() {
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction().remove(this).commit();
        }
    }

    private void saveLocationToSharedPreferences(LocationData locationData) {
        List<LocationData> locationDataList = getSavedLocations();
        if (locationDataList == null) {
            locationDataList = new ArrayList<>();
        }
        locationDataList.add(locationData);
        saveLocationsToSharedPreferences(locationDataList);
    }

    private void saveLocationsToSharedPreferences(List<LocationData> locationDataList) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FavouriteLocations", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(locationDataList);
        editor.putString("locations", json);
        editor.apply();
    }

    private List<LocationData> getSavedLocations() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FavouriteLocations", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("locations", null);
        if (json == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<LocationData>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
