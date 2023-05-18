package com.example.adhd_analyzer;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.adhd_analyzer.api.UserApi;
import com.example.adhd_analyzer.api.WebServiceApi;
import com.example.adhd_analyzer.logger_sensors.ModuleDB;
import com.example.adhd_analyzer.user.UserDetails;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Button passwordClick, logoutClick, fullNameClick;
    EditText passwordText, passwordTextVe, fullNameText;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment setting_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting_fragment, container, false);
        passwordClick = view.findViewById(R.id.ok_change_password);
        fullNameClick = view.findViewById(R.id.ok_change_Fullname);
        logoutClick = view.findViewById(R.id.logout);
        passwordText = view.findViewById(R.id.change_password);
        passwordTextVe = view.findViewById(R.id.change_password_verify);
        fullNameText = view.findViewById(R.id.change_FULLname);

        logoutClick.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 UserDetails details = new UserDetails(login.theUser.getFullName(),login.theUser.getUserName(),login.theUser.getPassword());
                 ModuleDB.getUserDetailsDB(v.getContext()).userDao().delete(details);
                 login.theUser.setUserName("");
                 login.theUser.setFullName("");
                 login.theUser.setPassword("");
                 Intent intent = new Intent(getActivity(), MainActivity.class);
                 startActivity(intent);
                 getActivity().finish();
             }
         });

        passwordClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((passwordText.getText().toString()).equals(passwordTextVe.getText().toString())) {
                    String n_password = passwordText.getText().toString();
                    /*
                    SharedPreferences prefs = getActivity().getSharedPreferences("myPrefs", MODE_PRIVATE);
                    String username = prefs.getString("username", null);
                    //String password = prefs.getString("password", null);
                    String fullname = prefs.getString("fullName", null);
                    */

                    String fullname = login.theUser.getFullName();
                    String username = login.theUser.getUserName();

                            // Make HTTP GET request to login
                    WebServiceApi api = UserApi.getRetrofitInstance().create(WebServiceApi.class);
                    Call<Void> call = api.updateUser(username, fullname, n_password);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                // Update successful
                                Toast.makeText(getActivity(), "User updated successfully", Toast.LENGTH_SHORT).show();
                                passwordText.setText("");
                                passwordTextVe.setText("");
                                login.theUser.setPassword(n_password);
                            } else {
                                // Update failed
                                Toast.makeText(getActivity(), "Failed to update user: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            // Error occurred while making HTTP request
                            Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "The password verify is not correct", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fullNameClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String n_fullName = fullNameText.getText().toString();

                    String username = login.theUser.getUserName();
                    String password = login.theUser.getPassword();
                    String fullname = login.theUser.getFullName();
                    // Make HTTP GET request to login
                    WebServiceApi api = UserApi.getRetrofitInstance().create(WebServiceApi.class);
                    Call<Void> call = api.updateUser(username, n_fullName, password);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                // Update successful
                                Toast.makeText(getActivity(), "User updated successfully", Toast.LENGTH_SHORT).show();
                                fullNameText.setText("");
                                login.theUser.setFullName(n_fullName);
                            } else {
                                // Update failed
                                Toast.makeText(getActivity(), "Failed to update user: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            // Error occurred while making HTTP request
                            Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });

        return view;
    }
}

