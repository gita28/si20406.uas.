package com.angga.econtacts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.angga.efoodies.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private EditText mainFullName, mainPhone;
    private Button mainButtonSave, mShowBtn;
    private FirebaseFirestore db;
    private String uName, uPhone,uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainFullName = findViewById(R.id.mainetFullName);
        mainPhone = findViewById(R.id.mainetPhone);
        mainButtonSave = findViewById(R.id.mainButtonSave);
        mShowBtn = findViewById(R.id.mShowBtn);

        db= FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            mainButtonSave.setText("Update");
            uName = bundle.getString("uName");
            uId = bundle.getString("uId");
            uPhone = bundle.getString("uPhone");
            mainFullName.setText(uName);
            mainPhone.setText(uPhone);
        }else{
            mainButtonSave.setText("Save");
        }

        mShowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ShowActivity.class));
            }
        });

        mainButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mainFullName.getText().toString();
                String phone = mainPhone.getText().toString();

                Bundle bundle1 = getIntent().getExtras();
                if (bundle1 != null){
                    String id = uId;
                    updateToFireStore(id, name, phone);
                }else {
                    String id = UUID.randomUUID().toString();
                    saveToFireStore(id, name, phone);
                }
            }
        });
    }

    private void updateToFireStore(String id, String name, String phone){

        db.collection("Documents").document(id).update("name", name, "phone", phone)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Data Updated!!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToFireStore(String id, String name, String phone){
        if (!name.isEmpty() && !phone.isEmpty()){
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", id);
            map.put("name", name);
            map.put("phone", phone);

            db.collection("Documents").document(id).set(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "Data Saved !!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Failed !!", Toast.LENGTH_SHORT).show();

                }
            });

        }else
            Toast.makeText(this, "Empty Fields not Allowed", Toast.LENGTH_SHORT).show();

    }
}