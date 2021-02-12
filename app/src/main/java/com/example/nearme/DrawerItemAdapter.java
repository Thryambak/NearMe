package com.example.nearme;

import android.app.Activity;
import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.awt.font.TextAttribute;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DrawerItemAdapter extends ArrayAdapter<DataModel> {
    Context context;
    int layoutResourceId;
    DataModel data[]= null;

    public DrawerItemAdapter(Context context,int layoutResourceId,DataModel[] data){
        super(context,layoutResourceId,data);
        this.context = context;
        this.data=data;
        this.layoutResourceId=layoutResourceId;


    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        LayoutInflater layoutInflater = ((Activity)context ).getLayoutInflater();
        listItem = layoutInflater.inflate(layoutResourceId,parent,false);

        ImageView imageView = listItem.findViewById(R.id.imageViewIcon);
        TextView textView = listItem.findViewById(R.id.textViewName);

        DataModel model = data[position];
        imageView.setImageResource(model.icon);
        textView.setText(model.name);
        return  listItem;

    }
}
