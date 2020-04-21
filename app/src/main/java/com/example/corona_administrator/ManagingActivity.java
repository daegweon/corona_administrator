package com.example.corona_administrator;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class ManagingActivity extends AppCompatActivity {

    public static final String NOTIFICATION_CHANNEL_ID = "10001";



    private PeopleListAdapter listAdapter;
    private TextView numOfIsolated, stateHeader;
    private SearchView searchView;
    private Button myInformBtn, refreshBtn;
    private RecyclerView peopleListView;

    private Toast mRefreshToast;

    //private Thread thrdRefreshPeopleList;
    private GetPeopleListTask getPeopleListTask;

    private ArrayList<Person> people = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managing);

        initViews();
        setViewsListener();

        // notification test (notify only at start)
        NotificationSomethings();
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

        listAdapter = new PeopleListAdapter(people);

        peopleListView = findViewById(R.id.recycler_view);
        peopleListView.setLayoutManager(new LinearLayoutManager(this));
        peopleListView.setAdapter(listAdapter);
    }

    private void setViewsListener() {

        stateHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.clearFocus();
                AlertDialog stateFilterDialog = getStateFilterDialog();
                stateFilterDialog.show();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                listAdapter.getFilter(PeopleListAdapter.FILTER_BY_TEXT).filter(newText);
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                listAdapter.getFilter(PeopleListAdapter.FILTER_BY_TEXT).filter(query);
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
                searchView.clearFocus();
                searchView.setQuery("", false);
                runRefreshListThread();

                if (mRefreshToast != null)
                    mRefreshToast.cancel();
                mRefreshToast = Toast.makeText(getApplicationContext(), "새로고침 완료", Toast.LENGTH_SHORT);
                mRefreshToast.show();
            }
        });
    }

    private AlertDialog getStateFilterDialog(){
        AlertDialog.Builder stateFilterBuilder = new AlertDialog.Builder(ManagingActivity.this);

        final String[] states = {"전체", "정상", "통신안됨", "이탈"};
        final EditText stateQuery = new EditText(this);

        stateFilterBuilder
                .setTitle("격리자 상태 선택")
                .setSingleChoiceItems(states, 0, new AlertDialog.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stateQuery.setText(states[which]);
                    }
                })
                .setPositiveButton(R.string.ok, new AlertDialog.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listAdapter.getFilter(PeopleListAdapter.FILTER_BY_STATE).filter(stateQuery.getText().toString());
                    }
                })

                .setNegativeButton(R.string.cancel, new AlertDialog.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return stateFilterBuilder.create();
    }


    private void runRefreshListThread () {
        people.clear();
        listAdapter.listRefresh();


        getPeopleListTask = new GetPeopleListTask();
        getPeopleListTask.execute();
    }

    class GetPeopleListTask extends AsyncTask<Void, Void, Void>{
        //https://youngest-programming.tistory.com/11
        //https://itmining.tistory.com/7

        HttpURLConnection urlConn;
        URL url;

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                url = new URL("http://143.248.53.196:8000/api/quarantined");
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("GET");
                urlConn.setRequestProperty("Content-Type", "application/quarantined");

                if (urlConn.getResponseCode() != urlConn.HTTP_OK){
                    urlConn.disconnect();
                    return null;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

                String line;
                String page = "";

                while ((line = reader.readLine()) != null){
                    page += line;
                }

                reader.close();

                JSONArray jsonPeople = new JSONObject(page).getJSONArray("people");
                JSONObject jsonPerson;
                for (int i = 0; i < jsonPeople.length(); i++){
                    jsonPerson = jsonPeople.getJSONObject(i);

                    Person person = new Person();

                    //need to set birthDate, State
                    person.setName(jsonPerson.getString("name"));
                    person.setAddress(jsonPerson.getString("addr") + ", " + jsonPerson.getString("addr_detail"));
                    person.setZipCode(jsonPerson.getString("zip_code"));
                    person.setTimeLastSent(jsonPerson.getLong("timeLastSent"));
                    person.setTimeLastStay(jsonPerson.getLong("timeLastStay"));
                    person.setPhoneNumber(jsonPerson.getString("contact"));

                    person.setState();

                    people.add(person);
                }


            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                urlConn.disconnect();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            listAdapter.notifyDataSetChanged();
            numOfIsolated.setText("자가격리자수 : " + String.valueOf(people.size()));
        }
    }


    public void NotificationSomethings() {


        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground)) //BitMap 이미지 요구
                .setContentTitle("상태바 드래그시 보이는 타이틀")
                .setContentText("상태바 드래그시 보이는 서브타이틀")
                // 더 많은 내용이라서 일부만 보여줘야 하는 경우 아래 주석을 제거하면 setContentText에 있는 문자열 대신 아래 문자열을 보여줌
                //.setStyle(new NotificationCompat.BigTextStyle().bigText("더 많은 내용을 보여줘야 하는 경우..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            CharSequence channelName  = "노티페케이션 채널";
            String description = "오레오 이상을 위한 것임";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName , importance);
            channel.setDescription(description);

            // 노티피케이션 채널을 시스템에 등록
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        }

        assert notificationManager != null;
        notificationManager.notify(1234, builder.build()); // 고유숫자로 노티피케이션 동작시킴

    }


}
