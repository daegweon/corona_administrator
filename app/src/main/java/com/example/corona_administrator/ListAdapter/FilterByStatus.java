package com.example.corona_administrator.ListAdapter;

import android.widget.Filter;

import com.example.corona_administrator.Person;

import java.util.ArrayList;

public class FilterByStatus extends Filter{
    private PeopleListAdapter mAdapter;
    private ArrayList<Person> mUnFilteredList;

    public FilterByStatus(PeopleListAdapter adapter){
        mAdapter = adapter;
        mUnFilteredList = adapter.getUnFilteredList();
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        String status = constraint.toString().toLowerCase();
        ArrayList<Person> filteringList = new ArrayList<>();
        FilterResults results = new FilterResults();

        if (status.equals("전체"))
        {
            results.values = mUnFilteredList;
        }
        else
        {

            for (Person person : mUnFilteredList)
            {
                if (person.getState().equals(status))
                    filteringList.add(person);
            }

            results.values = filteringList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        mAdapter.setFilteredList((ArrayList<Person>) results.values);
        mAdapter.notifyDataSetChanged();
    }
}
