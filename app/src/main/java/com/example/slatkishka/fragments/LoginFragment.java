package com.example.slatkishka.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slatkishka.database.DatabaseHelper;
import com.example.slatkishka.R;
import com.example.slatkishka.SessionManager;

// фрагмент за најавување во апликацијата

public class LoginFragment extends Fragment {
    private EditText etUsername, etPassword;
    private CheckBox cbRememberMe;
    private Button btnLogin;
    private TextView tvSignup;
    private DatabaseHelper db;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // иницијализација на views
        etUsername = view.findViewById(R.id.etLoginUsername);
        etPassword = view.findViewById(R.id.etLoginPassword);
        cbRememberMe = view.findViewById(R.id.cbRememberMe);
        btnLogin = view.findViewById(R.id.btnLogin);
        tvSignup = view.findViewById(R.id.tvGoToSignup);

        db = new DatabaseHelper(getContext());
        session = new SessionManager(getContext());

        btnLogin.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim(); // се добиваат корисничкото име
            String pass = etPassword.getText().toString().trim(); // и лозинката!

            if (user.isEmpty() || pass.isEmpty()) { // ако барем едно од полињата е празно
                Toast.makeText(getContext(), "Пополнете ги сите полиња!", Toast.LENGTH_SHORT).show();
            } else {
                // пребарување во SQLite дата базата
                boolean check = db.checkUsernamePassword(user, pass);
                if (check) {
                    // улога и податоци на корисникот
                    String role = db.getUserRole(user);
                    String name = db.getFullName(user);
                    String phone = db.getPhone(user);

                    // ги зачувуваме податоците во SharedPreferences за да може ProfileFragment да ги прочита!
                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", user);
                    editor.putString("role", role);
                    editor.putString("name", name);
                    editor.putString("phone", phone);
                    editor.apply();

                    // ако е штиклирано "Remember me" да активира SessionManager
                    if (cbRememberMe.isChecked()) {
                        session.createLoginSession(user, role);
                    }

                    // транзиција кон листата
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, new ListFragment());
                    transaction.commit();
                } else {
                    Toast.makeText(getContext(), "Погрешно корисничко име или лозинка!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // ако корисникот нема профил, се префрла на SignupFragment
        tvSignup.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SignupFragment())
                .addToBackStack(null)
                .commit());

        return view;
    }
}