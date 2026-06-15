package com.example.slatkishka.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.slatkishka.R;
import com.example.slatkishka.adapters.CategoryAdapter;
import com.example.slatkishka.models.BusinessModel;
import com.example.slatkishka.models.CategoryModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// фрагмент на почетната страница каде се излистуваат локалите по категории

public class ListFragment extends Fragment {

    private RecyclerView rvMainCategories; // главен вертикален RecyclerView за категориите
    private CategoryAdapter categoryAdapter;
    private List<CategoryModel> categoryList;
    private ImageButton btnTopMap, btnTopProfile; // локација и профил копчиња

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        // иницијализација на главниот RecyclerView
        rvMainCategories = view.findViewById(R.id.rvMainCategories);
        rvMainCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), categoryList);
        rvMainCategories.setAdapter(categoryAdapter);

        // иницијализација на горните копчиња
        btnTopMap = view.findViewById(R.id.btnTopMap);
        btnTopProfile = view.findViewById(R.id.btnTopProfile);

        // кликови за навигација
        btnTopMap.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MapFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnTopProfile.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // вчитување и групирање на податоците од Firebase
        loadGroupedDataFromFirebase();

        return view;
    }

    private void loadGroupedDataFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("...")
                .getReference("Businesses");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear(); // старата листа се чисти за да нема дупликати

                // хеш мапа во која ќе ги ставаме бизнисите групирани по името на категоријата
                Map<String, List<BusinessModel>> groupedData = new HashMap<>();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String imeBiznis = postSnapshot.getKey();

                    // вредност на една категорија
                    String kategorijaBiznis = postSnapshot.child("category").getValue(String.class);

                    if (imeBiznis != null && kategorijaBiznis != null) {
                        BusinessModel business = new BusinessModel(imeBiznis, kategorijaBiznis);

                        if (!groupedData.containsKey(kategorijaBiznis)) {
                            groupedData.put(kategorijaBiznis, new ArrayList<>());
                        }

                        // го додаваме бизнисот во листата за таа соодветна категорија
                        groupedData.get(kategorijaBiznis).add(business);
                    }
                }

                // ги трансформираме групираните податоци во CategoryModel објекти
                for (Map.Entry<String, List<BusinessModel>> entry : groupedData.entrySet()) {
                    categoryList.add(new CategoryModel(entry.getKey(), entry.getValue()));
                }

                // го известуваме адаптерот да го освежи приказот
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Грешка при вчитување: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
