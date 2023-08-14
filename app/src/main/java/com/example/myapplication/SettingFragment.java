package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class SettingFragment extends Fragment {
    private FirebaseAuth mFirebaseAuth;
    private Button btn_logout;
    private Switch mode_switch;
    private TextView modeStatus;
    private View view;
    private Button btn_send;
    private EditText em;
    private Button btn_alarm;
    private NotificationManager manager;
    private NotificationCompat.Builder builder;
    private static String CHANNEL_ID = "channel1";
    private static String CHANEL_NAME = "Channel1";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);

        //푸시 알림
        btn_alarm = view.findViewById(R.id.btn_alarm);
        btn_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoti();
            }

            private void showNoti() {
                builder = null;
                manager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    manager.createNotificationChannel(
                            new NotificationChannel(CHANNEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
                    );
                    builder = new NotificationCompat.Builder(requireContext(),CHANNEL_ID);
                }else{
                    builder = new NotificationCompat.Builder(requireContext());
                }

                builder.setContentTitle("Diemo");
                builder.setContentText("오늘 하루는 어떤가요?");
                builder.setSmallIcon(R.drawable.ic_alarm);

                builder.setAutoCancel(true);
                Intent intent = new Intent(requireContext(), WriteActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);

                Notification notification = builder.build();

                manager.notify(1,notification);
            }
        });
        //닉네임 변경
        EditText etNickname = view.findViewById(R.id.et_nickname);
        Button btn_saveNickname = view.findViewById(R.id.btn_save_nickname);
        btn_saveNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newNickname = etNickname.getText().toString().trim();

                updateNickname(newNickname);

                Toast.makeText(getActivity(), "닉네임 변경 완료", Toast.LENGTH_SHORT).show();
            }

            private void updateNickname(String newNickname) {
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("nickname", newNickname);
                editor.apply();
            }
        });
        //로그아웃
        mFirebaseAuth = FirebaseAuth.getInstance();
        btn_logout = view.findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(v -> {
            mFirebaseAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });
        //탈퇴처리
        Button btn_leave = view.findViewById(R.id.btn_leave);
        btn_leave.setOnClickListener(view1 -> {
            mFirebaseAuth.getCurrentUser().delete();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });

        //비밀번호 재설정
        em = view.findViewById(R.id.email);
        btn_send = view.findViewById(R.id.send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String e = em.getText().toString();
                FirebaseAuth.getInstance().sendPasswordResetEmail(e).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "send mail", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        //다크모드
        mode_switch = view.findViewById(R.id.mode_switch);
        modeStatus = view.findViewById(R.id.tv_mode);

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            modeStatus.setText("Dark Mode");
        } else if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_UNSPECIFIED){
            modeStatus.setText("UnSpecified Mode");
        } else {
            modeStatus.setText("Light Mode");
        }

        mode_switch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        return view;
    }
}
