package com.example.products_app;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Product {
    private String id;
    private String title;
    private String details;
    private String imageUrl;
    private String number;

    public Product(String id, String title, String details, String imageUrl, String number) {
        this.id = id;
        this.title = title;
        this.details = details;
        this.imageUrl = imageUrl;
        this.number = number;
    }

    public Product() {

    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public String getNumber() {
        return number;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static Product fromMap(Map<String, Object> map, String id) throws ParseException {
        return new Product(
                id,
                Objects.requireNonNull(map.getOrDefault("title","")).toString(),
                Objects.requireNonNull(map.getOrDefault("details", "")).toString(),
                Objects.requireNonNull(map.getOrDefault("imageUrl", "")).toString(),
                Objects.requireNonNull(map.getOrDefault("number", "")).toString()
        );
    }

    public final Map<String, Object> toMap() {
        return new HashMap<String, Object>() {{
            put("title", title);
            put("details", details);
            put("imageUrl", imageUrl);
            put("number", number);
        }};
    }
}
