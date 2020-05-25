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
    private EditText mName, mID, mPW, mPhone;
    private Button mLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
    }

    private void initViews(){
        mName = findViewById(R.id.edit_name);
        mID = findViewById(R.id.edit_id);
        mPW = findViewById(R.id.edit_password);
        mPhone = findViewById(R.id.edit_phone);

        mLoginBtn = findViewById(R.id.button_login);
        mLoginBtn.setOnClickListener(this);
    }

    //수정필요 - 서버에서 아이디 비밀번호 확인
    private boolean isValidUser(){
        return true;
    }

    private boolean isLogInfoEmpty(){
        if (mName.getText().toString().trim().isEmpty()
                || mID.getText().toString().trim().isEmpty()
                || mPW.getText().toString().trim().isEmpty()
                || mPhone.getText().toString().trim().isEmpty())
            return true;
        else
            return false;
    }

    @Override
    public void onClick(View v) {
        if (isLogInfoEmpty())
            Toast.makeText(getApplicationContext(), "로그인 정보를 전부 입력해주세요", Toast.LENGTH_SHORT).show();
        else
        {
            if(isValidUser())
            {
                Intent managingActivity = new Intent(getApplicationContext(), ManagingActivity.class);

                Manager.getInstance().setInfo(mName.getText().toString(), mID.getText().toString(), mPhone.getText().toString());

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
}
