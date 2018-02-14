package com.destinationalert.rishabhishan.destinationalert;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by rishu on 3/8/2016.
 */
public class CustomAdapterHistory extends BaseAdapter {

    Context context;
    List<RowItemForHistory> rowItems;

    CustomAdapterHistory(Context context, List<RowItemForHistory> rowItems) {
        this.context = context;
        this.rowItems = rowItems;
    }

    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }

    /* private view holder class */
    private class ViewHolder {
        TextView startletter;
        TextView bookname;
        TextView type;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.fav_list_row, null);
            holder = new ViewHolder();

            holder.bookname = (TextView) convertView
                    .findViewById(R.id.place_name);
            holder.startletter = (TextView) convertView
                    .findViewById(R.id.textstart);
            holder.type = (TextView) convertView.findViewById(R.id.datetime);




            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        RowItemForHistory row_pos = rowItems.get(position);

        // holder.profile_pic.setImageResource(row_pos.getProfile_pic_id());
        holder.bookname.setText(row_pos.getPlaceName());
        holder.startletter.setText(row_pos.getstartLetter());
        holder.type.setText(row_pos.getDateTime());

        return convertView;
    }


}
