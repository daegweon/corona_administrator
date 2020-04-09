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

    private ArrayList<Person> unFilteredList = new ArrayList<>();
    private ArrayList<Person> filteredList = new ArrayList<>();

    public PeopleListAdapter(ArrayList<Person> list){
        super();
        this.unFilteredList = list;
        this.filteredList = list;
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


    public Filter getFilter(int filterType) {

        if (filterType == FILTER_BY_TEXT) {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String inputText = constraint.toString().toLowerCase();

                    if (inputText.isEmpty())
                        filteredList = unFilteredList;

                    else
                    {
                        ArrayList<Person> filteringList = new ArrayList<>();

                        for (Person person : unFilteredList)
                        {

                            if (person.name.contains(inputText) || person.address.contains(inputText))
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
        else if (filterType == FILTER_BY_STATE)
        {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    return null;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                }
            };
        }

        return null;
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
