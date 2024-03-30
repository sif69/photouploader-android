package com.example.photouploader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class showActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Model> list;
    private MyAdapter adapter;

    private DatabaseReference root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new MyAdapter(this, list);
        recyclerView.setAdapter(adapter);

        // Get the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Get the UID of the current user
            String userId = user.getUid();
            // Reference the "Image" node under the user's UID
            root = FirebaseDatabase.getInstance().getReference("Image").child(userId);
            // Retrieve data from the database
            fetchData();
        }
    }

    private void fetchData() {
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                // Loop through the dataSnapshot to get each image URL
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String uniqueKey = dataSnapshot.getKey(); // Access the unique key
                    // Retrieve the imageUrl under this unique key
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                    Model model = new Model(imageUrl);
                    list.add(model);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }
}
