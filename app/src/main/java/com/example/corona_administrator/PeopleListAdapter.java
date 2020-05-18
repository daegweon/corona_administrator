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

    private ArrayList<Person> unFilteredList;
    private ArrayList<Person> filteredList;

    private Context mContext;

    private SparseBooleanArray selectedPerson = new SparseBooleanArray();
    private int prePosition = -1;

    public PeopleListAdapter(ArrayList<Person> list){
        super();
        this.unFilteredList = list;
        this.filteredList = list;
    }

    public void listRefresh(){
        filteredList = unFilteredList;
        selectedPerson.clear();
    }

    @NonNull
    @Override
    public PeopleItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.people_list_item, parent, false);
        return new PeopleItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PeopleItemViewHolder holder, int position) {
        holder.bind(filteredList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }


    public Filter getFilter(final int filter) {
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
    }


    class PeopleItemViewHolder extends RecyclerView.ViewHolder{

        private TextView name, address, state, phone, detail_state;

        private ConstraintLayout details;
        private Button call, show_location;

        private Person mPerson;

        private int position;


        public PeopleItemViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.item_header_name);
            state = itemView.findViewById(R.id.item_header_state);

            details = itemView.findViewById(R.id.item_details);
            phone = itemView.findViewById(R.id.item_details_phone);
            address = itemView.findViewById(R.id.item_details_address);
            detail_state = itemView.findViewById(R.id.item_details_state);

            call = itemView.findViewById(R.id.item_details_call);
            show_location = itemView.findViewById(R.id.item_details_show_location);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedPerson.get(position))
                        selectedPerson.delete(position);
                    else{
                        selectedPerson.delete(prePosition);
                        selectedPerson.put(position, true);
                    }

                    if (prePosition != -1) {
                        notifyItemChanged(prePosition);
                    }

                    notifyItemChanged(position);

                    prePosition = position;
                }
            });
        }

        public void bind(Person person, int position) {
            this.mPerson = person;
            this.position = position;


            name.setText(mPerson.getName());
            address.setText(mPerson.getAddress());
            state.setText(mPerson.getState());
            phone.setText(mPerson.getPhoneNumber());
            detail_state.setText(mPerson.getState() + mPerson.getStateTime());

            setListeners();

            if (selectedPerson.get(position))
                details.setVisibility(View.VISIBLE);
            else
                details.setVisibility(View.GONE);
        }

        private void setListeners(){
            call.setOnClickListener(new Button.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("tel:" + Uri.encode(phone.getText().toString()));
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
