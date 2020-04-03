package com.example.corona_administrator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Vector;

import static com.mongodb.client.model.Filters.eq;

public class ManagingActivity extends AppCompatActivity {

    //Connect to MongoDB server//
    MongoClient mongoClient = new MongoClient("143.248.56.151",19191);
    MongoDatabase database = mongoClient.getDatabase("corona19_app");

    //LIst of Isolated people//
    Vector<ObjectItem> ObjectItemData = new Vector<>();

    private ArrayAdapterItem adapter;
    //격리자 정보 다이얼로그//
    private CustomDialog mCustomDialog;

    //리스트뷰 업데이트 쓰레드//
    private Runnable updateUI = new Runnable() {
        public void run() {
            ManagingActivity.this.adapter.notifyDataSetChanged();
        }
    };

    private void addItem(ObjectItem item) {
        // ArrayList에 데이터를 추가하고, 화면에 반영하기 위해 runOnUiThread()를 호출하여 실시간 갱신한다.
        this.ObjectItemData.add(item);
        this.runOnUiThread(updateUI);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managing);

        final TextView num_of_isolated = findViewById(R.id.num_of_isolated); //자가격리자수 표기 텍스트
        ListView listview = (ListView)findViewById(R.id.listview1) ; //자가격리자 정보 리스트
        View header = getLayoutInflater().inflate(R.layout.listview_header, null, false) ; //리스트뷰 헤더 (이름, 격리주소, 격리지역 이탈여부)

        // 검색 텍스트
        EditText inputSearch = (EditText) findViewById(R.id.inputSearch);


        listview.addHeaderView(header);

        new Thread(){
            @Override
            public void run() {
                MongoCollection<Document> collection = database.getCollection("isolated_people");
                long count = collection.countDocuments();
                num_of_isolated.setText("자가격리자수 : "+String.valueOf(count));
                MongoCursor<Document> cursor = collection.find().iterator();
                try {
                    while (cursor.hasNext()) {
                        Document currentDoc = cursor.next();
                        String str_state = "";
                        switch (currentDoc.getInteger("state")){
                            //case 0은 모든 경우(All)
                            case 1:
                                str_state = "정상";
                                break;
                            case 2:
                                str_state = "통신안됨";
                                break;
                            case 3:
                                str_state = "이탈";
                                break;
                        }
                        ObjectItem item = new ObjectItem(currentDoc.getString("name"),currentDoc.getString("birthdate"),currentDoc.getString("phone_number"),currentDoc.getString("address"),str_state);
                        addItem(item);
                    }
                } finally {
                    cursor.close();
                }
            }
        }.start();

        // our adapter instance
        adapter = new ArrayAdapterItem(this, R.layout.listlayout, ObjectItemData);
        listview.setAdapter(adapter);

        //리스트뷰에서 격리자 정보를 클릭했을때
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //리스트뷰의 첫번째 item의 인덱스(i)가 1이다. 따라서 0이상부터 시작 0은 리스트뷰 헤더를 가리킨다.
                if(i>0) {
                    String personName = ObjectItemData.get(i - 1).personName;
                    String birthDate = ObjectItemData.get(i - 1).birthDate;
                    String phoneNumber = ObjectItemData.get(i - 1).phoneNumber;
                    String Address = ObjectItemData.get(i - 1).address;

                    mCustomDialog = new CustomDialog(ManagingActivity.this, personName, birthDate, phoneNumber, Address);
                    mCustomDialog.setCancelable(true);
                    mCustomDialog.show();
                }
                else{
                    System.out.println("listview header was clicked");
                }
            }
        });

        //edit text에 정보를 입력할 때
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {

                final CharSequence search_keyword = cs.toString().toLowerCase();
                new Thread(){
                    @Override
                    public void run() {
                        Block<Document> printBlock = new Block<Document>() {
                            @Override
                            public void apply(final Document document) {
                                String str_state = "";
                                switch (document.getInteger("state")){
                                    case 1:
                                        str_state = "정상";
                                        break;
                                    case 2:
                                        str_state = "통신안됨";
                                        break;
                                    case 3:
                                        str_state = "이탈";
                                        break;
                                }
                                ObjectItem item = new ObjectItem(document.getString("name"),document.getString("birthdate"),document.getString("phone_number"),document.getString("address"),str_state);
                                addItem(item);
                            }
                        };
                        MongoCollection<Document> collection = database.getCollection("isolated_people");
                        ObjectItemData.clear();
                        if(search_keyword.toString().equals("")){ //전체 리스트
                            MongoCursor<Document> cursor = collection.find().iterator();
                            try {
                                while (cursor.hasNext()) {
                                    Document currentDoc = cursor.next();
                                    String str_state = "";
                                    switch (currentDoc.getInteger("state")){
                                        case 1:
                                            str_state = "정상";
                                            break;
                                        case 2:
                                            str_state = "통신안됨";
                                            break;
                                        case 3:
                                            str_state = "이탈";
                                            break;
                                    }
                                    ObjectItem item = new ObjectItem(currentDoc.getString("name"),currentDoc.getString("birthdate"),currentDoc.getString("phone_number"),currentDoc.getString("address"),str_state);
                                    addItem(item);
                                }
                            } finally {
                                cursor.close();
                            }
                        }
                        else { //필터링
                            MongoCursor<Document> cursor = collection.find().iterator();
                            try {
                                while (cursor.hasNext()) {
                                    Document currentDoc = cursor.next();
                                    String str_state = "";

                                    //필터링이 이루어지는 부분
                                    String cur_name = currentDoc.getString("name").toLowerCase();
                                    String cur_address = currentDoc.getString("address").toLowerCase();

                                    if(cur_name.contains(search_keyword) || cur_address.contains(search_keyword)){
                                        //필터링된 결과물 한 행 반영
                                        switch (currentDoc.getInteger("state")){
                                            case 1:
                                                str_state = "정상";
                                                break;
                                            case 2:
                                                str_state = "통신안됨";
                                                break;
                                            case 3:
                                                str_state = "이탈";
                                                break;
                                        }
                                        ObjectItem item = new ObjectItem(currentDoc.getString("name"),currentDoc.getString("birthdate"),currentDoc.getString("phone_number"),currentDoc.getString("address"),str_state);
                                        addItem(item);
                                    }
                                }
                            } finally {
                                cursor.close();
                            }
                            }
                        }
                    }.start();
                adapter.clear();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        Button my_inform_btn = (Button)findViewById(R.id.myinform_btn); // 나의 정보
        Button refresh_btn = (Button)findViewById(R.id.refresh_btn); //새로고침 버튼
        final TextView num_of_isolated = (TextView)findViewById(R.id.num_of_isolated); //격리자 수
        final TextView state_header_txt = (TextView)findViewById(R.id.state_header_txt); //격리지역 이탈여부

        //나의 정보 버튼 클릭 이벤트 리스너
        my_inform_btn.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = getIntent();

                String id = intent.getStringExtra("id");
                String name = intent.getStringExtra("name");
                String phone_num = intent.getStringExtra("phone_num");

                MyInformationDialog myInformationDialog = new MyInformationDialog(ManagingActivity.this, id, name, phone_num);
                myInformationDialog.setCancelable(true);
                myInformationDialog.show();
            }

        });

        //새로고침 버튼 클릭 이벤트 리스너
        refresh_btn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                new Thread(){
                    @Override
                    public void run() {
                        MongoCollection<Document> collection = database.getCollection("isolated_people");
                        long count = collection.countDocuments();
                        num_of_isolated.setText("자가격리자수 : "+String.valueOf(count));
                        MongoCursor<Document> cursor = collection.find().iterator();
                        try {
                            while (cursor.hasNext()) {
                                Document currentDoc = cursor.next();
                                String str_state = "";
                                switch (currentDoc.getInteger("state")){
                                    case 1:
                                        str_state = "정상";
                                        break;
                                    case 2:
                                        str_state = "통신안됨";
                                        break;
                                    case 3:
                                        str_state = "이탈";
                                        break;
                                }
                                ObjectItem item = new ObjectItem(currentDoc.getString("name"),currentDoc.getString("birthdate"),currentDoc.getString("phone_number"),currentDoc.getString("address"),str_state);
                                addItem(item);
                            }
                        } finally {
                            cursor.close();
                        }
                    }
                } .start();
                adapter.clear();
                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "새로고침 완료", Toast.LENGTH_SHORT).show(); //새로고침 완료 시 토스트 메세지
            }
        });

        //격리지역 이탈여부 텍스트 클릭 시 이벤트리스너
        state_header_txt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Dialog dialog = onCreateDialog(null);
                dialog.show();
            }
        });
    }

    //격리지역 이탈여부 필터링 다이얼로그
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ArrayList selectedItems = new ArrayList();  // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final CharSequence[] items = {"전체","정상","통신안됨","이탈"};
        // Set the dialog title

        builder.setTitle(R.string.dialog_name)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setSingleChoiceItems(items, -1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int index) {
                                selectedItems.add(index);
                            }
                        })
                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the selectedItems results somewhere
                        // or return them to the component that opened the dialog
                        new Thread(){
                            @Override
                            public void run() {
                                Block<Document> printBlock = new Block<Document>() {
                                    @Override
                                    public void apply(final Document document) {
                                        String str_state = "";
                                        switch (document.getInteger("state")){
                                            case 1:
                                                str_state = "정상";
                                                break;
                                            case 2:
                                                str_state = "통신안됨";
                                                break;
                                            case 3:
                                                str_state = "이탈";
                                                break;
                                        }
                                        ObjectItem item = new ObjectItem(document.getString("name"),document.getString("birthdate"),document.getString("phone_number"),document.getString("address"),str_state);
                                        addItem(item);
                                    }
                                };
                                MongoCollection<Document> collection = database.getCollection("isolated_people");
                                ObjectItemData.clear();
                                if(selectedItems.get(0).equals(0)){ //전체 리스트
                                    MongoCursor<Document> cursor = collection.find().iterator();
                                    try {
                                        while (cursor.hasNext()) {
                                            Document currentDoc = cursor.next();
                                            String str_state = "";
                                            switch (currentDoc.getInteger("state")){
                                                case 1:
                                                    str_state = "정상";
                                                    break;
                                                case 2:
                                                    str_state = "통신안됨";
                                                    break;
                                                case 3:
                                                    str_state = "이탈";
                                                    break;
                                            }
                                            ObjectItem item = new ObjectItem(currentDoc.getString("name"),currentDoc.getString("birthdate"),currentDoc.getString("phone_number"),currentDoc.getString("address"),str_state);
                                            addItem(item);
                                        }
                                    } finally {
                                        cursor.close();
                                    }
                                }
                                else { //필터링
                                    if (collection.countDocuments(eq("state", selectedItems.get(0))) != 0) {
                                        collection.find(eq("state", selectedItems.get(0))).forEach(printBlock);
                                    } else {
                                        ManagingActivity.this.runOnUiThread(updateUI);
                                    }
                                    selectedItems.clear();
                                }
                            }
                        }.start();
                        Toast.makeText(getApplicationContext(), "선택 완료", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
}
