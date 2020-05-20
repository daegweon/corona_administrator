package com.example.corona_administrator;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


/*https://dev-imaec.tistory.com/27*/
public class PeopleListAdapter extends RecyclerView.Adapter<PeopleListAdapter.PeopleItemViewHolder> {
    static class Payload{
        final static int FOLD_UNFOLD = 100;
    }

    final static int FILTER_BY_TEXT = 0;
    final static int FILTER_BY_STATE = 1;



    private ArrayList<Person> list = new ArrayList<>();

    private static Context mContext;

    private static int selectedPosition = -1;
    private static int prePosition = -1;


    @NonNull
    @Override
    public PeopleItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View item_layout = LayoutInflater.from(mContext).inflate(R.layout.people_list_item, parent, false);
        PeopleItemViewHolder viewHolder = new PeopleItemViewHolder(item_layout);
        this.setListeners(viewHolder);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PeopleItemViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty())
            onBindViewHolder(holder, position);
        else{
            for (Object payload : payloads){
                int type = (int)payload;
                if (type == Payload.FOLD_UNFOLD)
                    holder.foldUnfold(position);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PeopleItemViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void setListeners(final PeopleItemViewHolder viewHolder){
        viewHolder.header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (prePosition == viewHolder.getAdapterPosition())
                {
                    if (viewHolder.details.getVisibility() == View.VISIBLE)
                        selectedPosition = -1;
                    else
                        selectedPosition = viewHolder.getAdapterPosition();
                }
                else
                {
                    selectedPosition = viewHolder.getAdapterPosition();

                    notifyItemChanged(prePosition, Payload.FOLD_UNFOLD);

                    prePosition = viewHolder.getAdapterPosition();
                }

                notifyItemChanged(viewHolder.getAdapterPosition(), Payload.FOLD_UNFOLD);
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
        list.add(person);
    }

    public void listRefresh(){
        list.clear();
        selectedPosition = -1;
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


    static class PeopleItemViewHolder extends RecyclerView.ViewHolder{

        private TextView name, address, state, phone, detail_state;

        private ConstraintLayout details;
        private Button call, show_location;
        private LinearLayout header;


        public PeopleItemViewHolder(@NonNull View itemView) {
            super(itemView);

            header = itemView.findViewById(R.id.item_header);
            name = itemView.findViewById(R.id.item_header_name);
            state = itemView.findViewById(R.id.item_header_state);

            details = itemView.findViewById(R.id.item_details);
            phone = itemView.findViewById(R.id.item_details_phone);
            address = itemView.findViewById(R.id.item_details_address);
            detail_state = itemView.findViewById(R.id.item_details_state);

            call = itemView.findViewById(R.id.item_details_call);
            show_location = itemView.findViewById(R.id.item_details_show_location);
        }

        public void bind(Person person) {
            name.setText(person.getName());
            address.setText(person.getAddress());
            state.setText(person.getState());
            phone.setText(person.getPhoneNumber());
            detail_state.setText(person.getState() + person.getStateTime());

            if ( ! state.getText().toString().equals(Person.STATE_NORMAL)) {
                header.setBackgroundResource(R.drawable.list_back_abnormal);
                //details.setBackgroundResource();
                //call.setBackgroundResouce();
                //show_location.setBackgroundResouce();
            }
        }

        private void foldUnfold(int position){
            if (selectedPosition == position)
                details.setVisibility(View.VISIBLE);
            else
                details.setVisibility(View.GONE);
        }
    }
}
