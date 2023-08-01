package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class DiaryFragment extends Fragment {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mRef;
    private DatabaseReference mDatabase;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<WriteInfo> arrayList;
    private View view;
    private Button btn_share;
    private Button btn_remove;
    private FloatingActionButton btn_floating;
    private String nickname;
    private String contents;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_diary, container, false);
        // Inflate the layout for this fragment

        database = FirebaseDatabase.getInstance();
        mDatabaseRef = database.getReference("diemo").child("write");
        mDatabase = database.getReference("diemo").child("bulletin");
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        long mNow = System.currentTimeMillis();
        Date mDate = new Date(mNow);
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
        String sDate = mFormat.format(mDate);

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        mRef = database.getReference("diemo").child("UserAccount");
        String userToken = firebaseUser.getUid();
        Query query1 = mRef.orderByChild("idToken").equalTo(userToken);
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserAccount userAccount = snapshot.getValue(UserAccount.class);
                    nickname = userAccount.getNickname();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DiaryFragment", String.valueOf(error.toException()));
            }
        });

        Query query = mDatabaseRef.orderByChild("idToken").equalTo(userToken);

        query.limitToLast(1).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    WriteInfo writeInfo = snapshot.getValue(WriteInfo.class);
                    contents = writeInfo.getContent();
                    arrayList.add(writeInfo);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DiaryFragment", String.valueOf(error.toException()));
            }
        });
        adapter = new WriteAdapter(arrayList, getActivity());
        recyclerView.setAdapter(adapter);

        btn_share = view.findViewById(R.id.btn_share);
        btn_share.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("공유");
            builder.setMessage("공유하시겠습니까?");
            builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                String sKey = mDatabase.push().getKey();
                if (sKey != null) {
                    mDatabase.child(sKey).child("content").setValue(contents);
                    mDatabase.child(sKey).child("name").setValue(nickname);
                    mDatabase.child(sKey).child("date").setValue(sDate);
                    Toast.makeText(getActivity(), "공유 성공", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "공유 실패", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
            builder.show();
        });

        btn_remove = view.findViewById(R.id.btn_remove);
        btn_remove.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("삭제");
            builder.setMessage("삭제하시겠습니까?");
            builder.setPositiveButton("Yes", (dialogInterface, i) -> query.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        dataSnapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }));
            builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
            builder.show();
        });

        btn_floating = view.findViewById(R.id.btn_floating);
        btn_floating.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), WriteActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
