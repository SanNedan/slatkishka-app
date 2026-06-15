package com.example.slatkishka.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.slatkishka.R;
import com.example.slatkishka.adapters.ProductAdapter;
import com.example.slatkishka.models.OrderModel;
import com.example.slatkishka.models.ProductModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusinessDetailsFragment extends Fragment {

    private TextView tvBusinessName, tvBusinessCategory;
    private RecyclerView rvProducts;
    private FloatingActionButton fabOrder;

    private List<ProductModel> productList;
    private ProductAdapter productAdapter;
    private Map<String, Integer> finalShoppingCart = new HashMap<>();

    private String businessName;
    private String loggedUser;
    private DatabaseReference ordersRef;

    public static BusinessDetailsFragment newInstance(String businessName, String category) {
        BusinessDetailsFragment fragment = new BusinessDetailsFragment();
        Bundle args = new Bundle();
        args.putString("business_name", businessName);
        args.putString("business_category", category);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_details, container, false);

        tvBusinessName = view.findViewById(R.id.tvDetailBusinessName);
        tvBusinessCategory = view.findViewById(R.id.tvDetailBusinessCategory);
        rvProducts = view.findViewById(R.id.rvBusinessProducts);
        fabOrder = view.findViewById(R.id.fabOrder);

        // преземање на купувачот од тековната сесија
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        loggedUser = sharedPreferences.getString("username", "Guest");

        rvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        productList = new ArrayList<>();

        // се поврзува адаптерот и се дефинира слушачот за кошничката
        productAdapter = new ProductAdapter(getContext(), productList, new ProductAdapter.OnCartChangedListener() {
            @Override
            public void onCartChanged(String productName, int quantity) {
                if (quantity == 0) {
                    finalShoppingCart.remove(productName);
                } else {
                    finalShoppingCart.put(productName, quantity);
                }
            }
        });
        rvProducts.setAdapter(productAdapter);

        if (getArguments() != null) {
            businessName = getArguments().getString("business_name");
        }

        if (businessName != null && !businessName.isEmpty()) {
            String category = getArguments().getString("business_category");
            tvBusinessName.setText(businessName);
            tvBusinessCategory.setText(category);
            fetchBusinessProducts(businessName);
        } else {
            Toast.makeText(getContext(), "Грешка: Не е пронајдено име на бизнис!", Toast.LENGTH_SHORT).show();
        }

        ordersRef = FirebaseDatabase.getInstance("https://slatkishka-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("Orders");

        // поднесување на нарачката при клик на копчето
        fabOrder.setOnClickListener(v -> sendOrderToFirebase());

        // активирање слушач за известување во реално време
        listenForOrderUpdates();

        return view;
    }

    // ги зема производите што ги нуди бизнисот!
    private void fetchBusinessProducts(String bName) {
        if (bName == null || bName.isEmpty()) {
            return;
        }

        FirebaseDatabase.getInstance("https://slatkishka-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("Businesses").child(bName).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            ProductModel product = data.getValue(ProductModel.class);
                            if (product != null) productList.add(product);
                        }
                        productAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    // за испраќање на нарачката до Firebase
    private void sendOrderToFirebase() {
        if (finalShoppingCart.isEmpty()) {
            Toast.makeText(getContext(), "Кошничката е празна!", Toast.LENGTH_SHORT).show();
            return;
        }

        String orderId = ordersRef.child(businessName).push().getKey();
        if (orderId != null) {
            OrderModel newOrder = new OrderModel("pending", finalShoppingCart, businessName, loggedUser, orderId);

            ordersRef.child(businessName).child(orderId).setValue(newOrder)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Нарачката е испратена! Се очекува одговор...", Toast.LENGTH_LONG).show();
                        finalShoppingCart.clear();
                        productAdapter.clearCart();
                    });
        }
    }

    // при настанување на промени
    private void listenForOrderUpdates() {
            if (businessName == null) {
                return; // спречува паѓање ако бизнисот е без име
            }

            if (loggedUser == null || loggedUser.isEmpty()) {
            Log.e("FirebaseError", "Корисникот не е најавен, не можам да следам нарачки.");
            return; // излези од методот, не прави ништо!
        }

        ordersRef.child(businessName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;

                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    OrderModel order = orderSnapshot.getValue(OrderModel.class);
                    if (order != null && order.getCustomerUsername().equals(loggedUser) && "ready".equals(order.getStatus())) {

                        // известување дека нарачката е готова!
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Нарачката е готова!!")
                                .setMessage("Вашата нарачка од " + businessName + " е спремна за подигнување.")
                                .setPositiveButton("Супер", (dialog, which) -> {
                                    // се брише нотификацијата од база откако е прочитана
                                    orderSnapshot.getRef().removeValue();
                                })
                                .show();
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}