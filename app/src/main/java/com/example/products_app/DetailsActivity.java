package com.example.products_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DetailsActivity extends AppCompatActivity {

    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent=getIntent();
        ImageView imageView =findViewById(R.id.image_details);
        FloatingActionButton floatingButton =findViewById(R.id.floatingAction);
        floatingButton.setOnClickListener(view -> {
            String url = "https://api.whatsapp.com/send?phone="+intent.getStringExtra("number")+"&text="+intent.getStringExtra("title");
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);

        });
        findViewById(R.id.back_btn).setOnClickListener(v -> finish());
        if(intent.getStringExtra("image").equals(""))
            imageView.setVisibility(View.GONE);
        else
        Glide.with(this)
                .load(intent.getStringExtra("image"))
                .centerCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imageView);
        TextView titleTextView=findViewById(R.id.title_details_tv);
        TextView detailsTextView=findViewById(R.id.detail_tv);
        titleTextView.setText(intent.getStringExtra("title"));
        detailsTextView.setText(intent.getStringExtra("details"));
    }
}