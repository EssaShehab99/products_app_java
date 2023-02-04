package com.example.products_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;
import java.util.UUID;

public class ProductsManagerActivity extends AppCompatActivity {
    ProgressBar progressBAR;
    int SELECT_PICTURE = 200;
    FirebaseStorage storage;
    StorageReference storageReference;
    ImageView imageManager;
    Uri filePath;
    FirebaseFirestore db;
    EditText titleEditText, detailsEditText, numberEditText;
    StorageReference storageRef;
    String id;
    boolean isEdit = false;
    Product product;
    Button postBTN ;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_manager);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        storageRef = storage.getReference();
        db = FirebaseFirestore.getInstance();
        progressBAR = findViewById(R.id.progress_bar);
        postBTN = findViewById(R.id.post_btn);
        titleEditText = findViewById(R.id.title_et);
        detailsEditText = findViewById(R.id.details_et);
        numberEditText = findViewById(R.id.number_et);
        imageManager = findViewById(R.id.image_manager);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && (!bundle.isEmpty())) {
            isEdit = true;
            product = new Product(bundle.getString("id"), bundle.getString("title"), bundle.getString("details"), bundle.getString("imageUrl"), bundle.getString("number"));
            id = product.getId();
            titleEditText.setText(product.getTitle());
            detailsEditText.setText(product.getDetails());
            numberEditText.setText(product.getNumber());
            if (!product.getImageUrl().equals("")) {
                imageManager.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(bundle.getString("imageUrl"))
                        .centerCrop()
                        .into(imageManager);
            }
        }
        findViewById(R.id.delete_btn).setOnClickListener(v -> {
            deletePost();
        });

        postBTN.setText(isEdit ? "تعديل" : "نشر");
        postBTN.setOnClickListener(v -> {
            if (validate()) {
                uploadFile(filePath);
            }
        });
        findViewById(R.id.back_btn).setOnClickListener(v -> finish());
        findViewById(R.id.image_btn).setOnClickListener(v -> {
            imageChooser();
        });
        progressBAR.setVisibility(View.GONE);
        postBTN.setVisibility(View.VISIBLE);
    }


    void uploadFile(Uri imageLocaleUrl) {
        postBTN.setVisibility(View.GONE);
        progressBAR.setVisibility(View.VISIBLE);
        try {
            if (imageLocaleUrl == null) {
                if (isEdit) {
                    updatePost(product.getImageUrl());
                } else {
                    setPost(null);
                }
            } else {
                StorageReference ref
                        = storageReference
                        .child(UUID.randomUUID().toString());
                ref.putFile(imageLocaleUrl)
                        .addOnSuccessListener(
                                taskSnapshot -> {
                                    Task<Uri> result = Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getReference()).getDownloadUrl();
                                    result.addOnSuccessListener(uri -> {
                                        String downloadLink = uri.toString();
                                        if (isEdit) {
                                            updatePost(downloadLink);
                                        } else {
                                            setPost(downloadLink);
                                        }

                                    });
                                })

                        .addOnFailureListener(e -> {
                            showMessage("لم يتم النشر");

                        })
                        .addOnProgressListener(
                                taskSnapshot -> {
                                });
            }
        } catch (Exception e) {
            showMessage("لم يتم النشر");
        }
    }

    void deletePost() {

        db.collection("post").document(id).delete().addOnSuccessListener(command -> {
            showMessage("حذف ناجح");
        });
    }

    void updatePost(String urlFile) {
        try {
            db.collection("post").document(id)
                    .update(new Product(null, titleEditText.getText().toString(), detailsEditText.getText().toString(), urlFile, numberEditText.getText().toString()).toMap())
                    .addOnSuccessListener(documentReference -> {
                        showMessage("تم التعديل بنجاح");
                    })
                    .addOnFailureListener(e ->
                            {
                                showMessage("لم يتم التعديل");
                            }
                    );
        } catch (Exception e) {
            showMessage("لم يتم التعديل");
        }
    }

    void setPost(String urlFile) {
        try {
            db.collection("post")
                    .add(new Product(null, titleEditText.getText().toString(), detailsEditText.getText().toString(), urlFile, numberEditText.getText().toString()).toMap())
                    .addOnSuccessListener(documentReference -> {
                        showMessage("تم النشر بنجاح");
                    })
                    .addOnFailureListener(e ->
                            {
                                showMessage("لم يتم النشر");
                            }
                    );
        } catch (Exception e) {
            showMessage("لم يتم النشر");
        }
    }

    void showMessage(String text) {
        progressBAR.setVisibility(View.GONE);
        postBTN.setVisibility(View.VISIBLE);
        Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT).show();
    }

    void imageChooser() {

        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == SELECT_PICTURE && data != null && data.getData() != null) {
                filePath = data.getData();
                try {
                    imageManager.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath));
                    imageManager.setVisibility(View.VISIBLE);

                } catch (Exception ignored) {
                }
            }
        }
    }

    private Boolean validate() {

        if (TextUtils.isEmpty(titleEditText.getText())) {
            showToast(R.string.invalid_data);
            return false;
        } else if (TextUtils.isEmpty(detailsEditText.getText())) {
            showToast(R.string.invalid_data);
            return false;
        }
        return true;
    }

    private void showToast(int text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}