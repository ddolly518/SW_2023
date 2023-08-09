package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class WriteActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mEmotion;
    private RadioGroup radioGroup;
    private TextView selection;
    private EditText et_content;
    private Button btn_write;
    private String randomMessage;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        database = FirebaseDatabase.getInstance();
        mDatabaseRef = database.getReference("diemo").child("write");
        radioGroup = findViewById(R.id.radio_group);
        selection = findViewById(R.id.selection);
        et_content = findViewById(R.id.et_content);
        btn_write = findViewById(R.id.btn_write);
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.radio_happy:
                    selection.setText("happy");
                    break;
                case R.id.radio_sad:
                    selection.setText("sad");
                    break;
                case R.id.radio_angry:
                    selection.setText("angry");
                    break;
                case R.id.radio_soso:
                    selection.setText("soso");
                    break;
            }
        });
        btn_write.setOnClickListener(view -> {
            long mNow = System.currentTimeMillis();
            Date mDate = new Date(mNow);
            SimpleDateFormat mFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
            String sDate = mFormat.format(mDate);
            String strContent = et_content.getText().toString();
            String sId = firebaseUser.getUid();

            List<String> messages = new ArrayList<>();
            String sEmotion = selection.getText().toString();
            mEmotion = database.getReference("diemo").child(sEmotion);
            mEmotion.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    messages.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String message = dataSnapshot.getValue(String.class);
                        messages.add(message);
                    }
                    Collections.shuffle(messages);
                    if(!messages.isEmpty()) {
                        randomMessage = messages.get(0);
                        String sKey = mDatabaseRef.push().getKey();
                        if (strContent.length() > 0) {
                            if (sKey != null) {
                                mDatabaseRef.child(sKey).child("content").setValue(strContent);
                                mDatabaseRef.child(sKey).child("date").setValue(sDate);
                                mDatabaseRef.child(sKey).child("idToken").setValue(sId);
                                mDatabaseRef.child(sKey).child("emotion").setValue(randomMessage);
                                Toast.makeText(WriteActivity.this, "저장 성공", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(WriteActivity.this, DiaryFragment.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(WriteActivity.this, "저장 실패", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(WriteActivity.this, "저장 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(WriteActivity.this, "저장 실패", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}