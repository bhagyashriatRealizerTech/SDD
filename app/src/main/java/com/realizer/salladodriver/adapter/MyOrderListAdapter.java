package com.realizer.salladodriver.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.realizer.salladodriver.R;
import com.realizer.salladodriver.databasemodel.UserDietDelivery;
import com.realizer.salladodriver.utils.ImageStorage;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Win on 09-05-2017.
 */

public class MyOrderListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    public View convertView;
    public  Context context;
    List<UserDietDelivery> orderFoods;

    public MyOrderListAdapter(List<UserDietDelivery> orderFoods, Context context){
        this.context = context;
        this.orderFoods = orderFoods;
        layoutInflater =LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return orderFoods.size();
    }

    @Override
    public Object getItem(int position) {
        return orderFoods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return orderFoods.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        ViewHolder holder;
        this.convertView = convertView;


        if (convertView == null) {
            this.convertView = layoutInflater.inflate(R.layout.my_order_list_item_layout, null);

            holder = new ViewHolder();
            holder.date = (TextView) this.convertView.findViewById(R.id.txt_date);
            holder.customerName = (TextView) this.convertView.findViewById(R.id.txt_customerName);
            holder.dishName = (TextView) this.convertView.findViewById(R.id.txt_dishName);
            holder.status = (TextView) this.convertView.findViewById(R.id.txt_status);
            holder.type = (TextView) this.convertView.findViewById(R.id.txt_type);
            holder.address = (TextView) this.convertView.findViewById(R.id.txt_address);
            holder.dishImage = (ImageView) this.convertView.findViewById(R.id.img_dish);

            this.convertView.setTag(holder);
        }

        else {
            holder = (ViewHolder) this.convertView.getTag();
        }

        holder.customerName.setText(orderFoods.get(position).getCustomerName());
        holder.dishName.setText(orderFoods.get(position).getDishName());
        holder.date.setText(orderFoods.get(position).getDate());
        holder.status.setText(orderFoods.get(position).getStatus());
        holder.address.setText(orderFoods.get(position).getDeliveryPoint());

        if(orderFoods.get(position).getType().equalsIgnoreCase("B"))
        holder.type.setText("Breakfast");
        else if(orderFoods.get(position).getType().equalsIgnoreCase("L"))
        holder.type.setText("Lunch");
        else if(orderFoods.get(position).getType().equalsIgnoreCase("S"))
            holder.type.setText("Snacks");
        else if(orderFoods.get(position).getType().equalsIgnoreCase("D"))
            holder.type.setText("Dinner");

        if(!orderFoods.get(position).getDishUrl().isEmpty()){
            ImageStorage.setThumbnail(holder.dishImage,orderFoods.get(position).getDishUrl());
        }

        return this.convertView;
    }
    static class ViewHolder{

        TextView date,customerName,dishName,status,type,address;
        ImageView dishImage;
    }
}


