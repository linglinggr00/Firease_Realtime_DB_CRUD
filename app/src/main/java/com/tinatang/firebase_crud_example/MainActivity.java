package com.tinatang.firebase_crud_example;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    String id, name, new_Image ,new_Status; //new
    String res_text, emo, user_name, res_status; //response
    String image, status, text, uid; //send
    String userId;

    TextView showText;

    Bitmap bmp;
    String newImage_URL, Image_URL;
    String Uid;
    int Identity; //身分有無重複

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 以匿名登入
        mAuth = FirebaseAuth.getInstance();

        Uid = mAuth.getInstance().getCurrentUser().getUid();

        Button addData_btn = findViewById(R.id.addData_Btn);
        Button getData_btn = findViewById(R.id.downloadData_Btn);
        Button updateData_btn = findViewById(R.id.updateData_Btn);
        showText = findViewById(R.id.showText);

        //放入測試圖片
        bmp = BitmapFactory.decodeResource(getResources(),R.drawable.test_pic);

        //初始化資料
        initData();

        //監聽Bot
        GetUserData();


        addData_btn.setOnClickListener(view -> {
            //new身分之前檢查有沒有重複的名稱
            checkIdentity();

            // 新增Data到Firebase
            //uploadNewImage(bmp);
            //storeUserName();
            //setData();
            //StoreNewUserData();

        });


        updateData_btn.setOnClickListener(view -> {
            uploadImage(bmp);
            //setData();
            //UpdateUserData();
        });

        //getData_btn.setOnClickListener(view -> GetUserData());

    }

    //確認有無重複身分
    private void checkIdentity() {
        /*抓DB*/
        getPreUser();
        //待處理

        if(Identity==1) {
            //上傳圖片 (new)
            uploadNewImage(bmp);
            storeUserName();
        }
        else {
            Toast.makeText(MainActivity.this,"IdentityError: 名稱已被使用",Toast.LENGTH_SHORT).show();
        }
    }

    //確認有無重複的名字
    private void getPreUser() {
        Query checkUser = FirebaseDatabase.getInstance().getReference().child("UserName").orderByChild("name");
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot Snapshot:snapshot.getChildren()) {
                    String value=Snapshot.getValue().toString();
                    Log.d("showuser", value);

                    name = "Tina";
                    String chname = "{name="+name+"}";
                    if(chname.equals(value)) {
                        Identity=2;
                        break;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    //上傳圖片 (update  data)
    private void uploadImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG,100, baos);
        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("Images")
                .child("pic.jpeg");

        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getDownloadUrl(reference);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,"ImgError: "+e.getCause(),Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void UpdateUserData() {
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("User");

        reference.child("image").setValue(image);
        reference.child("uid").setValue(uid);
        reference.child("text").setValue(text);
        reference.child("status").setValue(status);

    }

    //上傳圖片 (new data)
    private void uploadNewImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG,100, baos);
        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("New_Images")
                .child("test_pic.jpeg");

        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(taskSnapshot -> getNewDownloadUrl(reference))
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this,"ImgError: "+e.getCause(),Toast.LENGTH_SHORT).show());

    }

    private void getNewDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Log.d("TAG", "newImage Success: "+uri);
                    newImage_URL = uri.toString();

                    setNewData();
                });
    }

    private void setNewData() {
        //new data
        id = Integer.toString(i);
        name = "Tina";
        new_Image = newImage_URL;
        new_Status = "true";

        StoreNewUserData();
        //storeUserName();
    }

    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Log.d("TAG", "Image Success: "+uri);
                    Image_URL = uri.toString();
                    setData();
                });
    }

    //初始化資料
    int i = 1;
    private void initData() {

        //new data
        id = Integer.toString(i);
        name = "匿名";
        new_Image = "未設定";

        //send data
        uid = Uid;
        text = "未設定";
        image = "未設定";
        status = "false";

    }

    //得到Firebase的Data
    private void GetUserData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Bot");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("status").getValue().toString().equals("true")) {
                        res_text = snapshot.child("res_text").getValue().toString();
                        emo = snapshot.child("emo").getValue().toString();
                        user_name = snapshot.child("user_name").getValue().toString();
                        Uid = snapshot.child("uid").getValue().toString();
                        res_status = snapshot.child("status").getValue().toString();
                        //Toast.makeText(MainActivity.this,"Emo:"+Emo,Toast.LENGTH_SHORT).show();
                        showText.setText("Response: " + res_text + "  Emotion: " + emo + "  Name: " + user_name + "  Uid: " + Uid + "  Status: " + res_status);
                        setBotStatus();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,"QQError",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setBotStatus() {
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("Bot");

        reference.child("status").setValue("false");
    }

    // 設定Data
    private void setData() {

        //send data
        uid = Uid;
        text = "Hello!!";
        image = Image_URL;
        status = "true";

        UpdateUserData();
    }

    @Override
    public void onStart() {
        super.onStart();
        // 確認使用者登入
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        mAuth.signInAnonymously();
        uid = Uid;
    }

    // 新增Data到Firebase
    public void StoreNewUserData() {
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("New");

        reference.child("id").setValue(id);
        reference.child("name").setValue(name);
        reference.child("new_Image").setValue(new_Image);
        reference.child("new_Status").setValue(new_Status);

    }

    //儲存使用者名稱
    private void storeUserName() {
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("UserName");

        int id = (int)(Math.random()* 999 + 1);
        userId = Integer.toString(id);

        reference.child(uid+userId).child("name").setValue("Tina");

    }


    private void updateUI(FirebaseUser currentUser) {
    }



}