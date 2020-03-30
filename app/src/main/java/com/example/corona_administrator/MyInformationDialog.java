package com.example.corona_administrator;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MyInformationDialog extends Dialog{
    private String id;
    private String name;
    private String phone_num;

    private TextView btn_ok;

    public MyInformationDialog(Context context, String id, String name, String phone_num){
        super(context);

        this.id = id;
        this.name = name;
        this.phone_num = phone_num;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_information);

        btn_ok = findViewById(R.id.my_inform_btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                dismiss();
            }

        });

        TextView id_txtView = findViewById(R.id.my_inform_id);
        TextView name_txtView = findViewById(R.id.my_inform_name);
        TextView phone_num_txtView = findViewById(R.id.my_inform_phone_num);

        id_txtView.setText("공무원 ID: " + id);
        name_txtView.setText("이름: " + name);
        phone_num_txtView.setText("휴대폰 번호: " + phone_num);
    }
}
