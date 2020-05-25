package com.example.corona_administrator.ListAdapter;

import android.util.Log;
import android.widget.Filter;

import com.example.corona_administrator.Person;

import java.util.ArrayList;

public class FilterByText extends Filter{
    private PeopleListAdapter mAdapter;
    private ArrayList<Person> mUnFilteredList;

    public FilterByText(PeopleListAdapter adapter){
        mAdapter = adapter;
        mUnFilteredList = adapter.getUnFilteredList();
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        String input = constraint.toString().toLowerCase().trim();
        ArrayList<Person> filteringList = new ArrayList<>();
        FilterResults results = new FilterResults();

        if (input.isEmpty())
        {
            results.values = mUnFilteredList;
        }
        else
        {

            for (Person person : mUnFilteredList)
            {
                if (person.getName().toLowerCase().contains(input))
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
