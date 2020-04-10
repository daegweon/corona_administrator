package com.example.corona_administrator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.isolated_people_inform_item, parent, false);
        return new PeopleItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PeopleItemViewHolder holder, int position) {
        holder.bind(filteredList.get(position));
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
                                if (!person.name.toLowerCase().contains(filteringText)
                                    && !person.address.toLowerCase().contains(filteringText))
                                    continue;
                                else
                                    break;

                            case FILTER_BY_STATE:
                                if (!person.state.toLowerCase().contains(filteringText))
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


        public PeopleItemViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.isolated_people_inform_item_name);
            address = itemView.findViewById(R.id.isolated_people_inform_item_address);
            state = itemView.findViewById(R.id.isolated_people_inform_item_state);
            state_time = itemView.findViewById(R.id.isolated_people_inform_item_time);
        }

        public void bind(Person person) {
            name.setText(person.name);
            address.setText(person.address);
            state.setText(person.state);
            state_time.setText(person.state_time);
        }
    }
}
