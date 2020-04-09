package com.example.corona_administrator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;

public class MyManagingActivity extends AppCompatActivity {
    //Connect to MongoDB server//
    private MongoClient mongoClient = new MongoClient("143.248.56.151",19191);
    private MongoDatabase database = mongoClient.getDatabase("corona19_app");

    private PeopleListAdapter listAdapter;
    private TextView numOfIsolated, stateHeader;
    private SearchView searchView;
    private Button myInformBtn, refreshBtn;
    private RecyclerView peopleListView;

    private Thread thrdRefreshPeopleList;

    private ArrayList<Person> people = new ArrayList<>();

    private Runnable notifyToAdapter = new Runnable() {
        @Override
        public void run() { MyManagingActivity.this.listAdapter.notifyDataSetChanged(); }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_activity_managing);

        initViews();
        setViewsListener();
    }

    @Override
    protected void onStart() {
        super.onStart();

        runRefreshListThread();
    }

    private void initViews(){
        numOfIsolated = findViewById(R.id.num_of_isolated);
        stateHeader = findViewById(R.id.state_header);

        searchView = findViewById(R.id.search_view);

        myInformBtn = findViewById(R.id.myinform_btn);
        refreshBtn = findViewById(R.id.refresh_btn);


        //header 추가 필요
        listAdapter = new PeopleListAdapter(people);

        peopleListView = findViewById(R.id.recycler_view);
        peopleListView.setLayoutManager(new LinearLayoutManager(this));
        peopleListView.setAdapter(listAdapter);
    }

    private void setViewsListener() {
        //상태 버튼(stateHeader) 클릭 이벤트 리스너 필요(상태 필터링) --> header 추가 후에
        /*stateHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder stateFilterBuilder = new AlertDialog.Builder(MyManagingActivity.this);
                final String[] states = {"전체", "정상", "통신안됨", "이탈"};

                stateFilterBuilder.setTitle("격리자 상태 선택")
                        .setSingleChoiceItems(states, 0, )
            }
        });*/


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                listAdapter.getFilter(PeopleListAdapter.FILTER_BY_TEXT).filter(query);
                runOnUiThread(notifyToAdapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listAdapter.getFilter(PeopleListAdapter.FILTER_BY_TEXT).filter(newText);
                runOnUiThread(notifyToAdapter);
                return false;
            }

        });

        //공무원 DB에서 정보 가져오는 걸로 수정 필요
        Intent intent = getIntent();
        final MyInformationDialog myInformationDialog = new MyInformationDialog(this, intent.getStringExtra("id"), intent.getStringExtra("name"), intent.getStringExtra("phone_num"));
        myInformationDialog.setCancelable(true);
        myInformBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                myInformationDialog.show();
            }
        });


        refreshBtn.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                runRefreshListThread();
                Toast.makeText(getApplicationContext(), "새로고침 완료", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void runRefreshListThread () {
        thrdRefreshPeopleList = new Thread(new RefreshListRunnable());
        thrdRefreshPeopleList.start();
    }



    class RefreshListRunnable implements Runnable{

        @Override
        public void run() {
            MongoCollection<Document> collection = database.getCollection("isolated_people");

            long count = collection.countDocuments();
            numOfIsolated.setText("자가격리자수: " + String.valueOf(count));

            MongoCursor<Document> cursor = collection.find().iterator();

            try{
                if (people.size() != 0)
                    people.clear();

                while(cursor.hasNext()) {
                    Document currentDoc = cursor.next();
                    String state = "";

                    switch (currentDoc.getInteger("state")){
                        case 1:
                            state = "정상";
                            break;
                        case 2:
                            state = "통신안됨";
                            break;
                        case 3:
                            state = "이탈";
                            break;
                    }
                    Person person = new Person(currentDoc.getString("name"), currentDoc.getString("birthdate"),
                                                currentDoc.getString("phone_number"), currentDoc.getString("address"), state);

                    people.add(person);
                    runOnUiThread(notifyToAdapter);
                }
            } finally {
                cursor.close();
            }

        }
    }
}
