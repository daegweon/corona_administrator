package com.example.corona_administrator.ListAdapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.corona_administrator.MapsActivity;
import com.example.corona_administrator.Person;
import com.example.corona_administrator.R;

import java.util.ArrayList;


/*https://dev-imaec.tistory.com/27*/
public class PeopleListAdapter extends RecyclerView.Adapter<PeopleListAdapter.PeopleViewHolder> {
    final static int NORMAL = 0;
    final static int ABNORMAL = 1;

    private ArrayList<Person> unFilteredList = new ArrayList<>();
    private ArrayList<Person> filteredList = new ArrayList<>();

    private static Context mContext;

    private static int selectedPosition = -1;
    private static int prePosition = -1;

    static class PeopleViewHolder extends RecyclerView.ViewHolder{

        private TextView name, address, state, phone, detailState, phone2;

        private ConstraintLayout details;
        private Button checkResult, call, show_location;
        private LinearLayout header;


        public PeopleViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            initCommonViews();

            if (viewType == ABNORMAL)
                initAbnormalViews();
        }

        private void initCommonViews(){
            header = itemView.findViewById(R.id.item_header);
            name = itemView.findViewById(R.id.item_header_name);
            state = itemView.findViewById(R.id.item_header_state);

            details = itemView.findViewById(R.id.item_details);
            phone = itemView.findViewById(R.id.item_details_phone);
            phone2 = itemView.findViewById(R.id.item_details_phone2);
            address = itemView.findViewById(R.id.item_details_address);
            detailState = itemView.findViewById(R.id.item_details_state);

            checkResult = itemView.findViewById(R.id.item_details_check_result);
            call = itemView.findViewById(R.id.item_details_call);
            show_location = itemView.findViewById(R.id.item_details_show_location);
        }

        private void initAbnormalViews(){
            header.setBackgroundResource(R.drawable.item_button_red_back_press);
            details.setBackgroundResource(R.drawable.item_button_red_back_default);

            checkResult.setBackgroundResource(R.drawable.item_button_red_selector);
            call.setBackgroundResource(R.drawable.item_button_red_selector);
            show_location.setBackgroundResource(R.drawable.item_button_red_selector);
        }

        public void bind(Person person) {
            name.setText(person.getName());
            address.setText(person.getAddress());
            state.setText(person.getState());
            phone.setText(person.getPhoneNumber());
            phone2.setText(person.getPhoneNumber());
            detailState.setText(person.getState() + person.getStateTime());

            if (selectedPosition == getAdapterPosition())
                details.setVisibility(View.VISIBLE);
            else
                details.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public PeopleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View item_layout = LayoutInflater.from(mContext).inflate(R.layout.people_list_item, parent, false);
        PeopleViewHolder viewHolder = new PeopleViewHolder(item_layout, viewType);
        this.setListeners(viewHolder);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PeopleViewHolder holder, int position) {
        holder.bind(filteredList.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    @Override
    public int getItemViewType(int position) {
        String state = filteredList.get(position).getState();

        if (state.equals(Person.STATE_NORMAL))
            return NORMAL;
        else
            return ABNORMAL;
    }

    private void setListeners(final PeopleViewHolder viewHolder){
        viewHolder.header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = viewHolder.getAdapterPosition();

                if (prePosition == -1)
                {
                    prePosition = position;
                    selectedPosition = position;
                }
                else if (prePosition == position && viewHolder.details.getVisibility() == View.VISIBLE)
                {
                    selectedPosition = -1;
                }
                else
                {
                    selectedPosition = position;
                    notifyItemChanged(prePosition);
                    prePosition = position;
                }

                notifyItemChanged(position);
            }
        });

        viewHolder.call.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("tel:" + Uri.encode(viewHolder.phone.getText().toString()));
                mContext.startActivity(new Intent(Intent.ACTION_DIAL, uri));
            }
        });

        viewHolder.show_location.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                //지도 보여주기
                // 당장은 격리주소만 받아감, 나중에는 현재주소도 같이 받아야 함
                Intent intent = new Intent(mContext, MapsActivity.class);
                intent.putExtra("quarantine_address", viewHolder.address.getText().toString());
                mContext.startActivity(intent);
            }
        });
    }

    public void addItem(Person person){
        unFilteredList.add(person);
        filteredList = unFilteredList;
    }

    public void listRefresh(){
        selectedPosition = -1;
        unFilteredList.clear();
    }

    public void setFilteredList(ArrayList<Person> fList){
        filteredList = fList;
    }

    public ArrayList<Person> getUnFilteredList() {
        return unFilteredList;
    }
}
