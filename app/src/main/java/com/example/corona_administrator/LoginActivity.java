package com.example.corona_administrator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class LoginActivity extends AppCompatActivity implements Button.OnClickListener{
    private String name, id, pw, phone_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button login_btn = findViewById(R.id.login_btn);
        login_btn.setOnClickListener(this);
    }

    //수정필요 - 서버에서 아이디 비밀번호 확인
    private boolean isValidUser(){
        return true;
    }


    @Override
    public void onClick(View v) {
        name = ((EditText)findViewById(R.id.name_editText)).getText().toString();
        id = ((EditText)findViewById(R.id.id_editText)).getText().toString();
        pw = ((EditText)findViewById(R.id.pw_editText)).getText().toString();
        phone_num = ((EditText)findViewById(R.id.phone_editText)).getText().toString();


        if (name.isEmpty() || id.isEmpty() || pw.isEmpty() || phone_num.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "로그인 정보를 전부 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }


        if(isValidUser())
        {
            Intent managingActivity = new Intent(getApplicationContext(), ManagingActivity.class);

            managingActivity.putExtra("name", name);
            managingActivity.putExtra("id", id);
            managingActivity.putExtra("phone_num", phone_num);

            startActivity(managingActivity);
            finish();
        }

        else
        {//fail to log in
            AlertDialog.Builder login_fail = new AlertDialog.Builder(getApplicationContext());
            login_fail.setTitle("로그인 실패");
            login_fail.setMessage("로그인에 실패하였습니다. 입력하신 정보를 확인해주세요");
            login_fail.setPositiveButton("확인", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }

            });

            login_fail.show();
        }
    }
}
