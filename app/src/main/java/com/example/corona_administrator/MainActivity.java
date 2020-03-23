package com.example.corona_administrator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    //Connect to MongoDB server//
    MongoClient mongoClient = new MongoClient("143.248.56.151",19191);
    MongoDatabase database = mongoClient.getDatabase("corona19_app");

    //LIst of Isolated people//
    Vector<ObjectItem> ObjectItemData = new Vector<>();

    private ArrayAdapterItem adapter;

    private Runnable updateUI = new Runnable() {
        public void run() {
            MainActivity.this.adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView num_of_isolated = (TextView)findViewById(R.id.num_of_isolated); //자가격리자수 표기 텍스트
        ListView listview = (ListView)findViewById(R.id.listview1) ; //자가격리자 정보 리스트
        View header = getLayoutInflater().inflate(R.layout.listview_header, null, false) ; //리스트뷰 헤더 (이름, 격리주소, 격리지역 이탈여부)

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
                        ObjectItem item = new ObjectItem(currentDoc.getString("name"),currentDoc.getString("address"),currentDoc.getString("state"));
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        Button refresh_btn = (Button)findViewById(R.id.refresh_btn);
        final TextView num_of_isolated = (TextView)findViewById(R.id.num_of_isolated);
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
                                ObjectItem item = new ObjectItem(currentDoc.getString("name"),currentDoc.getString("address"),currentDoc.getString("state"));
                                addItem(item);
                            }
                        } finally {
                            cursor.close();
                        }
                    }
                }.start();
                adapter.clear();
                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "새로고침 완료", Toast.LENGTH_SHORT).show(); //새로고침 완료 시 토스트 메세지
            }
        });
    }

    private void addItem(ObjectItem item) {
        // ArrayList에 데이터를 추가하고, 화면에 반영하기 위해 runOnUiThread()를 호출하여 실시간 갱신한다.
        this.ObjectItemData.add(item);
        this.runOnUiThread(updateUI);
    }
}
