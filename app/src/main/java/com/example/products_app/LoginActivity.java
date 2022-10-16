package com.example.products_app;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText userName,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.back_btn).setOnClickListener(v -> finish());
         userName=findViewById(R.id.user_name);
          password=findViewById(R.id.password);
        findViewById(R.id.login_btn).setOnClickListener(v -> {
            if (validate()&& userName.getText().toString().equals("admin") &&password.getText().toString().equals("admin")){
                MainActivity.IS_LOGIN=true;
                setResult(RESULT_OK);
                finish();
            }else {
                showToast(R.string.not_validate);
            }
        });

    }
    private Boolean validate () {

        if (userName.getText() == null) {
            showToast(R.string.invalid_data);
            return false;
        } else if (password.getText() == null) {
            showToast(R.string.invalid_data);
            return false;
        }
        return true;
    }
    private void showToast ( int text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}