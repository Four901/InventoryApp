package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventoryapp.data.InventDbProvider;

import android.widget.Toast;
import com.example.android.inventoryapp.Inventory_Activity;
import com.example.android.inventoryapp.data.InventContract;
import com.example.android.inventoryapp.data.InventContract.InventEntry;


public class InventCursorAdapter extends CursorAdapter {
    private Context mContext;

    public InventCursorAdapter(Context context, Cursor c) {
        super(context, c,0);
        this.mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ImageView inventImageIdView=(ImageView)view.findViewById(R.id.invent_imageId);
        TextView inventNameView=(TextView)view.findViewById(R.id.invent_nameId);
        TextView inventPriceView=(TextView)view.findViewById(R.id.invent_priceId);
        TextView inventQuantityView=(TextView)view.findViewById(R.id.invent_quantityId);
        TextView inventButtonView=(Button)view.findViewById(R.id.invent_floatingId);

        int nameColumnIndex=cursor.getColumnIndex(InventEntry.INVENTAPP_NAME_COLUMN);
        int priceColumnIndex=cursor.getColumnIndex(InventEntry.INVENTAPP_PRICE_COLUMN);
        int quantityColumnIndex=cursor.getColumnIndex(InventEntry.INVENTAPP_QUANTITY_COLUMN);
        int imageIdColumnIndex=cursor.getColumnIndex(InventEntry.INVENTAPP_IMAGEID_COLUMN);
        int idColumnIndex=cursor.getColumnIndex(InventEntry._ID);
        String inventName=cursor.getString(nameColumnIndex);
        int inventPrice=cursor.getInt(priceColumnIndex);
        int inventQuantity=cursor.getInt(quantityColumnIndex);
        int inventImageId=cursor.getInt(imageIdColumnIndex);

         inventImageIdView.setImageResource(inventImageId);
         inventNameView.setText(inventName);

         String price_Str="Price :-";
         price_Str+="\r\n";
         price_Str+=String.valueOf(inventPrice);
         inventPriceView.setText(price_Str);

         String quality_Str="Quantity :-";
         quality_Str+="\r\n";
         quality_Str+=String.valueOf(inventQuantity);
         inventQuantityView.setText(quality_Str);


        if(inventQuantity>1)
        {

                inventButtonView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        if(mContext instanceof Inventory_Activity) {

                            ((Inventory_Activity) mContext).orderInvent(cursor);
                        }
                    }
                });

        }
         if(inventQuantity==1)
        {
            inventButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    if(mContext instanceof Inventory_Activity)
                   {
                    ((Inventory_Activity)mContext).orderInvent(cursor);
                   }
                    inventButtonView.setEnabled(false);
                    inventButtonView.setBackgroundColor(Color.GREEN);

                }
            });


        }

        if(inventQuantity==0)
        {
            inventButtonView.setEnabled(false);
            inventButtonView.setBackgroundColor(Color.GREEN);

        }






    }



}
