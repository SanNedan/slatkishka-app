package com.example.slatkishka.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.slatkishka.R;
import com.example.slatkishka.database.DatabaseHelper;
import com.example.slatkishka.models.BusinessModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupFragment extends Fragment {

    private EditText etUsername, etPassword, etName, etPhone;
    private Spinner spCategory;
    private RadioGroup rgRole;
    private RadioButton rbClient; // ни треба само референца до едното копче за да видиме дали било селектирано!
    private Button btnSignup;
    private DatabaseHelper db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        // потребните views
        etUsername = view.findViewById(R.id.etSignupUsername);
        etPassword = view.findViewById(R.id.etSignupPassword);
        etName = view.findViewById(R.id.etProfileFullName);
        etPhone = view.findViewById(R.id.etPhone);
        spCategory = view.findViewById(R.id.spinnerCategory);
        rbClient = view.findViewById(R.id.rbClient);
        btnSignup = view.findViewById(R.id.btnSignup);

        db = new DatabaseHelper(requireContext());
        RadioGroup rgRole = view.findViewById(R.id.rgRole);

        rgRole.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbClient) {
                spCategory.setVisibility(View.GONE); // ако е клиент треба да се скрие спинерот
            } else {
                spCategory.setVisibility(View.VISIBLE); // во спротивно за бизниси да се прикаже!!
            }
        });

        btnSignup.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            boolean isClient = rbClient.isChecked();
            String category = isClient ? "N/A" : spCategory.getSelectedItem().toString();
            String role = isClient ? "client" : "business";

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(getContext(), "Пополнете ги сите полиња!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (role.equals("business")) {
                // Firebase за бизнис
                BusinessModel businessObj = new BusinessModel(name, category);
                FirebaseDatabase.getInstance("https://slatkishka-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference("Businesses").child(user).setValue(businessObj)

                        .addOnSuccessListener(aVoid -> {
                            // откако ќе се запише во Firebase, запиши и во SQLite
                            if(db.insertUser(user, pass, role, name, phone, category)) {
                                Toast.makeText(getContext(), "Регистриран бизнис!", Toast.LENGTH_SHORT).show();
                                getParentFragmentManager().popBackStack();
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Firebase грешка: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                // запис во SQLite за клиент
                if(db.insertUser(user, pass, role, name, phone, category)) {
                    Toast.makeText(getContext(), "Регистриран клиент!", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), "Корисничкото име веќе постои!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}