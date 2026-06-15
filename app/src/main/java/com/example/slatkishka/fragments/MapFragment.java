package com.example.slatkishka.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.slatkishka.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // го наоѓаме SupportMapFragment кој е дефиниран во XML
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.google_map);

        if (mapFragment != null) {
            // getMapAsync ја подготвува мапата во позадина и кога е спремна го повикува onMapReady
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // дефинираме координати за центарот на мапата
        LatLng skopjeCenter = new LatLng(42.0045, 21.4095);

        // поставуваме маркери
        LatLng lokacija1 = new LatLng(42.0055, 21.4120);
        LatLng lokacija2 = new LatLng(42.0020, 21.4050);

        // Ги додаваме маркерите на мапата со наслови
        mMap.addMarker(new MarkerOptions().position(skopjeCenter).title("Slatkishka"));
        mMap.addMarker(new MarkerOptions().position(lokacija1).title(""));
        mMap.addMarker(new MarkerOptions().position(lokacija2).title(""));

        // ја местиме камерата на саканата локација
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(skopjeCenter, 15f));

        // zoom
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }
}