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
    }

    @NonNull
    @Override
    public PeopleItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.people_inform_item, parent, false);
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

        private TextView name, address, state, state_time;
        private String phoneNumber, birthDate;

        private LinearLayout when_expand;
        private Button call, show_location;

        private Person mPerson;

        private int position;


        public PeopleItemViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.people_inform_item_name);
            address = itemView.findViewById(R.id.people_inform_item_address);
            state = itemView.findViewById(R.id.people_inform_item_state);
            state_time = itemView.findViewById(R.id.people_inform_item_time);

            when_expand = itemView.findViewById(R.id.people_inform_item_when_expand);
            call = itemView.findViewById(R.id.people_inform_item_phone_call);
            show_location = itemView.findViewById(R.id.people_inform_item_show_location);

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
                        notifyItemChanged(position);
                    }

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
            state_time.setText(mPerson.getStateTime());

            birthDate = mPerson.getBirthDate();
            phoneNumber = mPerson.getPhoneNumber();

            setListeners();

            changeVisibility(selectedPerson.get(position));
        }

        private void setListeners(){
            call.setOnClickListener(new Button.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("tel:" + Uri.encode(phoneNumber));
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

        private void changeVisibility(final boolean isExpanded){
            int dpValue = 40;
            float d = mContext.getResources().getDisplayMetrics().density;
            int height = (int) (dpValue * d);

            ValueAnimator va = isExpanded? ValueAnimator.ofInt(0, height) : ValueAnimator.ofInt(height, 0);

            va.setDuration(300);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();

                    when_expand.getLayoutParams().height = value;
                    when_expand.requestLayout();

                    when_expand.setVisibility(isExpanded? View.VISIBLE : View.GONE);
                }
            });

            va.start();
        }
    }
}
