package com.example.products_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    CustomAdapter adapter;
    List<Product> productList = new ArrayList<>();
    RecyclerView recyclerView;
    public static boolean IS_LOGIN = false;
    FloatingActionButton actionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionButton=findViewById(R.id.floatingAction);
        actionButton.setVisibility(IS_LOGIN?View.VISIBLE:View.GONE);
        ImageView menuBTN = findViewById(R.id.menu_btn);
        menuBTN.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, menuBTN);
            popup.getMenuInflater().inflate(IS_LOGIN?R.menu.second_menu:R.menu.main_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.login) {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivityForResult(intent,1);
                }else   if (item.getItemId() == R.id.logout) {
                    IS_LOGIN=false;
                    actionButton.setVisibility(View.GONE);

                }
                return true;
            });
            popup.show();

        });
        fetchNews(this);

        adapter = new CustomAdapter(this, productList);
        recyclerView = findViewById(R.id.recycle_view);
        adapter.setOnItemClickListener((position, v) -> {
            Intent intent;
            if(IS_LOGIN){
                intent = new Intent(this, ProductsManagerActivity.class);
                intent.putExtra("id", productList.get(position).getId());
                intent.putExtra("title", productList.get(position).getTitle());
                intent.putExtra("details", productList.get(position).getDetails());
                intent.putExtra("imageUrl", productList.get(position).getImageUrl());
                intent.putExtra("number", productList.get(position).getNumber());
            }else {
                intent = new Intent(this, DetailsActivity.class);
                intent.putExtra("image", productList.get(position).getImageUrl());
                intent.putExtra("title", productList.get(position).getTitle());
                intent.putExtra("details", productList.get(position).getDetails());
                intent.putExtra("number", productList.get(position).getNumber());
            }
            startActivity(intent);
        });
        actionButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductsManagerActivity.class);
            startActivity(intent);

        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                actionButton.setVisibility(IS_LOGIN?View.VISIBLE:View.GONE);
            }
        }
    }

    void fetchNews(Context context) {
        FirebaseFirestore   db = FirebaseFirestore.getInstance();
        CollectionReference ref= db.collection("post");

        ref.addSnapshotListener((snapshots, e) -> {

            if (e != null) {
                showToast(R.string.error);
                return;
            }

            try {
                assert snapshots != null;
                for (DocumentChange dc : snapshots.getDocumentChanges()) {

                    switch (dc.getType()) {
                        case ADDED:
                            productList.add(Product.fromMap(dc.getDocument().getData(), dc.getDocument().getId()));
                            break;
                        case MODIFIED:
                            productList.remove(indexOf(dc.getDocument().getId()));
                            productList.add(Product.fromMap(dc.getDocument().getData(), dc.getDocument().getId()));
                            break;
                        case REMOVED:
                            productList.remove(indexOf(dc.getDocument().getId()));
                            break;
                    }
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    CustomAdapter myAdapter = new CustomAdapter(context, productList);
                    recyclerView.setAdapter(myAdapter);
                }
            } catch (Exception exception) {
                showToast(R.string.error);
            }

        });
    }

    int indexOf(String docId) {
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getId().equals(docId))
                return productList.indexOf(productList.get(i));
        }
        return 0;
    }
    private void showToast ( int text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}