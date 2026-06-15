package com.example.slatkishka.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.slatkishka.R;
import com.example.slatkishka.database.DatabaseHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ProfileFragment extends Fragment {

    private TextView tvUsername, tvRole, tvFullName, tvPhone;
    private Button btnGoToAddProduct, btnLogout, btnDelete;
    private ImageView ivProfilePicture;
    private SharedPreferences sharedPreferences;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private String loggedUser;
    private DatabaseHelper db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        db = new DatabaseHelper(requireContext());
        sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        // иницијализација на views
        tvUsername = view.findViewById(R.id.tvProfileUsername);
        tvRole = view.findViewById(R.id.tvProfileRole);
        tvFullName = view.findViewById(R.id.tvProfileFullName);
        tvPhone = view.findViewById(R.id.tvProfilePhone);
        btnGoToAddProduct = view.findViewById(R.id.btnGoToAddProduct);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnDelete = view.findViewById(R.id.btnDelete);
        ivProfilePicture = view.findViewById(R.id.ivProfilePicture);

        // вчитување податоци
        loggedUser = sharedPreferences.getString("username", "Гостин");
        String userRole = sharedPreferences.getString("role", "client");
        String fullName = sharedPreferences.getString("name", "Име и Презиме");
        String phone = sharedPreferences.getString("phone", "Нема телефонски број");

        tvUsername.setText(loggedUser);
        tvRole.setText("улога: " + ("business".equals(userRole) ? "бизнис" : "клиент"));
        tvFullName.setText(fullName);
        tvPhone.setText("Контакт: " + phone);

        // вчитување слика
        loadProfileImage();

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (saveImageToInternalStorage(selectedImageUri, loggedUser)) {
                            loadProfileImage();
                        }
                    }
                }
        );

        ivProfilePicture.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });

        if ("business".equals(userRole)) {
            btnGoToAddProduct.setVisibility(View.VISIBLE);
            btnGoToAddProduct.setOnClickListener(v -> {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AddProductFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }

        btnLogout.setOnClickListener(v -> performLogout());

        btnDelete.setOnClickListener(v -> {
            if (db.removeUser(loggedUser)) {
                // Бришење на сликата
                File profilePicFile = new File(requireContext().getFilesDir(), "profile_" + loggedUser + ".jpg");
                if (profilePicFile.exists()) profilePicFile.delete();

                Toast.makeText(getContext(), "Профилот е избришан!", Toast.LENGTH_SHORT).show();
                performLogout();
            } else {
                Toast.makeText(getContext(), "Грешка при бришење на сметката", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void loadProfileImage() {
        File profilePicFile = new File(requireContext().getFilesDir(), "profile_" + loggedUser + ".jpg");
        if (profilePicFile.exists()) {
            ivProfilePicture.setImageURI(Uri.fromFile(profilePicFile));
        } else {
            ivProfilePicture.setImageResource(R.drawable.profilna_default);
        }
    }

    private void performLogout() {
        sharedPreferences.edit().clear().apply();
        getParentFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();
    }

    private boolean saveImageToInternalStorage(Uri uri, String username) {
        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(new File(requireContext().getFilesDir(), "profile_" + username + ".jpg"))) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return true;
        } catch (Exception e) {
            Log.e("PROFILE_ERROR", "Грешка при зачувување!", e);
            return false;
        }
    }
}