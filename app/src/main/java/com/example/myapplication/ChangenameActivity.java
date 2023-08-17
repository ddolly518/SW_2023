package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangenameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_setting);

        EditText etNickname = findViewById(R.id.et_nickname);
        Button btnSaveNickname = findViewById(R.id.btn_save_nickname);

        btnSaveNickname.setOnClickListener(view -> {
            String newNickname = etNickname.getText().toString().trim();

            updateNickname(newNickname);

            Toast.makeText(ChangenameActivity.this, "닉네임 변경 완료", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateNickname(String newNickname) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("nickname", newNickname);
        editor.apply();
    }
}
