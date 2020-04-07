package com.example.corona_administrator;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

//reference : https://re-build.tistory.com/27

public class CustomDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private TextView btn_ok;
    private String personName;
    private String address;
    private String birthDate;
    private SpannableString phoneNumberText; // 전화 바로 걸기 링크를 위한 전화번호 : 010-XXXX_XXXX
    private String phoneNumber; // 순수 전화번호
    private SpannableString to_map; //지도 보기

    public CustomDialog(Context context, String personName, String birthDate,  String phoneNumber, String address) {
        super(context);
        mContext = context;
        this.personName=personName;
        this.birthDate = birthDate;
        this.phoneNumberText = SpannableString.valueOf("전화번호 : "+phoneNumber);
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.to_map = SpannableString.valueOf("지도 보기");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog);

        btn_ok = (TextView) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);


        TextView dialog_name = (TextView)findViewById(R.id.dialog_name);
        TextView dialog_birthdate = (TextView)findViewById(R.id.dialog_birthdate);
        TextView dialog_phonenumber = (TextView)findViewById(R.id.dialog_phonenumber);
        TextView dialog_address = (TextView)findViewById(R.id.dialog_address);
        // 지도 보기 텍스트뷰
        TextView dialog_to_map = (TextView)findViewById(R.id.dialog_to_map);

        //격리자정보 다이얼로그 화면 구성//
        dialog_name.setText("이름 : "+personName);
        dialog_birthdate.setText("생년월일 : "+birthDate);
        dialog_address.setText("주소 : "+address);

        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                //전화번호 클릭 시 다이얼 intent로 이동 후 전화번호 복사//
                Uri uri = Uri.parse("tel:"+Uri.encode(phoneNumber));
                Intent callIntent = new Intent(Intent.ACTION_DIAL, uri);
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(callIntent);
            }

            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(true);
            }
        };

        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                //지도 보기 클릭 시 map activity로 주소 가지고 넘겨주기//
                Intent intent = new Intent(mContext, MapsActivity.class);
                intent.putExtra("quarantine_address", address);
                mContext.startActivity(intent);

            }

            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(true);
            }
        };


        //전화번호 클릭할 수 있게 설정하는 부분//
        phoneNumberText.setSpan(clickableSpan1,7,phoneNumberText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        dialog_phonenumber.setText(phoneNumberText);
        dialog_phonenumber.setMovementMethod(LinkMovementMethod.getInstance());
        dialog_phonenumber.setHighlightColor(Color.TRANSPARENT);


        //지도 보기 클릭할 수 있게 설정하는 부분//
        to_map.setSpan(clickableSpan2,0,to_map.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        dialog_to_map.setText(to_map);
        dialog_to_map.setMovementMethod(LinkMovementMethod.getInstance());
        dialog_to_map.setHighlightColor(Color.TRANSPARENT);

    }

    //격리자정보 다이얼로그에서 OK버튼 클릭 시 닫힘//
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                dismiss();
                break;
        }
    }
}