package com.example.redlife;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class FirstActivity extends AppCompatActivity {
    public static final String BLOOD="com.example.redlife.BLOOD";
    private FirebaseAuth mAuth;
    private ImageButton LogoutButton;
    private Button NextBtn;
    private Spinner spinner;
    String bloodGroup=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        mAuth=FirebaseAuth.getInstance();
        LogoutButton=findViewById(R.id.logoutButton);
        NextBtn=findViewById(R.id.imageButton);
        spinner=findViewById(R.id.Spinner);
        bloodGroup=spinner.getSelectedItem().toString();
        ArrayAdapter<CharSequence>adapter=ArrayAdapter.createFromResource(this,R.array.Blood_groups,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        onSpinnerItemCLick();
        LogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                SendUserToLoginActivity();
            }
        });

        NextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bloodGroup.equals("Choose a blood group")){
                    Toast.makeText(FirstActivity.this, "Select blood group", Toast.LENGTH_SHORT).show();
                }else {
                    Intent Login=new Intent(FirstActivity.this, UserMapsActivity.class);
                    Login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Login.putExtra(BLOOD ,bloodGroup);
                    startActivity(Login);
                }



            }
        });


}

    private void onSpinnerItemCLick() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                if(parent.getItemAtPosition(i).equals("Choose a blood group")){
                    Toast.makeText(FirstActivity.this, "Select Blood group", Toast.LENGTH_SHORT).show();
                }
                else {
                    bloodGroup=parent.getItemAtPosition(i).toString();
                    Log.i("KEY",bloodGroup);

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void SendUserToLoginActivity() {
        Intent Login=new Intent(FirstActivity.this,LoginActivity.class);
        startActivity(Login);
        finish();


    }

    private void SendUsertoUserActivity() {
        Intent Login=new Intent(FirstActivity.this, UserMapsActivity.class);
        startActivity(Login);
        finish();

    }


    }
