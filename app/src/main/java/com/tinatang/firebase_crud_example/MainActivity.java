package com.tinatang.firebase_crud_example;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    String Id, Name, New_Image;
    String Res_Text, Emo, User_Name;

    TextView showText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 以匿名登入
        mAuth = FirebaseAuth.getInstance();

        Button sendData_btn = findViewById(R.id.addData_Btn);
        Button getData_btn = findViewById(R.id.downloadData_Btn);
        showText = findViewById(R.id.showText);

        initData();

        sendData_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 新增Data到Firebase
                setData();
                StoreNewUserData();

            }
        });


        getData_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetUserData();
            }
        });

    }

    private void initData() {
        Id = "1";
        Name = "Tina";
        New_Image = "new_001.jpg";
    }

    //得到Firebase的Data
    private void GetUserData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Bot");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Res_Text = snapshot.child("Res_Text").getValue().toString();
                    Emo = snapshot.child("Emo").getValue().toString();
                    User_Name = snapshot.child("User_Name").getValue().toString();
                    //Toast.makeText(MainActivity.this,"Emo:"+Emo,Toast.LENGTH_SHORT).show();
                    showText.setText("Response: "+Res_Text+"  Emotion: "+Emo+"  Name: "+User_Name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,"QQError",Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 設定Data
    int i = 1;
    private void setData() {
        Id = Integer.toString(i);
        New_Image = "new_00"+i+".jpg";
        i += 1;
    }

    @Override
    public void onStart() {
        super.onStart();
        // 確認使用者登入
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        mAuth.signInAnonymously();
    }

    // 新增Data到Firebase
    public void StoreNewUserData() {
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("New");

        //reference.setValue("First Record");
        UserDataClass addNewUser = new UserDataClass(Id, Name, New_Image);

        reference.setValue(addNewUser);

    }

    private void updateUI(FirebaseUser currentUser) {
    }



}