package com.example.corona_administrator;

import android.app.AlertDialog;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.graphics.Rect;

import android.content.Intent;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Filter;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.NotificationCompat;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.corona_administrator.ListAdapter.FilterByStatus;
import com.example.corona_administrator.ListAdapter.FilterByText;
import com.example.corona_administrator.ListAdapter.PeopleListAdapter;
import com.example.corona_administrator.messaging.PushMessagingService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//import java.util.ArrayList;
//import java.util.TimerTask;
//import java.util.concurrent.TimeUnit;



public class ManagingActivity extends AppCompatActivity {
    private PeopleListAdapter mListAdapter;


    private TextView mID, mName, mPhone;
    private Button mNumOfIsolatedBtn, mStateSelectBtn;
    private SearchView mSearchView;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

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

    private Toast mToast;
    private Filter mFilter;


    private static Handler timerHandler = new Handler();

    // refresh?
    private Runnable mRunRefresh = new Runnable() {
        @Override
        public void run() {
            runRefreshListThread();
        }
    };

    private static final int REFRESH_PERIOD = 30000;

    //runRefreshListThread();

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String title = intent.getStringExtra("TITLE");
            String body = intent.getStringExtra("BODY");
            Log.d("ManagingActivity", "mMessageReceiver: " + title + " / " + body);

            runRefreshListThread();
            mToast = Toast.makeText(getApplicationContext(), "refresh from FCM", Toast.LENGTH_SHORT);
            mToast.show();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver,
                new IntentFilter(PushMessagingService.ACTION_MSG_FROM_SERVER)
        );
    }


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

        mSearchView.clearFocus();
        runRefreshListThread();
    }

    //@Override
    //protected void onStop() {
    //    super.onStop();

    //    timerHandler.removeCallbacks(mRunRefresh);
    //}

    private void initViews(){
        mName = findViewById(R.id.text_name);
        mID = findViewById(R.id.text_id);
        mPhone = findViewById(R.id.text_phone);

        mName.setText("이름: " + Manager.getInstance().getName());
        mID.setText("공무원 ID: " + Manager.getInstance().getID());
        mPhone.setText("휴대폰 번호: " + Manager.getInstance().getPhone());

        mNumOfIsolatedBtn = findViewById(R.id.button_isolated_num);
        mStateSelectBtn = findViewById(R.id.button_state);

        mSearchView = findViewById(R.id.search);

        mListAdapter = new PeopleListAdapter();

        mRecyclerView = findViewById(R.id.list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mListAdapter);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = 30;
            }
        });

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh);
    }

    private void setViewsListener() {

        mStateSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.clearFocus();
                AlertDialog stateFilterDialog = getStateFilterDialog();
                stateFilterDialog.show();
            }
        });

        mSearchView.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mSearchView.clearFocus();
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                runRefreshListThread();

                if (mToast != null)
                    mToast.cancel();

                mToast = Toast.makeText(getApplicationContext(), "새로고침 완료", Toast.LENGTH_SHORT);
                mToast.show();
            }
        });


        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("Managing/newText: ", newText);
                
                if (mFilter == null || !(mFilter instanceof FilterByText))
                    mFilter = new FilterByText(mListAdapter);

                mFilter.filter(newText);

                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                onQueryTextChange(query);
                mSearchView.clearFocus();
                return false;
            }
        });

    }

    private AlertDialog getStateFilterDialog(){
        AlertDialog.Builder stateFilterBuilder = new AlertDialog.Builder(ManagingActivity.this);

        final String[] states = {"전체", Person.STATE_NORMAL, Person.STATE_LOST_COMMUN, Person.STATE_LEFT};
        final StringBuilder state = new StringBuilder(states[0]);

        stateFilterBuilder
                .setTitle("격리자 상태 선택")
                .setSingleChoiceItems(states, 0, new AlertDialog.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Log.d("AlertDialog", "Clicked: " + String.valueOf(which));
                        state.delete(0, state.length());
                        state.append(states[which]);
                    }
                })
                .setPositiveButton(R.string.ok, new AlertDialog.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mFilter == null || !(mFilter instanceof FilterByStatus))
                            mFilter = new FilterByStatus(mListAdapter);

                        mFilter.filter(state);
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
        //timerHandler.removeCallbacks(mRunRefresh);
        timerHandler.removeCallbacksAndMessages(null);

        mListAdapter.listRefresh();
        new GetPeopleListTask().execute();

        NotificationSomethings(numOfAbnormal, numOflessthanten, numOfmorethanten, numOfmorethanthirty, numOfmorethanhour);

        numOfAbnormal = 0;
        numOflessthanten = 0;
        numOfmorethanten = 0;
        numOfmorethanthirty = 0;
        numOfmorethanhour = 0;

        timerHandler.postDelayed(mRunRefresh, REFRESH_PERIOD);
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

                    //need to set birthDate
                    person.setName(jsonPerson.getString("name"));
                    person.setAddress(jsonPerson.getString("addr") /*+ ", " + jsonPerson.getString("addr_detail")*/);
                    person.setZipCode(jsonPerson.getString("zip_code"));
                    person.setTimeLastSent(jsonPerson.getLong("timeLastSent"));
                    person.setTimeLastStay(jsonPerson.getLong("timeLastStay"));
                    person.setPhoneNumber(jsonPerson.getString("contact"));

                    person.setState();

                    mListAdapter.addItem(person);


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
            mListAdapter.notifyDataSetChanged();
            mNumOfIsolatedBtn.setText("자가격리자수 : " + String.valueOf(mListAdapter.getItemCount()));

            mSwipeRefreshLayout.setRefreshing(false);
        }
    }


    public void NotificationSomethings(int numofabnormal, int numoflessthanten, int numofmorethanten, int numofmorethanthirty, int numofmorethanhour) {


        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        String title = Integer.toString(numofabnormal).concat("명 비정상 상태");

        String text = "1시간 이상:    ".concat(Integer.toString(numofmorethanhour)).concat("명\n 30분 이상 1시간 미만:    ").concat(Integer.toString(numofmorethanthirty))
                .concat("명\n10분 이상 30분 미만:    ").concat(Integer.toString(numofmorethanten)).concat("명\n10분 미만:    ").concat(Integer.toString(numoflessthanten)).concat("명");


        //Intent intent = new Intent(this, ManagingActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground)) //BitMap 이미지 요구
                .setContentTitle(title)
                .setContentText("세부사항 드래그해서 보기")
                // 더 많은 내용이라서 일부만 보여줘야 하는 경우 아래 주석을 제거하면 setContentText에 있는 문자열 대신 아래 문자열을 보여줌
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);
                //.setContentIntent(pIntent);

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
