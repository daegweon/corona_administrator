package com.example.corona_administrator;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


/*https://dev-imaec.tistory.com/27*/
public class PeopleListAdapter extends RecyclerView.Adapter<PeopleListAdapter.PeopleItemViewHolder> {
    final static int FILTER_BY_TEXT = 0;
    final static int FILTER_BY_STATE = 1;

    private ArrayList<Person> list = new ArrayList<>();

    private static Context mContext;

    private static int selected = -1;

    @NonNull
    @Override
    public PeopleItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.people_list_item, parent, false);
        return new PeopleItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PeopleItemViewHolder holder, int position) {
        holder.bind(list.get(position), position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addItem(Person person){
        list.add(person);
    }

    public void listRefresh(){
        list.clear();
        selected = -1;
        //filteredList = unFilteredList;
    }

    /*public Filter getFilter(final int filter) {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filteringText = constraint.toString().toLowerCase();

                if (filter == FILTER_BY_TEXT && filteringText.isEmpty()
                        || filter == FILTER_BY_STATE && filteringText.equals("전체"))
                    filteredList = unFilteredList;

                else
                {
                    ArrayList<Person> filteringList = new ArrayList<>();

                    for (Person person : unFilteredList)
                    {

                        switch (filter){
                            case FILTER_BY_TEXT:
                                if (!person.getName().toLowerCase().contains(filteringText)
                                    && !person.getAddress().toLowerCase().contains(filteringText))
                                    continue;
                                else
                                    break;

                            case FILTER_BY_STATE:
                                if (!person.getState().toLowerCase().contains(filteringText))
                                    continue;
                        }

                        filteringList.add(person);
                    }

                    filteredList = filteringList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (ArrayList<Person>) results.values;
                notifyDataSetChanged();
            }
        };
    }*/


    class PeopleItemViewHolder extends RecyclerView.ViewHolder{

        private TextView name, address, state, phone, detail_state;

        private ConstraintLayout details;
        private Button call, show_location;
        private View mView;

        private Person mPerson;
        private int mPosition;


        public PeopleItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            name = itemView.findViewById(R.id.item_header_name);
            state = itemView.findViewById(R.id.item_header_state);

            details = itemView.findViewById(R.id.item_details);
            phone = itemView.findViewById(R.id.item_details_phone);
            address = itemView.findViewById(R.id.item_details_address);
            detail_state = itemView.findViewById(R.id.item_details_state);

            call = itemView.findViewById(R.id.item_details_call);
            show_location = itemView.findViewById(R.id.item_details_show_location);

            setListeners();
        }

        public void bind(Person person, int position) {
            mPerson = person;
            mPosition = position;

            name.setText(mPerson.getName());
            address.setText(mPerson.getAddress());
            state.setText(mPerson.getState());
            phone.setText(mPerson.getPhoneNumber());
            detail_state.setText(mPerson.getState() + mPerson.getStateTime());

            if (selected == mPosition)
                details.setVisibility(View.VISIBLE);
            else
                details.setVisibility(View.GONE);
        }

        private void setListeners(){
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyItemChanged(selected);
                    selected = mPosition;
                    notifyItemChanged(mPosition);
                }
            });

            call.setOnClickListener(new Button.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("tel:" + Uri.encode(mPerson.getPhoneNumber()));
                    mContext.startActivity(new Intent(Intent.ACTION_DIAL, uri));
                }
            });

            show_location.setOnClickListener(new Button.OnClickListener(){

                @Override
                public void onClick(View v) {
                    //지도 보여주기
                    // 당장은 격리주소만 받아감, 나중에는 현재주소도 같이 받아야 함
                    Intent intent = new Intent(mContext, MapsActivity.class);
                    intent.putExtra("quarantine_address", mPerson.getAddress());
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
