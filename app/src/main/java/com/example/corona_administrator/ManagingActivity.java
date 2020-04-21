package com.example.corona_administrator;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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

    private int numOfAbnormal;
    private int numOflessthanten;
    private int numOfmorethanten;
    private int numOfmorethanthirty;
    private int numOfmorethanhour;



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

        // notification test (notify when people list is refreshed)
        NotificationSomethings(numOfAbnormal, numOflessthanten, numOfmorethanten, numOfmorethanthirty, numOfmorethanhour);

        numOfAbnormal = 0;
        numOflessthanten = 0;
        numOfmorethanten = 0;
        numOfmorethanthirty = 0;
        numOfmorethanhour = 0;

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

                    if(!person.getState().equals("정상")){
                        numOfAbnormal += 1;
                        if(person.getStateTime().equals("10분 이하")){
                            numOflessthanten += 1;
                        }
                        else if(person.getStateTime().equals("10분 이상")){
                            numOfmorethanten += 1;
                        }
                        else if(person.getStateTime().equals("30분 이상")){
                            numOfmorethanthirty += 1;
                        }
                        else{
                            numOfmorethanhour += 1;
                        }
                    }
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


    public void NotificationSomethings(int numofabnormal, int numoflessthanten, int numofmorethanten, int numofmorethanthirty, int numofmorethanhour) {


        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        String title = Integer.toString(numofabnormal).concat("명 비정상 상태");

        String text = "1시간 이상:    ".concat(Integer.toString(numofmorethanhour)).concat("명\n 30분 이상 1시간 미만:    ").concat(Integer.toString(numofmorethanthirty))
                .concat("명\n10분 이상 30분 미만:    ").concat(Integer.toString(numofmorethanten)).concat("명\n10분 미만:    ").concat(Integer.toString(numoflessthanten)).concat("명");

        // pending intent part start
        //Intent notificationIntent = new Intent(this, ManagingActivity.class);
        //notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
        // pending intent part end


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground)) //BitMap 이미지 요구
                .setContentTitle(title)
                .setContentText("세부사항 드래그해서 보기")
                // 더 많은 내용이라서 일부만 보여줘야 하는 경우 아래 주석을 제거하면 setContentText에 있는 문자열 대신 아래 문자열을 보여줌
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
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
