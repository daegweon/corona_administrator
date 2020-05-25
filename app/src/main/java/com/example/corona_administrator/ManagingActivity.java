package com.example.corona_administrator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Filter;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.corona_administrator.ListAdapter.FilterByStatus;
import com.example.corona_administrator.ListAdapter.FilterByText;
import com.example.corona_administrator.ListAdapter.PeopleListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class ManagingActivity extends AppCompatActivity {
    private PeopleListAdapter mListAdapter;

    private TextView mID, mName, mPhone;
    private Button mNumOfIsolatedBtn, mStateSelectBtn;
    private SearchView mSearchView;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Toast mToast;
    private Filter mFilter;

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
        mListAdapter.listRefresh();
        new GetPeopleListTask().execute();
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
}
