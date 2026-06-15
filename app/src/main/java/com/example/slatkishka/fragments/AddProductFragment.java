package com.example.slatkishka.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.slatkishka.R;
import com.example.slatkishka.models.ProductModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddProductFragment extends Fragment {

    private ImageView ivProductImage;
    private EditText etName, etPrice, etProductDescription;
    private Button btnSave;

    private Uri imageUri; // ја чува локалната патека на избраната слика
    private ActivityResultLauncher<Intent> galleryLauncher;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private SharedPreferences sharedPreferences;
    private String loggedBusinessUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        ivProductImage = view.findViewById(R.id.ivProductImage);
        etName = view.findViewById(R.id.etProductName);
        etPrice = view.findViewById(R.id.etProductPrice);
        etProductDescription = view.findViewById(R.id.etProductDescription);
        btnSave = view.findViewById(R.id.btnSaveProduct);

        sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        loggedBusinessUser = sharedPreferences.getString("username", "Unknown");

        // патека до Storage за слики и Database за текст
        storageReference = FirebaseStorage.getInstance().getReference("product_images");
        databaseReference = FirebaseDatabase.getInstance("...")
                .getReference("Businesses").child(loggedBusinessUser).child("Products");

        // подготовка на Launcher за галерија
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        ivProductImage.setImageURI(imageUri); // приказ на сликата на екранот
                    }
                }
        );

        // клик на сликата за отварање галерија
        ivProductImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });

        // клик за зачувување на сликата
        btnSave.setOnClickListener(v -> uploadData(loggedBusinessUser));

        return view;
    }

    private void uploadData(String business) {
        String name = etName.getText().toString().trim();
        String description = etProductDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty() || imageUri == null) {
            Toast.makeText(getContext(), "Пополнете ги задолжителните полиња и изберете слика!", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = 0.0;
        try {
            // превенција: замена на евентуална запирка со точка за да не пукне парсерот
            priceStr = priceStr.replace(",", ".");
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Внесете валидна цена (пр. 120)!", Toast.LENGTH_SHORT).show();
            return; // прекинуваме овде ако форматот е лош
        }

        // оневозможи го копчето за да не се кликне два пати додека се прикачува
        btnSave.setEnabled(false);
        btnSave.setText("Се прикачува...");

        // прикачување на сликата во Storage со уникатно име (timestamp)
        StorageReference fileRef = storageReference.child(System.currentTimeMillis() + ".jpg");

        double finalPrice = price;
        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // ако е успешно прикачена сликата бараме нејзин URL линк
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        // зачувување во Realtime Database
                        saveProductToDatabase(name, description, finalPrice, downloadUrl, business);

                    }).addOnFailureListener(e -> {
                        if (isAdded()) {
                            btnSave.setEnabled(true);
                            btnSave.setText("Зачувај производ");
                            Toast.makeText(getContext(), "Сликата е прикачена, но линкот не е преземен: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) {
                        btnSave.setEnabled(true);
                        btnSave.setText("Зачувај производ");
                        Toast.makeText(getContext(), "Грешка при прикачување слика: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveProductToDatabase(String name, String description, double price, String imageUrl, String business) {
        String productId = databaseReference.push().getKey();

        if (productId != null) {
            ProductModel newProduct = new ProductModel(productId, name, description, price, imageUrl, business);

            databaseReference.child(productId).setValue(newProduct)
                    .addOnSuccessListener(aVoid -> {
                        if (isAdded()) {
                            Toast.makeText(getContext(), "Производот е успешно додаден!", Toast.LENGTH_SHORT).show();
                            getParentFragmentManager().popBackStack(); // Враќање назад
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (isAdded()) {
                            btnSave.setEnabled(true);
                            btnSave.setText("Зачувај производ");
                            Toast.makeText(getContext(), "Грешка при запис во база: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}
