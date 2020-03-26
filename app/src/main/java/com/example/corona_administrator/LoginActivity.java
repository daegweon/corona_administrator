package com.example.corona_administrator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;



public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onLoginButtonClicked(View view){
        String name = ((EditText)findViewById(R.id.name_editText)).getText().toString();
        String id = ((EditText)findViewById(R.id.id_editText)).getText().toString();
        String pw = ((EditText)findViewById(R.id.pw_editText)).getText().toString();
        String phone_num = ((EditText)findViewById(R.id.phone_editText)).getText().toString();


        if (name.isEmpty() || id.isEmpty() || pw.isEmpty() || phone_num.isEmpty())
        {
            Toast.makeText(this, "로그인 정보를 전부 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            if(isValidUser(id, pw))
            {
                //1. send log to DB
                //2. go to managing_list
            }
            else
            {//fail to log in
                final AlertDialog.Builder login_fail = new AlertDialog.Builder(this);
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

    private boolean isValidUser(String id, String pw){
        return false;
    }
}
