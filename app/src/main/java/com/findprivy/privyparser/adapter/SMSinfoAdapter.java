package com.findprivy.privyparser.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.findprivy.privyparser.R;
import com.findprivy.privyparser.model.SMSinfo;

import java.util.List;

import javax.annotation.Nullable;

import io.realm.ObjectChangeSet;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmObjectChangeListener;
import io.realm.RealmResults;

public class SMSinfoAdapter extends RecyclerView.Adapter<SMSinfoAdapter.ViewHolder>
{

    private RealmResults<SMSinfo> smsInfoList;




    static class ViewHolder extends RecyclerView.ViewHolder
    {

        //each data item is just a string in this case
        TextView amount,merchant,bank,date;

        ViewHolder(View v)
        {
            super(v);
            amount = (TextView)v.findViewById(R.id.item_view_amount);
            merchant = (TextView) v.findViewById(R.id.item_view_merchant);
            bank = (TextView) v.findViewById(R.id.item_view_bank);
            date = (TextView) v.findViewById(R.id.item_view_date);
        }
    }

    public SMSinfoAdapter(RealmResults<SMSinfo> SMSinfoList)
    {
        this.smsInfoList = SMSinfoList;
        this.smsInfoList.addChangeListener(new RealmChangeListener<RealmResults<SMSinfo>>()
        {

            @Override
            public void onChange(RealmResults<SMSinfo> smSinfos)
            {
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public SMSinfoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //Creating a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_smsinfo,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SMSinfoAdapter.ViewHolder holder, int position) {

        // - get element from arraylist at this position
        // - replace the contents of the view with that element

        SMSinfo thisSMSinfo = smsInfoList.get(position);
        holder.amount.setText("₹"+thisSMSinfo.getAmount());
        holder.merchant.setText(thisSMSinfo.getMerchant());
        holder.bank.setText(thisSMSinfo.getBank());
        holder.date.setText(thisSMSinfo.getDate());
    }

    @Override
    public int getItemCount()
    {
        return smsInfoList.size();
    }
}
