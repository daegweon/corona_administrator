package com.example.corona_administrator;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Vector;

// here's our beautiful adapter
public class ArrayAdapterItem extends ArrayAdapter<ObjectItem> {

    Context mContext;
    int layoutResourceId;
    Vector<ObjectItem> data = null;

    public ArrayAdapterItem(Context mContext, int layoutResourceId, Vector<ObjectItem> data) {

        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
         * The convertView argument is essentially a "ScrapView" as described is Lucas post
         * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
         * It will have a non-null value when ListView is asking you recycle the row layout.
         * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
         */
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        // object item based on the position
        ObjectItem objectItem = data.get(position);

        // get the TextView and then set the text (item name) and tag (item ID) values
        TextView name = (TextView) convertView.findViewById(R.id.name_txt);
        TextView address = (TextView) convertView.findViewById(R.id.address_txt);
        TextView state = (TextView) convertView.findViewById(R.id.state_txt);
        name.setText(objectItem.personName);
        address.setText(objectItem.Address);
        state.setText(objectItem.State);
        return convertView;
    }
}