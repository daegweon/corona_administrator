package com.example.corona_administrator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    String name, id, pw, phone_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onLoginButtonCliked(View view){
        name = ((EditText)findViewById(R.id.name_editText)).getText().toString();
        id = ((EditText)findViewById(R.id.id_editText)).getText().toString();
        pw = ((EditText)findViewById(R.id.pw_editText)).getText().toString();
        phone_num = ((EditText)findViewById(R.id.phone_editText)).getText().toString();

        if (name.isEmpty() || id.isEmpty() || pw.isEmpty() || phone_num.isEmpty())
        {
            Toast.makeText(this, "로그인 정보를 전부 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            checkUserInfo(id, pw);
        }
    }

    public void checkUserInfo(String id, String pw){

    }
}
